package jvn.RentACar.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class ApplicationConfiguration {

    @Value("${LOG_STORAGE:agentapp-log-storage.log}")
    private String logStorage;

    @Value("${LOG_BACKUP_1:agentapp-log-backup-1.log}")
    private String logBackup1;

    @Value("${LOG_BACKUP_2:agentapp-log-backup-2.log}")
    private String logBackup2;
}
