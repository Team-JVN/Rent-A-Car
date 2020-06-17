package jvn.Logger.consumer;

import jvn.Logger.config.ApplicationConfiguration;
import jvn.Logger.config.RabbitMQConfiguration;
import jvn.Logger.model.Log;
import jvn.Logger.repository.FileRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class LogConsumer {

    private FileRepository<Log> repository;
    private ApplicationConfiguration configuration;

    @RabbitListener(queues = RabbitMQConfiguration.LOGS)
    public void write(String logStr) {
        System.out.println(logStr);
        try {
            repository.write(Paths.get(configuration.getLogStorage()), Log.parse(logStr));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RabbitListener(queues = RabbitMQConfiguration.LOGS)
    public void writeAll(List<String> logStrList) {
        try {
            List<Log> logs = new ArrayList<>();
            for (String logStr : logStrList) {
                System.out.println(logStr);
                logs.add(Log.parse(logStr));
            }
            repository.writeAll(Paths.get(configuration.getLogStorage()), logs);
        } catch (IOException e) {
            System.out.println("Cannot write log messages to a file.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Autowired
    public LogConsumer(FileRepository<Log> repository, ApplicationConfiguration configuration) {
        this.repository = repository;
        this.configuration = configuration;
    }

}
