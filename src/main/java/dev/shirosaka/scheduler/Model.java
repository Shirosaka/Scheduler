package dev.shirosaka.scheduler;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class Model extends AbstractTableModel {
    private final List<Process> processes = new ArrayList<>();
    private final List<String> columns = new ArrayList<>(List.of("Name", "Start priority"));

    public void addProcess(Process proc) {
        if (proc == null || processes.contains(proc))
            return;
        var i = processes.size();
        processes.add(proc);
        fireTableRowsInserted(i, i);
        Controller.log("Added process " + proc.getName() + "@" + i + " with work " + proc.getWork());
    }

    public void removeProcess(Process proc) {
        if (proc == null || !processes.contains(proc))
            return;
        var i = processes.indexOf(proc);
        processes.remove(proc);
        fireTableRowsDeleted(i, i);
        Controller.log("Deleted process " + proc.getName() + "@" + i + " with work " + proc.getWork());
    }

    public List<Process> getProcesses() {
        return processes;
    }

    public void tick(int tick) {
        columns.add("Tick " + tick);
        fireTableStructureChanged();
    }

    @Override
    public String getColumnName(int column) {
        return columns.get(column);
    }

    @Override
    public int getColumnCount() {
        return columns.size();
    }

    @Override
    public int getRowCount() {
        return processes.size();
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

        var tickCol = columnIndex - 2;
        Controller.log("" + tickCol);
        var p = processes.get(rowIndex);

        return switch (p.getHistory().get(tickCol)) {
            case COMPUTING -> "X";
            case IO -> "I";
            case WAITING, FINISHED -> "";
        };
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }


}
