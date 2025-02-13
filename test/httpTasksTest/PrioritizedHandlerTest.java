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
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static utils.Managers.getDefault;

public class PrioritizedHandlerTest {

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
    public void shouldCorrectPrioritizedTasks() throws IOException, InterruptedException {
        Task task = new Task("Task 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        taskManager.addTask(task);

        Epic epic = new Epic("Epic 2", "Testing epic 2");
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Testing Subtaks 1", Status.DONE, 2,
                Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(10));
        taskManager.addSubtask(subtask);

        URI urlPrioritized = URI.create("http://localhost:8080/prioritized");
        HttpRequest requestPrioritized = HttpRequest.newBuilder()
                .uri(urlPrioritized)
                .GET()
                .build();

        HttpResponse<String> responsePrioritized = client.send(requestPrioritized, HttpResponse.BodyHandlers.ofString());
        JsonArray jsonArray = JsonParser.parseString(responsePrioritized.body()).getAsJsonArray();

        assertEquals(200, responsePrioritized.statusCode(), "Код ответа неверный");
        assertNotNull(responsePrioritized.body(), "Список задач по приоритету не возвращается");
        assertEquals("Task 2", jsonArray.get(0).getAsJsonObject().get("nameTask").getAsString(),
                "Неправильный порядок задач по приоритету");
        assertEquals("Subtask 1", jsonArray.get(1).getAsJsonObject().get("nameTask").getAsString(),
                "Неправильный порядок задач по приоритету");

        TreeSet<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        assertNotNull(prioritizedTasks, "Список задач по приоритету в менеджере пустой");
        assertEquals(2, prioritizedTasks.size(), "Некорректное количество задач");

    }
}