package jvn.Cars.listener;

import jvn.Cars.dto.message.Log;
import jvn.Cars.producer.LogProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

@Component
public class StartupShutdownEventListener {

    private final String CLASS_PATH = this.getClass().getCanonicalName();
    private final String CLASS_NAME = this.getClass().getSimpleName();

    private LogProducer logProducer;

//    @EventListener
//    public void onStartup(ApplicationReadyEvent event) {
//        logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "APP", String.format("--- %s service started ---", Log.getServiceName(CLASS_PATH))));
//    }
//
//    @EventListener
//    public void onShutdown(ContextStoppedEvent event) {
//        logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "APP", String.format("--- %s service shut down ---", Log.getServiceName(CLASS_PATH))));
//    }
//
//    @PreDestroy
//    public void onDestroy() {
//        logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "APP", String.format("--- %s service shut down ---", Log.getServiceName(CLASS_PATH))));
//    }

    @Autowired
    public StartupShutdownEventListener(LogProducer logProducer) {
        this.logProducer = logProducer;
    }
}