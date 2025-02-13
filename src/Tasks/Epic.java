package tasks;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {

    private final ArrayList<Integer> subtaskIdToEpic;
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description, Status.NEW, null, null);
        this.subtaskIdToEpic = new ArrayList<>();
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void removeSubtask(Subtask subtask) {
        if (!subtaskIdToEpic.isEmpty()) {
            subtaskIdToEpic.remove(Integer.valueOf(subtask.getId()));
        }
    }

    public void removeAllSubtask() {
        if (!subtaskIdToEpic.isEmpty()) {
            subtaskIdToEpic.clear();
        }
    }

    public void addSubtask(Subtask subtask) {
        subtaskIdToEpic.add(subtask.id);
    }

    public ArrayList<Integer> getSubtaskIdToEpic() {
        return subtaskIdToEpic;
    }

}
