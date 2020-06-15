package jvn.Logger.repository;

import jvn.Logger.model.Log;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class LogRepository implements FileRepository<Log> {

//    @Override
//    public List<Log> readAll(Path file) throws IOException {
//        return Files.readAllLines(file)
//                .stream()
//                .map(log -> {
//                    try {
//                        return Log.parse(log);
//                    } catch (Exception e) {
//                        return null;
//                    }
//                })
//                .filter(Objects::nonNull)
//                .collect(Collectors.toList());
//    }

    @Override
    public void write(Path file, Log log) throws IOException {
        Files.write(file, (log.toFile() + "\n").getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
    }

    @Override
    public void writeAll(Path file, List<Log> t) throws IOException {
        Files.write(file, t.stream().map(Log::toFile).collect(Collectors.toList()),
                StandardOpenOption.APPEND, StandardOpenOption.CREATE);
    }
}
