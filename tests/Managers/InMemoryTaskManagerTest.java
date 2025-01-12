package Managers;

import Tasks.Epic;
import Tasks.Status;
import Tasks.Subtask;
import Tasks.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Managers.getDefault;

class InMemoryTaskManagerTest {

    TaskManager taskManager = getDefault();

    @Test
    void shouldAddNewTask() {
        Task task = new Task("Купить квартиру", "В москве", Status.NEW);
        int taskId = taskManager.addTask(task);

        Task savedTask = taskManager.getTaskById(taskId);

        Assertions.assertNotNull(savedTask, "Задача не найдена.");
        Assertions.assertEquals(task, savedTask, "Задачи не совпадают.");

        final Collection<Task> tasks = taskManager.getTask();
        ArrayList<Task> list = new ArrayList<>(tasks);

        Assertions.assertNotNull(tasks, "Задачи не возвращаются.");
        Assertions.assertEquals(1, tasks.size(), "Неверное количество задач.");
        Assertions.assertEquals(task, list.get(0), "Задачи не совпадают.");
    }

    @Test
    void shouldAddNewEpic() {
        Epic epic = new Epic("БАК", "Подготовить доклад+презентацию к понедельнику");
        int epicId = taskManager.addEpic(epic);

        Subtask thisEpicToSubtask = new Subtask(epic.getNameTask(), epic.getDescriptionTask(), Status.NEW, epicId);
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
    }

    @Test
    void shouldAddNewSubtask() {
        Epic epic = new Epic("БАК", "Подготовить доклад+презентацию к понедельнику");
        int epicId = taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Подготовить доклад", "доклад к понедельнику", Status.NEW, epicId);
        int subtaskId = taskManager.addSubtask(subtask);

        Subtask savedSubtask = taskManager.getSubtaskById(subtaskId);

        Assertions.assertNotNull(savedSubtask, "Задача не найдена.");
        Assertions.assertEquals(subtask, savedSubtask, "Задачи не совпадают.");

        final Collection<Subtask> subtasks = taskManager.getSubtask();
        ArrayList<Task> list = new ArrayList<>(subtasks);

        Assertions.assertNotNull(subtasks, "Задачи не возвращаются.");
        Assertions.assertEquals(1, subtasks.size(), "Неверное количество задач.");
        Assertions.assertEquals(subtask, list.get(0), "Задачи не совпадают.");
    }
}