package jvn.RentACar.repository;

import jvn.RentACar.config.ApplicationConfiguration;
import jvn.RentACar.model.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class LogRepository implements FileRepository<Log> {

    //    private static int MAX_FILE_SIZE = 10000000;    // 10 MB
    private static int MAX_FILE_SIZE = 1000;

    private ApplicationConfiguration configuration;

    @Override
    public void write(Path file, Log log) throws IOException {
        Files.write(file, (log.toFile() + "\n").getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
        if (Files.exists(file) && Files.size(file) > MAX_FILE_SIZE) {
            rotateLogFiles(file, Paths.get(configuration.getLogBackup1()), Paths.get(configuration.getLogBackup2()));
        }
    }

    @Override
    public void writeAll(Path file, List<Log> t) throws IOException {
        Files.write(file, t.stream().map(Log::toFile).collect(Collectors.toList()),
                StandardOpenOption.APPEND, StandardOpenOption.CREATE);
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
