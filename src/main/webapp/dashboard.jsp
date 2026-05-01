<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Wedding Planning Dashboard</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body {
            background: linear-gradient(135deg, #fff7f0 0%, #f8fbff 100%);
            min-height: 100vh;
        }
        .sidebar {
            min-height: 100vh;
            background: #17324d;
        }
        .sidebar a {
            color: #f8f9fa;
            text-decoration: none;
            display: block;
            padding: 12px 16px;
            border-radius: 10px;
            margin-bottom: 8px;
        }
        .sidebar a:hover,
        .sidebar a.active {
            background: #244a70;
        }
        .brand-box {
            background: linear-gradient(135deg, #d66b4d 0%, #f3b38a 100%);
            border-radius: 18px;
            color: #fff;
        }
        .module-card {
            border: none;
            border-radius: 18px;
            box-shadow: 0 10px 30px rgba(23, 50, 77, 0.08);
        }
    </style>
</head>
<body>
<div class="container-fluid">
    <div class="row">
        <aside class="col-lg-3 col-xl-2 sidebar p-4">
            <h3 class="text-white mb-4">Wedding System</h3>
            <a href="<%= request.getContextPath() %>/dashboard.jsp" class="active">Dashboard</a>
            <a href="<%= request.getContextPath() %>/vendors">Vendor Management</a>
            <a href="<%= request.getContextPath() %>/customers">Customer Management</a>
            <a href="<%= request.getContextPath() %>/packages">Package Management</a>
            <a href="<%= request.getContextPath() %>/bookings">Booking Management</a>
            <a href="<%= request.getContextPath() %>/reviews">Feedback & Review</a>
            <a href="<%= request.getContextPath() %>/admins">Admin Management</a>
        </aside>
        <main class="col-lg-9 col-xl-10 p-4 p-md-5">
            <div class="brand-box p-4 mb-4">
                <h1 class="h2">Wedding Planning and Vendor Booking System</h1>
                <p class="mb-0">JSP, Servlets, Bootstrap, and TXT file handling project dashboard.</p>
            </div>

            <div class="row g-4">
                <div class="col-md-6 col-xl-4">
                    <div class="card module-card h-100">
                        <div class="card-body">
                            <h5 class="card-title">Vendor Management</h5>
                            <p class="card-text">Add, search, edit, and remove wedding vendors using TXT file storage.</p>
                            <a href="<%= request.getContextPath() %>/vendors" class="btn btn-primary">Open Module</a>
                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-xl-4">
                    <div class="card module-card h-100">
                        <div class="card-body">
                            <h5 class="card-title">Customer Management</h5>
                            <p class="card-text">Scaffolded and ready for the next CRUD implementation step.</p>
                            <a href="customers" class="btn btn-outline-secondary disabled">Coming Next</a>
                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-xl-4">
                    <div class="card module-card h-100">
                        <div class="card-body">
                            <h5 class="card-title">Package Management</h5>
                            <p class="card-text">Planned for wedding package details, pricing, and status updates.</p>
                            <a href="packages" class="btn btn-outline-secondary disabled">Coming Next</a>
                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-xl-4">
                    <div class="card module-card h-100">
                        <div class="card-body">
                            <h5 class="card-title">Booking Management</h5>
                            <p class="card-text">Booking records will connect vendors, customers, and packages.</p>
                            <a href="bookings" class="btn btn-outline-secondary disabled">Coming Next</a>
                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-xl-4">
                    <div class="card module-card h-100">
                        <div class="card-body">
                            <h5 class="card-title">Feedback & Review</h5>
                            <p class="card-text">Customer ratings and comments can be handled in this module.</p>
                            <a href="reviews" class="btn btn-outline-secondary disabled">Coming Next</a>
                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-xl-4">
                    <div class="card module-card h-100">
                        <div class="card-body">
                            <h5 class="card-title">Admin Management</h5>
                            <p class="card-text">Administrative user records are scaffolded with OOP inheritance.</p>
                            <a href="admins" class="btn btn-outline-secondary disabled">Coming Next</a>
                        </div>
                    </div>
                </div>
            </div>
        </main>
    </div>
</div>
</body>
</html>
