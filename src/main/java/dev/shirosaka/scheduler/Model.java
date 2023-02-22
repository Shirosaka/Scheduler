package dev.shirosaka.scheduler;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class Model extends AbstractTableModel {
    private final int elapsedTime = 0;
    private final Scheduler scheduler;

    private final List<Process> processes = new ArrayList<>(List.of(
            new Process("A", 0, "C"),
            new Process("B", 2, "C"),
            new Process("C", 4, "C")
    ));

    private final List<String> columns = new ArrayList<>(List.of("Name", "Start priority"));

    public Model() {
        scheduler = new Scheduler(processes);
    }

    @Override
    public int getRowCount() {
        return 1 + processes.size();
    }

    @Override
    public int getColumnCount() {
        return elapsedTime;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return processes.get(rowIndex).getName();
        } else if (columnIndex == 1) {
            return processes.get(rowIndex).getOriginalPriority();
        }

        if (columns.size() < 3)
            return null;

        var computeNum = columnIndex - 2;


        return null;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return super.isCellEditable(rowIndex, columnIndex);
    }
}
