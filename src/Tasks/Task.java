package Tasks;

public class Task {
    protected String nameTask;
    protected String descriptionTask;
    protected Status statusTask;
    protected int id;

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

    public Task(String name, String description, Status status) {
        nameTask = name;
        descriptionTask = description;
        statusTask = status;
    }

}
