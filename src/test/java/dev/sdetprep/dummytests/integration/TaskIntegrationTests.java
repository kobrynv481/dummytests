package dev.sdetprep.dummytests.integration;

import dev.sdetprep.dummyproject.model.Task;
import dev.sdetprep.dummyproject.model.TaskRequest;
import dev.sdetprep.dummytests.BaseApiTest;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.testng.Assert.assertEquals;

/**
 * Interaction between two or more endpoints — reuses dummyproject's own
 * Task/TaskRequest classes (compile-time dependency on the dummyproject jar)
 * instead of hand-built JSON strings.
 */
public class TaskIntegrationTests extends BaseApiTest {

    @Test
    public void createThenGet_returnsTheSameTask() {
        TaskRequest request = new TaskRequest("Integration task", false);

        Task created = given()
                .contentType("application/json")
                .body(request)
        .when()
                .post("/api/tasks")
        .then()
                .statusCode(201)
                .extract().as(Task.class);

        given()
        .when()
                .get("/api/tasks/{id}", created.getId())
        .then()
                .statusCode(200)
                .body("id", equalTo(created.getId().intValue()))
                .body("title", equalTo("Integration task"));
    }

    @Test
    public void createThenUpdate_persistsNewTitleAndDoneFlag() {
        Task created = given()
                .contentType("application/json")
                .body(new TaskRequest("Before update", false))
        .when()
                .post("/api/tasks")
        .then()
                .statusCode(201)
                .extract().as(Task.class);

        Task updated = given()
                .contentType("application/json")
                .body(new TaskRequest("After update", true))
        .when()
                .put("/api/tasks/{id}", created.getId())
        .then()
                .statusCode(200)
                .extract().as(Task.class);

        assertEquals(updated.getTitle(), "After update");
        assertEquals(updated.isDone(), true);
    }

    @Test
    public void createThenDelete_taskNoLongerExists() {
        Task created = given()
                .contentType("application/json")
                .body(new TaskRequest("Temporary", false))
        .when()
                .post("/api/tasks")
        .then()
                .statusCode(201)
                .extract().as(Task.class);

        given()
        .when()
                .delete("/api/tasks/{id}", created.getId())
        .then()
                .statusCode(204);

        given()
        .when()
                .get("/api/tasks/{id}", created.getId())
        .then()
                .statusCode(404);
    }

    @Test
    public void createThenList_newTaskAppearsInFullList() {
        Task created = given()
                .contentType("application/json")
                .body(new TaskRequest("Findable in list", false))
        .when()
                .post("/api/tasks")
        .then()
                .statusCode(201)
                .extract().as(Task.class);

        given()
        .when()
                .get("/api/tasks")
        .then()
                .statusCode(200)
                .body("id", hasItem(created.getId().intValue()));
    }
}
