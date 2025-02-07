package managers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static utils.Managers.getDefaultHistory;

class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;
    private Task task1;
    private Task task2;
    private Epic epic;
    private Subtask subtask;

    @BeforeEach
    void setUp() {
        historyManager = getDefaultHistory();

        task1 = new Task("Купить квартиру", "В москве", Status.NEW, Duration.ofMinutes(50), LocalDateTime.of(2025, 2, 1, 15, 0));
        task1.setId(1);

        task2 = new Task("Купить квартиру", "В москве", Status.NEW, Duration.ofMinutes(50), LocalDateTime.of(2025, 2, 1, 15, 0));
        task2.setId(2);

        epic = new Epic("Epic 1", "Epic Description");
        epic.setId(3);

        subtask = new Subtask("Подгот312312овить доклад", "доклад к понедельнику", Status.NEW, epic.getId(), Duration.ofMinutes(50), LocalDateTime.of(2025, 2, 11, 15, 0));
        subtask.setId(4);
    }

    @Test
    void shouldAddTasksToHistory() {
        historyManager.add(task1);
        historyManager.add(task2);

        List<Task> history = historyManager.getHistory();
        Assertions.assertEquals(2, history.size(), "История не должна быть пустой");
        Assertions.assertEquals(task1, history.get(0), "Не возвращается в нужном порядке");
        Assertions.assertEquals(task2, history.get(1), "Не возвращается в нужном порядке");
    }

    @Test
    void shouldNoDuplicatesInHistory() {
        historyManager.add(task1);
        historyManager.add(task1);

        List<Task> history = historyManager.getHistory();
        Assertions.assertEquals(1, history.size(), "Не должна содержать дубликаты");
    }

    @Test
    void shouldEmptyHistory() {
        List<Task> history = historyManager.getHistory();
        Assertions.assertTrue(history.isEmpty(), "История должна быть пустой");
    }

    @Test
    void shouldRemoveFirstTaskFromHistory() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.remove(task1.getId());

        List<Task> history = historyManager.getHistory();
        Assertions.assertEquals(1, history.size(), "Размер истории после удаления задачи должен уменьшаться");
        Assertions.assertEquals(task2, history.get(0));
    }

    @Test
    void shouldRemoveLastTaskFromHistory() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.remove(task2.getId());

        List<Task> history = historyManager.getHistory();
        Assertions.assertEquals(1, history.size(), "Размер истории после удаления задачи должен уменьшаться");
        Assertions.assertEquals(task1, history.get(0));
    }

    @Test
    void shouldRemoveMiddleTaskFromHistory() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(subtask);

        historyManager.remove(task2.getId());

        List<Task> history = historyManager.getHistory();
        Assertions.assertEquals(2, history.size(), "Размер истории после удаления задачи должен уменьшаться");
        Assertions.assertEquals(task1, history.get(0));
        Assertions.assertEquals(subtask, history.get(1));
    }

}