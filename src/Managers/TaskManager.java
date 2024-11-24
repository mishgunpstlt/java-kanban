package Managers;

import Tasks.Epic;
import Tasks.Subtask;
import Tasks.Task;

import java.util.Collection;
import java.util.List;

public interface TaskManager {
    int addTask(Task task);

    int addEpic(Epic epic);

    int addSubtask(Subtask subtask);

    Collection<Task> getTask();

    Collection<Epic> getEpic();

    Collection<Subtask> getSubtask();

    void updateEpicStatus(Epic epic);

    Task getTaskById(int id);

    Subtask getSubtaskById(int id);

    Epic getEpicById(int id);

    void removeTask();

    void removeSubtask();

    void removeEpic();

    void updateTask(Task newTask);

    void updateEpic(Epic newEpic);

    void updateSubtask(Subtask newSubtask);

    void removeTaskById(int id);

    void removeEpicById(int id);

    void removeSubtaskById(int id);

    Collection<Subtask> getSubtaskByEpic(Epic epic);

    List<Task> getHistory();
}
