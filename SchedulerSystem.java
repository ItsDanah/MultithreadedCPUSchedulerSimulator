import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class SchedulerSystem {

    public static void main(String[] args) {

        Queue<PCB> jobQueue = new LinkedList<>();
        Queue<PCB> readyQueue = new LinkedList<>();
        LinkedList<PCB> terminatedProcesses = new LinkedList<>();

        Memory memory = new Memory();
        SystemCall systemCall = new SystemCall(memory);

        System.out.println(" CPU Scheduling Simulator ");
        System.out.println("System Started At: "+ systemCall.getRealTime());
        System.out.println("Available Memory: "+ systemCall.getAvailableMemory()+ " MB");

        //Job Reader
        JobReaderThread readerThread = new JobReaderThread(jobQueue,"job.txt");
        //Job Loader
        JobLoaderThread loaderThread = new JobLoaderThread(jobQueue,readyQueue,systemCall,readerThread);

        readerThread.start();
        loaderThread.start();

        try {
                readerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        while (true) {
            synchronized (jobQueue) { 
                if (jobQueue.isEmpty()) {
                         break;
                }
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Count Total Processes
        int totalProcesses;

        synchronized (readyQueue) {
            totalProcesses = readyQueue.size();
        }

        Scanner input = new Scanner(System.in);
        System.out.println("\nChoose Scheduling Algorithm:");
        System.out.println("1. Shortest Job First (SJF)");
        System.out.println("2. Round Robin (Quantum = 5 ms)");
        System.out.println("3. Priority Scheduling");
        System.out.print("\nEnter Choice: ");

        int choice = input.nextInt();

        SchedulingAlgorithms scheduler = new SchedulingAlgorithms(readyQueue,systemCall,terminatedProcesses,totalProcesses,0);

        switch (choice) {
            case 1:
                scheduler.SJF();
                break;

            case 2:
                scheduler.RR();
                break;

            case 3:
                scheduler.Priority();
                break;

            default:
                System.out.println("Invalid Choice");
                return;
        }
        System.out.println("\n***GANTT CHART***");
        System.out.println(scheduler.getGanttChart() + "|");
        System.out.println(scheduler.getGanttTimes());

        loaderThread.stopLoading();

        System.out.println(" Execution Results ");

        double totalWaiting = 0;
        double totalTurnaround = 0;

        System.out.println("\n***PROCESS TABLE***");
        System.out.printf("%-10s %-10s %-12s %-15s %-15s %-15s\n","Process", "Burst", "Start", "Termination", "Waiting", "Turnaround");
        terminatedProcesses.sort((a, b) -> a.getProcessID() - b.getProcessID());
        for (PCB process : terminatedProcesses) {
            totalWaiting += process.getWaitingTime();
            totalTurnaround += process.getTurnAroundTime();
            System.out.printf("%-10s %-10d %-12d %-15d %-15d %-15d\n",
            "P" + process.getProcessID(),
            process.getBurstTime(),
            process.getStartTime(),
            process.getTerminationTime(),
            process.getWaitingTime(),
            process.getTurnAroundTime());
            }
        /*for (PCB process : terminatedProcesses) {
                totalWaiting += process.getWaitingTime();

            totalTurnaround += process.getTurnAroundTime();

            System.out.println("\nProcess P" + process.getProcessID());
            System.out.println("State: " + process.getState());
            System.out.println("Waiting Time: " + process.getWaitingTime());
            System.out.println("Turnaround Time: " + process.getTurnAroundTime());
            System.out.println("Starvation: " + process.getStarvation());
        }*/
        double averageWaiting = totalWaiting / terminatedProcesses.size();
        double averageTurnaround = totalTurnaround / terminatedProcesses.size();

        System.out.println("\n***PERFORMANCE METRICS***");
        System.out.printf("Average Waiting Time: %.2f ms\n", averageWaiting);
        System.out.printf("Average Turnaround Time: %.2f ms\n", averageTurnaround);

        /*if (!terminatedProcesses.isEmpty()) {
            double averageWaiting = totalWaiting / terminatedProcesses.size();
            double averageTurnaround = totalTurnaround / terminatedProcesses.size();

            System.out.println("Average Waiting Time: " + averageWaiting);
            System.out.println("Average Turnaround Time: " + averageTurnaround);
            System.out.println("Remaining Memory: " + systemCall.getAvailableMemory() + " MB");}

        System.out.println("\nSimulation Finished.");
        input.close();
    }*/
}}
