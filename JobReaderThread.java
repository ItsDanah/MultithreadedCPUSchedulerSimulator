// ─────────────── This class reads jobs from a file and put them into a Job Queue ─────────────────────

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Queue;

public class JobReaderThread extends Thread { // the class becomes a Thread that can run concurrently.

	private Queue<PCB> jobQueue; // shared queue between the threads, holds processes waiting to be scheduled
	private String filename; // name of file to be read from

	public JobReaderThread(Queue<PCB> jobQueue, String filename) {
		this.jobQueue = jobQueue; 
		this.filename = filename; 
	}

	// the method that executes automatically when reader.start() is called in main
	public void run() {

		try (BufferedReader br = new BufferedReader(new FileReader(filename))) { 

			String line; // a variable to hold each line of text as it's read
			int arrivalOrder = 0; // tracks the order in which jobs arrive to determine which process came first

			while ((line = br.readLine()) != null) { // reads the file one line at a time

				line = line.trim(); // removes whitespaces/newlines

				if (line.isEmpty()) { // if the line is blank after trimming 
					continue;
				}

				// ── Split memory section  ──────────────────────────────────
				// (processID:burstTime:priority ; memoryRequired)

				// ── Split memoryRequired from the rest  ──
				String[] split1 = line.split(";"); // Splits the line at ";" into an array 
				int memoryRequired = Integer.parseInt(split1[1]); // Parses the second part (after ";") as an integer

				// ── Split PID, burst, priority  ──
				String[] split2 = split1[0].split(":"); // results ["processID", "burstTime", "priority"]

				int processID = Integer.parseInt(split2[0]);

				int burstTime = Integer.parseInt(split2[1]);

				int priority = Integer.parseInt(split2[2]);

				// ── create a process from the gathered attributes  ──────────────────────────────────
				PCB process = new PCB(processID, burstTime, priority, memoryRequired, arrivalOrder);

				// ── add to job queue  ──────────────────────────────────
				synchronized (jobQueue) { // lock the queue, prevents multiple threads from modifying the jobQueue at the same time

					jobQueue.add(process);  // adds to the end of the shared job queue (FIFO)
					jobQueue.notifyAll(); // signals the waiting threads (JobLoaderThread) that a new job has been added to the queue and it is no longer empty
					System.out.println("Added to Job Queue: " + process);
				}
				arrivalOrder++; // so the next process gets the next arrival order number
			}

		} catch (Exception e) {

			System.out.println("Error reading file.");
			e.printStackTrace();
		}

		System.out.println("Job Reader Thread Finished.");
	}
}
