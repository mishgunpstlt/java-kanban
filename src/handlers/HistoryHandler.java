package handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {

    public TaskManager taskManager;

    public HistoryHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (exchange.getRequestMethod().equals("GET")) {
            String response = gson.toJson(taskManager.getHistory());
            if (response.equals("[]")) {
                sendNotFound(exchange, "Список задач пуст");
            } else {
                sendText(exchange, response, 200);
            }
        } else {
            sendNotFound(exchange, "Такого эндпоинта нет");
        }
    }
}
