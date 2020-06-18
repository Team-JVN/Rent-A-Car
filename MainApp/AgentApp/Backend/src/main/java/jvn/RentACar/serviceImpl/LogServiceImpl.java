package jvn.RentACar.serviceImpl;

import jvn.RentACar.config.ApplicationConfiguration;
import jvn.RentACar.model.Log;
import jvn.RentACar.repository.FileRepository;
import jvn.RentACar.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

@Service
public class LogServiceImpl implements LogService {

    private FileRepository<Log> repository;
    private ApplicationConfiguration configuration;

    public void write(Log log) {
        System.out.println(log.toFile());
        try {
            repository.write(Paths.get(configuration.getLogStorage()), log);
        } catch (IOException e) {
            System.out.println("Cannot write log message to a file.");
        }
    }

    public void writeAll(List<Log> logs) {
        try {
            repository.writeAll(Paths.get(configuration.getLogStorage()), logs);
        } catch (IOException e) {
            System.out.println("Cannot write log messages to a file.");
        }
    }

    @Autowired
    public LogServiceImpl(FileRepository<Log> repository, ApplicationConfiguration configuration) {
        this.repository = repository;
        this.configuration = configuration;
    }

}
