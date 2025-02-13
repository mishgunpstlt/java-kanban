package httpTasksTest;

import adapter.DurationAdapter;
import adapter.LocalDateTimeAdapter;
import adapter.StatusAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import managers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import tasks.Status;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;
import static utils.Managers.getDefault;

public class TasksHandlerTest {

    private final HttpClient client = HttpClient.newHttpClient();
    TaskManager taskManager = getDefault();
    HttpTaskServer httpServer = new HttpTaskServer(taskManager);
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Status.class, new StatusAdapter())
            .create();

    @BeforeEach
    public void setUp() {
        taskManager.removeTask();
        taskManager.removeSubtask();
        taskManager.removeEpic();
        httpServer.start();
    }

    @AfterEach
    public void shutDown() {
        httpServer.stop();
    }

    @Test
    public void shouldAddTask() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());

        String taskJson = gson.toJson(task);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Код ответа неверный");

        Collection<Task> tasksFromManager = taskManager.getTask();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.iterator().next().getNameTask(), "Некорректное имя задачи");
    }

    @Test
    public void shouldGetTask() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());

        taskManager.addTask(task);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Код ответа неверный");

        assertNotNull(response.body(), "Задачи не возвращаются");
        assertTrue(response.body().split(",")[1].contains("Test 2"), "Некорректное имя задачи");
    }

    @Test
    public void shouldUpdateTask() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());

        taskManager.addTask(task);

        Task taskUpdate = new Task("Test Update", "Testing task update",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        taskUpdate.setId(task.getId());
        String taskJson = gson.toJson(taskUpdate);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Код ответа неверный");

        Collection<Task> tasksFromManager = taskManager.getTask();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test Update", tasksFromManager.iterator().next().getNameTask(),
                "Некорректное имя задачи");
    }

    @Test
    public void shouldGetTaskById() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());

        taskManager.addTask(task);

        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Код ответа неверный");

        assertNotNull(response.body(), "Задачи не возвращаются");
        assertTrue(response.body().split(",")[1].contains("Test 2"),
                "Некорректное имя задачи");
    }

    @Test
    public void shouldDeleteTask() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());

        taskManager.addTask(task);

        Task taskSave = new Task("Test Save", "Testing task 1",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.of(2025, 2, 12, 10, 0));

        taskManager.addTask(taskSave);

        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Код ответа неверный");

        Collection<Task> tasksFromManager = taskManager.getTask();
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test Save", tasksFromManager.iterator().next().getNameTask(),
                "Некорректное имя задачи");
    }
}
