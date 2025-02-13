package server;


import com.sun.net.httpserver.HttpServer;
import handlers.*;
import managers.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

import static utils.Managers.getDefault;

public class HttpTaskServer {

    private static final int PORT = 8080;

    private final HttpServer httpServer;

    public HttpTaskServer(TaskManager taskManager) {
        try {
            httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        httpServer.createContext("/tasks", new TasksHandler(taskManager));

        httpServer.createContext("/epics", new EpicsHandler(taskManager));

        httpServer.createContext("/subtasks", new SubtasksHandler(taskManager));

        httpServer.createContext("/history", new HistoryHandler(taskManager));

        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager));
    }

    public static void main(String[] args) {
        TaskManager taskManager = getDefault();
        HttpTaskServer httpServer = new HttpTaskServer(taskManager);
        httpServer.start();
    }

    public void start() {
        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public void stop() {
        httpServer.stop(0);
        System.out.println("HTTP-сервер остановлен.");
    }
}
