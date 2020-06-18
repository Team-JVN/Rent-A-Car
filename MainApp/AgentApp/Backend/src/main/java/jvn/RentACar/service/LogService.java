package jvn.RentACar.service;

import jvn.RentACar.model.Log;

import java.util.List;

public interface LogService {

    void write(Log log);

    void writeAll(List<Log> logs);

}
