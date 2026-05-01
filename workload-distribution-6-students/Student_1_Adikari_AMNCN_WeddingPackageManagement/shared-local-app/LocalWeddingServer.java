package com.wedding.local;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.wedding.model.Vendor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpCookie;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

public class LocalWeddingServer {
    private static final String SESSION_COOKIE = "WEDDING_SESSION";

    private final AppData data = new AppData(Paths.get("data"));
    private final AppHtml html = new AppHtml();
    private final Map<String, AppData.Session> sessions = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;
        LocalWeddingServer app = new LocalWeddingServer();
        app.data.ensureFiles();

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", app::root);
        server.createContext("/login", app::login);
        server.createContext("/register", app::register);
        server.createContext("/logout", app::logout);
        server.createContext("/admin/dashboard", app::adminDashboard);
        server.createContext("/admin/vendors", app::adminVendors);
        server.createContext("/admin/customers", app::adminCustomers);
        server.createContext("/admin/packages", app::adminPackages);
        server.createContext("/admin/bookings", app::adminBookings);
        server.createContext("/admin/reviews", app::adminReviews);
        server.createContext("/user/dashboard", app::userDashboard);
        server.createContext("/user/packages", app::userPackages);
        server.createContext("/user/vendors", app::userVendors);
        server.createContext("/user/book", app::userBook);
        server.createContext("/user/bookings", app::userBookings);
        server.createContext("/user/reviews", app::userReviews);
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
        System.out.println("Wedding app running on http://localhost:" + port + "/login");
    }

    private void root(HttpExchange ex) throws IOException {
        redirect(ex, "/login");
    }

    private void login(HttpExchange ex) throws IOException {
        if ("GET".equalsIgnoreCase(ex.getRequestMethod())) {
            sendHtml(ex, html.publicPage("Login", html.loginBody("", "", "")));
            return;
        }
        Map<String, String> p = form(ex);
        String role = safe(p.get("role"));
        String email = safe(p.get("email"));
        String password = safe(p.get("password"));
        if (password.length() < 6) {
            sendHtml(ex, html.publicPage("Login", html.loginBody("Password must be at least 6 characters.", role, email)));
            return;
        }
        if (AppData.ROLE_ADMIN.equals(role)) {
            AppData.AdminAccount admin = data.authenticateAdmin(email, password);
            if (admin != null) {
                createSession(ex, new AppData.Session(admin.id, admin.name, AppData.ROLE_ADMIN, admin.email));
                redirect(ex, "/admin/dashboard");
                return;
            }
        }
        if (AppData.ROLE_CUSTOMER.equals(role)) {
            AppData.CustomerAccount customer = data.authenticateCustomer(email, password);
            if (customer != null) {
                createSession(ex, new AppData.Session(customer.id, customer.name, AppData.ROLE_CUSTOMER, customer.email));
                redirect(ex, "/user/dashboard");
                return;
            }
        }
        sendHtml(ex, html.publicPage("Login", html.loginBody("Invalid role, email, or password.", role, email)));
    }

    private void register(HttpExchange ex) throws IOException {
        if ("GET".equalsIgnoreCase(ex.getRequestMethod())) {
            sendHtml(ex, html.publicPage("Register", html.registerBody("", new HashMap<>())));
            return;
        }
        Map<String, String> p = form(ex);
        String name = safe(p.get("name"));
        String email = safe(p.get("email"));
        String password = safe(p.get("password"));
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            sendHtml(ex, html.publicPage("Register", html.registerBody("Name, email, and password are required.", p)));
            return;
        }
        if (password.length() < 6) {
            sendHtml(ex, html.publicPage("Register", html.registerBody("Password must be at least 6 characters.", p)));
            return;
        }
        if (data.findCustomerByEmail(email) != null) {
            sendHtml(ex, html.publicPage("Register", html.registerBody("A customer account already exists for this email.", p)));
            return;
        }
        data.saveCustomer(new AppData.CustomerAccount(data.nextId(data.customers(), "C"), name, email, safe(p.get("phone")), safe(p.get("weddingDate")), safe(p.get("address")), password));
        sendHtml(ex, html.publicPage("Login", html.loginBody("Registration complete. Please log in.", AppData.ROLE_CUSTOMER, email)));
    }

    private void logout(HttpExchange ex) throws IOException {
        String token = cookie(ex, SESSION_COOKIE);
        if (!token.isBlank()) {
            sessions.remove(token);
        }
        ex.getResponseHeaders().add("Set-Cookie", SESSION_COOKIE + "=; Path=/; Max-Age=0");
        redirect(ex, "/login");
    }

    private void adminDashboard(HttpExchange ex) throws IOException {
        AppData.Session s = require(ex, AppData.ROLE_ADMIN);
        if (s == null) { return; }
        String body = html.hero("Admin Dashboard", "Manage vendors, customers, packages, bookings, and reviews from one system.")
            + "<div class='row g-4'>"
            + html.moduleCard("Manage Vendors", "Add, edit, and delete vendor records.", "/admin/vendors", "Open")
            + html.moduleCard("Manage Customers", "View registered customers.", "/admin/customers", "Open")
            + html.moduleCard("Manage Packages", "Create and update wedding packages.", "/admin/packages", "Open")
            + html.moduleCard("Manage Bookings", "Review booking requests and statuses.", "/admin/bookings", "Open")
            + html.moduleCard("Manage Reviews", "View customer feedback.", "/admin/reviews", "Open")
            + html.statCard("Vendors", String.valueOf(data.vendors().size()))
            + html.statCard("Customers", String.valueOf(data.customers().size()))
            + html.statCard("Packages", String.valueOf(data.packages().size()))
            + html.statCard("Bookings", String.valueOf(data.bookings().size()))
            + html.statCard("Reviews", String.valueOf(data.reviews().size()))
            + "</div>";
        sendHtml(ex, html.shellPage("Admin Dashboard", "/admin/dashboard", s, body));
    }

    private void adminVendors(HttpExchange ex) throws IOException {
        AppData.Session s = require(ex, AppData.ROLE_ADMIN);
        if (s == null) { return; }
        if ("POST".equalsIgnoreCase(ex.getRequestMethod())) {
            Map<String, String> p = form(ex);
            if ("delete".equals(safe(p.get("action")))) {
                data.deleteVendor(safe(p.get("id")));
            } else {
                String id = safe(p.get("id"));
                if (id.isBlank()) { id = data.nextId(data.vendors(), "V"); }
                data.saveVendor(new Vendor(id, safe(p.get("name")), safe(p.get("serviceType")), safe(p.get("phone")), safe(p.get("email")), safe(p.get("priceRange")), safe(p.get("availability")), safe(p.get("status"))));
            }
            redirect(ex, "/admin/vendors");
            return;
        }
        Map<String, String> q = query(ex);
        Vendor editing = data.findVendorById(safe(q.get("edit")));
        StringBuilder rows = new StringBuilder();
        for (Vendor v : data.vendors()) {
            rows.append("<tr>").append(html.cell(v.getId())).append(html.cell(v.getName())).append(html.cell(v.getServiceType())).append(html.cell(v.getPhone())).append(html.cell(v.getEmail())).append(html.cell(v.getPriceRange())).append(html.cell(v.getAvailability())).append(html.cell(v.getStatus()))
                .append("<td class='d-flex gap-2'><a class='btn btn-sm btn-warning' href='/admin/vendors?edit=").append(enc(v.getId())).append("'>Edit</a><form method='post'><input type='hidden' name='action' value='delete'><input type='hidden' name='id' value='").append(html.e(v.getId())).append("'><button class='btn btn-sm btn-danger'>Delete</button></form></td></tr>");
        }
        String body = html.hero("Manage Vendors", "Admin can add, view, edit, and delete vendor records.")
            + "<div class='row g-4'><div class='col-xl-4'>" + html.vendorForm(editing) + "</div><div class='col-xl-8'>" + html.table(new String[]{"ID", "Name", "Service", "Phone", "Email", "Price", "Availability", "Status", "Actions"}, rows.toString(), "No vendors found.") + "</div></div>";
        sendHtml(ex, html.shellPage("Manage Vendors", "/admin/vendors", s, body));
    }

    private void adminCustomers(HttpExchange ex) throws IOException {
        AppData.Session s = require(ex, AppData.ROLE_ADMIN);
        if (s == null) { return; }
        if ("POST".equalsIgnoreCase(ex.getRequestMethod())) {
            data.deleteCustomer(safe(form(ex).get("id")));
            redirect(ex, "/admin/customers");
            return;
        }
        StringBuilder rows = new StringBuilder();
        for (AppData.CustomerAccount c : data.customers()) {
            rows.append("<tr>").append(html.cell(c.id)).append(html.cell(c.name)).append(html.cell(c.email)).append(html.cell(c.phone)).append(html.cell(c.weddingDate)).append(html.cell(c.address))
                .append("<td><form method='post'><input type='hidden' name='id' value='").append(html.e(c.id)).append("'><button class='btn btn-sm btn-danger'>Delete</button></form></td></tr>");
        }
        String body = html.hero("Manage Customers", "View all registered customers.")
            + html.table(new String[]{"ID", "Name", "Email", "Phone", "Wedding Date", "Address", "Action"}, rows.toString(), "No customers registered.");
        sendHtml(ex, html.shellPage("Manage Customers", "/admin/customers", s, body));
    }

    private void adminPackages(HttpExchange ex) throws IOException {
        AppData.Session s = require(ex, AppData.ROLE_ADMIN);
        if (s == null) { return; }
        if ("POST".equalsIgnoreCase(ex.getRequestMethod())) {
            Map<String, String> p = form(ex);
            if ("delete".equals(safe(p.get("action")))) {
                data.deletePackage(safe(p.get("id")));
            } else {
                String id = safe(p.get("id"));
                if (id.isBlank()) { id = data.nextId(data.packages(), "P"); }
                data.savePackage(new AppData.PackageInfo(id, safe(p.get("name")), safe(p.get("category")), safe(p.get("description")), safe(p.get("price")), safe(p.get("status"))));
            }
            redirect(ex, "/admin/packages");
            return;
        }
        AppData.PackageInfo editing = data.findPackageById(safe(query(ex).get("edit")));
        StringBuilder rows = new StringBuilder();
        for (AppData.PackageInfo item : data.packages()) {
            rows.append("<tr>").append(html.cell(item.id)).append(html.cell(item.name)).append(html.cell(item.category)).append(html.cell(item.description)).append(html.cell(item.price)).append(html.cell(item.status))
                .append("<td class='d-flex gap-2'><a class='btn btn-sm btn-warning' href='/admin/packages?edit=").append(enc(item.id)).append("'>Edit</a><form method='post'><input type='hidden' name='action' value='delete'><input type='hidden' name='id' value='").append(html.e(item.id)).append("'><button class='btn btn-sm btn-danger'>Delete</button></form></td></tr>");
        }
        String body = html.hero("Manage Wedding Packages", "Create and maintain wedding packages.")
            + "<div class='row g-4'><div class='col-xl-4'>" + html.packageForm(editing) + "</div><div class='col-xl-8'>" + html.table(new String[]{"ID", "Name", "Category", "Description", "Price", "Status", "Actions"}, rows.toString(), "No packages found.") + "</div></div>";
        sendHtml(ex, html.shellPage("Manage Packages", "/admin/packages", s, body));
    }

    private void adminBookings(HttpExchange ex) throws IOException {
        AppData.Session s = require(ex, AppData.ROLE_ADMIN);
        if (s == null) { return; }
        if ("POST".equalsIgnoreCase(ex.getRequestMethod())) {
            Map<String, String> p = form(ex);
            if ("delete".equals(safe(p.get("action")))) { data.deleteBooking(safe(p.get("id"))); } else { data.updateBookingStatus(safe(p.get("id")), safe(p.get("status"))); }
            redirect(ex, "/admin/bookings");
            return;
        }
        StringBuilder rows = new StringBuilder();
        for (AppData.BookingInfo b : data.bookings()) {
            rows.append("<tr>").append(html.cell(b.id)).append(html.cell(data.customerName(b.customerId))).append(html.cell(data.packageName(b.packageId))).append(html.cell(data.vendorName(b.vendorId))).append(html.cell(b.bookingDate))
                .append("<td><form method='post' class='d-flex gap-2 align-items-center'><input type='hidden' name='id' value='").append(html.e(b.id)).append("'><input type='hidden' name='action' value='status'>").append(html.selectTag("status", b.status, List.of("Pending", "Confirmed", "Rejected"))).append("<button class='btn btn-sm btn-primary'>Save</button></form></td>")
                .append("<td><form method='post'><input type='hidden' name='action' value='delete'><input type='hidden' name='id' value='").append(html.e(b.id)).append("'><button class='btn btn-sm btn-danger'>Delete</button></form></td></tr>");
        }
        String body = html.hero("Manage Bookings", "Approve, reject, or remove customer bookings.")
            + html.table(new String[]{"ID", "Customer", "Package", "Vendor", "Booking Date", "Status", "Action"}, rows.toString(), "No bookings found.");
        sendHtml(ex, html.shellPage("Manage Bookings", "/admin/bookings", s, body));
    }

    private void adminReviews(HttpExchange ex) throws IOException {
        AppData.Session s = require(ex, AppData.ROLE_ADMIN);
        if (s == null) { return; }
        if ("POST".equalsIgnoreCase(ex.getRequestMethod())) {
            data.deleteReview(safe(form(ex).get("id")));
            redirect(ex, "/admin/reviews");
            return;
        }
        StringBuilder rows = new StringBuilder();
        for (AppData.ReviewInfo r : data.reviews()) {
            rows.append("<tr>").append(html.cell(r.id)).append(html.cell(data.customerName(r.customerId))).append(html.cell(data.vendorName(r.vendorId))).append(html.cell(r.rating)).append(html.cell(r.comment))
                .append("<td><form method='post'><input type='hidden' name='id' value='").append(html.e(r.id)).append("'><button class='btn btn-sm btn-danger'>Delete</button></form></td></tr>");
        }
        String body = html.hero("Manage Reviews", "See customer reviews and remove entries if needed.")
            + html.table(new String[]{"ID", "Customer", "Vendor", "Rating", "Comment", "Action"}, rows.toString(), "No reviews found.");
        sendHtml(ex, html.shellPage("Manage Reviews", "/admin/reviews", s, body));
    }

    private void userDashboard(HttpExchange ex) throws IOException {
        AppData.Session s = require(ex, AppData.ROLE_CUSTOMER);
        if (s == null) { return; }
        String body = html.hero("Customer Dashboard", "View packages, vendors, bookings, and reviews from one customer panel.")
            + "<div class='row g-4'>"
            + html.moduleCard("View Wedding Packages", "Browse available wedding packages.", "/user/packages", "Open")
            + html.moduleCard("View Vendors", "See available vendors.", "/user/vendors", "Open")
            + html.moduleCard("Make Booking", "Book a package and vendor.", "/user/book", "Open")
            + html.moduleCard("My Bookings", "Track all your bookings.", "/user/bookings", "Open")
            + html.moduleCard("Add Review", "Leave feedback for vendors.", "/user/reviews", "Open")
            + "</div>";
        sendHtml(ex, html.shellPage("Customer Dashboard", "/user/dashboard", s, body));
    }

    private void userPackages(HttpExchange ex) throws IOException {
        AppData.Session s = require(ex, AppData.ROLE_CUSTOMER);
        if (s == null) { return; }
        StringBuilder cards = new StringBuilder();
        for (AppData.PackageInfo p : data.packages()) {
            cards.append("<div class='col-md-6 col-xl-4'><div class='card module-card h-100'><div class='card-body'><h5 class='card-title'>").append(html.e(p.name)).append("</h5><p class='small text-muted mb-2'>").append(html.e(p.category)).append("</p><p>").append(html.e(p.description)).append("</p><p class='fw-semibold mb-2'>").append(html.e(p.price)).append("</p><p><span class='badge bg-success'>").append(html.e(p.status)).append("</span></p><a href='/user/book?packageId=").append(enc(p.id)).append("' class='btn btn-primary'>Book Now</a></div></div></div>");
        }
        if (cards.length() == 0) { cards.append("<div class='alert alert-secondary'>No packages available yet.</div>"); }
        String body = html.hero("View Wedding Packages", "Customer can review package details before booking.") + "<div class='row g-4'>" + cards + "</div>";
        sendHtml(ex, html.shellPage("View Wedding Packages", "/user/packages", s, body));
    }

    private void userVendors(HttpExchange ex) throws IOException {
        AppData.Session s = require(ex, AppData.ROLE_CUSTOMER);
        if (s == null) { return; }
        StringBuilder rows = new StringBuilder();
        for (Vendor v : data.vendors()) {
            rows.append("<tr>").append(html.cell(v.getId())).append(html.cell(v.getName())).append(html.cell(v.getServiceType())).append(html.cell(v.getPhone())).append(html.cell(v.getEmail())).append(html.cell(v.getPriceRange())).append(html.cell(v.getAvailability())).append(html.cell(v.getStatus())).append("</tr>");
        }
        String body = html.hero("View Vendors", "Customer can view vendors before making bookings.")
            + html.table(new String[]{"ID", "Name", "Service", "Phone", "Email", "Price", "Availability", "Status"}, rows.toString(), "No vendors available.");
        sendHtml(ex, html.shellPage("View Vendors", "/user/vendors", s, body));
    }

    private void userBook(HttpExchange ex) throws IOException {
        AppData.Session s = require(ex, AppData.ROLE_CUSTOMER);
        if (s == null) { return; }
        if ("POST".equalsIgnoreCase(ex.getRequestMethod())) {
            Map<String, String> p = form(ex);
            data.saveBooking(new AppData.BookingInfo(data.nextId(data.bookings(), "B"), s.userId, safe(p.get("vendorId")), safe(p.get("packageId")), safe(p.get("bookingDate")), "Pending"));
            redirect(ex, "/user/bookings");
            return;
        }
        String body = html.hero("Make Booking", "Choose a package, vendor, and booking date.") + html.bookingForm(data.packages(), data.vendors(), safe(query(ex).get("packageId")));
        sendHtml(ex, html.shellPage("Make Booking", "/user/book", s, body));
    }

    private void userBookings(HttpExchange ex) throws IOException {
        AppData.Session s = require(ex, AppData.ROLE_CUSTOMER);
        if (s == null) { return; }
        StringBuilder rows = new StringBuilder();
        for (AppData.BookingInfo b : data.bookings()) {
            if (b.customerId.equals(s.userId)) {
                rows.append("<tr>").append(html.cell(b.id)).append(html.cell(data.packageName(b.packageId))).append(html.cell(data.vendorName(b.vendorId))).append(html.cell(b.bookingDate)).append(html.cell(b.status)).append("</tr>");
            }
        }
        String body = html.hero("My Bookings", "Customer can track all personal bookings.")
            + html.table(new String[]{"Booking ID", "Package", "Vendor", "Booking Date", "Status"}, rows.toString(), "No bookings found.");
        sendHtml(ex, html.shellPage("My Bookings", "/user/bookings", s, body));
    }

    private void userReviews(HttpExchange ex) throws IOException {
        AppData.Session s = require(ex, AppData.ROLE_CUSTOMER);
        if (s == null) { return; }
        if ("POST".equalsIgnoreCase(ex.getRequestMethod())) {
            Map<String, String> p = form(ex);
            data.saveReview(new AppData.ReviewInfo(data.nextId(data.reviews(), "R"), s.userId, safe(p.get("vendorId")), safe(p.get("rating")), safe(p.get("comment"))));
            redirect(ex, "/user/reviews");
            return;
        }
        StringBuilder rows = new StringBuilder();
        for (AppData.ReviewInfo r : data.reviews()) {
            if (r.customerId.equals(s.userId)) {
                rows.append("<tr>").append(html.cell(r.id)).append(html.cell(data.vendorName(r.vendorId))).append(html.cell(r.rating)).append(html.cell(r.comment)).append("</tr>");
            }
        }
        String body = html.hero("Add Review", "Customer can submit and view vendor reviews.")
            + html.reviewForm(data.vendors())
            + html.table(new String[]{"ID", "Vendor", "Rating", "Comment"}, rows.toString(), "No reviews added yet.");
        sendHtml(ex, html.shellPage("Add Review", "/user/reviews", s, body));
    }

    private AppData.Session require(HttpExchange ex, String role) throws IOException {
        AppData.Session s = session(ex);
        if (s == null) { redirect(ex, "/login"); return null; }
        if (!role.equals(s.role)) { redirect(ex, s.isAdmin() ? "/admin/dashboard" : "/user/dashboard"); return null; }
        return s;
    }

    private AppData.Session session(HttpExchange ex) {
        String token = cookie(ex, SESSION_COOKIE);
        return token.isBlank() ? null : sessions.get(token);
    }

    private void createSession(HttpExchange ex, AppData.Session session) {
        String token = UUID.randomUUID().toString();
        sessions.put(token, session);
        ex.getResponseHeaders().add("Set-Cookie", SESSION_COOKIE + "=" + token + "; Path=/; HttpOnly");
    }

    private String cookie(HttpExchange ex, String name) {
        List<String> headers = ex.getRequestHeaders().get("Cookie");
        if (headers == null) { return ""; }
        for (String header : headers) {
            for (HttpCookie cookie : HttpCookie.parse(header)) {
                if (name.equals(cookie.getName())) { return safe(cookie.getValue()); }
            }
        }
        return "";
    }

    private Map<String, String> query(HttpExchange ex) { return parse(ex.getRequestURI().getRawQuery()); }
    private Map<String, String> form(HttpExchange ex) throws IOException { return parse(body(ex)); }

    private Map<String, String> parse(String raw) {
        Map<String, String> map = new LinkedHashMap<>();
        if (raw == null || raw.isBlank()) { return map; }
        for (String pair : raw.split("&")) {
            if (!pair.isEmpty()) {
                String[] kv = pair.split("=", 2);
                map.put(dec(kv[0]), kv.length > 1 ? dec(kv[1]) : "");
            }
        }
        return map;
    }

    private String body(HttpExchange ex) throws IOException {
        try (InputStream input = ex.getRequestBody()) { return new String(input.readAllBytes(), StandardCharsets.UTF_8); }
    }

    private void redirect(HttpExchange ex, String location) throws IOException {
        Headers headers = ex.getResponseHeaders();
        headers.set("Location", location);
        ex.sendResponseHeaders(302, -1);
        ex.close();
    }

    private void sendHtml(HttpExchange ex, String content) throws IOException {
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        ex.sendResponseHeaders(200, bytes.length);
        try (OutputStream output = ex.getResponseBody()) { output.write(bytes); }
    }

    private String dec(String value) { return URLDecoder.decode(value, StandardCharsets.UTF_8); }
    private String safe(String value) { return value == null ? "" : value.trim(); }
    private String enc(String value) { return safe(value).replace(" ", "+"); }
}
