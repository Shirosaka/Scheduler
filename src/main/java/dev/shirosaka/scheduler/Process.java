package dev.shirosaka.scheduler;

import java.util.ArrayList;
import java.util.List;

public class Process {
    private final String name;
    private final int originalPriority;
    private final List<ProcessState> work;
    private final List<ProcessState> history;

    private int priority;
    private int waitTime = 0;
    private int workPos = 0;

    public Process(String name, int priority, String workStr) {
        this.name = name;
        this.originalPriority = this.priority = priority;

        final List<ProcessState> work = new ArrayList<>();

        if (workStr.isEmpty())
            throw new RuntimeException("Work string cannot be empty");

        workStr = workStr.toUpperCase();
        for (int n = 0; n < workStr.length(); n++) {
            char c = workStr.charAt(n);

            switch (c) {
                case 'C' -> work.add(ProcessState.COMPUTING);
                case 'I' -> work.add(ProcessState.IO);
                default -> throw new RuntimeException(String.format("Invalid character %c @ %s:%d in process %s",
                        c,
                        workStr,
                        n,
                        name)
                );
            }
        }

        // make the work collection non-mutable
        this.work = List.copyOf(work);
        this.history = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public int getOriginalPriority() {
        return originalPriority;
    }

    public int getPriority() {
        return priority;
    }

    public int getWaitTime() {
        return waitTime;
    }

    public int getWorkPos() {
        return workPos;
    }

    public ProcessState tick(boolean isWaiting) {
        // check if process is finished
        if (workPos >= work.size()) {
            history.add(ProcessState.FINISHED);
            Controller.log(String.format("Process %s is finished.", name));
            return ProcessState.FINISHED;
        }

        // check if the scheduler scheduled this process to wait
        if (isWaiting) {
            history.add(ProcessState.WAITING);
            Controller.log(String.format("Process %s is waiting since %d tick(s).", name, waitTime++));
            return ProcessState.WAITING;
        }

        // check if the process had wait time,
        if (waitTime > 0) {
            Controller.log(String.format("Resetting wait time for process %s", name));
            waitTime = 0;
        }

        var curWork = work.get(workPos++);
        Controller.log(String.format("Found work for process %s", name));
        history.add(curWork);
        return curWork;
    }

    public List<ProcessState> getWork() {
        return work;
    }

    public List<ProcessState> getHistory() {
        return history;
    }
}
