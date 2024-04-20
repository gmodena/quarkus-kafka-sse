
package io.github.gmodena;

import io.smallrye.reactive.messaging.kafka.api.OutgoingKafkaRecordMetadata;
import org.eclipse.microprofile.reactive.messaging.Message;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
public class KafkaTopicMessageProducer {

    @Inject
    @Channel("intake")
    Emitter<String> emitter;

    public void send(String topic, String payload) {
        OutgoingKafkaRecordMetadata<String> metadata = OutgoingKafkaRecordMetadata.<String>builder()
                .withTopic(topic)
                .build();

        Message<String> message = Message.of(payload)
                .addMetadata(metadata);

        emitter.send(message);
    }
}