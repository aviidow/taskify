package io.github.aviidow.taskify.task.controller;

import io.github.aviidow.taskify.security.JwtService;
import io.github.aviidow.taskify.task.dto.TaskRequestDto;
import io.github.aviidow.taskify.task.model.Task;
import io.github.aviidow.taskify.task.repository.TaskRepository;
import io.github.aviidow.taskify.user.model.User;
import io.github.aviidow.taskify.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TaskControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private String authToken;
    private User testUser;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User();
        testUser.setEmail("tasktest@example.com");
        testUser.setPassword(passwordEncoder.encode("123456"));
        testUser.setName("Task Tester");
        testUser.setRole(User.Role.USER);
        testUser = userRepository.save(testUser);

        authToken = jwtService.generateToken(testUser);
    }

    @Test
    void createTask_Success_ReturnsCreatedTask() throws Exception {
        TaskRequestDto requestDto = new TaskRequestDto();
        requestDto.setTitle("H2 Test Task");
        requestDto.setDescription("Testing with H2");
        requestDto.setStatus(Task.Status.PENDING);
        requestDto.setPriority(Task.Priority.HIGH);

        mockMvc.perform(post("/api/tasks")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("H2 Test Task"))
                .andExpect(jsonPath("$.creatorEmail").value("tasktest@example.com"));
    }

    @Test
    void getTasks_ReturnsPageOfTasks() throws Exception {
        for (int i = 1; i <= 3; i++) {
            Task task = new Task();
            task.setTitle("Task " + i);
            task.setDescription("Description " + i);
            task.setStatus(Task.Status.PENDING);
            task.setPriority(Task.Priority.MEDIUM);
            task.setCreator(testUser);
            taskRepository.save(task);
        }

        mockMvc.perform(get("/api/tasks")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(3));
    }

    @Test
    void deleteTask_ByCreator_ReturnsNoContent() throws Exception {
        Task task = new Task();
        task.setTitle("Task to Delete");
        task.setCreator(testUser);
        task.setStatus(Task.Status.PENDING);
        task.setPriority(Task.Priority.MEDIUM);
        task = taskRepository.save(task);

        mockMvc.perform(delete("/api/tasks/{id}", task.getId())
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isNoContent());
    }
}
