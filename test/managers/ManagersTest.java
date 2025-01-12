package managers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import utils.Managers;

class ManagersTest {
    @Test
    void shouldReturnTaskManager() {
        TaskManager taskManager = Managers.getDefault();
        Assertions.assertNotNull(taskManager, "Возвращаем экземпляр не должен быть null");
    }

    @Test
    void shouldReturnHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        Assertions.assertNotNull(historyManager, "Возвращаем экземпляр не должен быть null");
    }
}