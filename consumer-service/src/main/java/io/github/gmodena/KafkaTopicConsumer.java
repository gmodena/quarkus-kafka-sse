package io.github.gmodena;

import io.smallrye.mutiny.Multi;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;


@ApplicationScoped
public class KafkaTopicConsumer {
    private static final int CONSUMER_TIMEOUT_MS = 100;

    @ConfigProperty(name = "kafka.bootstrap.servers")
    String bootstrapServers;

    @ConfigProperty(name = "kafka.group.id")
    String groupId;

    @ConfigProperty(name = "kafka.enable.auto.commit")
    String enableAutoCommit;

    @ConfigProperty(name = "kafka.auto.commit.interval.ms")
    String autoCommitIntervalMs;

    @ConfigProperty(name = "kafka.key.deserializer")
    String keyDeserializer;

    @ConfigProperty(name = "kafka.value.deserializer")
    String valueDeserializer;

    @Inject
    public KafkaTopicConsumer() {
    }

    private Properties getKafkaConsumerProperties() {
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("group.id", groupId);
        props.put("enable.auto.commit", enableAutoCommit);
        props.put("auto.commit.interval.ms", autoCommitIntervalMs);
        props.put("key.deserializer", keyDeserializer);
        props.put("value.deserializer", valueDeserializer);
        return props;
    }

    public Multi<ConsumerRecord<String, String>> receive(String topicName) {
        Properties config = getKafkaConsumerProperties();
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(config);
        consumer.subscribe(Collections.singletonList(topicName));

        return Multi.createFrom().emitter(emitter -> {
            try {
                while (true) {
                    ConsumerRecords<String, String> records = consumer.poll(
                            Duration.ofMillis(CONSUMER_TIMEOUT_MS));
                    for (ConsumerRecord<String, String> record : records) {
                        emitter.emit(record);
                    }
                }
            } catch (Exception e) {
                emitter.fail(e);
            } finally {
                consumer.close();
            }
        });
    }
}