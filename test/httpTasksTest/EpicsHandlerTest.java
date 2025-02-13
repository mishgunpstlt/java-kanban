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

public class EpicsHandlerTest {

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
    public void shouldAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 2", "Testing epic 2");

        String taskJson = gson.toJson(epic);

        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Код ответа неверный");

        Collection<Epic> tasksFromManager = taskManager.getEpic();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Epic 2", tasksFromManager.iterator().next().getNameTask(),
                "Некорректное имя задачи");
    }

    @Test
    public void shouldGetEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 2", "Testing epic 2");

        taskManager.addEpic(epic);

        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Код ответа неверный");

        assertNotNull(response.body(), "Задачи не возвращаются");
        assertTrue(response.body().split(",")[2].contains("Epic 2"), "Некорректное имя задачи");
    }

    @Test
    public void shouldUpdateEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 2", "Testing epic 2");

        taskManager.addEpic(epic);

        Epic epicUpdate = new Epic("Epic Update", "Testing epic update");
        epicUpdate.setId(epic.getId());
        String taskJson = gson.toJson(epicUpdate);

        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Код ответа неверный");

        Collection<Epic> tasksFromManager = taskManager.getEpic();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Epic Update", tasksFromManager.iterator().next().getNameTask(),
                "Некорректное имя задачи");
    }

    @Test
    public void shouldGetEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 2", "Testing epic 2");

        taskManager.addEpic(epic);

        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Код ответа неверный");

        assertNotNull(response.body(), "Задачи не возвращаются");
        assertTrue(response.body().split(",")[2].contains("Epic 2"),
                "Некорректное имя задачи");
    }

    @Test
    public void shouldDeleteEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 2", "Testing epic 2");

        taskManager.addEpic(epic);

        Epic epicSave = new Epic("Test Save", "Testing epic 1");

        taskManager.addEpic(epicSave);

        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Код ответа неверный");

        Collection<Epic> tasksFromManager = taskManager.getEpic();
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test Save", tasksFromManager.iterator().next().getNameTask(),
                "Некорректное имя задачи");
    }

    @Test
    public void shouldGetSubtasksByEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 2", "Testing epic 2");
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Testing Subtaks 1", Status.DONE, 1,
                Duration.ofMinutes(5), LocalDateTime.now());
        taskManager.addSubtask(subtask);

        Subtask subtaskSave = new Subtask("Test Save", "Testing save", Status.DONE, 1,
                Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(10));
        taskManager.addSubtask(subtaskSave);

        URI url = URI.create("http://localhost:8080/epics/1/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Код ответа неверный");

        assertNotNull(response.body(), "Задачи не возвращаются");
        assertTrue(response.body().contains("Subtask 1"), "Некорректное имя задачи");
        assertTrue(response.body().contains("Test Save"), "Некорректное имя задачи");
    }
}
