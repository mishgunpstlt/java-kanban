package managers;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;
import utils.Managers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    private int idNext = 1;

    @Override
    public int addTask(Task task) {
        try {
            if (isOverlayTask(task)) {
                throw new IllegalArgumentException("'" + task.getNameTask() + "'" + " пересекается по времени выполнения");
            }
            task.setId(idNext);
            tasks.put(task.getId(), task);
            addPrioritizedTasks(task);
            idNext++;
            return task.getId();

        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
            return -1;
        }
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
        try {
            if (epics.containsKey(subtask.getEpicId())) {
                if (isOverlayTask(subtask)) {
                    throw new IllegalArgumentException("'" + subtask.getNameTask() + "'" + " пересекается по времени выполнения");
                }
                if (subtask.getEpicId() == subtask.getId()) {
                    return -1;
                }
                subtask.setId(idNext);
                subtasks.put(subtask.getId(), subtask);
                addPrioritizedTasks(subtask);
                idNext++;

                Epic epic = epics.get(subtask.getEpicId());
                epic.addSubtask(subtask);
                updateEpicStatus(epic);
                updateStartTimeDurationEpic(epic);
                return subtask.getId();
            }
            return -1;
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
            return -1;
        }
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
        boolean allSubtaskNEW = epic.getSubtaskIdToEpic().stream()
                .map(id -> subtasks.get(id))
                .allMatch(subtask -> subtask != null && subtask.getStatusTask().equals(Status.NEW));

        boolean allSubtaskDONE = epic.getSubtaskIdToEpic().stream()
                .map(id -> subtasks.get(id))
                .allMatch(subtask -> subtask != null && subtask.getStatusTask().equals(Status.DONE));

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
        tasks.keySet().stream()
                .forEach(id -> {
                    prioritizedTasks.remove(tasks.get(id));
                    historyManager.remove(id);
                });

        tasks.clear();
    }

    @Override
    public void removeSubtask() {
        subtasks.keySet().stream()
                .forEach(id -> {
                    prioritizedTasks.remove(subtasks.get(id));
                    historyManager.remove(subtasks.get(id).getId());
                });
        subtasks.clear();

        epics.values().forEach(epic -> {
            epic.removeAllSubtask();
            updateEpicStatus(epic);
            updateStartTimeDurationEpic(epic);
        });

    }

    @Override
    public void removeEpic() {
        subtasks.keySet().stream()
                .forEach(id -> {
                    prioritizedTasks.remove(subtasks.get(id));
                    historyManager.remove(subtasks.get(id).getId());
                });

        epics.keySet().stream()
                .forEach(historyManager::remove);
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void updateTask(Task newTask) {
        try {
            if (tasks.containsKey(newTask.getId())) {
                prioritizedTasks.remove(tasks.get(newTask.getId()));
                if (isOverlayTask(newTask)) {
                    throw new IllegalArgumentException("Обновленная '" + newTask.getNameTask() + "' пересекается по времени выполнения");
                }
                tasks.put(newTask.getId(), newTask);
                addPrioritizedTasks(newTask);
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
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
        try {
            if (subtasks.containsKey(newSubtask.getId())) {
                if (subtasks.get(newSubtask.getId()).getEpicId() == newSubtask.getEpicId()) {
                    prioritizedTasks.remove(subtasks.get(newSubtask.getId()));
                    if (isOverlayTask(newSubtask)) {
                        throw new IllegalArgumentException("Обновленная '" + newSubtask.getNameTask() + "' пересекается по времени выполнения");
                    }
                    subtasks.put(newSubtask.getId(), newSubtask);
                    addPrioritizedTasks(newSubtask);
                    Epic epic = epics.get(newSubtask.getEpicId());
                    updateEpicStatus(epic);
                    updateStartTimeDurationEpic(epic);
                }
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    @Override
    public void removeTaskById(int id) {
        prioritizedTasks.remove(tasks.get(id));
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            epic.getSubtaskIdToEpic().stream()
                    .forEach(subtaskId -> {
                        prioritizedTasks.remove(subtasks.get(subtaskId));
                        subtasks.remove(subtaskId);
                        historyManager.remove(subtaskId);
                    });
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
            prioritizedTasks.remove(subtask);
            subtasks.remove(id);
            historyManager.remove(id);
            updateEpicStatus(epic);
            updateStartTimeDurationEpic(epic);
        }
    }

    @Override
    public Collection<Subtask> getSubtaskByEpic(Epic epic) {
        return epic.getSubtaskIdToEpic().stream()
                .map(subtaskId -> subtasks.get(subtaskId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    protected void setIdNext(int idNext) {
        this.idNext = idNext;
    }

    protected void addTaskFromFile(Task task) {
        tasks.put(task.getId(), task);
    }

    protected void addEpicFromFile(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    protected void addSubtaskFromFile(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);

        if (epics.containsKey(subtask.getEpicId())) {
            Epic epic = epics.get(subtask.getEpicId());
            epic.addSubtask(subtask);
            updateEpicStatus(epic);
            updateStartTimeDurationEpic(epic);
        }
    }

    @Override
    public void updateStartTimeDurationEpic(Epic epic) {
        Collection<Subtask> subtasksByEpic = getSubtaskByEpic(epic);
        long durationEpic = subtasksByEpic.stream()
                .map(Task::getDuration)
                .mapToLong(Duration::toMinutes)
                .sum();
        epic.setDuration(Duration.ofMinutes(durationEpic));

        LocalDateTime startTime = subtasksByEpic.stream()
                .map(Task::getStartTime)
                .min(LocalDateTime::compareTo)
                .orElse(null);
        epic.setStartTime(startTime);

        LocalDateTime endTime = subtasksByEpic.stream()
                .map(subtask -> subtask.getStartTime().plus(subtask.getDuration()))
                .max(LocalDateTime::compareTo)
                .orElse(null);
        epic.setEndTime(endTime);
    }

    @Override
    public void addPrioritizedTasks(Task task) {
        if (task.getStartTime() != null) {
            getPrioritizedTasks().add(task);
        }
    }

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    private boolean isOverlayTask(Task newTask) {
        return getPrioritizedTasks().stream()
                .anyMatch(existTask -> !(newTask.getEndTime().isBefore(existTask.getStartTime()) ||
                        newTask.getStartTime().isAfter(existTask.getEndTime())));
    }
}
