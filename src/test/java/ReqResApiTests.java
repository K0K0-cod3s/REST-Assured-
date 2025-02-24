import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ReqResApiTests {

    @Before
    public void setup() {
        // Set base URI
        RestAssured.baseURI = "https://reqres.in/api";
    }

    // ==================== GET Tests ====================
    @Test
    public void test01_GetSingleUser() {
        Response response = RestAssured.given()
                .when()
                .get("/users/2")
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .contentType(ContentType.JSON)
                .and()
                .body("data.id", Matchers.equalTo(2))
                .body("data.email", Matchers.notNullValue())
                .body("data.first_name", Matchers.equalTo("Janet"))
                .body("data.last_name", Matchers.equalTo("Weaver"))
                .extract().response();

        // Additional assertions
        Assert.assertEquals("2", response.path("data.id").toString());
        Assert.assertTrue(response.path("data.avatar").toString().contains("https://"));

        // Log response time
        System.out.println("Response Time: " + response.getTime() + "ms");
    }

    @Test
    public void test02_GetListUsers() {
        RestAssured.given()
                .queryParam("page", 2)
                .when()
                .get("/users")
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .contentType(ContentType.JSON)
                .and()
                .body("page", Matchers.equalTo(2))
                .body("data", Matchers.hasSize(Matchers.greaterThan(0)))
                .body("data[0].id", Matchers.notNullValue())
                .body("total_pages", Matchers.greaterThan(0));
    }

    @Test
    public void test03_GetNonExistentUser() {
        RestAssured.given()
                .when()
                .get("/users/23")
                .then()
                .assertThat()
                .statusCode(404);
    }

    // ==================== POST Tests ====================
    @Test
    public void test04_CreateUser() {
        Map<String, String> user = new HashMap<>();
        user.put("name", "John Doe");
        user.put("job", "Software Tester");

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post("/users")
                .then()
                .assertThat()
                .statusCode(201)
                .and()
                .body("name", Matchers.equalTo("John Doe"))
                .body("job", Matchers.equalTo("Software Tester"))
                .body("id", Matchers.notNullValue())
                .body("createdAt", Matchers.notNullValue())
                .extract().response();

        // Store the ID for potential future use
        String userId = response.jsonPath().getString("id");
        System.out.println("Created User ID: " + userId);
    }

    @Test
    public void test05_RegisterUser() {
        Map<String, String> user = new HashMap<>();
        user.put("email", "eve.holt@reqres.in");
        user.put("password", "pistol");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post("/register")
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("id", Matchers.notNullValue())
                .body("token", Matchers.notNullValue());
    }

    @Test
    public void test06_UnsuccessfulRegister() {
        Map<String, String> user = new HashMap<>();
        user.put("email", "sydney@fife");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post("/register")
                .then()
                .assertThat()
                .statusCode(400)
                .and()
                .body("error", Matchers.equalTo("Missing password"));
    }

    // ==================== PUT Tests ====================
    @Test
    public void test07_UpdateUser() {
        Map<String, String> user = new HashMap<>();
        user.put("name", "John Updated");
        user.put("job", "Senior Tester");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .put("/users/2")
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("name", Matchers.equalTo("John Updated"))
                .body("job", Matchers.equalTo("Senior Tester"))
                .body("updatedAt", Matchers.notNullValue());
    }

    @Test
    public void test08_PartialUpdateUser() {
        Map<String, String> user = new HashMap<>();
        user.put("name", "John Patched");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .patch("/users/2")
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("name", Matchers.equalTo("John Patched"))
                .body("updatedAt", Matchers.notNullValue());
    }

    // ==================== DELETE Tests ====================
    @Test
    public void test09_DeleteUser() {
        RestAssured.given()
                .when()
                .delete("/users/2")
                .then()
                .assertThat()
                .statusCode(204);
    }

    // ==================== Additional Tests ====================

    @Test
    public void test10_ResponseTime() {
        RestAssured.given()
                .when()
                .get("/users?page=2")
                .then()
                .assertThat()
                .time(Matchers.lessThan(2000L)); // Response time should be less than 2 seconds
    }

    @Test
    public void test11_ResponseHeaders() {
        RestAssured.given()
                .when()
                .get("/users/2")
                .then()
                .assertThat()
                .header("Content-Type", Matchers.containsString("application/json"))
                .header("Transfer-Encoding", Matchers.equalTo("chunked"))
                .header("Connection", Matchers.equalTo("keep-alive"));
    }
}