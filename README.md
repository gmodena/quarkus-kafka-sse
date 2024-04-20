# quarkus-kafka-sse

A prototype to demo integrating Quarkus with Kafka and Server-Sent Events (SSE).

This repo contain two services:
- `consumer-service`, that exposes a `/consume/<topic>`: a service that listens to a kafka topic and sends the messages to a SSE endpoint.
- `producer-service`, that exposes a `/produce/<topic>` a service that publishes messages to a kafka topic.

Both services expose metrics via Prometheus, and log in ECS format.

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Quick start

Start a Kafka cluster using docker-compose:
```shell script
docker-compose up -d
```
This will spin up a Confluent Kafka cluster with a single broker, a single zookeeper, and a single schema-registry.
See https://docs.confluent.io/platform/current/platform-quickstart.html for more details.


Start `consumer-service` with:
```commandline
./gradlew :consumer-service:quarkusRun
```

Start `producer-service` with:
```commandline
./gradlew :producer-service:quarkusRun
```

### Publish and consume events
In another terminal, start listening on the `test` topic:
```commandline
curl http://0.0.0.0:8880/consume/test
```

Publish messages into the `test` topic:
```commandline
curl -X POST -H "Content-Type: application/json" -d '{"message": "Hello, World!"}' http://localhost:8881/produce/test
curl -X POST -H "Content-Type: application/json" -d '{"message": "Goodbye, World!"}' http://localhost:8881/produce/test
```



## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./gradlew :<service>:quarkusDev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using:
```shell script
./gradlew :<service>:build
```
It produces the `quarkus-run.jar` file in the `build/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `build/quarkus-app/lib/` directory.

The application is now runnable using `java -jar build/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:
```shell script
./gradlew :<service>:build -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar build/*-runner.jar`.

## Creating a native executable

You can create a native executable using: 
```shell script
./gradlew :<service>:build -Dquarkus.package.type=native
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./gradlew :<service>:build -Dquarkus.package.type=native -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./build/message-service-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/gradle-tooling.
