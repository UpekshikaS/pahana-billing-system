package com.pahanaedu.bookshop.resource;

import com.pahanaedu.bookshop.dao.NotificationDAO;
import com.pahanaedu.bookshop.dao.ReturnDAO;
import com.pahanaedu.bookshop.model.Return;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.sql.SQLException;

@Path("/returns")
public class ReturnResource {

    private final ReturnDAO returnDAO = new ReturnDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createReturn(Return aReturn) {
        try {
            // Process the return and get the customer's notification message
            String customerNotificationMessage = returnDAO.processReturn(aReturn);

            notificationDAO.createNotification(customerNotificationMessage, aReturn.getProcessedByUserId());

            return Response.status(Response.Status.CREATED)
                    .entity(aReturn)
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Failed to process return. Details: " + e.getMessage() + "\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Unexpected error occurred.\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }
}
