package io.github.aviidow.taskify.task.repository;

import io.github.aviidow.taskify.task.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    Page<Task> findByCreatorId(Long creatorId, Pageable pageable);

    Page<Task> findByAssigneeId(Long assigneeId, Pageable pageable);

    @Query("SELECT t FROM Task t WHERE t.creator.id = :userId OR t.assignee.id = :userId")
    Page<Task> findAllByUserId(@Param("userId") Long userId, Pageable pageable);
}
