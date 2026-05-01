<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.wedding.model.Vendor" %>
<%
    List<Vendor> vendors = (List<Vendor>) request.getAttribute("vendors");
    String search = request.getAttribute("search") == null ? "" : request.getAttribute("search").toString();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Vendor Management</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<div class="container py-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h1 class="h3 mb-1">Vendor Management</h1>
            <p class="text-muted mb-0">Create, search, update, and delete wedding vendors.</p>
        </div>
        <div class="d-flex gap-2">
            <a href="<%= request.getContextPath() %>/dashboard.jsp" class="btn btn-outline-secondary">Dashboard</a>
            <a href="<%= request.getContextPath() %>/vendors?action=new" class="btn btn-primary">Add Vendor</a>
        </div>
    </div>

    <div class="card shadow-sm border-0 mb-4">
        <div class="card-body">
            <form action="<%= request.getContextPath() %>/vendors" method="get" class="row g-3">
                <input type="hidden" name="action" value="list">
                <div class="col-md-10">
                    <input type="text" name="search" class="form-control" placeholder="Search by ID, name, service type, or status" value="<%= search %>">
                </div>
                <div class="col-md-2 d-grid">
                    <button type="submit" class="btn btn-dark">Search</button>
                </div>
            </form>
        </div>
    </div>

    <div class="card shadow-sm border-0">
        <div class="card-body p-0">
            <div class="table-responsive">
                <table class="table table-hover align-middle mb-0">
                    <thead class="table-dark">
                    <tr>
                        <th>ID</th>
                        <th>Name</th>
                        <th>Service Type</th>
                        <th>Phone</th>
                        <th>Email</th>
                        <th>Price Range</th>
                        <th>Availability</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    <%
                        if (vendors == null || vendors.isEmpty()) {
                    %>
                    <tr>
                        <td colspan="9" class="text-center py-4">No vendors found.</td>
                    </tr>
                    <%
                        } else {
                            for (Vendor vendor : vendors) {
                    %>
                    <tr>
                        <td><%= vendor.getId() %></td>
                        <td><%= vendor.getName() %></td>
                        <td><%= vendor.getServiceType() %></td>
                        <td><%= vendor.getPhone() %></td>
                        <td><%= vendor.getEmail() %></td>
                        <td><%= vendor.getPriceRange() %></td>
                        <td><%= vendor.getAvailability() %></td>
                        <td><span class="badge bg-success"><%= vendor.getStatus() %></span></td>
                        <td>
                            <a href="<%= request.getContextPath() %>/vendors?action=edit&id=<%= vendor.getId() %>" class="btn btn-sm btn-warning">Edit</a>
                            <a href="<%= request.getContextPath() %>/vendors?action=delete&id=<%= vendor.getId() %>" class="btn btn-sm btn-danger">Delete</a>
                        </td>
                    </tr>
                    <%
                            }
                        }
                    %>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>
</body>
</html>
