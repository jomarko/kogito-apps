package org.kie.kogito.test;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.awaitility.Awaitility;
import org.kie.kogito.test.quarkus.kafka.KafkaTestClient;

import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;

public class TestUtils {

    private TestUtils() {
    }

    public static JsonPath waitForEvent(KafkaTestClient kafkaClient, String topic, long seconds) throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final AtomicReference<String> cloudEvent = new AtomicReference<>();
        kafkaClient.consume(topic, rawCloudEvent -> {
            cloudEvent.set(rawCloudEvent);
            countDownLatch.countDown();
        });
        // give some time to consume the event.
        assertThat(countDownLatch.await(seconds, TimeUnit.SECONDS)).isTrue();
        return new JsonPath(cloudEvent.get());
    }

    public static void assertJobsAndProcessOnDataIndex(String dataIndexURL, String processId, String processInstanceId, String processStatus, String jobStatus, Duration timeout) {
        if (dataIndexURL != null) {
            String query = "{  \"query\" : " +
                    "\"{ProcessInstances (where : {" +
                    "    id: {equal : \\\"" + processInstanceId + "\\\" }" +
                    "  }) {" +
                    "    id,processId,state" +
                    "  }" +
                    "}\"" +
                    "}";
            Awaitility.await()
                    .atMost(timeout)
                    .untilAsserted(() -> given()
                            .baseUri(dataIndexURL)
                            .contentType(ContentType.JSON)
                            .body(query)
                            .when().post("/graphql")
                            .then().statusCode(200)
                            .body("data.ProcessInstances.size()", is(1))
                            .body("data.ProcessInstances[0].id", is(processInstanceId))
                            .body("data.ProcessInstances[0].processId", is(processId))
                            .body("data.ProcessInstances[0].state", is(processStatus)));

            String queryJobs = "{  \"query\" : " +
                    "\"{Jobs (where : {" +
                    "    processInstanceId: {equal : \\\"" + processInstanceId + "\\\" }" +
                    "  }) {" +
                    "    status" +
                    "  }" +
                    "}\"" +
                    "}";

            given()
                    .baseUri(dataIndexURL)
                    .contentType(ContentType.JSON)
                    .body(queryJobs)
                    .when().post("/graphql")
                    .then().statusCode(200)
                    .body("data.Jobs.size()", is(1))
                    .body("data.Jobs[0].status", is(jobStatus));
        }
    }
}
