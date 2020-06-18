package jvn.RentACar.repository;

import jvn.RentACar.model.Writable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface FileRepository<T extends Writable> {

    void write(Path file, T t) throws IOException;

    void writeAll(Path file, List<T> t) throws IOException;
}
