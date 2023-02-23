package dev.shirosaka.scheduler;

import java.util.List;

public class Scheduler {
    private List<Process> processes;

    public Scheduler(List<Process> processes) {
        Controller.log("Initializing scheduler");
        this.processes = processes;
    }

    public void cycle() {

    }
}
