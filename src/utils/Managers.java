package utils;

import managers.TaskManager;
import managers.InMemoryTaskManager;
import managers.InMemoryHistoryManager;
import managers.HistoryManager;

public final class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
