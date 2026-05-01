package com.wedding.servlet;

import com.wedding.model.Vendor;
import com.wedding.service.FileService;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/vendors")
public class VendorServlet extends HttpServlet {
    private static final String FILE_NAME = "vendors.txt";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null || action.isBlank()) {
            action = "list";
        }

        switch (action) {
            case "new":
                request.getRequestDispatcher("/vendor/add.jsp").forward(request, response);
                break;
            case "edit":
                showEditForm(request, response);
                break;
            case "delete":
                showDeletePage(request, response);
                break;
            default:
                listVendors(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null || action.isBlank()) {
            action = "create";
        }

        switch (action) {
            case "update":
                updateVendor(request, response);
                break;
            case "delete":
                deleteVendor(request, response);
                break;
            default:
                createVendor(request, response);
                break;
        }
    }

    private void listVendors(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String search = safeValue(request.getParameter("search")).toLowerCase();
        List<Vendor> allVendors = getAllVendors();
        List<Vendor> filteredVendors = new ArrayList<>();

        for (Vendor vendor : allVendors) {
            String searchable = (vendor.getId() + " " + vendor.getName() + " " + vendor.getServiceType() + " " + vendor.getStatus()).toLowerCase();
            if (search.isBlank() || searchable.contains(search)) {
                filteredVendors.add(vendor);
            }
        }

        request.setAttribute("vendors", filteredVendors);
        request.setAttribute("search", request.getParameter("search"));
        request.getRequestDispatcher("/vendor/list.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Vendor vendor = findVendorById(request.getParameter("id"));
        if (vendor == null) {
            response.sendRedirect("vendors");
            return;
        }

        request.setAttribute("vendor", vendor);
        request.getRequestDispatcher("/vendor/edit.jsp").forward(request, response);
    }

    private void showDeletePage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Vendor vendor = findVendorById(request.getParameter("id"));
        if (vendor == null) {
            response.sendRedirect("vendors");
            return;
        }

        request.setAttribute("vendor", vendor);
        request.getRequestDispatcher("/vendor/delete.jsp").forward(request, response);
    }

    private void createVendor(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Vendor vendor = buildVendorFromRequest(request);
        FileService fileService = new FileService(getServletContext());
        fileService.appendLine(FILE_NAME, vendor.toFileRecord());
        response.sendRedirect("vendors");
    }

    private void updateVendor(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Vendor updatedVendor = buildVendorFromRequest(request);
        List<Vendor> vendors = getAllVendors();
        List<String> updatedLines = new ArrayList<>();

        for (Vendor vendor : vendors) {
            if (vendor.getId().equals(updatedVendor.getId())) {
                updatedLines.add(updatedVendor.toFileRecord());
            } else {
                updatedLines.add(vendor.toFileRecord());
            }
        }

        FileService fileService = new FileService(getServletContext());
        fileService.writeAllLines(FILE_NAME, updatedLines);
        response.sendRedirect("vendors");
    }

    private void deleteVendor(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String vendorId = request.getParameter("id");
        List<Vendor> vendors = getAllVendors();
        List<String> updatedLines = new ArrayList<>();

        for (Vendor vendor : vendors) {
            if (!vendor.getId().equals(vendorId)) {
                updatedLines.add(vendor.toFileRecord());
            }
        }

        FileService fileService = new FileService(getServletContext());
        fileService.writeAllLines(FILE_NAME, updatedLines);
        response.sendRedirect("vendors");
    }

    private List<Vendor> getAllVendors() {
        FileService fileService = new FileService(getServletContext());
        List<String> lines = fileService.readAllLines(FILE_NAME);
        List<Vendor> vendors = new ArrayList<>();

        for (String line : lines) {
            if (!line.isBlank()) {
                Vendor vendor = Vendor.fromFileRecord(line);
                if (vendor != null) {
                    vendors.add(vendor);
                }
            }
        }

        return vendors;
    }

    private Vendor findVendorById(String vendorId) {
        for (Vendor vendor : getAllVendors()) {
            if (vendor.getId().equals(vendorId)) {
                return vendor;
            }
        }
        return null;
    }

    private Vendor buildVendorFromRequest(HttpServletRequest request) {
        return new Vendor(
            safeValue(request.getParameter("id")),
            safeValue(request.getParameter("name")),
            safeValue(request.getParameter("serviceType")),
            safeValue(request.getParameter("phone")),
            safeValue(request.getParameter("email")),
            safeValue(request.getParameter("priceRange")),
            safeValue(request.getParameter("availability")),
            safeValue(request.getParameter("status"))
        );
    }

    private String safeValue(String value) {
        return value == null ? "" : value.trim();
    }
}
