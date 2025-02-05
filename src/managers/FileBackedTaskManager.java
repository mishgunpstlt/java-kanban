package managers;

import exception.ManagerSaveException;
import tasks.*;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File autoSaveFile;

    public FileBackedTaskManager(File autoSaveFile) {
        this.autoSaveFile = autoSaveFile;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);

        int maxId = 0;

        try (BufferedReader readerToFile = new BufferedReader(new FileReader(file))) {
            readerToFile.readLine();
            String line;
            while ((line = readerToFile.readLine()) != null) {
                Task task = fileBackedTaskManager.fromString(line);
                if (task instanceof Subtask subtask) {
                    fileBackedTaskManager.addSubtaskFromFile(subtask);
                } else if (task instanceof Epic epic) {
                    fileBackedTaskManager.addEpicFromFile(epic);
                } else {
                    fileBackedTaskManager.addTaskFromFile(task);
                }
                maxId = Math.max(maxId, task.getId());
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при чтении файла");
        }

        fileBackedTaskManager.setIdNext(maxId + 1);
        return fileBackedTaskManager;
    }

    private String toString(Task task) {
        if (task instanceof Subtask) {
            return task.getId() + "," + TypeTask.SUBTASK + "," + task.getNameTask() + "," + task.getStatusTask()
                    + "," + task.getDescriptionTask() + "," + ((Subtask) task).getEpicId() + ","
                    + task.getDuration().toMinutes() + "," + task.getStartTime() + "," + task.getEndTime();
        } else if (task instanceof Epic) {
            return task.getId() + "," + TypeTask.EPIC + "," + task.getNameTask() + "," + task.getStatusTask()
                    + "," + task.getDescriptionTask() + "," + task.getDuration()
                    + "," + task.getStartTime() + "," + task.getEndTime();
        } else {
            return task.getId() + "," + TypeTask.TASK + "," + task.getNameTask() + "," + task.getStatusTask()
                    + "," + task.getDescriptionTask() + "," + task.getDuration().toMinutes()
                    + "," + task.getStartTime() + "," + task.getEndTime();
        }
    }

    private Task fromString(String value) {
        String[] taskString = value.split(",");
        if (taskString[1].equals("SUBTASK")) {
            Subtask subtask = new Subtask(taskString[2], taskString[4], Status.valueOf(taskString[3]),
                    Integer.parseInt(taskString[5]), Duration.ofMinutes(Long.parseLong(taskString[6])),
                    LocalDateTime.parse(taskString[7]));
            subtask.setId(Integer.parseInt(taskString[0]));
            return subtask;
        } else if (taskString[1].equals("EPIC")) {
            Epic epic = new Epic(taskString[2], taskString[4]);
            epic.setId(Integer.parseInt(taskString[0]));
            return epic;
        } else {
            Task task = new Task(taskString[2], taskString[4], Status.valueOf(taskString[3]),
                    Duration.ofMinutes(Long.parseLong(taskString[5])), LocalDateTime.parse(taskString[6]));
            task.setId(Integer.parseInt(taskString[0]));
            return task;
        }
    }

    private void save() {
        try (BufferedWriter writerToFile = new BufferedWriter(new FileWriter(autoSaveFile))) {
            writerToFile.write("id,type,name,status,description,epic,duration,startTime,endTime");
            writerToFile.newLine();
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
            throw new ManagerSaveException("Ошибка при сохранении в файл");
        }
    }

    @Override
    public int addTask(Task task) {
        super.addTask(task);
        save();
        return super.addTask(task);
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
        return super.addSubtask(subtask);
    }

    @Override
    public void updateEpicStatus(Epic epic) {
        super.updateEpicStatus(epic);
        save();
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
    public void updateStartTimeDurationEpic(Epic epic) {
        super.updateStartTimeDurationEpic(epic);
        save();
    }
}
