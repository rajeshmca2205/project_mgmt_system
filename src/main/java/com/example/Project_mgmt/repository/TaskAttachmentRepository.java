package com.example.Project_mgmt.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Project_mgmt.entity.TaskAttachment;

public interface TaskAttachmentRepository
extends JpaRepository<TaskAttachment, Long> {

List<TaskAttachment> findByTaskId(Long taskId);

Optional<TaskAttachment> findByIdAndTaskId(
    Long id,
    Long taskId
);
}
