package com.wedding.local;

import com.wedding.model.Vendor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

final class AppHtml {
    String publicPage(String title, String body) {
        return "<!DOCTYPE html><html lang='en'><head><meta charset='UTF-8'><meta name='viewport' content='width=device-width, initial-scale=1.0'>"
            + "<title>" + e(title) + "</title><link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css' rel='stylesheet'>"
            + "<style>body{min-height:100vh;margin:0;display:flex;align-items:center;justify-content:center;padding:24px;background:radial-gradient(circle at top left,#ffe7db 0,#f8fbff 45%,#eef5fb 100%);font-family:Segoe UI,Tahoma,sans-serif}.auth-card{width:min(100%,520px);border-radius:24px}.password-wrap{position:relative}.password-wrap .form-control{padding-right:56px}.toggle-password{position:absolute;top:50%;right:10px;transform:translateY(-50%);border:0;background:transparent;color:#6c757d;font-size:1.1rem;line-height:1;cursor:pointer}.toggle-password:hover{color:#17324d}@media (max-width:575.98px){body{padding:16px}.auth-card .card-body{padding:24px 18px!important}}</style>"
            + "</head><body>" + body + passwordScript() + "</body></html>";
    }

    String shellPage(String title, String activePath, AppData.Session session, String body) {
        return "<!DOCTYPE html><html lang='en'><head><meta charset='UTF-8'><meta name='viewport' content='width=device-width, initial-scale=1.0'>"
            + "<title>" + e(title) + "</title><link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css' rel='stylesheet'>"
            + "<style>:root{--sidebar-width:300px}body{margin:0;background:linear-gradient(135deg,#fff7f0 0%,#f8fbff 100%);font-family:Segoe UI,Tahoma,sans-serif;overflow-x:hidden}.app-shell{min-height:100vh}.mobile-bar{display:none;position:sticky;top:0;z-index:1030;background:rgba(248,251,255,.92);backdrop-filter:blur(10px);padding:12px 16px;border-bottom:1px solid rgba(23,50,77,.08)}.mobile-toggle{border:0;background:#17324d;color:#fff;border-radius:12px;padding:10px 12px;font-size:1.1rem;line-height:1}.sidebar{position:fixed;top:0;left:0;bottom:0;width:var(--sidebar-width);overflow-y:auto;background:#17324d;color:#fff;padding:28px 18px;box-shadow:18px 0 40px rgba(23,50,77,.15);transition:transform .25s ease}.sidebar a{display:block;color:#f8f9fa;text-decoration:none;padding:12px 14px;border-radius:12px;margin-bottom:8px;word-break:break-word}.sidebar a:hover,.sidebar a.active{background:#244a70}.user-chip{background:rgba(255,255,255,.12);border-radius:14px;padding:12px 14px;margin-bottom:18px}.content{margin-left:var(--sidebar-width);padding:36px 28px;min-height:100vh}.page-hero{background:linear-gradient(135deg,#d66b4d 0%,#f3b38a 100%);color:#fff;border-radius:22px;padding:24px 28px;margin-bottom:24px;box-shadow:0 14px 36px rgba(214,107,77,.18)}.module-card,.table-card{border:none;border-radius:20px;box-shadow:0 10px 30px rgba(23,50,77,.08)}.module-card .card-body,.table-card .card-body{padding:1.25rem}.table-responsive{border-radius:20px}.table td,.table th{white-space:nowrap}.content .row{margin-left:0;margin-right:0}@media (max-width:991.98px){.mobile-bar{display:flex;align-items:center;justify-content:space-between;gap:12px}.sidebar{transform:translateX(-100%);width:min(86vw,320px);z-index:1040}.sidebar.sidebar-open{transform:translateX(0)}.sidebar-backdrop{position:fixed;inset:0;background:rgba(0,0,0,.28);z-index:1035;display:none}.sidebar-backdrop.show{display:block}.content{margin-left:0;padding:20px 16px 28px}.page-hero{padding:20px 18px;border-radius:18px}.table td,.table th{font-size:.92rem}.module-card .btn{width:100%}}@media (max-width:575.98px){.content{padding:16px 12px 24px}.page-hero{padding:18px 16px}.page-hero .h2{font-size:1.5rem}.table td,.table th{font-size:.86rem}.user-chip{padding:10px 12px}}</style>"
            + "</head><body><div class='app-shell'><div class='mobile-bar'><button type='button' class='mobile-toggle' data-sidebar-toggle aria-label='Open menu' aria-expanded='false'>&#9776;</button><div class='fw-semibold text-truncate'>" + e(title) + "</div></div><div class='sidebar-backdrop' data-sidebar-backdrop></div>" + sidebar(activePath, session) + "<main class='content'>" + body + "</main></div>" + shellScript() + "</body></html>";
    }

