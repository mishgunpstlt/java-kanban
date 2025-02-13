package handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;
import tasks.Task;

import java.io.IOException;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {

    public TaskManager taskManager;

    public TasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_TASKS: {
                handleGetTask(exchange);
                break;
            }
            case GET_TASKBYID: {
                handleGetTaskById(exchange);
                break;
            }
            case POST_TASK: {
                handlePostTask(exchange);
                break;
            }
            case DELET_TASK: {
                handleDeleteTask(exchange);
                break;
            }
            default:
                sendNotFound(exchange, "Такого эндпоинта не существует");
        }
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 2 && pathParts[1].equals("tasks")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_TASKS;
            }
            if (requestMethod.equals("POST")) {
                return Endpoint.POST_TASK;
            }
        }
        if (pathParts.length == 3 && pathParts[1].equals("tasks")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_TASKBYID;
            }
            if (requestMethod.equals("DELETE")) {
                return Endpoint.DELET_TASK;
            }
        }
        return Endpoint.UNKNOWN;
    }

    private void handleGetTask(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.getTask());
        if (response.equals("[]")) {
            sendNotFound(exchange, "Список задач пуст");
        } else {
            sendText(exchange, response, 200);
        }
    }

    private void handlePostTask(HttpExchange exchange) throws IOException {
        String requestBody = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
        Task newTask = gson.fromJson(requestBody, Task.class);

        if (newTask.getId() > 0 && taskManager.getTaskById(newTask.getId()) != null) {
            taskManager.updateTask(newTask);
            sendText(exchange, "Задача с ID=" + newTask.getId() + " обновлена", 201);
        } else {
            int taskId = taskManager.addTask(newTask);
            if (taskId > 0) {
                sendText(exchange, "Задача добавлена с ID: " + taskId, 201);
            } else {
                sendHasInteractions(exchange);
            }
        }
    }

    private void handleGetTaskById(HttpExchange exchange) throws IOException {
        String[] requestURI = exchange.getRequestURI().getPath().split("/");
        String response = gson.toJson(taskManager.getTaskById(Integer.parseInt(requestURI[2])));
        if (response.equals("null")) {
            sendNotFound(exchange, "Такой задачи нет");
        } else {
            sendText(exchange, response, 200);
        }
    }

    private void handleDeleteTask(HttpExchange exchange) throws IOException {
        String[] requestURI = exchange.getRequestURI().getPath().split("/");
        if (taskManager.getTaskById(Integer.parseInt(requestURI[2])) != null) {
            taskManager.removeTaskById(Integer.parseInt(requestURI[2]));
            sendText(exchange, "Задача с id=" + requestURI[2] + " удалена", 201);
        } else {
            sendNotFound(exchange, "Такой задачи нет");
        }
    }
}
