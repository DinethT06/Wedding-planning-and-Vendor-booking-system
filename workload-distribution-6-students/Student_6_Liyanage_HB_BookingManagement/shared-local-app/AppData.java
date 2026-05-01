package com.wedding.local;

import com.wedding.model.Vendor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

final class AppData {
    static final String ROLE_ADMIN = "Admin";
    static final String ROLE_CUSTOMER = "Customer";

    final Path adminsFile;
    final Path customersFile;
    final Path vendorsFile;
    final Path packagesFile;
    final Path bookingsFile;
    final Path reviewsFile;

    AppData(Path dataDir) {
        adminsFile = dataDir.resolve("admins.txt");
        customersFile = dataDir.resolve("customers.txt");
        vendorsFile = dataDir.resolve("vendors.txt");
        packagesFile = dataDir.resolve("packages.txt");
        bookingsFile = dataDir.resolve("bookings.txt");
        reviewsFile = dataDir.resolve("reviews.txt");
    }

    void ensureFiles() throws IOException {
        Files.createDirectories(adminsFile.getParent());
        for (Path file : List.of(adminsFile, customersFile, vendorsFile, packagesFile, bookingsFile, reviewsFile)) {
            if (!Files.exists(file)) {
                Files.createFile(file);
            }
        }
        if (isBlankFile(adminsFile)) {
            append(adminsFile, "A001|System Admin|admin@gmail.com|0770000000|Admin|admin@gmail.com|123456");
        }
        if (isBlankFile(packagesFile)) {
            append(packagesFile, "P001|Golden Celebration|Full Wedding|Venue styling, photography, and catering|LKR 450,000|Available");
            append(packagesFile, "P002|Classic Elegance|Ceremony Focus|Decor, floral work, and coordination support|LKR 220,000|Available");
        }
    }

    AdminAccount authenticateAdmin(String email, String password) throws IOException {
        for (AdminAccount admin : admins()) {
            if ((admin.email.equalsIgnoreCase(email) || admin.username.equalsIgnoreCase(email)) && admin.password.equals(password)) {
                return admin;
            }
        }
        return null;
    }

    CustomerAccount authenticateCustomer(String email, String password) throws IOException {
        for (CustomerAccount customer : customers()) {
            if (customer.email.equalsIgnoreCase(email) && customer.password.equals(password)) {
                return customer;
            }
        }
        return null;
    }

    CustomerAccount findCustomerByEmail(String email) throws IOException {
        for (CustomerAccount customer : customers()) {
            if (customer.email.equalsIgnoreCase(email)) {
                return customer;
            }
        }
        return null;
    }

    CustomerAccount findCustomerById(String id) throws IOException {
        for (CustomerAccount customer : customers()) {
            if (customer.id.equals(id)) {
                return customer;
            }
        }
        return null;
    }

    Vendor findVendorById(String id) throws IOException {
        for (Vendor vendor : vendors()) {
            if (vendor.getId().equals(id)) {
                return vendor;
            }
        }
        return null;
    }

    PackageInfo findPackageById(String id) throws IOException {
        for (PackageInfo item : packages()) {
            if (item.id.equals(id)) {
                return item;
            }
        }
        return null;
    }

    List<AdminAccount> admins() throws IOException { return read(adminsFile, AdminAccount::fromRecord); }
    List<CustomerAccount> customers() throws IOException { return read(customersFile, CustomerAccount::fromRecord); }
    List<Vendor> vendors() throws IOException { return read(vendorsFile, Vendor::fromFileRecord); }
    List<PackageInfo> packages() throws IOException { return read(packagesFile, PackageInfo::fromRecord); }
    List<BookingInfo> bookings() throws IOException { return read(bookingsFile, BookingInfo::fromRecord); }
    List<ReviewInfo> reviews() throws IOException { return read(reviewsFile, ReviewInfo::fromRecord); }

    void saveCustomer(CustomerAccount customer) throws IOException {
        append(customersFile, customer.toRecord());
    }

