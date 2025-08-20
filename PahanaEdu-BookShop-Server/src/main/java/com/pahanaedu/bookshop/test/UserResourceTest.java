//package com.pahanaedu.bookshop.test;
//
//import com.pahanaedu.bookshop.model.ProfileUpdateRequest;
//import com.pahanaedu.bookshop.model.User;
//import jakarta.ws.rs.client.Client;
//import jakarta.ws.rs.client.ClientBuilder;
//import jakarta.ws.rs.client.Entity;
//import jakarta.ws.rs.core.MediaType;
//import jakarta.ws.rs.core.Response;
//
//public class UserResourceTest {
//
//    private static final String BASE_URL = "http://localhost:8080/PahanaEdu-BookShop-Server/api/users";
//
//    public static void main(String[] args) {
//        Client client = ClientBuilder.newClient();
//
//        System.out.println("=== SUCCESS TESTS ===");
//        testAddUser(client);
//        testGetAllUsers(client);
//        testGetUserById(client, 1);
//        testUpdateUser(client, 1);
//        testUpdateUserProfile(client, 1);
//        testDeleteUser(client, 1);
//
//        System.out.println("\n=== FAILURE TESTS ===");
//        testGetNonExistingUser(client, 9999);
//        testAddDuplicateUser(client);
//        testUpdateNonExistingUser(client, 9999);
//        testDeleteNonExistingUser(client, 9999);
//
//        client.close();
//    }
//
//    // ================== SUCCESS SCENARIOS ==================
//    private static void testAddUser(Client client) {
//        User user = new User();
//        user.setUsername("testuser");
//        user.setPasswordHash("password123");
//        user.setRole("USER");
//        user.setName("Test User");
//        user.setEmail("test@example.com");
//        user.setPhone("1234567890");
//        user.setAddress("Test Address");
//
//        Response response = client.target(BASE_URL)
//                .request(MediaType.APPLICATION_JSON)
//                .post(Entity.entity(user, MediaType.APPLICATION_JSON));
//
//        System.out.println("ADD USER: " + response.getStatus());
//        System.out.println(response.readEntity(String.class));
//    }
//
//    private static void testGetAllUsers(Client client) {
//        Response response = client.target(BASE_URL)
//                .request(MediaType.APPLICATION_JSON)
//                .get();
//
//        System.out.println("GET ALL USERS: " + response.getStatus());
//        System.out.println(response.readEntity(String.class));
//    }
//
//    private static void testGetUserById(Client client, int userId) {
//        Response response = client.target(BASE_URL + "/" + userId)
//                .request(MediaType.APPLICATION_JSON)
//                .get();
//
//        System.out.println("GET USER BY ID: " + response.getStatus());
//        System.out.println(response.readEntity(String.class));
//    }
//
//    private static void testUpdateUser(Client client, int userId) {
//        User user = new User();
//        user.setRole("ADMIN");
//        user.setName("Updated Name");
//        user.setEmail("updated@example.com");
//        user.setPhone("9876543210");
//        user.setAddress("Updated Address");
//
//        Response response = client.target(BASE_URL + "/" + userId)
//                .request(MediaType.APPLICATION_JSON)
//                .put(Entity.entity(user, MediaType.APPLICATION_JSON));
//
//        System.out.println("UPDATE USER: " + response.getStatus());
//        System.out.println(response.readEntity(String.class));
//    }
//
//    private static void testUpdateUserProfile(Client client, int userId) {
//        ProfileUpdateRequest request = new ProfileUpdateRequest();
//        request.setName("Profile Name");
//        request.setEmail("profile@example.com");
//        request.setPhone("111222333");
//        request.setAddress("Profile Address");
//        request.setNewPassword("newpassword123");
//
//        Response response = client.target(BASE_URL + "/profile/" + userId)
//                .request(MediaType.APPLICATION_JSON)
//                .put(Entity.entity(request, MediaType.APPLICATION_JSON));
//
//        System.out.println("UPDATE PROFILE: " + response.getStatus());
//        System.out.println(response.readEntity(String.class));
//    }
//
//    private static void testDeleteUser(Client client, int userId) {
//        Response response = client.target(BASE_URL + "/" + userId)
//                .request(MediaType.APPLICATION_JSON)
//                .delete();
//
//        System.out.println("DELETE USER: " + response.getStatus());
//    }
//
//    // ================== FAILURE SCENARIOS ==================
//    private static void testGetNonExistingUser(Client client, int userId) {
//        Response response = client.target(BASE_URL + "/" + userId)
//                .request(MediaType.APPLICATION_JSON)
//                .get();
//
//        System.out.println("GET NON-EXISTING USER: " + response.getStatus());
//        System.out.println(response.readEntity(String.class));
//    }
//
//    private static void testAddDuplicateUser(Client client) {
//        User user = new User();
//        user.setUsername("testuser"); // duplicate username
//        user.setPasswordHash("password123");
//        user.setRole("USER");
//
//        Response response = client.target(BASE_URL)
//                .request(MediaType.APPLICATION_JSON)
//                .post(Entity.entity(user, MediaType.APPLICATION_JSON));
//
//        System.out.println("ADD DUPLICATE USER: " + response.getStatus());
//        System.out.println(response.readEntity(String.class));
//    }
//
//    private static void testUpdateNonExistingUser(Client client, int userId) {
//        User user = new User();
//        user.setRole("ADMIN");
//
//        Response response = client.target(BASE_URL + "/" + userId)
//                .request(MediaType.APPLICATION_JSON)
//                .put(Entity.entity(user, MediaType.APPLICATION_JSON));
//
//        System.out.println("UPDATE NON-EXISTING USER: " + response.getStatus());
//        System.out.println(response.readEntity(String.class));
//    }
//
//    private static void testDeleteNonExistingUser(Client client, int userId) {
//        Response response = client.target(BASE_URL + "/" + userId)
//                .request(MediaType.APPLICATION_JSON)
//                .delete();
//
//        System.out.println("DELETE NON-EXISTING USER: " + response.getStatus());
//    }
//}