    String loginBody(String message, String role, String email) {
        return "<div class='auth-card card border-0 shadow-lg'><div class='card-body p-4 p-md-5'><div class='text-center mb-4'><h1 class='h3 mb-2'>Wedding Planning and Vendor Booking System</h1><p class='text-muted mb-0'>One login page for admin and customer access.</p></div>"
            + alert(message, "danger")
            + "<form method='post' class='row g-3'><div class='col-12'><label class='form-label'>Login As</label><select name='role' class='form-select'>"
            + roleOption("", "Select role", role) + roleOption(AppData.ROLE_ADMIN, AppData.ROLE_ADMIN, role) + roleOption(AppData.ROLE_CUSTOMER, AppData.ROLE_CUSTOMER, role)
            + "</select></div><div class='col-12'><label class='form-label'>Email</label><input type='email' name='email' class='form-control' value='" + e(email) + "' required></div><div class='col-12'><label class='form-label'>Password</label>" + passwordInput("password", "password", true) + "<div class='form-text'>Password must be at least 6 characters.</div></div><div class='col-12 d-grid gap-2'><button class='btn btn-primary btn-lg'>Login</button><a href='/register' class='btn btn-outline-secondary'>Register</a></div></form></div></div>";
    }

    String registerBody(String message, Map<String, String> values) {
        return "<div class='auth-card card border-0 shadow-lg'><div class='card-body p-4 p-md-5'><div class='text-center mb-4'><h1 class='h3 mb-2'>Customer Registration</h1><p class='text-muted mb-0'>Create a customer account in the same system.</p></div>"
            + alert(message, "danger")
            + "<form method='post' class='row g-3'>"
            + input("Name", "name", values.get("name"), "text", true)
            + input("Email", "email", values.get("email"), "email", true)
            + input("Phone", "phone", values.get("phone"), "text", false)
            + input("Wedding Date", "weddingDate", values.get("weddingDate"), "date", false)
            + "<div class='col-12'><label class='form-label'>Address</label><textarea name='address' class='form-control' rows='3'>" + e(values.get("address")) + "</textarea></div>"
            + "<div class='col-12'><label class='form-label'>Password</label>" + passwordInput("password", "password", true) + "<div class='form-text'>Password must be at least 6 characters.</div></div><div class='col-12 d-grid gap-2'><button class='btn btn-primary btn-lg'>Register</button><a href='/login' class='btn btn-outline-secondary'>Back to Login</a></div></form></div></div>";
    }

    String hero(String title, String text) { return "<div class='page-hero'><h1 class='h2 mb-2'>" + e(title) + "</h1><p class='mb-0'>" + e(text) + "</p></div>"; }
    String moduleCard(String title, String text, String href, String btn) { return "<div class='col-md-6 col-xl-4'><div class='card module-card h-100'><div class='card-body'><h5 class='card-title'>" + e(title) + "</h5><p class='card-text'>" + e(text) + "</p><a href='" + href + "' class='btn btn-primary'>" + e(btn) + "</a></div></div></div>"; }
    String statCard(String title, String value) { return "<div class='col-sm-6 col-lg-3'><div class='card module-card h-100'><div class='card-body'><p class='text-muted text-uppercase small mb-2'>" + e(title) + "</p><div class='display-6 fw-semibold'>" + e(value) + "</div></div></div></div>"; }
    String cell(String value) { return "<td>" + e(value) + "</td>"; }
    String alert(String msg, String type) { return safe(msg).isBlank() ? "" : "<div class='alert alert-" + type + "'>" + e(msg) + "</div>"; }

    String table(String[] headers, String rows, String empty) {
        StringBuilder head = new StringBuilder();
        for (String h : headers) { head.append("<th>").append(e(h)).append("</th>"); }
        String body = rows.isBlank() ? "<tr><td colspan='" + headers.length + "' class='text-center py-4'>" + e(empty) + "</td></tr>" : rows;
        return "<div class='card table-card'><div class='card-body p-0'><div class='table-responsive'><table class='table table-hover align-middle mb-0'><thead class='table-dark'><tr>" + head + "</tr></thead><tbody>" + body + "</tbody></table></div></div></div>";
    }

