package dev.shirosaka.scheduler;

import javax.swing.*;
import java.awt.*;

public class View extends JFrame {
    // Table stuff
    private final JTable table;
    private final JButton btnRun;
    private final JButton btnCreate;
    private final JButton btnDelete;
    private final JButton btnManualTick;
    private final JButton btnReset;

    // Debug stuff
    private final JTextArea taDebugLog;

    public View() {
        setSize(1280, 720);
        setTitle("Scheduler by Shirosaka");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Table stuff
        final JPanel tablePanel = new JPanel(new BorderLayout());
        table = new JTable();
        table.setFocusable(false);

        final JScrollPane tableScrollPane = new JScrollPane(table);
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);

        final JPanel tableTopContent = new JPanel(new GridLayout(2, 1));
        final JLabel labelTableInfo = new JLabel("X = Computing work, X (number) = Computing work with priority change, I = IO work, Empty = waiting, F = Finished", SwingConstants.CENTER);
        final JPanel tableManipulationPanel = new JPanel();
        btnRun = new JButton("Run");
        btnCreate = new JButton("Create");
        btnDelete = new JButton("Delete");
        btnManualTick = new JButton("Manual tick");
        btnReset = new JButton("Reset");
        tableManipulationPanel.add(labelTableInfo);
        tableManipulationPanel.add(btnRun);
        tableManipulationPanel.add(btnCreate);
        tableManipulationPanel.add(btnDelete);
        tableManipulationPanel.add(btnManualTick);
        tableManipulationPanel.add(btnReset);
        tableTopContent.add(labelTableInfo);
        tableTopContent.add(tableManipulationPanel);
        tablePanel.add(tableTopContent, BorderLayout.NORTH);

        // Debug stuff
        taDebugLog = new JTextArea();
        taDebugLog.setEditable(false);
        taDebugLog.setLineWrap(true);
        final JScrollPane debugScrollPane = new JScrollPane(taDebugLog);
        debugScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        debugScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        final JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Table", tablePanel);
        tabbedPane.addTab("Debug", debugScrollPane);
        tabbedPane.setSelectedIndex(1);
        add(tabbedPane);
    }

    public JTable getTable() {
        return table;
    }

    public JButton getBtnRun() {
        return btnRun;
    }

    public JButton getBtnCreate() {
        return btnCreate;
    }

    public JButton getBtnDelete() {
        return btnDelete;
    }

    public JButton getBtnManualTick() {
        return btnManualTick;
    }

    public JButton getBtnReset() {
        return btnReset;
    }

    public JTextArea getTaDebugLog() {
        return taDebugLog;
    }
}
