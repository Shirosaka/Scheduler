package dev.shirosaka.scheduler;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WaitingPriorityScheduler extends Scheduler {
    private int activePid = 0;
    private int pidTicksActive = 0;
    private List<Process> processes;

    public WaitingPriorityScheduler(List<Process> processes) {
        Controller.log("Initializing WaitingPriorityScheduler");
        this.processes = processes;
    }

    @Override
    public void cycle() {
        Controller.log(String.format("Scheduler::cycle(): last activePid = %d", activePid));
        var lastProcess = processes.get(activePid);
        var lastProcessNextState = lastProcess.getNextState();

        if (pidTicksActive >= 5 || lastProcessNextState != ProcessState.COMPUTING) {
            Controller.log(String.format(
                    "Scheduler::cycle(): activePid %d has worked 5 ticks or has no computing work to do, searching for new process",
                    activePid));

            Map<Integer, List<Process>> ok = processes.stream().filter(p -> p != lastProcess && !p.isFinished())
                    .collect(Collectors.groupingBy(Process::getPriority));
            Controller.log(ok.toString());

            if (ok.isEmpty()) {
                Controller.log("Scheduler::cycle(): no new processes found");
                activePid = -1;
            } else {
                // TODO: get last
                var lowestPrio = ok.keySet().stream().();
                if (lowestPrio.isPresent()) {
                    var procMaybe = ok.get(lowestPrio.get()).stream().findFirst();
                    if (procMaybe.isPresent()) {
                        activePid = processes.indexOf(procMaybe.get());
                    } else {
                        activePid = -1;
                    }
                } else {
                    activePid = -1;
                }
            }
        } else {
            Controller.log(String.format("Scheduler::cycle(): activePid %d can continue working", activePid));
        }

        Controller.log(String.format("Scheduler::cycle(): new activePid = %d", activePid));
        for (int pid = 0; pid < processes.size(); pid++) {
            Controller.log(String.format("Scheduler::cycle(): pid %d", pid));
            var p = processes.get(pid);
            var res = p.tick(pid != activePid);
        }
    }
}
