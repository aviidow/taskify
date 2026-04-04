package io.github.aviidow.taskify.task.repository;

import io.github.aviidow.taskify.task.model.Task;
import io.github.aviidow.taskify.user.model.User;
import io.github.aviidow.taskify.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User creator;
    private User assignee;

    @BeforeEach
    void setUp() {
        creator = new User();
        creator.setEmail("creator@test.com");
        creator.setPassword("encoded");
        creator.setName("Creator");
        creator = userRepository.save(creator);

        assignee = new User();
        assignee.setEmail("assignee@test.com");
        assignee.setPassword("encoded");
        assignee.setName("Assignee");
        assignee = userRepository.save(assignee);
    }

    @Test
    void findByCreatorId_ReturnsTasks() {
        // given
        Task task = new Task();
        task.setTitle("Test Task");
        task.setDescription("Description");
        task.setStatus(Task.Status.PENDING);
        task.setPriority(Task.Priority.MEDIUM);
        task.setCreator(creator);
        task.setAssignee(assignee);
        taskRepository.save(task);

        // when
        Page<Task> result = taskRepository.findByCreatorId(creator.getId(), PageRequest.of(0, 10));

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Test Task");
    }

    @Test
    void findAllByUserId_ReturnsTasksWhereUserIsCreatorOrAssignee() {
        // given
        Task task1 = new Task();
        task1.setTitle("Creator Task");
        task1.setCreator(creator);
        task1.setAssignee(null);
        taskRepository.save(task1);

        Task task2 = new Task();
        task2.setTitle("Assignee Task");
        task2.setCreator(assignee);
        task2.setAssignee(creator);
        taskRepository.save(task2);

        // when
        Page<Task> result = taskRepository.findAllByUserId(creator.getId(), PageRequest.of(0, 10));

        // then
        assertThat(result.getContent()).hasSize(2);
    }
}
