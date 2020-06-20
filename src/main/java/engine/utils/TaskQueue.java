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

    public void addTask(Task task, TaskPriority priority) {
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

    public boolean hasTask() {
        return isNotEmpty();
    }

    public boolean isEmpty() {
        return lowQueue.isEmpty() && middleQueue.isEmpty() && highQueue.isEmpty();
    }

    public boolean isNotEmpty() {
        return !isEmpty();
    }

    public Task getTask() {
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

}
