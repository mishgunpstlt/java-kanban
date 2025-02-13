package httpTasksTest;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static utils.Managers.getDefault;

public class HistoryHandlerTest {

    private final HttpClient client = HttpClient.newHttpClient();
    TaskManager taskManager = getDefault();
    HttpTaskServer httpServer = new HttpTaskServer(taskManager);

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
    public void shouldCorrectHistory() throws IOException, InterruptedException {
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

        URI urlEpic = URI.create("http://localhost:8080/epics/2");
        HttpRequest requestEpic = HttpRequest.newBuilder()
                .uri(urlEpic)
                .GET()
                .build();

        URI urlSubtask = URI.create("http://localhost:8080/subtasks/3");
        HttpRequest requestSubtask = HttpRequest.newBuilder()
                .uri(urlSubtask)
                .GET()
                .build();

        client.send(requestEpic, HttpResponse.BodyHandlers.ofString());
        client.send(requestSubtask, HttpResponse.BodyHandlers.ofString());
        client.send(requestTask, HttpResponse.BodyHandlers.ofString());

        URI urlHistory = URI.create("http://localhost:8080/history");
        HttpRequest requestHistory = HttpRequest.newBuilder()
                .uri(urlHistory)
                .GET()
                .build();

        HttpResponse<String> responseHistory = client.send(requestHistory, HttpResponse.BodyHandlers.ofString());
        JsonArray jsonArray = JsonParser.parseString(responseHistory.body()).getAsJsonArray();

        assertEquals(200, responseHistory.statusCode(), "Код ответа неверный");
        assertNotNull(responseHistory.body(), "История не возвращается");
        assertEquals("Epic 2", jsonArray.get(0).getAsJsonObject().get("nameTask").getAsString(),
                "Неправильный порядок задач в истории");
        assertEquals("Subtask 1", jsonArray.get(1).getAsJsonObject().get("nameTask").getAsString(),
                "Неправильный порядок задач в истории");
        assertEquals("Task 2", jsonArray.get(2).getAsJsonObject().get("nameTask").getAsString(),
                "Неправильный порядок задач в истории");

        List<Task> history = taskManager.getHistory();
        assertNotNull(history, "История менеджера пустая");
        assertEquals(3, history.size(), "Некорректное количество задач");

    }
}