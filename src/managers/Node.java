package managers;

import tasks.Task;

public class Node {
    Task task;
    Node next;
    Node prev;

    public Node(Task task) {
        this.task = task;
    }
}
