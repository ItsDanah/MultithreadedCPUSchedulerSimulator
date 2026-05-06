package os;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class SystemCall {
    private Memory MainMemory;
    private int systemTime; 

    public SystemCall(Memory memory) {
        this.MainMemory = memory;
        this.systemTime = 0;
    }

    // ── Process Control ──────────────────────────────
    public void createProcess(int processId) {
        System.out.println("[SYSTEM CALL] Process " + processId + " created at " + getSystemTime() );
    }

    public void terminateProcess(int processId) {
        System.out.println("[SYSTEM CALL] Process " + processId + " terminated at " + getSystemTime());
    }

    // ── Memory Management ────────────────────────────
    public boolean allocateMemory(int processId, int size) {
        boolean success = MainMemory.allocateMemory(size);
        if (success)
            System.out.println("Memory allocated for P" + processId + " (" + size + " MB)");
        else
            System.out.println(" Memory allocation FAILED for P" + processId + " (" + size + " MB) Just "+getAvailableMemory() + "MB is available");
        return success;
    }

    public void deallocateMemory(int processId, int size) {
        MainMemory.deallocateMemory(size);
        System.out.println(" Memory freed for P" + processId + " (" + size + " MB)");
    }

    // ── Information ──────────────────────────────────
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