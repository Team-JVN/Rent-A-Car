package jvn.Logger.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class ApplicationConfiguration {

    @Value("${LOG_STORAGE:mainapp-log-storage.log}")
    private String logStorage;

    @Value("${LOG_BACKUP_1:mainapp-log-backup-1.log}")
    private String logBackup1;

    @Value("${LOG_BACKUP_2:mainapp-log-backup-2.log}")
    private String logBackup2;
}
