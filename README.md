# HANDS-ON: EDA & Kafka
## Assignment 
1. Create class Scheduler that sends a message to Kafka topic named "scheduled" every 10 seconds. 
2. Create class Processor which consumes messages with payload of type String from topic "scheduled". 
3. Make class Processor convert payload of every message (right after consuming it) to uppercase and then send it as message to topic "uppercase". 
4. Make class Processor consume messages from topic "uppercase". 
5. Configure a *dead letter queue* failure strategy for topic "uppercase" with a dead letter topic called "dead-letter-topic-uppercase". 
6. (Optional) Make some messages randomly get nacked to be sent to the dead letter topic. 
7. (Optional) Run Kafka via podman-compose (see *resources/docker-compose.yml* file) and connect to this broker from your Quarkus application. 

## Cheat Sheet 
### Running the app in dev mode 
```
quarkus dev 
```
### Using the Smallrye Reactive Messaging - Kafka Extension with Quarkus 

#### Adding the dependency 
```
quarkus extension add messaging-kafka
```
```
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-messaging-kafka</artifactId>
</dependency>
```

#### Configuration 
##### Kafka Broker URL 
```
# Not needed when using Dev Services 
kafka.bootstrap.servers = localhost:9092
%prod.kafka.bootstrap.servers = kafka:9092
```
##### Channels 
```
# Incoming Channel
mp.messaging.incoming.[channel-name].connector = smallrye-kafka 
mp.messaging.incoming.[channel-name].topic = example-topic 
```
```
# Outgoing Channel
mp.messaging.outgoing.[channel-name].connector = smallrye-kafka 
mp.messaging.outgoing.[channel-name].topic = another-topic 
```
##### Failure Strategy 
```
# Setting the failure strategy to DLQ 
mp.messaging.incoming.[your-channel].failure-strategy=dead-letter-queue

# Configuring the Dead Letter Topic 
mp.messaging.incoming.[your-channel].dead-letter-queue.topic=dead-letter-topic-$your-channel
```
##### Health Checks 
```
# Broker Readiness Check 
quarkus.kafka.health.enabled=true 

# Disable both Liveliness and Readiness Check 
mp.messaging.incoming.[your-channel].health-enabled=false

# Disable only the Readiness Check with 
mp.messaging.incoming.[your-channel].health-readiness-enabled=false
```

#### Receiving messages 
```
@ApplicationScoped
public class PriceConsumer 
{
    @Incoming("prices-in")
    public void consume(double price) 
    {
        // process your price 
    }



    @Incoming("prices-in")
    public CompletionStage<Void> consume(Message<Double> message) 
    {
        // access record metadata
        var metadata = 	message.getMetadata(IncomingKafkaRecordMetadata.class)
        .orElseThrow();
        
        // process the message payload 
        double price = message.getPayload();
        
        // acknowledge the incoming message 
        return message.ack();
    }
}
```

#### Sending messages 
```
@ApplicationScoped
public class PriceProducer 
{
    private final Random random = new Random();

    @Outgoing("prices-out")
    public Multi<Double> produce() 
    {
        // build an infinite stream of random prices
        // it emits a price every second
        return 		Multi.createFrom().ticks().every(Duration.ofSeconds(1))
            .map(x -> random.nextDouble());    
    }
}
```
##### Using Emitter 
```
@Path("/prices")
public class PriceResource {

    @Inject
    @Channel("price-create")
    Emitter<Double> priceEmitter;

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    public void addPrice(Double price) {
        CompletionStage<Void> ack = priceEmitter.send(price);
    }
}
```

### Scheduling tasks 
```
quarkus extension add scheduler

<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-scheduler</artifactId>
</dependency>
```
```
@Scheduled(every = “10s”, delayed = "10s")
public void performScheduledTask() {
     performTask();
}


@Scheduled(every = “${interval.configured.in.properties}”)
public void performScheduledTask() {
     performTask();
}
```

### Running Kafka via Docker/Podman Compose 

```
podman-compose up –d 

# application.properties 
kafka.bootstrap.servers=localhost:9092 
```

## Resources 
- https://quarkus.io/guides/kafka 
- https://quarkus.io/guides/kafka-getting-started 
- https://quarkus.io/extensions/io.quarkus/quarkus-smallrye-reactive-messaging-kafka/ 
- https://smallrye.io/smallrye-reactive-messaging/3.14.0/getting-started/ 
- https://smallrye.io/smallrye-reactive-messaging/smallrye-reactive-messaging/3.4/kafka/kafka.html 

