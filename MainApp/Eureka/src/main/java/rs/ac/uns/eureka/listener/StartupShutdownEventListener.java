package rs.ac.uns.eureka.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import rs.ac.uns.eureka.message.Log;
import rs.ac.uns.eureka.producer.LogProducer;

import javax.annotation.PreDestroy;

@Component
public class StartupShutdownEventListener {

    private final String CLASS_PATH = this.getClass().getCanonicalName();
    private final String CLASS_NAME = this.getClass().getSimpleName();

    private LogProducer logProducer;

//    @EventListener
//    public void onStartup(ApplicationReadyEvent event) {
//        logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "APP", String.format("--- %s service registry started ---", Log.getServiceName(CLASS_PATH))));
//    }
//
//    @EventListener
//    public void onShutdown(ContextStoppedEvent event) {
//        logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "APP", String.format("--- %s service registry shut down ---", Log.getServiceName(CLASS_PATH))));
//    }
//
//    @PreDestroy
//    public void onDestroy() {
//        logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "APP", String.format("--- %s service registry shut down ---", Log.getServiceName(CLASS_PATH))));
//    }

    @Autowired
    public StartupShutdownEventListener(LogProducer logProducer) {
        this.logProducer = logProducer;
    }
}