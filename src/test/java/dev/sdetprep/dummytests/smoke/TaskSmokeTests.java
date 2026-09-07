package dev.sdetprep.dummytests.smoke;

import dev.sdetprep.dummytests.BaseApiTest;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Atomic checks: one endpoint, one behavior, no dependency between tests.
 * Meant to run first and fast — if these fail, don't bother with integration/e2e.
 */
public class TaskSmokeTests extends BaseApiTest {

    @Test
    public void healthEndpointIsUp() {
        given()
        .when()
            .get("/actuator/health")
        .then()
            .statusCode(200)
            .body("status", equalTo("UP"));
    }

    @Test
    public void createTask_returns201WithGeneratedId() {
        given()
            .contentType("application/json")
            .body("{\"title\":\"Smoke task\"}")
        .when()
            .post("/api/tasks")
        .then()
            .statusCode(201)
            .body("id", notNullValue())
            .body("title", equalTo("Smoke task"))
            .body("done", equalTo(false));
    }

    @Test
    public void getMissingTask_returns404() {
        given()
        .when()
            .get("/api/tasks/{id}", 999_999)
        .then()
            .statusCode(404);
    }

    @Test
    public void listTasks_returns200WithArray() {
        given()
        .when()
            .get("/api/tasks")
        .then()
            .statusCode(200)
            .body("$", isA(java.util.List.class));
    }
}
