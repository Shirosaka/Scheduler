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
                        name));
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

    public void lowerPriority() {
        priority -= 2;
    }

    public int getWaitTime() {
        return waitTime;
    }

    public void tick(boolean isWaiting) {
        // check if process is finished
        if (workPos >= work.size()) {
            history.add(ProcessState.FINISHED);
            Controller.log(String.format("Process %s is finished.", name));
            return;
        }

        var curWork = work.get(workPos);

        // check if the scheduler scheduled this process to wait
        if (isWaiting) {
            // check if there is any open IO work to do
            if (curWork == ProcessState.IO) {
                history.add(ProcessState.IO);
                Controller.log(String.format("Process %s is scheduled to wait, but has open IO work, waiting for IO instead.", name));
                workPos++;
                return;
            }

            history.add(ProcessState.WAITING);
            Controller.log(String.format("Process %s is waiting since %d tick(s).", name, waitTime++));
            return;
        }

        // check if the process had wait time,
        if (waitTime > 0) {
            Controller.log(String.format("Resetting wait time for process %s", name));
            waitTime = 0;
        }

        Controller.log(String.format("Found work for process %s", name));
        history.add(curWork);
        workPos++;
    }

    public List<ProcessState> getWork() {
        return work;
    }

    public ProcessState getNextState() {
        if ((workPos + 1) >= work.size())
            return ProcessState.FINISHED;

        return work.get(workPos);
    }

    public List<ProcessState> getHistory() {
        return history;
    }

    public boolean isFinished() {
        return (workPos + 1) >= work.size();
    }

    @Override
    public String toString() {
        return String.format(
                "Process %s with original priority %d; new priority %d; wait time %d",
                name,
                originalPriority,
                priority,
                waitTime);
    }

}
