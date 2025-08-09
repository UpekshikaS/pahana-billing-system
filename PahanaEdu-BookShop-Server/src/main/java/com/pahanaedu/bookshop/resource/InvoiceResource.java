package com.pahanaedu.bookshop.resource;

import com.pahanaedu.bookshop.dao.InvoiceDAO;
import com.pahanaedu.bookshop.dto.InvoiceItemDetailsDTO;
import com.pahanaedu.bookshop.model.Invoice;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

import java.sql.SQLException;

@Path("/invoices")
public class InvoiceResource {

    private final InvoiceDAO invoiceDAO = new InvoiceDAO();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createInvoice(Invoice invoice) {
        try {
            String invoiceNumber = "INV-" + java.time.LocalDate.now().toString().replace("-", "") + "-" + System.currentTimeMillis() / 1000;
            invoice.setInvoiceNumber(invoiceNumber);

            int newInvoiceId = invoiceDAO.createInvoice(invoice);
            invoice.setInvoiceId(newInvoiceId);

            return Response.status(Response.Status.CREATED).entity(invoice).build();
        } catch (SQLException e) {
            e.printStackTrace();
            if (e.getMessage().startsWith("Insufficient stock")) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("{\"error\":\"" + e.getMessage() + "\"}")
                        .build();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Failed to create invoice due to a database error.\"}")
                    .build();
        }
    }

    @GET
    @Path("/search/{invoiceNumber}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchInvoiceItems(@PathParam("invoiceNumber") String invoiceNumber) {
        try {
            List<InvoiceItemDetailsDTO> items = invoiceDAO.findInvoiceItemsByInvoiceNumber(invoiceNumber);
            if (items.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"No items found for this invoice number.\"}")
                        .build();
            }
            return Response.ok(items).build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Database error during invoice search.\"}")
                    .build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllInvoices() {
        System.out.println("Get ALL Invoices ReQEST");
        try {
            List<Invoice> invoices = invoiceDAO.getAllInvoices();
            return Response.ok(invoices).build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Failed to retrieve invoices.\"}")
                    .build();
        }
    }

}
