package io.github.gmodena;

import io.micrometer.core.instrument.MeterRegistry;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;

import static io.quarkiverse.loggingjson.providers.KeyValueStructuredArgument.kv;

@Path("/produce/{topic}")
public class KafkaTopicProducerResource {
    @Inject
    MeterRegistry registry;

    @Inject
    KafkaTopicProducer kafkaProducer;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void produce(@PathParam("topic") String topic, String payload) {
        Log.infov("", kv("topic", topic));
        kafkaProducer.send(topic, payload);
    }
}