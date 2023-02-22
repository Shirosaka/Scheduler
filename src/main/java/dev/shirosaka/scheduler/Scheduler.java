package dev.shirosaka.scheduler;

import java.util.List;

public class Scheduler {
    private List<Process> processes;

    public Scheduler(List<Process> processes) {
        this.processes = processes;
    }
}
