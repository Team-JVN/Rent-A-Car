package jvn.Logger.model;

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

    public static Log parse(String logStr) throws Exception {
        StringTokenizer tokenizer = new StringTokenizer(logStr, DELIMITER);
        if (tokenizer.countTokens() != 6) {
            throw new Exception("Invalid log format.");
        }

        return new Log(LocalDateTime.parse(tokenizer.nextToken(), DateTimeFormatter.ISO_LOCAL_DATE_TIME), tokenizer.nextToken(), tokenizer.nextToken(),
                tokenizer.nextToken(), tokenizer.nextToken(), tokenizer.nextToken());
    }
}
