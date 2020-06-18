package jvn.RentACar.listener;

import jvn.RentACar.model.Log;
import jvn.RentACar.service.LogService;
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

    private LogService logService;

    @EventListener
    public void onStartup(ApplicationReadyEvent event) {
        logService.write(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "APP", String.format("--- %s application started ---", Log.getServiceName(CLASS_PATH))));
    }

    @EventListener
    public void onShutdown(ContextStoppedEvent event) {
        logService.write(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "APP", String.format("--- %s application shut down ---", Log.getServiceName(CLASS_PATH))));
    }

    @PreDestroy
    public void onDestroy() {
        logService.write(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "APP", String.format("--- %s application shut down ---", Log.getServiceName(CLASS_PATH))));
    }

    @Autowired
    public StartupShutdownEventListener(LogService logService) {
        this.logService = logService;
    }
}