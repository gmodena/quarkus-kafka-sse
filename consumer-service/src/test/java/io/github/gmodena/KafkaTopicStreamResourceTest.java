package io.github.gmodena;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.smallrye.mutiny.Multi;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@QuarkusTest
public class KafkaTopicStreamResourceTest {

    @InjectMock
    KafkaTopicMessageConsumer kafkaConsumer;

    @Test
    public void testStreamTopic() {
        String topic = "test-topic";

        Mockito.when(kafkaConsumer.receive(topic))
                .thenReturn(Multi.createFrom().item(new ConsumerRecord<>(topic, 0, 0, "Test Key", "Test Message")));

        RestAssured.given()
                .pathParam("topic", topic)
                .when().get("/stream/{topic}")
                .then()
                .statusCode(200);

        Mockito.verify(kafkaConsumer).receive(topic);
    }
}