# dummytests

Separate SDET test project used for CI/CD practice. Drives the
[`dummyproject`](https://github.com/kobrynv481/dummyproject) API over HTTP with
REST-assured + TestNG. Depends on `dummyproject` as a plain Maven dependency (reuses
its `Task`/`TaskRequest` model classes instead of hand-built JSON), so `dummyproject`
must be built and installed (or otherwise resolvable) before this project builds.

## Suites

| File | Contents |
|---|---|
| `src/test/resources/testng-smoke.xml` | Atomic, single-endpoint checks. Fail fast. |
| `src/test/resources/testng-integration.xml` | Multi-endpoint interactions (create→get, create→update, create→delete, create→list). |
| `src/test/resources/testng-e2e.xml` | One full task lifecycle end to end. |
| `src/test/resources/testng.xml` | All three, in order — the default for `mvn test`. |

## Running locally

1. Build and install `dummyproject` so it's resolvable as a Maven dependency:
   ```
   cd ../dummyproject && mvn install -DskipTests
   ```
2. Start it:
   ```
   java -jar ../dummyproject/target/dummyproject-1.0.0-exec.jar &
   ```
3. Run a suite against it:
   ```
   mvn test -DsuiteXmlFile=src/test/resources/testng-smoke.xml
   mvn test -DsuiteXmlFile=src/test/resources/testng-integration.xml
   mvn test -DsuiteXmlFile=src/test/resources/testng-e2e.xml
   # or everything at once:
   mvn test
   ```

`api.base.url` defaults to `http://localhost:8080`; override with
`-Dapi.base.url=...` if dummyproject runs elsewhere (e.g. a different port/host in CI).

## CI/CD

Workflow wiring (building `dummyproject` first, publishing/caching its artifact, starting
it, then running each suite as its own job/stage) lives in `.github/workflows/*.yml` —
intentionally not included here yet.