    String vendorForm(Vendor editing) {
        String id = editing == null ? "" : editing.getId();
        return "<div class='card module-card'><div class='card-body p-4'><h2 class='h4 mb-3'>" + (editing == null ? "Add Vendor" : "Edit Vendor") + "</h2><form method='post' class='row g-3'>"
            + "<input type='hidden' name='action' value='save'><input type='hidden' name='id' value='" + e(id) + "'>"
            + input("Vendor Name", "name", editing == null ? "" : editing.getName(), "text", true)
            + input("Service Type", "serviceType", editing == null ? "" : editing.getServiceType(), "text", true)
            + input("Phone", "phone", editing == null ? "" : editing.getPhone(), "text", true)
            + input("Email", "email", editing == null ? "" : editing.getEmail(), "email", true)
            + input("Price Range", "priceRange", editing == null ? "" : editing.getPriceRange(), "text", true)
            + input("Availability", "availability", editing == null ? "" : editing.getAvailability(), "text", true)
            + select("Status", "status", editing == null ? "Active" : editing.getStatus(), List.of("Active", "Busy", "Inactive"))
            + "<div class='col-12 d-grid gap-2'><button class='btn btn-primary'>" + (editing == null ? "Save Vendor" : "Update Vendor") + "</button><a class='btn btn-outline-secondary' href='/admin/vendors'>Clear</a></div></form></div></div>";
    }

    String packageForm(AppData.PackageInfo editing) {
        return "<div class='card module-card'><div class='card-body p-4'><h2 class='h4 mb-3'>" + (editing == null ? "Add Package" : "Edit Package") + "</h2><form method='post' class='row g-3'>"
            + "<input type='hidden' name='action' value='save'><input type='hidden' name='id' value='" + e(editing == null ? "" : editing.id) + "'>"
            + input("Package Name", "name", editing == null ? "" : editing.name, "text", true)
            + input("Category", "category", editing == null ? "" : editing.category, "text", true)
            + "<div class='col-12'><label class='form-label'>Description</label><textarea name='description' class='form-control' rows='3'>" + e(editing == null ? "" : editing.description) + "</textarea></div>"
            + input("Price", "price", editing == null ? "" : editing.price, "text", true)
            + select("Status", "status", editing == null ? "Available" : editing.status, List.of("Available", "Limited", "Closed"))
            + "<div class='col-12 d-grid gap-2'><button class='btn btn-primary'>" + (editing == null ? "Save Package" : "Update Package") + "</button><a class='btn btn-outline-secondary' href='/admin/packages'>Clear</a></div></form></div></div>";
    }

    String bookingForm(List<AppData.PackageInfo> packages, List<Vendor> vendors, String selectedPackageId) {
        StringBuilder packageOptions = new StringBuilder("<option value=''>Select package</option>");
        for (AppData.PackageInfo item : packages) {
            packageOptions.append("<option value='").append(e(item.id)).append("'");
            if (item.id.equals(selectedPackageId)) { packageOptions.append(" selected"); }
            packageOptions.append(">").append(e(item.name)).append("</option>");
        }
        StringBuilder vendorOptions = new StringBuilder("<option value=''>Select vendor</option>");
        for (Vendor vendor : vendors) {
            vendorOptions.append("<option value='").append(e(vendor.getId())).append("'>").append(e(vendor.getName())).append(" - ").append(e(vendor.getServiceType())).append("</option>");
        }
        return "<div class='card module-card'><div class='card-body p-4'><form method='post' class='row g-3'><div class='col-md-6'><label class='form-label'>Package</label><select name='packageId' class='form-select' required>" + packageOptions + "</select></div><div class='col-md-6'><label class='form-label'>Vendor</label><select name='vendorId' class='form-select' required>" + vendorOptions + "</select></div><div class='col-md-6'><label class='form-label'>Booking Date</label><input type='date' name='bookingDate' class='form-control' value='" + LocalDate.now() + "' required></div><div class='col-12 d-grid'><button class='btn btn-primary'>Create Booking</button></div></form></div></div>";
    }

    String reviewForm(List<Vendor> vendors) {
        StringBuilder options = new StringBuilder("<option value=''>Select vendor</option>");
        for (Vendor vendor : vendors) { options.append("<option value='").append(e(vendor.getId())).append("'>").append(e(vendor.getName())).append("</option>"); }
        return "<div class='card module-card mb-4'><div class='card-body p-4'><h2 class='h4 mb-3'>Submit Review</h2><form method='post' class='row g-3'><div class='col-md-4'><label class='form-label'>Vendor</label><select name='vendorId' class='form-select' required>" + options + "</select></div><div class='col-md-3'><label class='form-label'>Rating</label>" + selectTag("rating", "5", List.of("5", "4", "3", "2", "1")) + "</div><div class='col-md-5'><label class='form-label'>Comment</label><input type='text' name='comment' class='form-control' required></div><div class='col-12 d-grid'><button class='btn btn-primary'>Add Review</button></div></form></div></div>";
    }

