package com.pahanaedu.bookshop.resource;

import com.pahanaedu.bookshop.dao.ProductDAO;
import com.pahanaedu.bookshop.model.Product;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.SQLException;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

@Path("/products")
public class ProductResource {

    private final ProductDAO productDAO = new ProductDAO();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllProducts() {
        try {
            List<Product> products = productDAO.getAllProducts();
            return Response.ok(products).build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Database error fetching products.\"}")
                    .build();
        }
    }

    @GET
    @Path("/{itemId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProductByItemId(@PathParam("itemId") String itemId) {
        try {
            Product product = productDAO.getProductByItemId(itemId);
            if (product != null) {
                return Response.ok(product).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Product not found.\"}")
                        .build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Database error fetching product.\"}")
                    .build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addProduct(Product product) {
        try {
            productDAO.addProduct(product);
            return Response.status(Response.Status.CREATED).entity(product).build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Failed to add product.\"}")
                    .build();
        }
    }

    @PUT
    @Path("/{productId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateProduct(@PathParam("productId") int productId, Product product) {
        try {
            product.setProductId(productId);
            productDAO.updateProduct(product);
            return Response.ok(product).build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Failed to update product.\"}")
                    .build();
        }
    }

    @DELETE
    @Path("/{productId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteProduct(@PathParam("productId") int productId) {
        try {
            productDAO.deleteProduct(productId);
            return Response.noContent().build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Failed to delete product.\"}")
                    .build();
        }
    }

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadProducts(
            @FormDataParam("file") InputStream fileInputStream,
            @FormDataParam("file") FormDataContentDisposition fileMetaData) {

        List<Product> productsToUpload = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(fileInputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            if (rows.hasNext()) {
                rows.next(); // skip header
            }
            while (rows.hasNext()) {
                Row currentRow = rows.next();
                Product product = new Product();

                product.setItemId(currentRow.getCell(0).getStringCellValue());
                product.setName(currentRow.getCell(1).getStringCellValue());
                product.setDescription(currentRow.getCell(2).getStringCellValue());
                product.setPrice(new BigDecimal(currentRow.getCell(3).getNumericCellValue()));
                product.setStockQuantity((int) currentRow.getCell(4).getNumericCellValue());

                productsToUpload.add(product);
            }

            productDAO.bulkUpsertProducts(productsToUpload);

            return Response.ok("{\"message\":\"Successfully uploaded and processed " + productsToUpload.size() + " products.\"}").build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Failed to process file. Please check the file format and data. Details: " + e.getMessage() + "\"}")
                    .build();
        }
    }
}
