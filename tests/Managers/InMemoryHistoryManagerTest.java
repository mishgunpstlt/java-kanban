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

        Collection<Task> tasks = taskManager.getTask();
        ArrayList<Task> listTask = new ArrayList<>(tasks);
        Collection<Epic> epics = taskManager.getEpic();
        Collection<Subtask> subtasks = taskManager.getSubtask();

        Assertions.assertEquals(listTask.get(1), taskManager.getHistoryManager().getHistory().get(0), "Задача в истории не совпадает с версией просмотренной задачи");
        Assertions.assertNotEquals(11, taskManager.getHistoryManager().getHistory().size(), "Размер не должен быть больше 10");
        Assertions.assertNotNull(taskManager.getHistoryManager().getHistory(), "История пустая");


    }
  
}