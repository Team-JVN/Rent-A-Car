package jvn.RentACar.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.StringTokenizer;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Log implements Writable {

    public static final String INFO = "INFO";
    public static final String WARN = "WARN";
    public static final String ERROR = "ERROR";

    private static final String DELIMITER = "|";
    private static final String MESSAGE_FORMAT = "%s" + DELIMITER + "%s" + DELIMITER + "%s" + DELIMITER + "%s" + DELIMITER + "%s" + DELIMITER + "%s";

    private LocalDateTime timestamp;
    private String logType;
    private String packageName;
    private String className;
    private String eventCode;
    private String message;

    @Override
    public String toFile() {
        return String.format(MESSAGE_FORMAT, timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), logType, packageName, className, eventCode, message);
    }

    public Log(String logType, String packageName, String className, String eventCode, String message) {
        this.timestamp = LocalDateTime.now();
        this.logType = logType;
        this.packageName = packageName;
        this.className = className;
        this.eventCode = eventCode;
        this.message = message;
    }

    public static String getServiceName(String classPath) {
        return classPath.split("\\.")[1];
    }
}
