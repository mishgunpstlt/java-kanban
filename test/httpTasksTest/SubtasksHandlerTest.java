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
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;

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

public class SubtasksHandlerTest {

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
    public void shouldAddSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 2", "Testing epic 2");
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Testing Subtaks 1", Status.DONE, 1,
                Duration.ofMinutes(5), LocalDateTime.now());

        String taskJson = gson.toJson(subtask);

        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Код ответа неверный");

        Collection<Subtask> tasksFromManager = taskManager.getSubtask();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Subtask 1", tasksFromManager.iterator().next().getNameTask(),
                "Некорректное имя задачи");
        assertEquals(epic.getSubtaskIdToEpic().getFirst(), tasksFromManager.iterator().next().getId(),
                "Неверный id в хранилище подзадач эпика");
    }

    @Test
    public void shouldGetSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 2", "Testing epic 2");
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Testing Subtaks 1", Status.DONE, 1,
                Duration.ofMinutes(5), LocalDateTime.now());
        taskManager.addSubtask(subtask);

        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Код ответа неверный");

        assertNotNull(response.body(), "Задачи не возвращаются");
        assertTrue(response.body().split(",")[2].contains("Subtask 1"), "Некорректное имя задачи");
    }

    @Test
    public void shouldUpdateSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 2", "Testing epic 2");
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Testing Subtask 1", Status.DONE, 1,
                Duration.ofMinutes(5), LocalDateTime.now());
        taskManager.addSubtask(subtask);

        Subtask subtaskUpdate = new Subtask("Subtask Update", "Testing Subtask update", Status.DONE, 1,
                Duration.ofMinutes(5), LocalDateTime.now());
        subtaskUpdate.setId(subtask.getId());
        String taskJson = gson.toJson(subtaskUpdate);

        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Код ответа неверный");

        Collection<Subtask> tasksFromManager = taskManager.getSubtask();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Subtask Update", tasksFromManager.iterator().next().getNameTask(),
                "Некорректное имя задачи");
    }

    @Test
    public void shouldGetSubtaskById() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 2", "Testing epic 2");
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Testing Subtaks 1", Status.DONE, 1,
                Duration.ofMinutes(5), LocalDateTime.now());
        taskManager.addSubtask(subtask);

        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Код ответа неверный");

        assertNotNull(response.body(), "Задачи не возвращаются");
        assertTrue(response.body().split(",")[2].contains("Subtask 1"), "Некорректное имя задачи");
    }

    @Test
    public void shouldDeleteSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 2", "Testing epic 2");
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Testing Subtaks 1", Status.DONE, 1,
                Duration.ofMinutes(5), LocalDateTime.now());
        taskManager.addSubtask(subtask);

        Subtask subtaskSave = new Subtask("Test Save", "Testing save", Status.DONE, 1,
                Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(10));

        taskManager.addSubtask(subtaskSave);

        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Код ответа неверный");

        Collection<Subtask> tasksFromManager = taskManager.getSubtask();
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test Save", tasksFromManager.stream().iterator().next().getNameTask(),
                "Некорректное имя задачи");
        assertEquals(epic.getSubtaskIdToEpic().getFirst(), tasksFromManager.iterator().next().getId(),
                "Неверный id в хранилище подзадач эпика");
    }
}
