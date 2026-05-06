import java.util.*;

public class Scheduler {

    private Queue<PCB> readyQueue;
    private ArrayList<PCB> completedProcesses;

    private int totalProcesses;
    private int currentTime = 0;

    public Scheduler(Queue<PCB> readyQueue,
                     ArrayList<PCB> completedProcesses,
                     int totalProcesses) {

        this.readyQueue = readyQueue;
        this.completedProcesses = completedProcesses;
        this.totalProcesses = totalProcesses;
    }

    
    // Shortest Job First (Non-Preemptive)
    public void runSJF() {

        System.out.println("\n===== SJF Scheduling Started =====");

        ArrayList<PCB> localQueue = new ArrayList<>();

        while (completedProcesses.size() < totalProcesses) {

            moveProcesses(localQueue);

            if (localQueue.isEmpty()) {
                sleepALittle();
                continue;
            }

            // Shortest burst first
            localQueue.sort((a, b) ->
                    a.getBurstTime() - b.getBurstTime());

            PCB current = localQueue.remove(0);

            executeFully(current);
        }

        System.out.println("===== SJF Scheduling Finished =====");
    }

    // Round Robin
    public void runRR() {

        System.out.println("\n===== Round Robin Started =====");

        int quantum = 5;

        Queue<PCB> localQueue = new LinkedList<>();

        while (completedProcesses.size() < totalProcesses) {

            synchronized (readyQueue) {

                while (!readyQueue.isEmpty()) {
                    localQueue.add(readyQueue.poll());
                }
            }

            if (localQueue.isEmpty()) {
                sleepALittle();
                continue;
            }

            PCB current = localQueue.poll();

            if (current.getStartTime() == -1) {
                current.setStartTime(currentTime);
            }

            current.setState(PCB.State.RUNNING);

            int executionTime =
                    Math.min(quantum,
                    current.getRemainingBurst());

            System.out.println(
                    "[Time " + currentTime + "] " +
                    "Running P" + current.getProcessID() +
                    " for " + executionTime + " ms"
            );

            for (int i = 0; i < executionTime; i++) {

                current.executeOneUnit();

                currentTime++;

                applyWaitingTime(localQueue);
            }

            if (current.isFinished()) {

                finishProcess(current);

            } else {

                current.setState(PCB.State.READY);

                localQueue.add(current);
            }
        }

        System.out.println("===== Round Robin Finished =====");
    }

    
    // Priority Scheduling (Non-Preemptive)
    public void runPriority() {

        System.out.println("\n===== Priority Scheduling Started =====");

        ArrayList<PCB> localQueue = new ArrayList<>();

        while (completedProcesses.size() < totalProcesses) {

            moveProcesses(localQueue);

            if (localQueue.isEmpty()) {
                sleepALittle();
                continue;
            }

            applyAging(localQueue);

            // Smaller number = higher priority
            localQueue.sort((a, b) ->
                    a.getPriority() - b.getPriority());

            PCB current = localQueue.remove(0);

            detectStarvation(current, localQueue.size());

            executeFully(current);
        }

        System.out.println("===== Priority Scheduling Finished =====");
    }

    // Execute Process Completely
    private void executeFully(PCB process) {

        if (process.getStartTime() == -1) {
            process.setStartTime(currentTime);
        }

        process.setState(PCB.State.RUNNING);

        System.out.println(
                "[Time " + currentTime + "] " +
                "Running P" + process.getProcessID()
        );

        while (!process.isFinished()) {

            process.executeOneUnit();

            currentTime++;
        }

        finishProcess(process);
    }

    // Finish Process
    private void finishProcess(PCB process) {

        process.setTerminationTime(currentTime);

        process.calculateTimes();

        process.setState(PCB.State.TERMINATED);

        completedProcesses.add(process);

        System.out.println(
                "[Time " + currentTime + "] " +
                "P" + process.getProcessID() +
                " Finished"
        );
    }

    
    // Move Processes From Ready Queue
    private void moveProcesses(ArrayList<PCB> localQueue) {

        synchronized (readyQueue) {

            while (!readyQueue.isEmpty()) {

                PCB process = readyQueue.poll();

                if (process != null) {

                    process.setState(PCB.State.READY);

                    localQueue.add(process);
                }
            }
        }
    }

    // Every 4 ms:
    // decrease priority number by 1
    private void applyAging(ArrayList<PCB> localQueue) {

        for (PCB process : localQueue) {

            int waitingTime =
                    currentTime - process.getArrivalTime();

            if (waitingTime > 0 &&
                waitingTime % 4 == 0) {

                process.applyAging();

                System.out.println(
                        "[Time " + currentTime + "] " +
                        "Aging Applied to P" +
                        process.getProcessID()
                );
            }
        }
    }

    // Starvation Detection
    private void detectStarvation(PCB process,
                                  int queueSize) {

        int waitingTime =
                currentTime - process.getArrivalTime();

        if (waitingTime > queueSize * 5) {

            process.markStarved();

            System.out.println(
                    "[Time " + currentTime + "] " +
                    "Starvation Detected for P" +
                    process.getProcessID()
            );
        }
    }

    // Increase Waiting Time
private void applyWaitingTime(
            Queue<PCB> queue) {

        for (PCB process : queue) {

            process.setWaitingTime(
                    process.getWaitingTime() + 1);
        }
    }

    // Sleep
 private void sleepALittle() {

        try {

            Thread.sleep(50);

        } catch (InterruptedException e) {

            e.printStackTrace();
        }
    }
}
