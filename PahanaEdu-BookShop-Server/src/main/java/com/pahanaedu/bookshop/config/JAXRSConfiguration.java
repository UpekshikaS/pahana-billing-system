package com.pahanaedu.bookshop.config;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * Configures JAX-RS for the application.
 * The @ApplicationPath annotation defines the base URI for all RESTful web services.
 * All API endpoints will be accessible under "/api/*".
 * For example: /PahanaEdu-BookShop-Server/api/auth/login
 */
@ApplicationPath("api")
public class JAXRSConfiguration extends Application {

}
