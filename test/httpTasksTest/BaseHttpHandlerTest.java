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
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Managers.getDefault;

public class BaseHttpHandlerTest {

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
    public void shouldCorrectSendStatusCode() throws IOException, InterruptedException {
        Task task = new Task("Task 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        taskManager.addTask(task);

        Epic epic = new Epic("Epic 2", "Testing epic 2");
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Testing Subtaks 1", Status.DONE, 2,
                Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(10));
        taskManager.addSubtask(subtask);

        URI urlTask = URI.create("http://localhost:8080/tasks/1");
        HttpRequest requestTask = HttpRequest.newBuilder()
                .uri(urlTask)
                .GET()
                .build();

        URI urlEpic = URI.create("http://localhost:8080/epics/10");
        HttpRequest requestEpic = HttpRequest.newBuilder()
                .uri(urlEpic)
                .GET()
                .build();

        URI urlSubtask = URI.create("http://localhost:8080/subtasks/3");
        HttpRequest requestSubtask = HttpRequest.newBuilder()
                .uri(urlSubtask)
                .GET()
                .build();

        Task taskErr = new Task("Test Err", "Testing task err",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        String taskJson = gson.toJson(taskErr);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest requestErr = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        Task taskCorrect = new Task("Test Correct", "Testing task Correct",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(20));
        String taskJsonCor = gson.toJson(taskCorrect);

        URI urlCor = URI.create("http://localhost:8080/tasks");
        HttpRequest requestCorrect = HttpRequest.newBuilder()
                .uri(urlCor)
                .POST(HttpRequest.BodyPublishers.ofString(taskJsonCor))
                .build();

        HttpResponse<String> responseTask = client.send(requestTask, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responseEpic = client.send(requestEpic, HttpResponse.BodyHandlers.ofString());
        client.send(requestSubtask, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responseErr = client.send(requestErr, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responseCor = client.send(requestCorrect, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseTask.statusCode(), "Код ответа неверный");
        assertEquals(404, responseEpic.statusCode(), "Код ответа неверный");
        assertEquals(406, responseErr.statusCode(), "Код ответа неверный");
        assertEquals(201, responseCor.statusCode(), "Код ответа неверный");
    }
}