package engine.utils;

import yapi.manager.worker.Task;

import java.util.ArrayDeque;
import java.util.Queue;

public class TaskQueue {

    public enum TaskPriority {
        LOW,
        MIDDLE,
        HIGH,
    }

    private Queue<Task> lowQueue = new ArrayDeque<>();
    private Queue<Task> middleQueue = new ArrayDeque<>();
    private Queue<Task> highQueue = new ArrayDeque<>();

    public synchronized void addTask(Task task, TaskPriority priority) {
        if (priority == TaskPriority.LOW) {
            lowQueue.add(task);
        }
        if (priority == TaskPriority.MIDDLE) {
            middleQueue.add(task);
        }
        if (priority == TaskPriority.HIGH) {
            highQueue.add(task);
        }
    }

    public synchronized boolean hasTask() {
        return isNotEmpty();
    }

    public synchronized boolean isEmpty() {
        return lowQueue.isEmpty() && middleQueue.isEmpty() && highQueue.isEmpty();
    }

    public synchronized boolean isNotEmpty() {
        return !isEmpty();
    }

    public synchronized Task getTask() {
        if (isEmpty()) return null;
        if (!highQueue.isEmpty()) {
            return highQueue.poll();
        }
        if (!middleQueue.isEmpty()) {
            return middleQueue.poll();
        }
        if (!lowQueue.isEmpty()) {
            return lowQueue.poll();
        }
        return null;
    }

    @Override
    public String toString() {
        return "TaskQueue{" +
                "lowQueue=" + lowQueue +
                ", middleQueue=" + middleQueue +
                ", highQueue=" + highQueue +
                '}';
    }
}
