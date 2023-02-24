package dev.shirosaka.scheduler;

import javax.swing.*;
import java.awt.*;

public class View extends JFrame {
    // Table stuff
    private final JTable table;
    private final JButton btnCreate;
    private final JButton btnDelete;
    private final JButton btnManualTick;

    // Debug stuff
    private final JTextArea taDebugLog;

    private final JTabbedPane tabbedPane;

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

        final JPanel tableManipulationPanel = new JPanel();
        btnCreate = new JButton("Create");
        btnDelete = new JButton("Delete");
        btnManualTick = new JButton("Manual tick");
        tableManipulationPanel.add(btnCreate);
        tableManipulationPanel.add(btnDelete);
        tableManipulationPanel.add(btnManualTick);
        tablePanel.add(tableManipulationPanel, BorderLayout.SOUTH);

        // Debug stuff
        taDebugLog = new JTextArea();
        taDebugLog.setEditable(false);
        taDebugLog.setLineWrap(true);
        final JScrollPane debugScrollPane = new JScrollPane(taDebugLog);
        debugScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        debugScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Table", tablePanel);
        tabbedPane.addTab("Debug", debugScrollPane);
        tabbedPane.setSelectedIndex(1);
        add(tabbedPane);
    }

    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    public JTable getTable() {
        return table;
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

    public JTextArea getTaDebugLog() {
        return taDebugLog;
    }
}
