package handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;
import tasks.Epic;

import java.io.IOException;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {

    public TaskManager taskManager;

    public EpicsHandler(TaskManager taskManager) {
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
            case GET_SUBTASKSBYEPIC: {
                handleGetSubtasksByEpic(exchange);
                break;
            }
            default:
                sendNotFound(exchange, "Такого эндпоинта не существует");
        }
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 2 && pathParts[1].equals("epics")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_TASKS;
            }
            if (requestMethod.equals("POST")) {
                return Endpoint.POST_TASK;
            }
        }
        if (pathParts.length == 3 && pathParts[1].equals("epics")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_TASKBYID;
            }
            if (requestMethod.equals("DELETE")) {
                return Endpoint.DELET_TASK;
            }
        }
        if (pathParts.length == 4 && pathParts[1].equals("epics")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_SUBTASKSBYEPIC;
            }
        }
        return Endpoint.UNKNOWN;
    }

    private void handleGetTask(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.getEpic());
        if (response.equals("[]")) {
            sendNotFound(exchange, "Список задач пуст");
        } else {
            sendText(exchange, response, 200);
        }
    }

    private void handlePostTask(HttpExchange exchange) throws IOException {
        String requestBody = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
        Epic newEpic = gson.fromJson(requestBody, Epic.class);
        if (newEpic.getId() > 0 && taskManager.getEpicById(newEpic.getId()) != null) {
            taskManager.updateEpic(newEpic);
            sendText(exchange, "Задача с ID=" + newEpic.getId() + " обновлена", 201);
        } else {
            int taskId = taskManager.addEpic(newEpic);
            if (taskId > 0) {
                sendText(exchange, "Задача добавлена с ID: " + taskId, 201);
            } else {
                sendHasInteractions(exchange);
            }
        }
    }

    private void handleGetTaskById(HttpExchange exchange) throws IOException {
        String[] requestURI = exchange.getRequestURI().getPath().split("/");
        String response = gson.toJson(taskManager.getEpicById(Integer.parseInt(requestURI[2])));
        if (response.equals("null")) {
            sendNotFound(exchange, "Такой задачи нет");
        } else {
            sendText(exchange, response, 200);
        }
    }

    private void handleDeleteTask(HttpExchange exchange) throws IOException {
        String[] requestURI = exchange.getRequestURI().getPath().split("/");
        if (taskManager.getEpicById(Integer.parseInt(requestURI[2])) != null) {
            taskManager.removeEpicById(Integer.parseInt(requestURI[2]));
            sendText(exchange, "Задача с id=" + requestURI[2] + " удалена", 201);
        } else {
            sendNotFound(exchange, "Такой задачи нет");
        }
    }

    private void handleGetSubtasksByEpic(HttpExchange exchange) throws IOException {
        String[] requestURI = exchange.getRequestURI().getPath().split("/");
        String response = gson.toJson(taskManager.getSubtaskByEpic(taskManager.getEpicById(Integer.parseInt(requestURI[2]))));
        if (response.equals("[]")) {
            sendNotFound(exchange, "Список задач пуст");
        } else {
            sendText(exchange, response, 200);
        }
    }
}