    void saveVendor(Vendor saved) throws IOException {
        List<String> lines = new ArrayList<>();
        boolean found = false;
        for (Vendor vendor : vendors()) {
            if (vendor.getId().equals(saved.getId())) {
                lines.add(saved.toFileRecord());
                found = true;
            } else {
                lines.add(vendor.toFileRecord());
            }
        }
        if (!found) {
            lines.add(saved.toFileRecord());
        }
        write(vendorsFile, lines);
    }

    void savePackage(PackageInfo saved) throws IOException {
        List<String> lines = new ArrayList<>();
        boolean found = false;
        for (PackageInfo item : packages()) {
            if (item.id.equals(saved.id)) {
                lines.add(saved.toRecord());
                found = true;
            } else {
                lines.add(item.toRecord());
            }
        }
        if (!found) {
            lines.add(saved.toRecord());
        }
        write(packagesFile, lines);
    }

    void saveBooking(BookingInfo booking) throws IOException { append(bookingsFile, booking.toRecord()); }
    void saveReview(ReviewInfo review) throws IOException { append(reviewsFile, review.toRecord()); }

    void updateBookingStatus(String id, String status) throws IOException {
        List<String> lines = new ArrayList<>();
        for (BookingInfo booking : bookings()) {
            if (booking.id.equals(id)) {
                booking.status = status;
            }
            lines.add(booking.toRecord());
        }
        write(bookingsFile, lines);
    }

    void deleteVendor(String id) throws IOException { delete(vendorsFile, Vendor::fromFileRecord, Vendor::getId, Vendor::toFileRecord, id); }
    void deleteCustomer(String id) throws IOException { delete(customersFile, CustomerAccount::fromRecord, c -> c.id, CustomerAccount::toRecord, id); }
    void deletePackage(String id) throws IOException { delete(packagesFile, PackageInfo::fromRecord, p -> p.id, PackageInfo::toRecord, id); }
    void deleteBooking(String id) throws IOException { delete(bookingsFile, BookingInfo::fromRecord, b -> b.id, BookingInfo::toRecord, id); }
    void deleteReview(String id) throws IOException { delete(reviewsFile, ReviewInfo::fromRecord, r -> r.id, ReviewInfo::toRecord, id); }

    String nextId(List<?> items, String prefix) {
        int max = 0;
        for (Object item : items) {
            String id = "";
            if (item instanceof Vendor) { id = ((Vendor) item).getId(); }
            if (item instanceof CustomerAccount) { id = ((CustomerAccount) item).id; }
            if (item instanceof PackageInfo) { id = ((PackageInfo) item).id; }
            if (item instanceof BookingInfo) { id = ((BookingInfo) item).id; }
            if (item instanceof ReviewInfo) { id = ((ReviewInfo) item).id; }
            if (item instanceof AdminAccount) { id = ((AdminAccount) item).id; }
            if (id.startsWith(prefix)) {
                try { max = Math.max(max, Integer.parseInt(id.substring(1))); } catch (NumberFormatException ignored) { }
            }
        }
        return String.format(Locale.ROOT, "%s%03d", prefix, max + 1);
    }

    String customerName(String id) throws IOException {
        CustomerAccount customer = findCustomerById(id);
        return customer == null ? id : customer.name;
    }

    String vendorName(String id) throws IOException {
        Vendor vendor = findVendorById(id);
        return vendor == null ? id : vendor.getName();
    }

    String packageName(String id) throws IOException {
        PackageInfo item = findPackageById(id);
        return item == null ? id : item.name;
    }

