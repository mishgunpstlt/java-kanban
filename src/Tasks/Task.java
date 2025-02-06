package tasks;

import java.time.Duration;
import java.time.LocalDateTime;

public class Task {
    protected String nameTask;
    protected String descriptionTask;
    protected Status statusTask;
    protected int id;
    protected Duration duration;
    protected LocalDateTime startTime;

    public Task(String name, String description, Status status, Duration duration, LocalDateTime startTime) {
        nameTask = name;
        descriptionTask = description;
        statusTask = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public String getNameTask() {
        return nameTask;
    }

    public void setNameTask(String nameTask) {
        this.nameTask = nameTask;
    }

    public String getDescriptionTask() {
        return descriptionTask;
    }

    public void setDescriptionTask(String descriptionTask) {
        this.descriptionTask = descriptionTask;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatusTask() {
        return statusTask;
    }

    public void setStatusTask(Status statusTask) {
        this.statusTask = statusTask;
    }

    public LocalDateTime getEndTime() {
        if (startTime != null) {
            return startTime.plus(duration);
        }
        return null;
    }

    @Override
    public String toString() {
        return "Имя: " + getNameTask() + " Статус: " + getStatusTask() + " Продолжительность: "
                + getDuration() + " Время и дата начала: " + getStartTime() + " Время и дата конца: " + getEndTime();
    }

}
