import io.qameta.allure.Step;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;

public class TestUtils {

    /**
     * Create a user map for test data
     * @param name User's name
     * @param job User's job
     * @return Map containing user data
     */
    @Step("Creating user data with name: {name}, job: {job}")
    public static Map<String, String> createUserMap(String name, String job) {
        Map<String, String> user = new HashMap<>();
        user.put("name", name);
        user.put("job", job);
        return user;
    }

    /**
     * Create registration data
     * @param email User's email
     * @param password User's password
     * @return Map containing registration data
     */
    @Step("Creating registration data with email: {email}")
    public static Map<String, String> createRegistrationMap(String email, String password) {
        Map<String, String> registration = new HashMap<>();
        registration.put("email", email);
        registration.put("password", password);
        return registration;
    }

    /**
     * Log the response details for debugging
     * @param response REST Assured Response object
     */
    @Step("Logging response details")
    public static void logResponseDetails(Response response) {
        System.out.println("Status Code: " + response.getStatusCode());
        System.out.println("Response Body: " + response.getBody().asString());
        System.out.println("Response Time: " + response.getTime() + "ms");
        System.out.println("Content Type: " + response.getContentType());
    }
}