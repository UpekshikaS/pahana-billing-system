package com.pahanaedu.bookshop.resource;

import com.pahanaedu.bookshop.dao.NotificationDAO;
import com.pahanaedu.bookshop.model.Notification;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationResourceTest {

    private NotificationDAO notificationDAOMock;
    private NotificationResource notificationResource;

    @BeforeEach
    void setUp() throws Exception {
        notificationDAOMock = mock(NotificationDAO.class);

        // Inject mock into NotificationResource via reflection
        notificationResource = new NotificationResource() {{
            try {
                java.lang.reflect.Field field = NotificationResource.class.getDeclaredField("notificationDAO");
                field.setAccessible(true);
                field.set(this, notificationDAOMock);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }};
    }

    @Test
    void getUnreadNotifications_returnsNotifications() throws SQLException {
        List<Notification> notifications = new ArrayList<>();
        Notification n = new Notification();
        n.setNotificationId(1);
        n.setMessage("Test");
        notifications.add(n);

        when(notificationDAOMock.getUnreadNotificationsForUser(1)).thenReturn(notifications);

        Response response = notificationResource.getUnreadNotifications(1);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(notifications, response.getEntity());
        verify(notificationDAOMock, times(1)).getUnreadNotificationsForUser(1);
    }

    @Test
    void getUnreadNotifications_throwsSQLException() throws SQLException {
        when(notificationDAOMock.getUnreadNotificationsForUser(1)).thenThrow(new SQLException("DB error"));

        Response response = notificationResource.getUnreadNotifications(1);

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        verify(notificationDAOMock, times(1)).getUnreadNotificationsForUser(1);
    }

    @Test
    void getAllNotifications_returnsNotifications() throws SQLException {
        List<Notification> notifications = new ArrayList<>();
        Notification n = new Notification();
        n.setNotificationId(2);
        n.setMessage("All Test");
        notifications.add(n);

        when(notificationDAOMock.getAllNotificationsForUser(1)).thenReturn(notifications);

        Response response = notificationResource.getAllNotifications(1);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(notifications, response.getEntity());
        verify(notificationDAOMock, times(1)).getAllNotificationsForUser(1);
    }

    @Test
    void getAllNotifications_throwsSQLException() throws SQLException {
        when(notificationDAOMock.getAllNotificationsForUser(1)).thenThrow(new SQLException("DB error"));

        Response response = notificationResource.getAllNotifications(1);

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        verify(notificationDAOMock, times(1)).getAllNotificationsForUser(1);
    }

    @Test
    void markNotificationAsRead_executesSuccessfully() throws SQLException {
        doNothing().when(notificationDAOMock).markAsRead(1);

        Response response = notificationResource.markNotificationAsRead(1);

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        verify(notificationDAOMock, times(1)).markAsRead(1);
    }

    @Test
    void markNotificationAsRead_throwsSQLException() throws SQLException {
        doThrow(new SQLException("DB error")).when(notificationDAOMock).markAsRead(1);

        Response response = notificationResource.markNotificationAsRead(1);

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        verify(notificationDAOMock, times(1)).markAsRead(1);
    }

    @Test
    void markAllNotificationsAsRead_executesSuccessfully() throws SQLException {
        doNothing().when(notificationDAOMock).markAllAsRead(1);

        Response response = notificationResource.markAllNotificationsAsRead(1);

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        verify(notificationDAOMock, times(1)).markAllAsRead(1);
    }

    @Test
    void markAllNotificationsAsRead_throwsSQLException() throws SQLException {
        doThrow(new SQLException("DB error")).when(notificationDAOMock).markAllAsRead(1);

        Response response = notificationResource.markAllNotificationsAsRead(1);

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        verify(notificationDAOMock, times(1)).markAllAsRead(1);
    }

    @Test
    void deleteAllNotifications_executesSuccessfully() throws SQLException {
        doNothing().when(notificationDAOMock).deleteAllNotificationsForUser(1);

        Response response = notificationResource.deleteAllNotifications(1);

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        verify(notificationDAOMock, times(1)).deleteAllNotificationsForUser(1);
    }

    @Test
    void deleteAllNotifications_throwsSQLException() throws SQLException {
        doThrow(new SQLException("DB error")).when(notificationDAOMock).deleteAllNotificationsForUser(1);

        Response response = notificationResource.deleteAllNotifications(1);

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        verify(notificationDAOMock, times(1)).deleteAllNotificationsForUser(1);
    }

    @Test
    void deleteSingleNotification_executesSuccessfully() throws SQLException {
        doNothing().when(notificationDAOMock).deleteNotification(1);

        Response response = notificationResource.deleteSingleNotification(1);

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        verify(notificationDAOMock, times(1)).deleteNotification(1);
    }

    @Test
    void deleteSingleNotification_throwsSQLException() throws SQLException {
        doThrow(new SQLException("DB error")).when(notificationDAOMock).deleteNotification(1);

        Response response = notificationResource.deleteSingleNotification(1);

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        verify(notificationDAOMock, times(1)).deleteNotification(1);
    }

    @Test
    void getUnreadNotificationsWithoutUser_returnsNotifications() throws SQLException {
        List<Notification> notifications = new ArrayList<>();
        Notification n = new Notification();
        n.setNotificationId(3);
        n.setMessage("General unread");
        notifications.add(n);

        when(notificationDAOMock.getUnreadNotifications()).thenReturn(notifications);

        Response response = notificationResource.getUnreadNotifications();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(notifications, response.getEntity());
        verify(notificationDAOMock, times(1)).getUnreadNotifications();
    }

    @Test
    void getUnreadNotificationsWithoutUser_throwsSQLException() throws SQLException {
        when(notificationDAOMock.getUnreadNotifications()).thenThrow(new SQLException("DB error"));

        Response response = notificationResource.getUnreadNotifications();

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertTrue(((String) response.getEntity()).contains("Database error fetching notifications"));
        verify(notificationDAOMock, times(1)).getUnreadNotifications();
    }
}
