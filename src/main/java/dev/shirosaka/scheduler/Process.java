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

        // iterate over the workStr and populate `work` accordingly
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

    /**
     * Gets the process name.
     * @return the process name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the original process priority, unmodified by Process::lowerPriority.
     * @return the original process priority.
     */
    public int getOriginalPriority() {
        return originalPriority;
    }

    /**
     * Gets the process priority.
     * @return the process priority.
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Lowers the process priority by 2.
     */
    public void lowerPriority() {
        priority -= 2;
    }

    /**
     * Gets the waiting time of the process.
     * @return the waiting time.
     */
    public int getWaitTime() {
        return waitTime;
    }

    /**
     * Execute one tick on the process.
     * @param isWaiting whether the scheduler thinks this process should be idle.
     */
    public void tick(boolean isWaiting) {
        // check if process is finished
        if (isFinished()) {
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

    /**
     * Gets the next process state.
     * @return the next process state.
     */
    public ProcessState getNextState() {
        if (isFinished())
            return ProcessState.FINISHED;

        return work.get(workPos);
    }

    /**
     * Gets the process work history.
     * @return the process history.
     */
    public List<ProcessState> getHistory() {
        return history;
    }

    /**
     * Whether the process is finished.
     * @return true, if the process has no more work; otherwise false.
     */
    public boolean isFinished() {
        return workPos >= work.size();
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
