package dev.sdetprep.dummytests.e2e;

import dev.sdetprep.dummyproject.model.Task;
import dev.sdetprep.dummyproject.model.TaskRequest;
import dev.sdetprep.dummytests.BaseApiTest;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Full user-facing flow through several steps in sequence — the kind of test that
 * exercises the whole API surface the way a real client would, not just one call.
 */
public class TaskLifecycleE2ETest extends BaseApiTest {

    @Test
    public void fullTaskLifecycle_createUpdateListDelete() {
        // 1. create a new task
        Task created = given()
                .contentType("application/json")
                .body(new TaskRequest("Plan sprint demo", false))
        .when()
                .post("/api/tasks")
        .then()
                .statusCode(201)
                .extract().as(Task.class);
        assertFalse(created.isDone());

        // 2. read it back
        given()
        .when()
                .get("/api/tasks/{id}", created.getId())
        .then()
                .statusCode(200);

        // 3. mark it done
        Task updated = given()
                .contentType("application/json")
                .body(new TaskRequest(created.getTitle(), true))
        .when()
                .put("/api/tasks/{id}", created.getId())
        .then()
                .statusCode(200)
                .extract().as(Task.class);
        assertTrue(updated.isDone());

        // 4. confirm it shows up as done in the full list
        given()
        .when()
                .get("/api/tasks")
        .then()
                .statusCode(200)
                .body("id", hasItem(created.getId().intValue()));

        // 5. delete it
        given()
        .when()
                .delete("/api/tasks/{id}", created.getId())
        .then()
                .statusCode(204);

        // 6. confirm it's gone from both the single-item and list endpoints
        given()
        .when()
                .get("/api/tasks/{id}", created.getId())
        .then()
                .statusCode(404);

        given()
        .when()
                .get("/api/tasks")
        .then()
                .statusCode(200)
                .body("id", not(hasItem(created.getId().intValue())));
    }
}
