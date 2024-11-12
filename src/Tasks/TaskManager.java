package Tasks;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;


public class TaskManager {

    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int idNext = 1;

    public void addTask(Task task) {
        task.id = idNext;
        tasks.put(task.getId(), task);
        idNext++;
        System.out.println("ID: " + task.getId() + ", nameTask: " + task.getNameTask());
    }

    public void addEpic(Epic epic) {
        epic.id = idNext;
        epics.put(epic.getId(), epic);
        idNext++;
        System.out.println("ID: " + epic.getId() + ", nameEpic: " + epic.getNameTask());
    }

    public void addSubtask(Subtask subtask) {
        if (epics.containsKey(subtask.getEpicId())) {
            subtask.id = idNext;
            subtasks.put(subtask.getId(), subtask);
            idNext++;

            Epic epic = epics.get(subtask.getEpicId());
            epic.addSubtask(subtask);
            updateEpicStatus(epic);
            System.out.println("ID: " + subtask.getId() + ", nameSubtask: " + subtask.getNameTask());
        }
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


    private void updateEpicStatus(Epic epic) {
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
            epic.statusTask = Status.NEW;
        } else if (allSubtaskDONE) {
            epic.statusTask = Status.DONE;
        } else {
            epic.statusTask = Status.IN_PROGRESS;
        }
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public void removeTask() {
        tasks.clear();
    }

    public void removeSubtask() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.removeAllSubtask();
            updateEpicStatus(epic);
        }
    }

    public void removeEpic() {
        epics.clear();
        subtasks.clear();
    }

    public void updateTask(Task newTask) {
        if (tasks.containsKey(newTask.getId())) {
            tasks.put(newTask.getId(), newTask);
        }
    }

    public void updateEpic(Epic newEpic) {
        Epic oldEpic = epics.get(newEpic.getId());
        oldEpic.nameTask = newEpic.nameTask;
        oldEpic.descriptionTask = newEpic.descriptionTask;
    }

    public void updateSubtask(Subtask newSubtask) {
        if (subtasks.containsKey(newSubtask.getId())) {
            if (subtasks.get(newSubtask.getId()).getEpicId() == newSubtask.getEpicId()) {
                subtasks.put(newSubtask.getId(), newSubtask);
                Epic epic = epics.get(newSubtask.getEpicId());
                updateEpicStatus(epic);
            }
        }
    }

    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    public void removeEpicById(int id) {
        Epic epic = epics.get(id);

        for (Integer subtaskId : epic.getSubtaskIdToEpic()) {
            subtasks.remove(subtaskId);
        }
        epics.remove(id);
    }

    public void removeSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        Epic epic = epics.get(subtask.getEpicId());

        epic.removeSubtask(subtask);
        subtasks.remove(id);
        updateEpicStatus(epic);
    }

    public Collection<Subtask> getSubtaskByEpic(Epic epic) {
        Collection<Subtask> subtaskCollection = new ArrayList<>();

        for (Integer subtaskId : epic.getSubtaskIdToEpic()) {
            Subtask subtask = subtasks.get(subtaskId);
            subtaskCollection.add(subtask);
        }
        return subtaskCollection;
    }
}
