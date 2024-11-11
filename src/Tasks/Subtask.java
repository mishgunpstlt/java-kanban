package Tasks;

public class Subtask extends Task {

    public int epicId;
    public String statusSubtask;

    public Subtask(String name, String description, Status status) {
        super(name, description, status);
        statusSubtask = String.valueOf(status);
    }
}
