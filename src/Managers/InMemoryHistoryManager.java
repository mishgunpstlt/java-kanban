package Managers;

import Tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private Node head;
    private Node tail;
    private HashMap<Integer, Node> taskIdNode = new HashMap<>();

    @Override
    public List<Task> getHistory() {
        List<Task> history = new ArrayList<>();
        Node current = head;
        while (current != null) {
            history.add(current.task);
            current = current.next;
        }
        return history;
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }

        Node node = taskIdNode.get(task.getId());
        if (node != null) {
            removeNode(node);
        }

        linkLast(task);
    }

    @Override
    public void remove(int id) {
        Node node = taskIdNode.get(id);
        removeNode(node);
    }

    public void linkLast(Task task) {
        Node newNode = new Node(task);
        if (tail == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
        taskIdNode.put(task.getId(), newNode);
    }

    public void removeNode(Node node) {
        if (node == null) return;

        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }

        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }

        taskIdNode.remove(node.task.getId());
    }
}
