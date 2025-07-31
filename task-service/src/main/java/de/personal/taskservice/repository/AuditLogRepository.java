package de.personal.taskservice.repository;

import de.personal.taskservice.model.AuditLogRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLogRecord, Long> {
}
