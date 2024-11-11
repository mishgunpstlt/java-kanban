package Tasks;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;


public class TaskManager {

    public HashMap<Integer, Task> tasks = new HashMap<>();
    public HashMap<Integer, Epic> epics = new HashMap<>();
    public HashMap<Integer, Subtask> subtasks = new HashMap<>();
    public int idNext = 1;

    public void addTask(Task task) {
        task.id = idNext;
        tasks.put(task.id, task);
        idNext++;
    }

    public void addEpic(Epic epic) {
        epic.id = idNext;
        epics.put(epic.id, epic);
        idNext++;
    }

    public void addSubtask(Subtask subtask) {
        subtask.id = idNext;
        subtasks.put(subtask.id, subtask);
        idNext++;

        Epic epic = epics.get(subtask.epicId);
        epic.epicsId.add(subtask.id);
        updateEpicStatus(epic);
    }

    public Collection<Task> getTask() {
        return tasks.values();
    }

    public Collection<Epic> getEpic() {
        return epics.values();
    }

    public Collection<Subtask> getSubtask() {
        return subtasks.values();
    }

    public void updateEpicStatus(Epic epic) {
        boolean allSubtaskNEW = true;
        boolean allSubtaskDONE = true;
        for (Integer subtaskId : epic.epicsId) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask != null && !subtask.statusSubtask.equals("NEW")) {
                allSubtaskNEW = false;
            }
            if (!subtask.statusSubtask.equals("DONE")) {
                allSubtaskDONE = false;
            }
        }
        if (allSubtaskNEW) {
            epic.statusEpic = "NEW";
        } else if (allSubtaskDONE) {
            epic.statusEpic = "DONE";
        } else {
            epic.statusEpic = "IN_PROGRESS";
        }
    }

    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            return task;
        } else {
            return null;
        }
    }

    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            return subtask;
        } else {
            return null;
        }
    }

    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            return epic;
        } else {
            return null;
        }
    }

    public void removeTask() {
        tasks.clear();
    }

    public void removeSubtask() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.epicsId.clear();
            epic.statusTask = "NEW";
        }
    }

    public void removeEpic() {
        epics.clear();
        subtasks.clear();
    }

    public void updateTask(int id, Task newTask) {
        newTask.id = id;
        tasks.put(newTask.id, newTask);
    }

    public void updateEpic(int id, Epic newEpic) {
        Epic oldEpic = epics.get(id);
        ArrayList<Integer> epicsId = new ArrayList<>(oldEpic.epicsId);

        newEpic.id = id;
        newEpic.epicsId = epicsId;
        epics.put(newEpic.id, newEpic);
        updateEpicStatus(newEpic);
    }

    public void updateSubtask(int id, Subtask newSubtask) {
        Subtask oldSubtask = subtasks.get(id);

        int epicId = oldSubtask.epicId;
        newSubtask.id = id;
        newSubtask.epicId = epicId;
        subtasks.put(newSubtask.id, newSubtask);

        Epic epic = epics.get(epicId);

        updateEpicStatus(epic);
    }

    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    public void removeEpicById(int id) {
        Epic epic = epics.get(id);

        for (Integer subtaskId : epic.epicsId) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask != null) {
                subtasks.remove(subtaskId);
            }
        }
        epic.epicsId.clear();
        epics.remove(id);
    }

    public void removeSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);

        if (subtask == null) {
            return;
        }

        Epic epic = epics.get(subtask.epicId);

        epic.epicsId.remove(Integer.valueOf(id));
        subtasks.remove(id);
        updateEpicStatus(epic);
    }

    public Collection<Subtask> getSubtaskByEpic(Epic epic) {
        Collection<Subtask> subtaskCollection = new ArrayList<>();

        for (Integer subtaskId : epic.epicsId) {
            Subtask subtask = subtasks.get(subtaskId);
            subtaskCollection.add(subtask);
        }
        return subtaskCollection;
    }
}
