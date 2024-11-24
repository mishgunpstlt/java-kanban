package utils;

import Managers.TaskManager;
import Managers.InMemoryTaskManager;
import Managers.InMemoryHistoryManager;
import Managers.HistoryManager;

public final class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
