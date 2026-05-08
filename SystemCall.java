// ─────────────── This class simulates operating system services ─────────────────────

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class SystemCall {
    private Memory MainMemory; // for memory management
    private int systemTime; // simulated system time

    public SystemCall(Memory memory) {
        this.MainMemory = memory; // for memory management
        this.systemTime = 0; // simulated system time
    }

    // ── Process Control ──────────────────────────────
    public void createProcess(int processId) { // prints process creation message
        System.out.println("[SYSTEM CALL] Process " + processId + " created at " + getSystemTime() );
    }

    public void terminateProcess(int processId) { // prints termination message
        System.out.println("[SYSTEM CALL] Process " + processId + " terminated at " + getSystemTime());
    }

    // ── Memory Management ────────────────────────────
    public boolean allocateMemory(int processId, int size) { 
        boolean success = MainMemory.allocateMemory(size); // allocates memory through the Memory class
        if (success)
            System.out.println("Memory allocated for P" + processId + " (" + size + " MB)");
        else
            System.out.println(" Memory allocation FAILED for P" + processId + " (" + size + " MB) Just "+getAvailableMemory() + "MB is available");
        return success;
    }

    public void deallocateMemory(int processId, int size) {
        MainMemory.deallocateMemory(size); // deallocates memory through the Memory class
        System.out.println(" Memory freed for P" + processId + " (" + size + " MB)");
    }

    // ── setters and getters ──────────────────────────────────
    public void setTime(int time) {
        this.systemTime = time;
    }

    public int getSystemTime() {
        return systemTime;
    }

    public String getRealTime() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    public int getAvailableMemory() {
        return MainMemory.getavailableMemory();
    }
}
