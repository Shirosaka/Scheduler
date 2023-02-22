package dev.shirosaka.scheduler;

import javax.swing.*;
import java.awt.*;

public class View extends JFrame {
    // Table stuff
    private final JTable table;
    private final JButton btnCreate;
    private final JButton btnDelete;

    // Debug stuff
    private final JTextArea taDebugLog;

    private final JTabbedPane tabbedPane;

    public View() {
        setSize(1280, 720);
        setTitle("Scheduler by Shirosaka");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Table stuff
        final JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(table = new JTable(), BorderLayout.CENTER);

        final JPanel tableManipulationPanel = new JPanel();
        btnCreate = new JButton("Create");
        btnDelete = new JButton("Delete");
        tableManipulationPanel.add(btnCreate);
        tableManipulationPanel.add(btnDelete);
        tablePanel.add(tableManipulationPanel, BorderLayout.SOUTH);

        // Debug stuff
        taDebugLog = new JTextArea();
        taDebugLog.setEditable(false);
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

    public JTextArea getTaDebugLog() {
        return taDebugLog;
    }
}
