package Managers;

import Tasks.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    public ArrayList<Task> history = new ArrayList<>();

    @Override
    public ArrayList<Task> getHistory() {
        return history;
    }

    @Override
    public void add(Task task) {
        if (history.size() != 10) {
            history.add(task);
        } else {
            history.remove(history.getFirst());
            history.add(task);
        }
    }
}