    private String sidebar(String activePath, AppData.Session session) {
        StringBuilder nav = new StringBuilder("<aside class='sidebar' data-sidebar><div class='d-flex justify-content-between align-items-center mb-3'><h3 class='mb-0'>Wedding System</h3><button type='button' class='mobile-toggle d-lg-none' data-sidebar-close aria-label='Close menu'>&times;</button></div><div class='user-chip'><div class='fw-semibold'>").append(e(session.name)).append("</div><div class='small'>").append(e(session.role)).append("</div></div>");
        if (session.isAdmin()) {
            nav.append(link("/admin/dashboard", "Admin Dashboard", activePath)).append(link("/admin/vendors", "Manage Vendors", activePath)).append(link("/admin/customers", "Manage Customers", activePath)).append(link("/admin/packages", "Manage Packages", activePath)).append(link("/admin/bookings", "Manage Bookings", activePath)).append(link("/admin/reviews", "Manage Reviews", activePath));
        } else {
            nav.append(link("/user/dashboard", "Customer Dashboard", activePath)).append(link("/user/packages", "View Wedding Packages", activePath)).append(link("/user/vendors", "View Vendors", activePath)).append(link("/user/book", "Make Booking", activePath)).append(link("/user/bookings", "My Bookings", activePath)).append(link("/user/reviews", "Add Review", activePath));
        }
        return nav.append(link("/logout", "Logout", "")).append("</aside>").toString();
    }

    private String input(String label, String name, String value, String type, boolean required) { return "<div class='col-md-6'><label class='form-label'>" + e(label) + "</label><input type='" + type + "' name='" + name + "' class='form-control' value='" + e(value) + "'" + (required ? " required" : "") + "></div>"; }
    private String select(String label, String name, String current, List<String> options) { return "<div class='col-md-6'><label class='form-label'>" + e(label) + "</label>" + selectTag(name, current, options) + "</div>"; }
    String selectTag(String name, String current, List<String> options) { StringBuilder b = new StringBuilder("<select name='").append(name).append("' class='form-select'>"); for (String option : options) { b.append("<option value='").append(e(option)).append("'"); if (option.equals(current)) { b.append(" selected"); } b.append(">").append(e(option)).append("</option>"); } return b.append("</select>").toString(); }
    private String passwordInput(String label, String name, boolean required) { return "<div class='password-wrap'><input type='password' id='" + label + "' name='" + name + "' class='form-control' minlength='6'" + (required ? " required" : "") + "><button type='button' class='toggle-password' data-target='" + label + "' aria-label='Show password' aria-pressed='false'>&#128065;</button></div>"; }
    private String passwordScript() { return "<script>document.querySelectorAll('.toggle-password').forEach(function(button){button.addEventListener('click',function(){var input=document.getElementById(button.dataset.target);var show=input.type==='password';input.type=show?'text':'password';button.setAttribute('aria-label',show?'Hide password':'Show password');button.setAttribute('aria-pressed',show?'true':'false');button.innerHTML=show?'&#128584;':'&#128065;';});});</script>"; }
    private String shellScript() { return "<script>(function(){var sidebar=document.querySelector('[data-sidebar]');var backdrop=document.querySelector('[data-sidebar-backdrop]');var toggle=document.querySelector('[data-sidebar-toggle]');var closeBtn=document.querySelector('[data-sidebar-close]');if(!sidebar){return;}function setOpen(open){sidebar.classList.toggle('sidebar-open',open);if(backdrop){backdrop.classList.toggle('show',open);}if(toggle){toggle.setAttribute('aria-expanded',open?'true':'false');}}if(toggle){toggle.addEventListener('click',function(){setOpen(!sidebar.classList.contains('sidebar-open'));});}if(closeBtn){closeBtn.addEventListener('click',function(){setOpen(false);});}if(backdrop){backdrop.addEventListener('click',function(){setOpen(false);});}document.querySelectorAll('.sidebar a').forEach(function(link){link.addEventListener('click',function(){if(window.innerWidth<992){setOpen(false);}});});window.addEventListener('resize',function(){if(window.innerWidth>=992){setOpen(false);}});})();</script>"; }
    private String link(String href, String label, String activePath) { return "<a href='" + href + "'" + (href.equals(activePath) ? " class='active'" : "") + ">" + e(label) + "</a>"; }
    private String roleOption(String value, String label, String current) { return "<option value='" + e(value) + "'" + (value.equals(current) ? " selected" : "") + ">" + e(label) + "</option>"; }
    private String safe(String value) { return value == null ? "" : value.trim(); }
    String e(String value) { String s = safe(value); return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;"); }
}
