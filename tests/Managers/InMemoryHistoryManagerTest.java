package Managers;

import Tasks.Epic;
import Tasks.Status;
import Tasks.Subtask;
import Tasks.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static utils.Managers.getDefault;

class InMemoryHistoryManagerTest {

    TaskManager taskManager = getDefault();

    @Test
    void shouldSaveHistoryTask() {
        Task task1 = new Task("Купить квартиру", "В москве", Status.NEW);
        Task task2 = new Task("Купить квартиру", "В москве", Status.NEW);
        Task task3 = new Task("Купить квартиру", "В москве", Status.NEW);
        Task task4 = new Task("Купить квартиру", "В москве", Status.NEW);
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
        taskManager.addTask(task4);

        Epic epic = new Epic("БАК", "Подготовить доклад+презентацию к понедельнику");
        int epicId = taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Подготовить доклад", "доклад к понедельнику", Status.NEW, epicId);
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
    }
}