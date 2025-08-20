package com.pahanaedu.bookshop.dao;

import com.pahanaedu.bookshop.model.User;
import com.pahanaedu.bookshop.model.ProfileUpdateRequest;
import java.sql.SQLException;
import java.util.List;

public interface IUserDAO {
    User getUserByUsername(String username) throws SQLException;
    boolean checkPassword(String plainPassword, String hashedPassword);
    void addUser(User user) throws SQLException;
    List<User> getAllUsers() throws SQLException;
    void updateUser(User user) throws SQLException;
    void deleteUser(int userId) throws SQLException;
    void updateUserProfile(int userId, ProfileUpdateRequest request) throws SQLException;
    void changeUserPassword(int userId, String newPassword) throws SQLException;
    User getUserById(int userId) throws SQLException;
}

