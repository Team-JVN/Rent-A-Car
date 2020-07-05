package jvn.Logger.repository;

import jvn.Logger.config.ApplicationConfiguration;
import jvn.Logger.model.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;

@Repository
public class LogRepository implements FileRepository<Log> {

    private static int MAX_FILE_SIZE = 10000000;    // 10 MB to store around 100,000 entries
    // private static int MAX_FILE_SIZE = 1000;    // 1 KB for testing

    private ApplicationConfiguration configuration;

    @Override
    public void write(Path file, Log log) throws IOException {
        String logStr = log.toFile() + "\n";
        Files.write(file, logStr.getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
        if (Files.exists(file) && Files.size(file) > MAX_FILE_SIZE) {
            rotateLogFiles(file, Paths.get(configuration.getLogBackup1()), Paths.get(configuration.getLogBackup2()));
        }
    }

    @Async
    public void rotateLogFiles(Path storage, Path backup1, Path backup2) throws IOException {
        // Replace Backup 2 with Backup 1
        if (Files.exists(backup1)) {
            Files.copy(backup1, backup2, StandardCopyOption.REPLACE_EXISTING);
        }
        // Backup Log storage into Backup 1
        Files.copy(storage, backup1, StandardCopyOption.REPLACE_EXISTING);
        // Empty Log storage
        BufferedWriter writer = Files.newBufferedWriter(storage);
        writer.write("");
        writer.flush();
    }

    @Autowired
    public LogRepository(ApplicationConfiguration configuration) {
        this.configuration = configuration;
    }
}
