package com.ibm.quarkusacademy;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import io.quarkus.logging.Log;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class Scheduler {

    @Inject
    @Channel("scheduled-out")
    Emitter<String> emitter;

    public void sendMessage(String event) {
        Log.infof("Sending message: %s", event);
        emitter.send(event);
    }

    @Scheduled(every = "${kafka.message.interval}", delayed = "10s")
    public void sendScheduledMessage() {
        sendMessage("Test-Message");
    }

}
