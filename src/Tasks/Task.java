package Tasks;

public class Task {
    public String nameTask;
    public String descriptionTask;
    public String statusTask;
    public int id;

    public Task(String name, String description, Status status) {
        nameTask = name;
        descriptionTask = description;
        statusTask = String.valueOf(status);
    }
}
