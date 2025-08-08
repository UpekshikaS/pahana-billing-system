package com.pahanaedu.bookshop.resource;

import com.pahanaedu.bookshop.dao.NotificationDAO;
import com.pahanaedu.bookshop.model.Notification;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.List;

@Path("/notifications")
public class NotificationResource {

    private final NotificationDAO notificationDAO = new NotificationDAO();

    @GET
    @Path("/unread/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUnreadNotifications(@PathParam("userId") int userId) {
        try {
            List<Notification> notifications = notificationDAO.getUnreadNotificationsForUser(userId);
            return Response.ok(notifications).build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("/all/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllNotifications(@PathParam("userId") int userId) {
        try {
            List<Notification> notifications = notificationDAO.getAllNotificationsForUser(userId);
            return Response.ok(notifications).build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PUT
    @Path("/read/{notificationId}")
    public Response markNotificationAsRead(@PathParam("notificationId") int notificationId) {
        try {
            notificationDAO.markAsRead(notificationId);
            return Response.noContent().build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PUT
    @Path("/readAll/{userId}")
    public Response markAllNotificationsAsRead(@PathParam("userId") int userId) {
        try {
            notificationDAO.markAllAsRead(userId);
            return Response.noContent().build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DELETE
    @Path("/deleteAll/{userId}")
    public Response deleteAllNotifications(@PathParam("userId") int userId) {
        try {
            notificationDAO.deleteAllNotificationsForUser(userId);
            return Response.noContent().build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DELETE
    @Path("/delete/{notificationId}")
    public Response deleteSingleNotification(@PathParam("notificationId") int notificationId) {
        try {
            notificationDAO.deleteNotification(notificationId);
            return Response.noContent().build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("/unread")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUnreadNotifications() {
        try {
            List<Notification> notifications = notificationDAO.getUnreadNotifications();
            return Response.ok(notifications).build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Database error fetching notifications.\"}")
                    .build();
        }
    }

}
