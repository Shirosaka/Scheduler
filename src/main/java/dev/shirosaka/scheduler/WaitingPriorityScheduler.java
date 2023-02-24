package dev.shirosaka.scheduler;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WaitingPriorityScheduler extends Scheduler {
    private final Model model;

    private final List<Process> processes;
    private int activePid = 0;
    private int pidTicksActive = 0;

    public WaitingPriorityScheduler(Model model, List<Process> processes) {
        Controller.log("Initializing WaitingPriorityScheduler");
        this.model = model;
        this.processes = processes;
    }

    @Override
    public boolean cycle() {
        Controller.log(String.format("Scheduler::cycle(): last activePid = %d", activePid));
        var lastProcess = processes.get(activePid);
        var lastProcessNextState = lastProcess.getNextState();

        if (pidTicksActive > 5 || lastProcessNextState != ProcessState.COMPUTING) {
            Controller.log(String.format(
                    "Scheduler::cycle(): activePid %d has worked 5 ticks or has no computing work to do, searching for new process",
                    activePid));
            lastProcess.lowerPriority();
            model.updateCurrentProcessPriority(processes.get(activePid));
            pidTicksActive = 0;

            Map<Integer, List<Process>> procMap = processes.stream()
                    .filter(p -> p != lastProcess && !p.isFinished())
                    .collect(Collectors.groupingBy(Process::getPriority));
            Controller.log(procMap.toString());

            if (procMap.isEmpty()) {
                Controller.log("Scheduler::cycle(): no new processes found");
                return false;
            } else {
                var highestPrio = procMap.keySet().stream().min((p1, p2) -> p2 - p1).get();
                var procMaybe = procMap.get(highestPrio).stream().max(Comparator.comparingInt(Process::getWaitTime));

                if (procMaybe.isEmpty()) {
                    Controller.log("Scheduler::cycle(): failed to retrieve process");
                    return false;
                }

                activePid = processes.indexOf(procMaybe.get());
            }
        } else {
            Controller.log(String.format("Scheduler::cycle(): activePid %d can continue working, has been working for %d tick(s).", activePid, pidTicksActive));
        }

        Controller.log(String.format("Scheduler::cycle(): new activePid = %d", activePid));
        for (int pid = 0; pid < processes.size(); pid++) {
            Controller.log(String.format("Scheduler::cycle(): pid %d", pid));
            var p = processes.get(pid);
            p.tick(pid != activePid);
        }

        pidTicksActive++;
        return true;
    }

    @Override
    public Process getCurrentProcess() {
        if (activePid == -1)
            return null;
        return processes.get(activePid);
    }
}
