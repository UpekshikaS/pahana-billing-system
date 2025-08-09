package com.pahanaedu.bookshop.resource;

import com.pahanaedu.bookshop.dao.UserDAO;
import com.pahanaedu.bookshop.model.User;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.SQLException;
import java.util.List;
import com.pahanaedu.bookshop.model.ProfileUpdateRequest;

@Path("/users")
public class UserResource {

    private final UserDAO userDAO = new UserDAO();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers() {
        try {
            List<User> users = userDAO.getAllUsers();
            return Response.ok(users).build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Database error fetching users.\"}")
                    .build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addUser(User user) {
        try {
            // Basic validation
            if (user.getPassword() == null || user.getPassword().toString().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"Password is required for a new user.\"}")
                        .build();
            }
            userDAO.addUser(user);
            return Response.status(Response.Status.CREATED).entity("{\"message\":\"User created successfully.\"}").build();
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { // MySQL duplicate entry error
                return Response.status(Response.Status.CONFLICT)
                        .entity("{\"error\":\"A user with this username already exists.\"}")
                        .build();
            }
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Failed to add user.\"}")
                    .build();
        }
    }

    @PUT
    @Path("/{userId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(@PathParam("userId") int userId, User user) {
        try {
            user.setUserId(userId);
            userDAO.updateUser(user);
            return Response.ok(user).build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Failed to update user.\"}")
                    .build();
        }
    }

    @DELETE
    @Path("/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteUser(@PathParam("userId") int userId) {
        try {
            userDAO.deleteUser(userId);
            return Response.noContent().build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Failed to delete user.\"}")
                    .build();
        }
    }

    @PUT
    @Path("/profile/{userId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUserProfile(@PathParam("userId") int userId, ProfileUpdateRequest request) {
        try {
            userDAO.updateUserProfile(userId, request);

            if (request.getNewPassword() != null && !request.getNewPassword().isEmpty()) {
                userDAO.changeUserPassword(userId, request.getNewPassword());
            }

            return Response.ok("{\"message\":\"Profile updated successfully.\"}").build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Failed to update profile.\"}")
                    .build();
        }
    }

    @GET
    @Path("/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserById(@PathParam("userId") int userId) {
        try {
            User user = userDAO.getUserById(userId);
            if (user == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"User not found.\"}")
                        .build();
            }
            return Response.ok(user).build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Failed to fetch user.\"}")
                    .build();
        }
    }

}
