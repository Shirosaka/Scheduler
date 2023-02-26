package dev.shirosaka.scheduler;

public abstract class Scheduler {
    public abstract boolean cycle();
    public abstract Process getCurrentProcess();
    public abstract boolean allFinished();
    public abstract void reset();
}
