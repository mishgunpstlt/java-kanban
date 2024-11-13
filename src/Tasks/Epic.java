package Tasks;
import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> subtaskIdToEpic = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
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
