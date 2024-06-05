package com.ibm.quarkusacademy;

import java.util.concurrent.CompletionStage;
import org.eclipse.microprofile.reactive.messaging.Message;

// consumes messages from "scheduled" topic, converts them to uppercase 
// then sends the messages to topic "uppercase" and consumes this topic 
public class Processor {

    public Message<String> consumeAndSendMessage (Message<String> message) {

    }

    public CompletionStage<Void> consumeMessage (Message<String> message) {

    }
    
    
}
