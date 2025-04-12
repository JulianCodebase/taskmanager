package de.personal.taskmanager.respository;

import de.personal.taskmanager.model.AuditLogRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLogRecord, Long> {
}
