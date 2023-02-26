package dev.shirosaka.scheduler;

import javax.swing.*;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class Controller {
    private static Controller singleton;
    private final Model model;
    private final View view;
    private int ticks;
    private Scheduler scheduler;

    public Controller(Model model, View view) {
        singleton = this;

        this.model = model;
        this.view = view;

        ticks = 0;
        initView();
    }

    public static void log(String message) {
        Calendar calendar = Calendar.getInstance();
        String formatted = calendar.getTime() + " | " + message;
        System.out.println(formatted);
        if (singleton != null)
            singleton.view.getTaDebugLog().append(formatted + "\n");
    }

    public void initView() {
        log("calling initView()");
        view.getTable().setModel(model);
    }

    public void initController() {
        log("calling initController()");
        scheduler = new WaitingPriorityScheduler(model, model.getProcesses());
        view.getBtnRun().addActionListener(l -> {
            log("BtnRun click");
            while (!scheduler.allFinished()) {
                log("!!!!! Ticking");
                tick();
            }
            log("All current processes are finished, finished running.");
            JOptionPane.showMessageDialog(null, "All current processes are finished, finished running.");
        });
        view.getBtnCreate().addActionListener(l -> {
            log("BtnCreate click");
            var procName = JOptionPane.showInputDialog("Please enter the process name:");
            var procPrio = Integer.parseInt(JOptionPane.showInputDialog("Please enter the original process priority:"));
            var procWork = JOptionPane.showInputDialog("Please enter the process work (C for Compute; I for IO waiting):");
            model.addProcess(new Process(procName, procPrio, procWork));
        });
        view.getBtnDelete().addActionListener(l -> {
            log("BtnDelete click");
            var rows = view.getTable().getSelectedRows();
            if (rows.length > 1) {
                log(String.format("Deleting rows: %s", Arrays.toString(rows)));
                model.deleteProcesses(rows);
            } else if (rows.length == 1) {
                log(String.format("Deleting row: %d", rows[0]));
            } else {
                log("Nothing to delete");
            }
        });
        view.getBtnManualTick().addActionListener(l -> {
            if (scheduler.allFinished()) {
                log("All current processes are finished, cancelling manual tick.");
                JOptionPane.showMessageDialog(null, "All current processes are finished, not executing manual tick.");
                return;
            }
            log("!!!!! Manually ticking");
            tick();
        });
        view.getBtnReset().addActionListener(l -> {
            log("BtnReset click");
            model.reset();
            scheduler.reset();
            ticks = 0;
            JOptionPane.showMessageDialog(null, "Reset successful.");
        });
    }

    private void tick() {
        log("trying to tick");
        if (!scheduler.cycle()) {
            log("Failed to tick in Scheduler!");
            return;
        }
        ticks++;
        log("ticked: " + ticks);
        model.tick(ticks);
    }
}
