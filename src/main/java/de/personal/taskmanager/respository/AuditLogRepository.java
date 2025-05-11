package de.personal.taskmanager.respository;

import de.personal.taskmanager.model.AuditLogRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLogRecord, Long> {
}
