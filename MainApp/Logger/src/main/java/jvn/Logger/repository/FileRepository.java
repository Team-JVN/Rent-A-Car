package jvn.Logger.repository;

import jvn.Logger.model.Writable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface FileRepository<T extends Writable> {

//    List<T> readAll(Path file) throws IOException;

    void write(Path file, T t) throws IOException;

    void writeAll(Path file, List<T> t) throws IOException;
}
