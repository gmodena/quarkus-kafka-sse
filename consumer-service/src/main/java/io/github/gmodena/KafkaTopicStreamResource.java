package io.github.gmodena;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.quarkus.logging.Log;
import io.vertx.core.http.HttpServerRequest;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseEventSink;
import org.jboss.resteasy.annotations.SseElementType;

import static io.quarkiverse.loggingjson.providers.KeyValueStructuredArgument.kv;

@Path("/consume")
public class KafkaTopicStreamResource {
    private final KafkaTopicMessageConsumer kafkaConsumer;
    @Inject
    Sse sse;
    @Inject
    MeterRegistry registry;

    @Inject
    public KafkaTopicStreamResource(KafkaTopicMessageConsumer kafkaConsumer) {
        this.kafkaConsumer = kafkaConsumer;
    }

    @GET
    @Path("/{topic}")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    @SseElementType(MediaType.TEXT_PLAIN)
    public void streamTopic(@PathParam("topic") String topic, @Context SseEventSink eventSink, @Context HttpServerRequest request) {
        String clientIp = request.remoteAddress().host();
        // TODO: string templates are preview in Java21.
        Log.infov(STR."New connection from \{clientIp} to \{topic}.", kv("client_ip", clientIp), kv("topic", topic));
        registry.counter("stream_counter", Tags.of("topic", topic)).increment();

        kafkaConsumer.receive(topic)
                .map(record -> record.value())
                .subscribe().with(
                        event -> {
                            eventSink.send(sse.newEventBuilder().data(event).build());
                        },
                        Throwable::printStackTrace
                );
    }
}