    private <T> List<T> read(Path file, Function<String, T> mapper) throws IOException {
        ensureFiles();
        List<T> items = new ArrayList<>();
        for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
            if (line.isBlank()) {
                continue;
            }
            T item = mapper.apply(line);
            if (item != null) {
                items.add(item);
            }
        }
        return items;
    }

    private boolean isBlankFile(Path file) throws IOException {
        for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
            if (!line.isBlank()) {
                return false;
            }
        }
        return true;
    }

    private <T> void delete(Path file, Function<String, T> mapper, Function<T, String> idGetter, Function<T, String> serializer, String id) throws IOException {
        List<String> lines = new ArrayList<>();
        for (T item : read(file, mapper)) {
            if (!idGetter.apply(item).equals(id)) {
                lines.add(serializer.apply(item));
            }
        }
        write(file, lines);
    }

    private void append(Path file, String line) throws IOException {
        Files.writeString(file, line + System.lineSeparator(), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    private void write(Path file, List<String> lines) throws IOException {
        Files.write(file, lines, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
    }

    static final class Session {
        final String userId;
        final String name;
        final String role;
        final String email;

        Session(String userId, String name, String role, String email) {
            this.userId = userId;
            this.name = name;
            this.role = role;
            this.email = email;
        }

        boolean isAdmin() { return ROLE_ADMIN.equals(role); }
    }

    static final class AdminAccount {
        final String id, name, email, phone, role, username, password;
        AdminAccount(String id, String name, String email, String phone, String role, String username, String password) {
            this.id = id; this.name = name; this.email = email; this.phone = phone; this.role = role; this.username = username; this.password = password;
        }
        static AdminAccount fromRecord(String line) {
            String[] p = line.split("\\|", -1);
            return p.length < 7 ? null : new AdminAccount(p[0], p[1], p[2], p[3], p[4], p[5], p[6]);
        }
    }

    static final class CustomerAccount {
        final String id, name, email, phone, weddingDate, address, password;
        CustomerAccount(String id, String name, String email, String phone, String weddingDate, String address, String password) {
            this.id = id; this.name = name; this.email = email; this.phone = phone; this.weddingDate = weddingDate; this.address = address; this.password = password;
        }
        static CustomerAccount fromRecord(String line) {
            String[] p = line.split("\\|", -1);
            return p.length < 7 ? null : new CustomerAccount(p[0], p[1], p[2], p[3], p[4], p[5], p[6]);
        }
        String toRecord() { return String.join("|", id, name, email, phone, weddingDate, address, password); }
    }

    static final class PackageInfo {
        final String id, name, category, description, price, status;
        PackageInfo(String id, String name, String category, String description, String price, String status) {
            this.id = id; this.name = name; this.category = category; this.description = description; this.price = price; this.status = status;
        }
        static PackageInfo fromRecord(String line) {
            String[] p = line.split("\\|", -1);
            return p.length < 6 ? null : new PackageInfo(p[0], p[1], p[2], p[3], p[4], p[5]);
        }
        String toRecord() { return String.join("|", id, name, category, description, price, status); }
    }

    static final class BookingInfo {
        final String id, customerId, vendorId, packageId, bookingDate;
        String status;
        BookingInfo(String id, String customerId, String vendorId, String packageId, String bookingDate, String status) {
            this.id = id; this.customerId = customerId; this.vendorId = vendorId; this.packageId = packageId; this.bookingDate = bookingDate; this.status = status;
        }
        static BookingInfo fromRecord(String line) {
            String[] p = line.split("\\|", -1);
            return p.length < 6 ? null : new BookingInfo(p[0], p[1], p[2], p[3], p[4], p[5]);
        }
        String toRecord() { return String.join("|", id, customerId, vendorId, packageId, bookingDate, status); }
    }

    static final class ReviewInfo {
        final String id, customerId, vendorId, rating, comment;
        ReviewInfo(String id, String customerId, String vendorId, String rating, String comment) {
            this.id = id; this.customerId = customerId; this.vendorId = vendorId; this.rating = rating; this.comment = comment;
        }
        static ReviewInfo fromRecord(String line) {
            String[] p = line.split("\\|", -1);
            return p.length < 5 ? null : new ReviewInfo(p[0], p[1], p[2], p[3], p[4]);
        }
        String toRecord() { return String.join("|", id, customerId, vendorId, rating, comment); }
    }
}
