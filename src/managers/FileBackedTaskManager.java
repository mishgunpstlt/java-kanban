package managers;

import exception.ManagerSaveException;
import tasks.*;

import java.io.*;
import java.util.Collection;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File autoSaveFile;

    public FileBackedTaskManager(File autoSaveFile) {
        this.autoSaveFile = autoSaveFile;
    }

    public static FileBackedTaskManager loadFromFile(File file) {

        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);

        try (BufferedReader readerToFile = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = readerToFile.readLine()) != null) {
                Task task = fileBackedTaskManager.fromString(line);
                if (task instanceof Epic epic) {
                    fileBackedTaskManager.addEpic(epic);
                } else if (task instanceof Subtask subtask) {
                    fileBackedTaskManager.addSubtask(subtask);
                } else {
                    fileBackedTaskManager.addTask(task);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return fileBackedTaskManager;
    }

    public String toString(Task task) {
        if (task instanceof Subtask) {
            return task.getId() + "," + TypeTask.SUBTASK + "," + task.getNameTask() + "," + task.getStatusTask()
                    + "," + task.getDescriptionTask() + "," + ((Subtask) task).getEpicId();
        } else if (task instanceof Epic) {
            return task.getId() + "," + TypeTask.EPIC + "," + task.getNameTask() + "," + task.getStatusTask()
                    + "," + task.getDescriptionTask();
        } else {
            return task.getId() + "," + TypeTask.TASK + "," + task.getNameTask() + "," + task.getStatusTask()
                    + "," + task.getDescriptionTask();
        }
    }

    public Task fromString(String value) {
        String[] taskString = value.split(",");
        if (taskString[1].equals("SUBTASK")) {
            return new Subtask(taskString[2], taskString[4], Status.valueOf(taskString[3]), Integer.parseInt(taskString[5]));
        } else if (taskString[1].equals("EPIC")) {
            return new Epic(taskString[2], taskString[4]);
        } else {
            return new Task(taskString[2], taskString[4], Status.valueOf(taskString[3]));
        }
    }

    public void save() {
        try (BufferedWriter writerToFile = new BufferedWriter(new FileWriter(autoSaveFile))) {
            for (Task task : getTask()) {
                writerToFile.write(toString(task));
                writerToFile.newLine();
            }
            for (Epic epic : getEpic()) {
                writerToFile.write(toString(epic));
                writerToFile.newLine();
            }
            for (Subtask subtask : getSubtask()) {
                writerToFile.write(toString(subtask));
                writerToFile.newLine();
            }
        } catch (IOException exception) {
            try {
                throw new ManagerSaveException("Ошибка при сохранении в файл");
            } catch (ManagerSaveException e) {
                e.getMessage();
            }
        }
    }

    @Override
    public int addTask(Task task) {
        super.addTask(task);
        save();
        return task.getId();
    }

    @Override
    public int addEpic(Epic epic) {
        super.addEpic(epic);
        save();
        return epic.getId();
    }

    @Override
    public int addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
        return subtask.getId();
    }

    @Override
    public Collection<Task> getTask() {
        return super.getTask();
    }

    @Override
    public Collection<Epic> getEpic() {
        return super.getEpic();

    }

    @Override
    public Collection<Subtask> getSubtask() {
        return super.getSubtask();
    }

    @Override
    public void updateEpicStatus(Epic epic) {
        super.updateEpicStatus(epic);
        save();
    }

    @Override
    public Task getTaskById(int id) {
        return super.getTaskById(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        return super.getSubtaskById(id);
    }

    @Override
    public Epic getEpicById(int id) {
        return super.getEpicById(id);
    }

    @Override
    public void removeTask() {
        super.removeTask();
        save();
    }

    @Override
    public void removeSubtask() {
        super.removeSubtask();
        save();
    }

    @Override
    public void removeEpic() {
        super.removeEpic();
        save();
    }

    @Override
    public void updateTask(Task newTask) {
        super.updateTask(newTask);
        save();
    }

    @Override
    public void updateEpic(Epic newEpic) {
        super.updateEpic(newEpic);
        save();
    }

    @Override
    public void updateSubtask(Subtask newSubtask) {
        super.updateSubtask(newSubtask);
        save();
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void removeSubtaskById(int id) {
        super.removeSubtaskById(id);
        save();
    }

    @Override
    public Collection<Subtask> getSubtaskByEpic(Epic epic) {
        return super.getSubtaskByEpic(epic);
    }

    @Override
    public List<Task> getHistory() {
        return super.getHistory();
    }
}
