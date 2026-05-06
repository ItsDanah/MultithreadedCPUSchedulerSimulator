package os;
import java.util.Queue;

public class JobLoaderThread extends Thread{
	
	private Queue<PCB> jobQueue;
	private Queue<PCB> readyQueue;
	private SystemCall systemCalls;
	 private JobReaderThread readerThread;
	private volatile boolean running = true;
	
	  public JobLoaderThread(Queue<PCB> jobQueue, Queue<PCB> readyQueue,SystemCall systemCall, JobReaderThread readerThread) {
this.jobQueue = jobQueue;
this.readyQueue = readyQueue;
this.systemCalls = systemCall;
this.readerThread = readerThread;
}
 public  void run() {
 
	 System.out.println("Job Loader Started At Time 0 ms");
	 
	 while (running && (readerThread.isAlive() || !jobQueue.isEmpty())) {
         synchronized (jobQueue) {
             if (jobQueue.isEmpty()) {
                pauseUntilJobsArrive();
             } else {
                 LoadJob();
             }
         }
     }
     System.out.println("[LOADER] Finished at " + systemCalls.getRealTime());
     
 }
 private void pauseUntilJobsArrive() {
	 try {
         jobQueue.wait(100);
     } catch (InterruptedException e) {
         running = false;
     }
 }
 private void LoadJob() {
     PCB nextJob = jobQueue.peek();
     if (memoryIsAvailable(nextJob)) {
    	 moveToReadyQueue(nextJob); 
     } else {
         pauseUntilMemoryFrees();
     }
 }
 
 private boolean memoryIsAvailable(PCB job) {
     return systemCalls.allocateMemory( job.getProcessID(),job.getMemoryRequired() );
 }
 private void moveToReadyQueue(PCB job) {
	    jobQueue.poll();
	    job.setState(PCB.State.READY);
	    systemCalls.createProcess(job.getProcessID());
	    synchronized (readyQueue) {
	        readyQueue.add(job);
	    }
	    System.out.println("LOADER THREAD " + systemCalls.getRealTime()
	            + " | P" + job.getProcessID()
	            + " Loaded to Ready Queue | Memory remaining: "
	            + systemCalls.getAvailableMemory() + " MB");
	}
 
 private void pauseUntilMemoryFrees() {
     System.out.println("[LOADER]  No enough memory for P"
             + jobQueue.peek().getProcessID()
             + " | Waiting... ("
             + systemCalls.getAvailableMemory() + " MB available)");
     try {
         jobQueue.wait(50);
     } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
         running = false;
     }
 }

 public void stopLoading() {
     running = false;
 }
	 
 }
	
    


