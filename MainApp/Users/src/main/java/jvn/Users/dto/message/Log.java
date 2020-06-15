package jvn.Users.dto.message;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
public class Log {

    public static final String INFO = "INFO";
    public static final String WARN = "WARN";
    public static final String ERROR = "ERROR";

    private static final String DELIMITER = "|";
    private static final String MESSAGE_FORMAT = "%s" + DELIMITER + "%s" + DELIMITER + "%s" + DELIMITER + "%s" + DELIMITER + "%s";

    private LocalDateTime timestamp;
    private String logType;
    private String location;
    private String eventCode;
    private String message;

    public Log(String logType, String location, String eventCode, String message) {
        this.timestamp = LocalDateTime.now();
        this.logType = logType;
        this.location = location;
        this.eventCode = eventCode;
        this.message = message;
    }

    @Override
    public String toString() {
        return String.format(MESSAGE_FORMAT, timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), logType, location, eventCode, message);
    }
}
