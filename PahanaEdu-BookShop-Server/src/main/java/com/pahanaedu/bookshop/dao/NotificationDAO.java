package com.pahanaedu.bookshop.dao;

import com.pahanaedu.bookshop.model.Notification;
import com.pahanaedu.bookshop.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    public List<Notification> getUnreadNotificationsForUser(int userId) throws SQLException {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE (user_id = ? OR user_id IS NULL) AND is_read = false ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getInstance().getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    notifications.add(mapResultSetToNotification(rs));
                }
            }
        }
        return notifications;
    }

    public void markAsRead(int notificationId) throws SQLException {
        String sql = "UPDATE notifications SET is_read = true WHERE notification_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, notificationId);
            pstmt.executeUpdate();
        }
    }

    private Notification mapResultSetToNotification(ResultSet rs) throws SQLException {
        Notification notification = new Notification();
        notification.setNotificationId(rs.getInt("notification_id"));
        if (rs.getObject("user_id") != null) {
            notification.setUserId(rs.getInt("user_id"));
        }
        notification.setMessage(rs.getString("message"));
        notification.setRead(rs.getBoolean("is_read"));
        notification.setCreatedAt(rs.getTimestamp("created_at"));
        return notification;
    }

    public List<Notification> getAllNotificationsForUser(int userId) throws SQLException {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE user_id = ? OR user_id IS NULL ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getInstance().getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    notifications.add(mapResultSetToNotification(rs));
                }
            }
        }
        return notifications;

    }

    public void markAllAsRead(int userId) throws SQLException {
        String sql = "UPDATE notifications SET is_read = true WHERE user_id = ? OR user_id IS NULL";
        try (Connection conn = DBConnection.getInstance().getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        }
    }

    public void deleteAllNotificationsForUser(int userId) throws SQLException {
        String sql = "DELETE FROM notifications WHERE user_id = ? OR user_id IS NULL";
        try (Connection conn = DBConnection.getInstance().getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        }
    }

    public void deleteNotification(int notificationId) throws SQLException {
        String sql = "DELETE FROM notifications WHERE notification_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, notificationId);
            pstmt.executeUpdate();
        }
    }

    public void createNotification(String message, Integer userId) throws SQLException {
        String sql = "INSERT INTO notifications (message, user_id, is_read, created_at) VALUES (?, ?, false, NOW())";
        try (Connection conn = DBConnection.getInstance().getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, message);
            if (userId != null) {
                pstmt.setInt(2, userId);
            } else {
                pstmt.setNull(2, java.sql.Types.INTEGER);
            }
            pstmt.executeUpdate();
        }
    }

    public List<Notification> getUnreadNotifications() throws SQLException {
        List<Notification> unreadNotifications = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE is_read = false ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getInstance().getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Notification notification = new Notification();
                notification.setNotificationId(rs.getInt("notification_id"));
                notification.setMessage(rs.getString("message"));
                notification.setRead(rs.getBoolean("is_read"));
                notification.setCreatedAt(rs.getTimestamp("created_at"));
                unreadNotifications.add(notification);
            }
        }
        return unreadNotifications;
    }

}
