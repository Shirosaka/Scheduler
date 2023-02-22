package dev.shirosaka.scheduler;

import java.util.ArrayList;
import java.util.List;

public class Process {
    private final String name;

    private int priority;
    private final int originalPriority;
    private int waitTime = 0;
    private final List<WorkType> work;

    public Process(String name, int priority, String workStr) {
        this.name = name;
        this.originalPriority = this.priority = priority;

        final List<WorkType> work = new ArrayList<>();

        if (workStr.isEmpty())
            throw new RuntimeException("Work string cannot be empty");

        workStr = workStr.toUpperCase();
        for (int n = 0; n < workStr.length(); n++) {
            char c = workStr.charAt(n);

            switch (c) {
                case 'C' -> work.add(WorkType.COMPUTE);
                case 'W' -> work.add(WorkType.WAIT);
                case 'I' -> work.add(WorkType.IO);
                default -> throw new RuntimeException("Invalid character " + c + " @ " + workStr + " character " + n);
            }
        }

        this.work = work;
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

    public void setPriority(int newPriority) {
        priority = newPriority;
    }

    public int getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(int newWaitTime) {
        waitTime = newWaitTime;
    }

    public List<WorkType> getWork() {
        return work;
    }
}
