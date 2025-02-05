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
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;

    protected abstract T createTaskManager();

    @BeforeEach
    public void setUp() {
        taskManager = createTaskManager();
    }

    @Test
    void shouldCorrectTask() {
        Task task = new Task("Купить квартиру", "В москве", Status.NEW, Duration.ofMinutes(50), LocalDateTime.of(2025, 2, 4, 15, 0));
        int taskId = taskManager.addTask(task);

        Task savedTask = taskManager.getTaskById(taskId);

        Assertions.assertNotNull(savedTask, "Задача не найдена.");
        Assertions.assertEquals(task, savedTask, "Задачи не совпадают.");

        final Collection<Task> tasks = taskManager.getTask();
        ArrayList<Task> list = new ArrayList<>(tasks);

        Assertions.assertNotNull(tasks, "Задачи не возвращаются.");
        Assertions.assertEquals(1, tasks.size(), "Неверное количество задач.");
        Assertions.assertEquals(task, list.get(0), "Задачи не совпадают.");

        taskManager.removeTask();
        Assertions.assertTrue(taskManager.getTask().isEmpty(), "Задачи должны быть удалены");
    }

    @Test
    void shouldCorrectEpic() {
        Epic epic = new Epic("БАК", "Подготовить доклад+презентацию к понедельнику");
        int epicId = taskManager.addEpic(epic);

        Subtask thisEpicToSubtask = new Subtask(epic.getNameTask(), epic.getDescriptionTask(), Status.NEW, epicId, Duration.ofMinutes(50), LocalDateTime.of(2025, 2, 4, 15, 0));
        thisEpicToSubtask.setId(epicId);

        assertEquals(-1, taskManager.addSubtask(thisEpicToSubtask), "Эпик должен отклонять добавление самого себя в виде подзадачи.");

        Epic savedEpic = taskManager.getEpicById(epicId);

        Assertions.assertNotNull(savedEpic, "Задача не найдена.");
        Assertions.assertEquals(epic, savedEpic, "Задачи не совпадают.");

        final Collection<Epic> epics = taskManager.getEpic();
        ArrayList<Task> list = new ArrayList<>(epics);

        Assertions.assertNotNull(epics, "Задачи не возвращаются.");
        Assertions.assertEquals(1, epics.size(), "Неверное количество задач.");
        Assertions.assertEquals(epic, list.get(0), "Задачи не совпадают.");

        taskManager.removeEpic();
        Assertions.assertTrue(taskManager.getEpic().isEmpty(), "Эпики должны быть удалены");
    }

    @Test
    void shouldCorrectSubtask() {
        Epic epic = new Epic("БАК", "Подготовить доклад+презентацию к понедельнику");
        int epicId = taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Подготовить доклад", "доклад к понедельнику", Status.NEW, epicId, Duration.ofMinutes(50), LocalDateTime.of(2025, 2, 5, 15, 0));
        taskManager.addSubtask(subtask);

        Subtask savedSubtask = taskManager.getSubtaskById(subtask.getId());

        Assertions.assertNotNull(savedSubtask, "Задача не найдена.");
        Assertions.assertEquals(subtask, savedSubtask, "Задачи не совпадают.");

        final Collection<Subtask> subtasks = taskManager.getSubtask();
        ArrayList<Task> list = new ArrayList<>(subtasks);

        Assertions.assertNotNull(subtasks, "Задачи не возвращаются.");
        Assertions.assertEquals(1, subtasks.size(), "Неверное количество задач.");
        Assertions.assertEquals(subtask, list.get(0), "Задачи не совпадают.");

        taskManager.removeSubtask();
        Assertions.assertTrue(taskManager.getSubtask().isEmpty(), "Эпики должны быть удалены");
    }

    @Test
    void shouldSaveHistoryTask() {
        Task task1 = new Task("Купить квартиру", "В москве", Status.NEW, Duration.ofMinutes(50), LocalDateTime.of(2025, 2, 4, 15, 0));
        Task task2 = new Task("Купить квартиру", "В москве", Status.NEW, Duration.ofMinutes(50), LocalDateTime.of(2025, 2, 5, 15, 0));
        Task task3 = new Task("Купить квартиру", "В москве", Status.NEW, Duration.ofMinutes(50), LocalDateTime.of(2025, 2, 6, 15, 0));
        Task task4 = new Task("Купить квартиру", "В москве", Status.NEW, Duration.ofMinutes(50), LocalDateTime.of(2025, 2, 7, 15, 0));
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
        taskManager.addTask(task4);

        Epic epic = new Epic("БАК", "Подготовить доклад+презентацию к понедельнику");
        int epicId = taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Подготовить доклад", "доклад к понедельнику", Status.NEW, epicId, Duration.ofMinutes(50), LocalDateTime.of(2025, 2, 8, 15, 0));
        taskManager.addSubtask(subtask);

        taskManager.getTaskById(1);
        Task taskId2 = taskManager.getTaskById(2);
        taskManager.getTaskById(3);
        taskManager.getTaskById(4);
        taskManager.getTaskById(1);
        taskManager.getEpicById(5);
        Task taskId6 = taskManager.getSubtaskById(6);

        Assertions.assertEquals(taskId2, taskManager.getHistory().get(0), "Задача в истории не совпадает с версией просмотренной задачи");
        Assertions.assertNotNull(taskManager.getHistory(), "История пустая");
        Assertions.assertEquals(6, taskManager.getHistory().size(), "История должна быть без дубликатов задач");
        Assertions.assertEquals(taskId6, taskManager.getHistory().getLast(), "Последний элемент в истории - последний просмотренный");

        taskManager.removeEpicById(epicId);
        Assertions.assertEquals(4, taskManager.getHistory().size(), "История должна уменьшаться после удаления");
        Assertions.assertFalse(taskManager.getHistory().contains(subtask), "При удалении эпика должны удаляться и его подзадачи из истории");

        taskManager.removeEpic();
        Assertions.assertEquals(4, taskManager.getHistory().size(), "При удаление всех эпиков, должны удаляться и подзадачи");
    }

    @Test
    void shouldCorrectEpicStatus() {
        Epic epic = new Epic("БАК", "Подготовить доклад+презентацию к понедельнику");
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Подготовить доклад", "доклад к понедельнику", Status.NEW, epic.getId(), Duration.ofMinutes(50), LocalDateTime.of(2025, 2, 3, 15, 0));
        Subtask subtask2 = new Subtask("Подготовить доклад", "доклад к понедельнику", Status.NEW, epic.getId(), Duration.ofMinutes(50), LocalDateTime.of(2025, 2, 4, 15, 0));
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        Assertions.assertEquals(Status.NEW, epic.getStatusTask(), "Подзадачи эпика со статусом NEW, значит эпик должен быть со статусом NEW");

        subtask1.setStatusTask(Status.DONE);
        taskManager.updateEpicStatus(epic);
        Assertions.assertEquals(Status.IN_PROGRESS, epic.getStatusTask(), "Подзадачи эпика со статусами NEW и DONE, значит эпик должен быть со статусом IN_PROGRESS");

        subtask2.setStatusTask(Status.DONE);
        taskManager.updateEpicStatus(epic);
        Assertions.assertEquals(Status.DONE, epic.getStatusTask(), "Подзадачи эпика со статусом DONE, значит эпик должен быть со статусом DONE");

        subtask1.setStatusTask(Status.IN_PROGRESS);
        subtask2.setStatusTask(Status.IN_PROGRESS);
        taskManager.updateEpicStatus(epic);
        Assertions.assertEquals(Status.IN_PROGRESS, epic.getStatusTask(), "Подзадачи эпика со статусом IN_PROGRESS, значит эпик должен быть со статусом IN_PROGRESS");
    }

    @Test
    void shouldCaclStartTimeDurationEpic() {
        Epic epic = new Epic("БАК", "Подготовить доклад+презентацию к понедельнику");
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Подготовить доклад", "доклад к понедельнику", Status.NEW, epic.getId(), Duration.ofMinutes(50), LocalDateTime.of(2025, 2, 3, 15, 0));
        Subtask subtask2 = new Subtask("Подготовить доклад", "доклад к понедельнику", Status.NEW, epic.getId(), Duration.ofMinutes(50), LocalDateTime.of(2025, 2, 4, 15, 0));
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        Assertions.assertEquals(LocalDateTime.of(2025, 2, 3, 15, 0), epic.getStartTime(), "Время начала — дата старта самой ранней подзадачи");
        Assertions.assertEquals(Duration.ofMinutes(100), epic.getDuration(), "Продолжительность эпика — сумма продолжительностей всех его подзадач");
    }

    @Test
    void shouldAddGetPrioritizedTasks() {
        Task task = new Task("Купить квартиру", "В москве", Status.NEW, Duration.ofMinutes(50), LocalDateTime.of(2025, 2, 4, 16, 0));
        taskManager.addTask(task);
        Epic epic = new Epic("БАК", "Подготовить доклад+презентацию к понедельнику");
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Подготовить доклад", "доклад к понедельнику", Status.NEW, epic.getId(), Duration.ofMinutes(50), LocalDateTime.of(2025, 2, 3, 15, 0));
        Subtask subtask2 = new Subtask("Подготовить доклад", "доклад к понедельнику", Status.NEW, epic.getId(), Duration.ofMinutes(50), LocalDateTime.of(2025, 2, 4, 15, 0));
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        Assertions.assertNotNull(taskManager.getPrioritizedTasks(), "Список задач по приоритету не возвращаются");
        Assertions.assertEquals(taskManager.getPrioritizedTasks().getFirst(), subtask1, "Список задач должен быть в порядке приоритета");
        Assertions.assertEquals(taskManager.getPrioritizedTasks().getLast(), task, "Список задач должен быть в порядке приоритета");
    }

    @Test
    void shouldNotOverlayTask() {
        Task task1 = new Task("Купить квартиру", "В москве", Status.NEW, Duration.ofMinutes(50), LocalDateTime.of(2025, 2, 21, 15, 0));
        Task task2 = new Task("Купить квартиру", "В москве", Status.NEW, Duration.ofMinutes(50), LocalDateTime.of(2025, 2, 21, 15, 0));
        int idTask1 = taskManager.addTask(task1);
        int idTask2 = taskManager.addTask(task2);
        Assertions.assertNull(taskManager.getTaskById(idTask2), "Задачи не должны пересекаться");
    }
}
