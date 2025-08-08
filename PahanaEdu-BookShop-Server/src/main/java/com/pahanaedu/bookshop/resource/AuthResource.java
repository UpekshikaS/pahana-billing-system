package com.pahanaedu.bookshop.resource;

import com.pahanaedu.bookshop.dao.UserDAO;
import com.pahanaedu.bookshop.model.LoginRequest;
import com.pahanaedu.bookshop.model.User;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.sql.SQLException;

@Path("/auth")
public class AuthResource {

    private final UserDAO userDAO = new UserDAO();

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON) // This endpoint accepts JSON data
    @Produces(MediaType.APPLICATION_JSON) // This endpoint returns JSON data
    public Response login(LoginRequest loginRequest) {
        try {
            User user = userDAO.getUserByUsername(loginRequest.getUsername());

            System.out.println("Response login obj: " + user);

            if (user != null && userDAO.checkPassword(loginRequest.getPassword(), user.getPasswordHash())) {

                System.out.println("Response login user not null: " + user);

                user.setPasswordHash(null);

                return Response.ok(user).build();
            } else {

                System.out.println("Response login UNAUTHORIZED: " + user);
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\":\"Invalid username or password\"}")
                        .build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Database error occurred.\"}")
                    .build();
        }
    }
}
