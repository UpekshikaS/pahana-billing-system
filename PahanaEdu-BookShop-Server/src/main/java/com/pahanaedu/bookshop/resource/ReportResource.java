package com.pahanaedu.bookshop.resource;

import com.pahanaedu.bookshop.dao.ReportDAO;
import com.pahanaedu.bookshop.model.report.SalesSummaryDTO;
import com.pahanaedu.bookshop.model.report.TopCustomerDTO;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import com.pahanaedu.bookshop.util.ExcelExporter;
import jakarta.ws.rs.core.StreamingOutput;
import java.io.ByteArrayOutputStream;

@Path("/reports")
public class ReportResource {

    private final ReportDAO reportDAO = new ReportDAO();

    @GET
    @Path("/sales-summary")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSalesSummary(@QueryParam("startDate") String startDateStr,
            @QueryParam("endDate") String endDateStr) {
        try {
            Date startDate = Date.valueOf(startDateStr);
            Date endDate = Date.valueOf(endDateStr);
            SalesSummaryDTO summary = reportDAO.getSalesSummary(startDate, endDate);
            return Response.ok(summary).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Invalid date format. Please use YYYY-MM-DD.\"}")
                    .build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Database error fetching sales summary.\"}")
                    .build();
        }
    }

    @GET
    @Path("/top-customers")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTopCustomers(@QueryParam("startDate") String startDateStr,
            @QueryParam("endDate") String endDateStr) {
        try {
            Date startDate = Date.valueOf(startDateStr);
            Date endDate = Date.valueOf(endDateStr);
            List<TopCustomerDTO> topCustomers = reportDAO.getTopCustomers(startDate, endDate);
            return Response.ok(topCustomers).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Invalid date format. Please use YYYY-MM-DD.\"}")
                    .build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Database error fetching top customers.\"}")
                    .build();
        }
    }

    @GET
    @Path("/stock")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStockReport() {
        try {
            return Response.ok(reportDAO.getStockSummary()).build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Database error fetching stock summary.\"}")
                    .build();
        }
    }

    @GET
    @Path("/export-sales")
    @Produces("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public Response exportSalesReport(@QueryParam("startDate") String startDateStr,
            @QueryParam("endDate") String endDateStr) {
        try {
            Date startDate = Date.valueOf(startDateStr);
            Date endDate = Date.valueOf(endDateStr);

            SalesSummaryDTO summary = reportDAO.getSalesSummary(startDate, endDate);

            ByteArrayOutputStream outputStream = ExcelExporter.generateSalesReport(summary);

            StreamingOutput stream = outputStream::writeTo;

            return Response.ok(stream)
                    .header("Content-Disposition", "attachment; filename=\"sales-report.xlsx\"")
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to generate sales report.")
                    .build();
        }
    }

}
