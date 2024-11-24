package Managers;

import Tasks.Epic;
import Tasks.Status;
import Tasks.Subtask;
import Tasks.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;
import static utils.Managers.getDefault;

class InMemoryHistoryManagerTest {

    TaskManager taskManager = getDefault();

    @Test
    void shouldSaveHistoryTask() {
        Task task1 = new Task("Купить квартиру", "В москве", Status.NEW);
        Task task2 = new Task("Купить квартиру", "В москве", Status.NEW);
        Task task3 = new Task("Купить квартиру", "В москве", Status.NEW);
        Task task4 = new Task("Купить квартиру", "В москве", Status.NEW);
        Task task5 = new Task("Купить квартиру", "В москве", Status.NEW);
        Task task6 = new Task("Купить квартиру", "В москве", Status.NEW);
        Task task7 = new Task("Купить квартиру", "В москве", Status.NEW);
        Task task8 = new Task("Купить квартиру", "В москве", Status.NEW);
        Task task9 = new Task("Купить квартиру", "В москве", Status.NEW);
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
        taskManager.addTask(task4);
        taskManager.addTask(task5);
        taskManager.addTask(task6);
        taskManager.addTask(task7);
        taskManager.addTask(task8);
        taskManager.addTask(task9);


        Epic epic = new Epic("БАК", "Подготовить доклад+презентацию к понедельнику");
        int epicId = taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Подготовить доклад", "доклад к понедельнику", Status.NEW, epicId);
        taskManager.addSubtask(subtask);

        taskManager.getTaskById(1);
        Task task = taskManager.getTaskById(2);
        taskManager.getTaskById(3);
        taskManager.getTaskById(4);
        taskManager.getTaskById(5);
        taskManager.getTaskById(6);
        taskManager.getTaskById(7);
        taskManager.getTaskById(8);
        taskManager.getTaskById(9);
        taskManager.getEpicById(10);
        taskManager.getSubtaskById(11);

        System.out.println("История: " + taskManager.getHistory());

        Assertions.assertEquals(task, taskManager.getHistory().get(0), "Задача в истории не совпадает с версией просмотренной задачи");
        Assertions.assertTrue(taskManager.getHistory().size() <= 10, "Размер истории не должен превышать 10 элементов");
        Assertions.assertNotNull(taskManager.getHistory(), "История пустая");
    }
  
}