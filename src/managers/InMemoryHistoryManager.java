package managers;

import tasks.Task;

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

    private void linkLast(Task task) {
        Node newNode = new Node(task);
        if (tail == null) {
            head = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
        }
        tail = newNode;
        taskIdNode.put(task.getId(), newNode);
    }

    private void removeNode(Node node) {
        if (node == null) return;

        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
            if (head != null) {
                head.prev = null;
            }
        }

        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
            if (tail != null) {
                tail.next = null;
            }
        }

        taskIdNode.remove(node.task.getId());
    }
}
