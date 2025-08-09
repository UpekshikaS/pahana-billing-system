package com.pahanaedu.bookshop.resource;

import com.pahanaedu.bookshop.dao.DiscountDAO;
import com.pahanaedu.bookshop.model.Discount;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.SQLException;
import java.util.List;

@Path("/discounts")
public class DiscountResource {

    private final DiscountDAO discountDAO = new DiscountDAO();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllDiscounts() {
        try {
            List<Discount> discounts = discountDAO.getAllDiscounts();
            return Response.ok(discounts).build();
        } catch (SQLException e) {
            return Response.serverError().entity("{\"error\":\"Error loading discounts.\"}").build();
        }
    }

    @GET
    @Path("/{code}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDiscountByCode(@PathParam("code") String code) {
        try {
            Discount discount = discountDAO.getByCode(code);
            if (discount == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Discount code not found or inactive.\"}")
                        .build();
            }
            return Response.ok(discount).build();
        } catch (SQLException e) {
            return Response.serverError().entity("{\"error\":\"Error retrieving discount.\"}").build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createDiscount(Discount discount) {
        try {
            discountDAO.insertDiscount(discount);
            return Response.status(Response.Status.CREATED).build();
        } catch (SQLException e) {
            return Response.serverError().entity("{\"error\":\"Error creating discount.\"}").build();
        }
    }
}
