package Tasks;
import java.util.ArrayList;

public class Epic extends Task {

    ArrayList<Integer> epicsId = new ArrayList<>();
    public String statusEpic;

    public Epic(String name, String description, Status status) {
        super(name, description, status);
        statusEpic = String.valueOf(status);
    }
}
