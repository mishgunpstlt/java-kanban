package Managers;

import Tasks.Epic;
import Tasks.Status;
import Tasks.Subtask;
import Tasks.Task;
import utils.Managers;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    private Map<Integer, Task> tasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();
    private Map<Integer, Subtask> subtasks = new HashMap<>();
    private int idNext = 1;

    private final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public int addTask(Task task) {
        task.setId(idNext);
        tasks.put(task.getId(), task);
        idNext++;
        return task.getId();
    }

    @Override
    public int addEpic(Epic epic) {
        epic.setId(idNext);
        epics.put(epic.getId(), epic);
        idNext++;
        return epic.getId();
    }

    @Override
    public int addSubtask(Subtask subtask) {
        if (epics.containsKey(subtask.getEpicId())) {
            if (subtask.getEpicId() == subtask.getId()) {
                return -1;
            }
            subtask.setId(idNext);
            subtasks.put(subtask.getId(), subtask);
            idNext++;

            Epic epic = epics.get(subtask.getEpicId());
            epic.addSubtask(subtask);
            updateEpicStatus(epic);
            return subtask.getId();
        }
        return -1;
    }

    @Override
    public Collection<Task> getTask() {
        return tasks.values();
    }

    @Override
    public Collection<Epic> getEpic() {
        return epics.values();
    }

    @Override
    public Collection<Subtask> getSubtask() {
        return subtasks.values();
    }

    @Override
    public void updateEpicStatus(Epic epic) {
        boolean allSubtaskNEW = true;
        boolean allSubtaskDONE = true;
        for (Integer subtaskId : epic.getSubtaskIdToEpic()) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask != null && !subtask.getStatusTask().equals(Status.NEW)) {
                allSubtaskNEW = false;
            }
            if (!subtask.getStatusTask().equals(Status.DONE)) {
                allSubtaskDONE = false;
            }
        }
        if (allSubtaskNEW) {
            epic.setStatusTask(Status.NEW);
        } else if (allSubtaskDONE) {
            epic.setStatusTask(Status.DONE);
        } else {
            epic.setStatusTask(Status.IN_PROGRESS);
        }
    }

    @Override
    public Task getTaskById(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public void removeTask() {
        tasks.clear();
    }

    @Override
    public void removeSubtask() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.removeAllSubtask();
            updateEpicStatus(epic);
        }
    }

    @Override
    public void removeEpic() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void updateTask(Task newTask) {
        if (tasks.containsKey(newTask.getId())) {
            tasks.put(newTask.getId(), newTask);
        }
    }

    @Override
    public void updateEpic(Epic newEpic) {
        Epic oldEpic = epics.get(newEpic.getId());
        if (oldEpic != null) {
            oldEpic.setNameTask(newEpic.getNameTask());
            oldEpic.setDescriptionTask(newEpic.getDescriptionTask());
        }
    }

    @Override
    public void updateSubtask(Subtask newSubtask) {
        if (subtasks.containsKey(newSubtask.getId())) {
            if (subtasks.get(newSubtask.getId()).getEpicId() == newSubtask.getEpicId()) {
                subtasks.put(newSubtask.getId(), newSubtask);
                Epic epic = epics.get(newSubtask.getEpicId());
                updateEpicStatus(epic);
            }
        }
    }

    @Override
    public void removeTaskById(int id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            for (Integer subtaskId : epic.getSubtaskIdToEpic()) {
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);

            }
            epics.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void removeSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());

            epic.removeSubtask(subtask);
            subtasks.remove(id);
            historyManager.remove(id);
            updateEpicStatus(epic);
        }
    }

    @Override
    public Collection<Subtask> getSubtaskByEpic(Epic epic) {
        Collection<Subtask> subtaskCollection = new ArrayList<>();

        for (Integer subtaskId : epic.getSubtaskIdToEpic()) {
            Subtask subtask = subtasks.get(subtaskId);
            subtaskCollection.add(subtask);
        }
        return subtaskCollection;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
