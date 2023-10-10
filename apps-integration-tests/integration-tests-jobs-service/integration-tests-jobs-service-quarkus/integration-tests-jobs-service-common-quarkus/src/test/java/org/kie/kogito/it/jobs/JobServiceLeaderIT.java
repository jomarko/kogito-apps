package org.kie.kogito.it.jobs;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.kogito.test.resources.CompositeTestResource;
import org.kie.kogito.testcontainers.JobServiceContainer;
import org.kie.kogito.testcontainers.KogitoPostgreSqlContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

public class JobServiceLeaderIT {

    private static CompositeTestResource resource;
    private final static Logger logger = LoggerFactory.getLogger(JobServiceLeaderIT.class);

    private static List<JobServiceContainer> containers;

    @BeforeAll
    public static void init() {
        KogitoPostgreSqlContainer postgreSqlContainer = new KogitoPostgreSqlContainer();
        resource = new CompositeTestResource(new JobServiceContainer())
                .withServiceContainer("job-service-1", new JobServiceContainer(), postgreSqlContainer)
                .withServiceContainer("job-service-2", new JobServiceContainer(), postgreSqlContainer)
                .withDependencyToService(CompositeTestResource.MAIN_SERVICE_ID, postgreSqlContainer);
        containers = resource.getServiceContainers(JobServiceContainer.class);
        //decrease the leader expiration timeout to 2 seconds to make the test quicker
        containers.forEach(c -> c.withEnv("KOGITO_JOBS_SERVICE_MANAGEMENT_HEARTBEAT_EXPIRATION_IN_SECONDS", "2"));
        resource.start();
    }

    @Test
    public void testSingleLeader() {
        List<Integer> responses = containers.stream()
                .map(container -> healthRequest(container))
                .collect(Collectors.toList());
        //only one instance should be the leader (returning 200 OK)
        assertThat(responses).containsExactlyInAnyOrder(200, 503, 503);
    }

    @Test
    public void testChangingLeader() {
        //find the leader
        JobServiceContainer leader = containers.stream()
                .filter(container -> 200 == healthRequest(container))
                .findFirst()
                .orElse(null);
        //stop the leader
        leader.stop();
        //get the non-leaders
        List<JobServiceContainer> remain = containers.stream()
                .filter(container -> container.isRunning())
                .collect(Collectors.toList());
        assertThat(remain).hasSize(2);

        //wait until a non-leader become leader
        await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    List<Integer> responses = remain.stream()
                            .map(container -> healthRequest(container))
                            .collect(Collectors.toList());
                    assertThat(responses).containsExactlyInAnyOrder(200, 503);
                });

        //start the old leader again
        leader.start();
        //check if the old leader it not a leader anymore
        assertThat(healthRequest(leader)).isEqualTo(503);
    }

    private static int healthRequest(JobServiceContainer container) {
        String url = "http://" + container.getHost() + ":" + container.getMappedPort();
        logger.debug("Request to URL " + url);
        return given()
                .baseUri(url)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get("/q/health/ready")
                .statusCode();
    }
}
