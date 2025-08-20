package com.pahanaedu.bookshop.resource;

import com.pahanaedu.bookshop.dao.CustomerDAO;
import com.pahanaedu.bookshop.dao.NotificationDAO;
import com.pahanaedu.bookshop.model.Customer;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.SQLException;
import java.util.List;

@Path("/customers")
public class CustomerResource {

    private CustomerDAO customerDAO;
    private NotificationDAO notificationDAO;

    // Default constructor
    public CustomerResource() {
        this.customerDAO = new CustomerDAO();
        this.notificationDAO = new NotificationDAO();
    }

    // Constructor for tests (inject mocks)
    public CustomerResource(CustomerDAO customerDAO, NotificationDAO notificationDAO) {
        this.customerDAO = customerDAO;
        this.notificationDAO = notificationDAO;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCustomers() {
        try {
            List<Customer> customers = customerDAO.getAllCustomers();
            return Response.ok(customers).build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Database error: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomerById(@PathParam("id") int id) {
        try {
            Customer customer = customerDAO.getCustomerById(id);
            if (customer != null) {
                return Response.ok(customer).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("Customer not found.").build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Database error: " + e.getMessage())
                    .build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addCustomer(Customer customer) {
        try {
            customerDAO.addCustomer(customer);

            String message = "A new customer '" + customer.getName() + "' has been added.";
            notificationDAO.createNotification(message, null);

            return Response.status(Response.Status.CREATED).entity(customer).build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Database error: " + e.getMessage())
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCustomer(@PathParam("id") int id, Customer updatedCustomer) {
        try {
            Customer existingCustomer = customerDAO.getCustomerById(id);
            if (existingCustomer != null) {
                updatedCustomer.setCustomerId(id);
                customerDAO.updateCustomer(updatedCustomer);

                String message = "Customer '" + existingCustomer.getName() + "' has been updated.";
                notificationDAO.createNotification(message, null);

                return Response.ok(updatedCustomer).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("Customer not found.").build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Database error: " + e.getMessage())
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteCustomer(@PathParam("id") int id) {
        try {
            Customer customerToDelete = customerDAO.getCustomerById(id);
            if (customerToDelete != null) {
                customerDAO.deleteCustomer(id);

                String message = "Customer '" + customerToDelete.getName() + "' has been deleted.";
                notificationDAO.createNotification(message, null);

                return Response.noContent().build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("Customer not found.").build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Database error: " + e.getMessage())
                    .build();
        }
    }
}
