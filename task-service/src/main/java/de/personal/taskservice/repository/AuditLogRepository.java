package de.personal.taskservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLogRecord, Long> {
}
