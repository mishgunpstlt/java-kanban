package managers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    protected File fileTest = new File("fileTest.cvs");
    protected FileBackedTaskManager fileBackedTaskManager;

    @BeforeEach
    void beforeEach() {
        fileBackedTaskManager = new FileBackedTaskManager(fileTest);
    }

    @Override
    protected FileBackedTaskManager createTaskManager() {
        return new FileBackedTaskManager(new File("fileTest.cvs"));
    }

    @Test
    void shouldSaveTasksToFile() {
        Task task1 = new Task("Купить квартиру", "В москве", Status.NEW, Duration.ofMinutes(50), LocalDateTime.of(2025, 2, 1, 15, 0));
        Epic epic1 = new Epic("БАК", "Подготовить доклад+презентацию к понедельнику");

        fileBackedTaskManager.addTask(task1);
        fileBackedTaskManager.addEpic(epic1);

        Subtask subtask1 = new Subtask("Подгот312312овить доклад", "доклад к понедельнику", Status.NEW, epic1.getId(), Duration.ofMinutes(50), LocalDateTime.of(2025, 2, 11, 15, 0));
        Subtask subtask2 = new Subtask("Подго31231231товить доклад", "доклад к понедельнику", Status.NEW, epic1.getId(), Duration.ofMinutes(50), LocalDateTime.of(2025, 2, 12, 15, 0));
        fileBackedTaskManager.addSubtask(subtask1);
        fileBackedTaskManager.addSubtask(subtask2);

        Assertions.assertTrue(fileTest.exists(), "Файл не существует");
        Assertions.assertTrue(fileTest.length() > 0, "Файл пуст");

    }

    @Test
    void shouldLoadingFromFile() {
        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(fileTest);

        Assertions.assertFalse(fileBackedTaskManager.getTask().isEmpty(), "Задачи не выгружены");
        Assertions.assertFalse(fileBackedTaskManager.getEpic().isEmpty(), "Эпики не выгружены");
        Assertions.assertFalse(fileBackedTaskManager.getSubtask().isEmpty(), "Подзадачи не существует");
    }

    @Test
    void shouldThrowExceptionForNonExistentFile() {
        File nonExistFile = new File("test.cvs");
        assertThrows(RuntimeException.class, () -> {
            FileBackedTaskManager.loadFromFile(nonExistFile);
        }, "Ожидалось исключение при попытке загрузки несуществующего файла");
    }
}
