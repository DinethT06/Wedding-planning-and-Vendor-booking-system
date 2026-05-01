<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.wedding.model.Vendor" %>
<%
    Vendor vendor = (Vendor) request.getAttribute("vendor");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Edit Vendor</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<div class="container py-5">
    <div class="row justify-content-center">
        <div class="col-lg-8">
            <div class="card shadow-sm border-0">
                <div class="card-body p-4">
                    <h1 class="h3 mb-4">Edit Vendor</h1>
                    <form action="<%= request.getContextPath() %>/vendors" method="post" class="row g-3">
                        <input type="hidden" name="action" value="update">

                        <div class="col-md-6">
                            <label class="form-label">Vendor ID</label>
                            <input type="text" name="id" class="form-control" value="<%= vendor.getId() %>" readonly>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Vendor Name</label>
                            <input type="text" name="name" class="form-control" value="<%= vendor.getName() %>" required>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Service Type</label>
                            <input type="text" name="serviceType" class="form-control" value="<%= vendor.getServiceType() %>" required>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Phone</label>
                            <input type="text" name="phone" class="form-control" value="<%= vendor.getPhone() %>" required>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Email</label>
                            <input type="email" name="email" class="form-control" value="<%= vendor.getEmail() %>" required>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Price Range</label>
                            <input type="text" name="priceRange" class="form-control" value="<%= vendor.getPriceRange() %>" required>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Availability</label>
                            <input type="text" name="availability" class="form-control" value="<%= vendor.getAvailability() %>" required>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Status</label>
                            <select name="status" class="form-select">
                                <option value="Active" <%= "Active".equals(vendor.getStatus()) ? "selected" : "" %>>Active</option>
                                <option value="Busy" <%= "Busy".equals(vendor.getStatus()) ? "selected" : "" %>>Busy</option>
                                <option value="Inactive" <%= "Inactive".equals(vendor.getStatus()) ? "selected" : "" %>>Inactive</option>
                            </select>
                        </div>
                        <div class="col-12 d-flex gap-2">
                            <a href="<%= request.getContextPath() %>/vendors" class="btn btn-outline-secondary">Back</a>
                            <button type="submit" class="btn btn-warning">Update Vendor</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
