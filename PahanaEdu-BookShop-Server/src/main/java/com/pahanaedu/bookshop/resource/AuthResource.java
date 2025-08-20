package com.pahanaedu.bookshop.resource;

import com.pahanaedu.bookshop.dao.IUserDAO;
import com.pahanaedu.bookshop.dao.UserDAO;
import com.pahanaedu.bookshop.model.LoginRequest;
import com.pahanaedu.bookshop.model.User;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.sql.SQLException;

@Path("/auth")
public class AuthResource {

    private final IUserDAO userDAO;

    // Production constructor
    public AuthResource() {
        this.userDAO = new UserDAO();
    }

    // Test constructor
    public AuthResource(IUserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(LoginRequest loginRequest) {
        if (loginRequest == null || loginRequest.getUsername() == null || loginRequest.getPassword() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Username and password are required\"}")
                    .build();
        }

        try {
            User user = userDAO.getUserByUsername(loginRequest.getUsername());

            if (user != null && userDAO.checkPassword(loginRequest.getPassword(), user.getPasswordHash())) {
                user.setPasswordHash(null);
                return Response.ok(user).build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\":\"Invalid username or password\"}")
                        .build();
            }
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Database error occurred.\"}")
                    .build();
        }
    }
}
