package dev.shirosaka.scheduler;

import javax.swing.*;
import java.util.Calendar;
import java.util.List;

public class Controller {
    private final Model model;
    private final View view;

    private int ticks;
    private Scheduler scheduler;

    private static Controller singleton;

    public Controller(Model model, View view) {
        singleton = this;

        this.model = model;
        this.view = view;

        ticks = 0;
        initView();
    }

    public void initView() {
        log("calling initView()");
        view.getTable().setModel(model);

         List.of(
                new Process("A", 12, "CCIIIICCCCCC"),
                new Process("B", 10, "CIICIIC"),
                new Process("C", 11, "CCIICCCCC")
        ).forEach(model::addProcess);
    }

    public void initController() {
        log("calling initController()");

        view.getBtnCreate().addActionListener(l -> {
            log("BtnCreate click");
            var procName = JOptionPane.showInputDialog("Please enter the process name:");
            var origProcPrioStr = JOptionPane.showInputDialog("Please enter the original process priority:");
            var origProcPrio = Integer.parseInt(origProcPrioStr);
            var procWork = JOptionPane.showInputDialog("Please enter the process work (C for Compute; I for IO waiting):");
            model.addProcess(new Process(procName, origProcPrio, procWork));
        });
        view.getBtnDelete().addActionListener(l -> log("BtnDelete click"));
        view.getBtnManualTick().addActionListener(l -> {
            log("!!!!! Manually ticking");
            tick();
        });
    }

    public static void log(String message) {
        Calendar calendar = Calendar.getInstance();
        String formatted = calendar.getTime() + " | " + message;
        System.out.println(formatted);
        if (singleton != null)
            singleton.view.getTaDebugLog().append(formatted + "\n");
    }

    private void tick() {
        ticks++;
        log("tick: " + ticks);
        model.tick(ticks);
    }
}
