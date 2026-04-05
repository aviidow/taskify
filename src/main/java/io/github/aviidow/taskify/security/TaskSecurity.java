package io.github.aviidow.taskify.security;

import io.github.aviidow.taskify.task.model.Task;
import io.github.aviidow.taskify.task.repository.TaskRepository;
import io.github.aviidow.taskify.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("taskSecurity")
@RequiredArgsConstructor
public class TaskSecurity {

    private final TaskRepository taskRepository;

    public boolean canViewTask(Long taskId) {
        User currentUser = getCurrentUser();

        Task task = taskRepository.findById(taskId).orElse(null);
        if (task == null) return false;

        if (currentUser.getRole() == User.Role.ADMIN) return true;

        return task.getCreator().getId().equals(currentUser.getId()) ||
                (task.getAssignee() != null && task.getAssignee().getId().equals(currentUser.getId()));
    }

    public boolean canEditTask(Long taskId) {
        User currentUser = getCurrentUser();

        Task task = taskRepository.findById(taskId).orElse(null);
        if (task == null) return false;

        if (currentUser.getRole() == User.Role.ADMIN) return true;

        return task.getCreator().getId().equals(currentUser.getId());
    }

    public boolean canDeleteTask(Long taskId) {
        return canEditTask(taskId);
    }

    public boolean canAssignTask(Long taskId) {
        return canEditTask(taskId);
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (User) auth.getPrincipal();
    }
}
