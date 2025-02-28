import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // Ensures tests run in order
public class ReqResApiTests {

    private static final String BASE_URI = "https://reqres.in/api";

    @BeforeEach
    void setup() {
        RestAssured.baseURI = BASE_URI;
    }

    // ==================== GET Tests ====================
    @Test
    @Order(1)
    void testGetSingleUser() {
        Response response = given()
                .when().get("/users/2")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("data.id", equalTo(2))
                .body("data.first_name", equalTo("Janet"))
                .body("data.last_name", equalTo("Weaver"))
                .extract().response();

        // Additional assertions
        assert response.path("data.avatar").toString().contains("https://");
        System.out.println("Response Time: " + response.getTime() + "ms");
    }

    @Test
    @Order(2)
    void testGetListUsers() {
        given()
                .queryParam("page", 2)
                .when().get("/users")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("page", equalTo(2))
                .body("data", hasSize(greaterThan(0)))
                .body("total_pages", greaterThan(0));
    }

    @Test
    @Order(3)
    void testGetNonExistentUser() {
        given()
                .when().get("/users/23")
                .then()
                .statusCode(404);
    }

    // ==================== POST Tests ====================
    @Test
    @Order(4)
    void testCreateUser() {
        Map<String, String> user = Map.of("name", "John Doe", "job", "Software Tester");

        Response response = given()
                .contentType(ContentType.JSON)
                .body(user)
                .when().post("/users")
                .then()
                .statusCode(201)
                .body("name", equalTo("John Doe"))
                .body("job", equalTo("Software Tester"))
                .body("id", notNullValue())
                .body("createdAt", notNullValue())
                .extract().response();

        System.out.println("Created User ID: " + response.jsonPath().getString("id"));
    }

    @Test
    @Order(5)
    void testRegisterUser() {
        Map<String, String> user = Map.of("email", "eve.holt@reqres.in", "password", "pistol");

        given()
                .contentType(ContentType.JSON)
                .body(user)
                .when().post("/register")
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("token", notNullValue());
    }

    @Test
    @Order(6)
    void testUnsuccessfulRegister() {
        Map<String, String> user = Map.of("email", "sydney@fife");

        given()
                .contentType(ContentType.JSON)
                .body(user)
                .when().post("/register")
                .then()
                .statusCode(400)
                .body("error", equalTo("Missing password"));
    }

    // ==================== PUT Tests ====================
    @Test
    @Order(7)
    void testUpdateUser() {
        Map<String, String> user = Map.of("name", "John Updated", "job", "Senior Tester");

        given()
                .contentType(ContentType.JSON)
                .body(user)
                .when().put("/users/2")
                .then()
                .statusCode(200)
                .body("name", equalTo("John Updated"))
                .body("job", equalTo("Senior Tester"))
                .body("updatedAt", notNullValue());
    }

    @Test
    @Order(8)
    void testPartialUpdateUser() {
        Map<String, String> user = Map.of("name", "John Patched");

        given()
                .contentType(ContentType.JSON)
                .body(user)
                .when().patch("/users/2")
                .then()
                .statusCode(200)
                .body("name", equalTo("John Patched"))
                .body("updatedAt", notNullValue());
    }

    // ==================== DELETE Tests ====================
    @Test
    @Order(9)
    void testDeleteUser() {
        given()
                .when().delete("/users/2")
                .then()
                .statusCode(204);
    }

    // ==================== Additional Tests ====================
    @Test
    @Order(10)
    void testResponseTime() {
        given()
                .when().get("/users?page=2")
                .then()
                .time(lessThan(2000L)); // Response time should be < 2s
    }

    @Test
    @Order(11)
    void testResponseHeaders() {
        given()
                .when().get("/users/2")
                .then()
                .header("Content-Type", containsString("application/json"))
                .header("Transfer-Encoding", equalTo("chunked"))
                .header("Connection", equalTo("keep-alive"));
    }
}
