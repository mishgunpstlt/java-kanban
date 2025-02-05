import managers.FileBackedTaskManager;
import managers.TaskManager;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Scanner;

import static utils.Managers.getDefault;


public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        System.out.println("1 - Создание задача и получение истории");
        System.out.println("2 - Добавление задач в файл");
        System.out.println("3 - Загрузка задач из файла");

        Scanner scanner = new Scanner(System.in);
        int value = scanner.nextInt();

        switch (value) {
            case 1:
                createTask();
                break;
            case 2:
                addTaskToFile();
                break;
            case 3:
                loadTaskFromFile();
                break;
            default:
                System.exit(0);
                break;
        }
    }

    private static void loadTaskFromFile() {
        File file = new File("src/resources/autoSave.csv");

        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);

        System.out.println(fileBackedTaskManager.getTask());
        System.out.println(fileBackedTaskManager.getEpic());
        System.out.println(fileBackedTaskManager.getSubtask());

        Task task6 = new Task("Купить п312321родукты", "Овощи, фрукты, колбасу", Status.NEW, Duration.ofMinutes(50), LocalDateTime.of(2025, 2, 4, 15, 0));
        System.out.println("Id новой добавленной задачи: " + fileBackedTaskManager.addTask(task6));
    }

    private static void addTaskToFile() {
        File file = new File("src/resources/autoSave.csv");
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);

        // Добавляем задачи
        Task task1 = new Task("Задача 1", "Описание задачи 1", Status.NEW, Duration.ofMinutes(50), LocalDateTime.of(2025, 2, 1, 15, 0));
        Task task2 = new Task("Задача 2", "Описание задачи 2", Status.IN_PROGRESS, Duration.ofMinutes(33), LocalDateTime.of(2025, 2, 2, 10, 55));
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        Subtask subtask1 = new Subtask("Подзаdasdasдача 1", "Описание подзадачи 1", Status.DONE, 3, Duration.ofMinutes(66), LocalDateTime.of(2025, 2, 3, 15, 0));
        Subtask subtask2 = new Subtask("dasdaasdadasdaа 1", "Описание подзадачи 2", Status.DONE, 3, Duration.ofMinutes(99), LocalDateTime.of(2025, 2, 4, 15, 0));
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");

        fileBackedTaskManager.addTask(task1);
        fileBackedTaskManager.addTask(task2);
        fileBackedTaskManager.addEpic(epic1);
        fileBackedTaskManager.addEpic(epic2);
        fileBackedTaskManager.addSubtask(subtask1);
        fileBackedTaskManager.addSubtask(subtask2);
    }

    private static void createTask() {
        TaskManager taskManager = getDefault();

        Task task1 = new Task("Купить квартиру", "В москве", Status.NEW, Duration.ofMinutes(50), LocalDateTime.of(2025, 2, 10, 15, 0));
        Task task2 = new Task("Купить продукты", "Овощи, фрукты, колбасу", Status.NEW, Duration.ofMinutes(50), LocalDateTime.of(2025, 2, 2, 15, 0));
        Task task3 = new Task("Купить квар31231тиру", "В москве", Status.NEW, Duration.ofMinutes(50), LocalDateTime.of(2025, 2, 2, 15, 0));
        Task task4 = new Task("Купить про31231дукты", "Овощи, фрукты, колбасу", Status.NEW, Duration.ofMinutes(50), LocalDateTime.of(2025, 2, 4, 15, 0));
        Task task5 = new Task("Купить к31231вартиру", "В москве", Status.NEW, Duration.ofMinutes(50), LocalDateTime.of(2025, 2, 4, 16, 0));
        Task task6 = new Task("Купить п312321родукты", "Овощи, фрукты, колбасу", Status.NEW, Duration.ofMinutes(50), LocalDateTime.of(2025, 2, 4, 17, 0));
        Epic epic1 = new Epic("БАК", "Подготовить доклад+презентацию к понедельнику");
        Epic epic2 = new Epic("Сдать лр", "Развернуть виртуалку");
        Epic epic3 = new Epic("312 лр", "Разв31231ернуть виртуалку");

        Subtask subtask11 = new Subtask("Подготовить доклад", "доклад к понедельнику", Status.DONE, 7, Duration.ofMinutes(50), LocalDateTime.of(2025, 2, 4, 21, 0));
        Subtask subtask12 = new Subtask("Подготовить презентацию", "презентацию к понедельнику", Status.NEW, 7, Duration.ofMinutes(50), LocalDateTime.of(2025, 2, 4, 19, 0));
        Subtask subtask21 = new Subtask("Развернуть виртуалку", "ОС Windows 10 64bit", Status.DONE, 8, Duration.ofMinutes(50), LocalDateTime.of(2025, 2, 4, 20, 0));

        System.out.println("Добваление задач");
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
        taskManager.addTask(task4);
        taskManager.addTask(task5);
        taskManager.addTask(task6);

        System.out.println("\nДобваление эпиков");
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        taskManager.addEpic(epic3);


        System.out.println("\nДобваление сабтасков");
        taskManager.addSubtask(subtask11);
        taskManager.addSubtask(subtask12);
        taskManager.addSubtask(subtask21);

        printAllTasks(taskManager);
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getTask()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getEpic()) {
            System.out.println(epic);

            for (Task task : manager.getSubtaskByEpic((Epic) epic)) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getSubtask()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        manager.getTaskById(1);
        manager.getTaskById(2);
        manager.getTaskById(3);
        manager.getTaskById(4);
        manager.getTaskById(5);
        manager.getTaskById(6);
        manager.getEpicById(7);
        manager.getEpicById(8);
        manager.getEpicById(9);
        manager.getSubtaskById(10);
        manager.getSubtaskById(11);
        manager.getSubtaskById(12);

        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }

        System.out.println("-------");
        manager.removeTaskById(2);
        manager.removeEpicById(7);
        manager.removeEpic();
        System.out.println("-------");

        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }

        System.out.println("Отсортированные задачи по времени:");
        for (Task task : manager.getPrioritizedTasks()) {
            System.out.println(task);
        }
    }
}
