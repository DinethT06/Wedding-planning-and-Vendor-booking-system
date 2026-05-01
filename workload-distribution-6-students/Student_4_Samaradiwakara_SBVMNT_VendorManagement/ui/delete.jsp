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
    <title>Delete Vendor</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<div class="container py-5">
    <div class="row justify-content-center">
        <div class="col-lg-6">
            <div class="card shadow-sm border-0">
                <div class="card-body p-4">
                    <h1 class="h3 text-danger mb-3">Delete Vendor</h1>
                    <p>Are you sure you want to remove this vendor record?</p>
                    <ul class="list-group mb-4">
                        <li class="list-group-item"><strong>ID:</strong> <%= vendor.getId() %></li>
                        <li class="list-group-item"><strong>Name:</strong> <%= vendor.getName() %></li>
                        <li class="list-group-item"><strong>Service Type:</strong> <%= vendor.getServiceType() %></li>
                        <li class="list-group-item"><strong>Status:</strong> <%= vendor.getStatus() %></li>
                    </ul>
                    <form action="<%= request.getContextPath() %>/vendors" method="post" class="d-flex gap-2">
                        <input type="hidden" name="action" value="delete">
                        <input type="hidden" name="id" value="<%= vendor.getId() %>">
                        <a href="<%= request.getContextPath() %>/vendors" class="btn btn-outline-secondary">Cancel</a>
                        <button type="submit" class="btn btn-danger">Delete Vendor</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
