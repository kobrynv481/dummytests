package dev.sdetprep.dummytests;

import io.restassured.RestAssured;
import org.testng.annotations.BeforeSuite;

public abstract class BaseApiTest {

    @BeforeSuite(alwaysRun = true)
    public void configureRestAssured() {
        RestAssured.baseURI = System.getProperty("api.base.url", "http://localhost:8080");
    }
}
