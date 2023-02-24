package dev.shirosaka.scheduler;

import org.apache.commons.lang3.ArrayUtils;

import javax.swing.table.AbstractTableModel;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Model extends AbstractTableModel {
    private final List<Process> processes = new ArrayList<>();
    // processindex, (tickindex, new priority)
    // used for keeping track of new priority updates on the table
    private final Map<Integer, List<Map.Entry<Integer, Integer>>> processPriorityList = new HashMap<>();
    private final List<String> columns = new ArrayList<>(List.of("Name", "Start priority"));

    private int currentTick = 0;

    public void addProcess(Process proc) {
        if (proc == null || processes.contains(proc))
            return;
        var i = processes.size();
        processes.add(proc);
        fireTableRowsInserted(i, i);
        Controller.log("Added process " + proc.getName() + "@" + i + " with work " + proc.getWork());
    }

    public void deleteProcesses(int[] rows) {
        if (rows[0] < 0 || rows[rows.length - 1] >= processes.size())
            return;

        ArrayUtils.reverse(rows);
        for (int n : rows) {
            Controller.log(String.format("Deleting process %d", n));
            processPriorityList.remove(n);
            processes.remove(processes.get(n));
        }

        fireTableRowsDeleted(rows[0], rows[rows.length - 1]);
    }

    public List<Process> getProcesses() {
        return processes;
    }

    public void tick(int tick) {
        Controller.log("Model::tick()");
        columns.add("Tick " + tick);
        currentTick = tick;
        fireTableStructureChanged();
    }

    public void updateCurrentProcessPriority(Process curProc) {
        var pid = processes.indexOf(curProc);

        if (!processPriorityList.containsKey(pid)) {
            Controller.log(String.format("Added processPriorityList entry for pid %d", pid));
            processPriorityList.put(pid, new ArrayList<>());
        }

        // hack to show the updated priority at the correct location
        var tick = currentTick > 0 ? currentTick - 1 : currentTick;

        Controller.log(String.format("Added currentProcessPriority for pid %d with priority %d and tick(s) %d",
                pid,
                curProc.getPriority(),
                tick));
        var entry = new AbstractMap.SimpleEntry<>(tick, curProc.getPriority());
        processPriorityList.get(pid).add(entry);
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

        var tick = columnIndex - 2;
        var p = processes.get(rowIndex);

        return switch (p.getHistory().get(tick)) {
            case COMPUTING -> checkAndAppendPrio(rowIndex, tick, "X");
            case IO -> checkAndAppendPrio(rowIndex, tick, "I");
            case WAITING -> checkAndAppendPrio(rowIndex, tick, "S");
            case FINISHED -> checkAndAppendPrio(rowIndex, tick, "F");
        };
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    private String checkAndAppendPrio(int pid, int tick, String val) {
        if (!processPriorityList.containsKey(pid))
            return val;

        var list = processPriorityList.get(pid);
        var maybeIdx = list.stream().filter(e -> e.getKey() == tick).findFirst();

        if (maybeIdx.isEmpty())
            return val;

        return String.format("%s (%d)", val, maybeIdx.get().getValue());
    }
}
