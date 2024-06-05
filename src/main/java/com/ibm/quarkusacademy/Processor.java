package com.ibm.quarkusacademy;

import java.util.Random;
import java.util.concurrent.CompletionStage;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import io.quarkus.logging.Log;

public class Processor {

    @Incoming("scheduled-in")
    @Outgoing("uppercase-out")
    public Message<String> consumeAndSendMessage (Message<String> message) {

        String event = message.getPayload(); 
        Log.infof("Incoming scheduled event: %s", event);

        String uppercaseEvent = event.toUpperCase();
        Log.infof("Outgoing uppercase event: %s", event);
        return Message.of(uppercaseEvent);

    }

    @Incoming("uppercase-in")
    public CompletionStage<Void> consumeMessage (Message<String> message) {

        Random random = new Random(); 
        if (random.nextBoolean()) {
            String event = message.getPayload(); 
            Log.infof("Incoming uppercase event: %s", event);
            return message.ack();
        }
        else {
            return message.nack(new Exception());
        }
        

    }
    
    
}
