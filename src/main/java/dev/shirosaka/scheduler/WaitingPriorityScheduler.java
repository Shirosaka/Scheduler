package dev.shirosaka.scheduler;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WaitingPriorityScheduler extends Scheduler {
    private final Model model;

    private final List<Process> processes;
    private int activePid = -1;
    private int lastActivePid = -1;
    private int pidTicksActive = 0;

    public WaitingPriorityScheduler(Model model, List<Process> processes) {
        Controller.log("Initializing WaitingPriorityScheduler");
        this.model = model;
        this.processes = processes;
    }

    // cycle the scheduler once
    @Override
    public boolean cycle() {
        log("cycling");
        // this is to determine a new process to run.
        if (!currentProcessCanContinue()) {
            var lastProcess = lastActivePid > -1 ? processes.get(lastActivePid) : null;
            // collect all unfinished processes into a map, sorted by priority
            Map<Integer, List<Process>> procMap = processes.stream()
                    .filter(p -> p != lastProcess && p.getNextState() == ProcessState.COMPUTING)
                    .collect(Collectors.groupingBy(Process::getPriority));

            if (procMap.isEmpty()) {
                // we don't do anything in case all processes are finished or waiting for IO
                log("no new processes found");
                activePid = -1;
            } else {
                // get process list with the highest priority
                var highestPrio = procMap.keySet().stream().min((p1, p2) -> p2 - p1).get();
                // get the longest waiting process
                var procMaybe = procMap.get(highestPrio).stream().max(Comparator.comparingInt(Process::getWaitTime));

                // if we fail to retrieve it, return false
                if (procMaybe.isEmpty()) {
                    log("failed to retrieve process");
                    return false;
                }

                // set active pid to that of the chosen process
                activePid = processes.indexOf(procMaybe.get());
            }
        }

        log("activePid = %d", activePid);
        log("lastActivePid = %d", lastActivePid);

        // tick all processes in the process list
        for (int pid = 0; pid < processes.size(); pid++) {
            log("pid %d tick", pid);
            var p = processes.get(pid);
            p.tick(pid != activePid);
        }

        if (activePid > -1) {
            // increase active time for current pid
            ++pidTicksActive;

            // if process has been active for 5 ticks or more, is finished, or is not computing anything next cycle,
            // remove process from being active
            if (!currentProcessCanContinue()) {
                log("pid %d has worked %d/5 ticks or has no computing work to do, searching for new process",
                        activePid,
                        pidTicksActive);
                var curProc = getCurrentProcess();
                var nextState = curProc.getNextState();
                log("pid %d nextState=%s", activePid, nextState.name());
                curProc.lowerPriority();
                model.updateCurrentProcessPriority(curProc);
                lastActivePid = activePid;
                activePid = -1;
                pidTicksActive = 0;
            } else {
                log("activePid %d can continue working, has been working for %d tick(s).", activePid, pidTicksActive);
            }
        } else {
            log("no work done this cycle");
        }

        return true;
    }

    /**
     * Gets the currently active process.
     * @return the currently active process; or null if none
     */
    @Override
    public Process getCurrentProcess() {
        if (activePid == -1)
            return null;
        return processes.get(activePid);
    }

    @Override
    public void reset() {
        log("resetting");
        activePid = -1;
        lastActivePid = -1;
        pidTicksActive = 0;
    }

    /**
     * Whether all processes are finished.
     * @return true if all processes are finished; otherwise false
     */
    @Override
    public boolean allFinished() {
        return processes.stream().allMatch(Process::isFinished);
    }

    // checks if the process pointed to by activePid can continue
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean currentProcessCanContinue() {
        return activePid != -1 && pidTicksActive < 5 && processes.get(activePid).getNextState() == ProcessState.COMPUTING;
    }

    // log utility
    @SuppressWarnings("SameParameterValue")
    private void log(String format, Object... args) {
        Controller.log(String.format("Scheduler: %s", String.format(format, args)));
    }
}
