import java.util.*;
public class SchedulingAlgorithms {
private Queue<PCB> readyQueue;
private SystemCall SystemCall;
private LinkedList<PCB> terminatedProccesses;
private int totalProccesses;
private int currentTime=0;
private String ganttChart = "";
private String ganttTimes = String.format("%-7d", 0);;




public SchedulingAlgorithms(Queue<PCB> readyQueue, SystemCall systemCalls, LinkedList<PCB> terminatedProccesses,
	int totalProccesses, int currentTime) {
	this.readyQueue = readyQueue;
	this.SystemCall = systemCalls;
	this.terminatedProccesses = terminatedProccesses;
	this.totalProccesses = totalProccesses;
	this.currentTime = currentTime;
}
	
	
	
public void SJF() {
	currentTime=0;
	ArrayList<PCB> workingQueue = new ArrayList<>();
	System.out.println("========================================================");
	System.out.println("Shortest Job First Executing "+"[Time: "+currentTime+"ms ]");
	System.out.println("========================================================");
	
	while(terminatedProccesses.size() < totalProccesses) {
		transferProccesses(workingQueue); //helper
		if(workingQueue.isEmpty()) { waits(); continue; }
		
		workingQueue.sort((a,b) -> a.getBurstTime() - b.getBurstTime() );    //sort by shortest burst time
		PCB proccess = workingQueue.remove(0);
		execute(proccess);
		
		
	}
}
	
	
public void RR()	{
	currentTime=0;
	int quantum = 5;
	System.out.println("===========================Round Robin Executing [Time: " +currentTime+ "ms & Quantum: 5ms]=============================");

	while(terminatedProccesses.size() < totalProccesses) {
		PCB currentP;
		
		synchronized (readyQueue) {
			if(readyQueue.isEmpty()) {waits();continue;}
			currentP = readyQueue.remove();
		
		}	
		if(currentP.getStartTime() == -1) {
			currentP.setStartTime(currentTime);
			//********************************************************************************************* 
			//SystemCall.setTime(currentTime);
			//SystemCall.createProcess(currentP.getProcessID());
		}
		
		
		currentP.setState(PCB.State.RUNNING);
		
		int execTime = Math.min(quantum, currentP.getRemainingBurst());
		ganttChart += String.format("| %-4s ", "P" + currentP.getProcessID());	
		currentTime += execTime;	
		ganttTimes += String.format("%-7d", currentTime);		
		currentP.setRemainingBurst(currentP.getRemainingBurst()- execTime);
		
		
		
		
		if(currentP.getRemainingBurst() == 0) {
			
			currentP.setTerminationTime(currentTime);
			currentP.setTurnAroundTime(currentP.getTerminationTime());
			currentP.setWaitingTime(currentP.getTerminationTime()-currentP.getBurstTime());
			currentP.setState(PCB.State.TERMINATED);
			
			
			SystemCall.setTime(currentTime);
			//SystemCall.createProcess(currentP.getProcessID());
			SystemCall.terminateProcess(currentP.getProcessID());
			

			terminatedProccesses.add(currentP);
			SystemCall.deallocateMemory(currentP.getProcessID(), currentP.getMemoryRequired());	
		}
		else{
			currentP.setState(PCB.State.READY);
			synchronized(readyQueue) {
				readyQueue.add(currentP);
			}
		}

	}

	}
		

public void Priority() {
	currentTime=0;
	ArrayList<PCB> workingQueue = new ArrayList<PCB>();
	System.out.println("========================================================");
	System.out.println("Priority Executing "+"[Time: "+currentTime+" ms]");
	System.out.println("========================================================");
	while(terminatedProccesses.size() < totalProccesses) {
	
		synchronized(readyQueue){
			while(!readyQueue.isEmpty()) {
				workingQueue.add(readyQueue.remove());
			}
		}
		

		if(workingQueue.isEmpty()) {waits(); continue;}
		for(PCB proccess : workingQueue) {
			proccess.setWaitingTime(currentTime-proccess.getArrivalTime());
		}
		
		Aging(workingQueue);
		
		workingQueue.sort((a,b) -> {if(a.getPriority()!=b.getPriority()) {return a.getPriority() -b.getPriority();} return a.getArrivalOrder() - b.getArrivalOrder(); } );
		int starvationlimit = 3 *5;
		//int starvationlimit = workingQueue.size()*5
		PCB current = workingQueue.remove(0);
		if(current.getWaitingTime()>starvationlimit ) {
    		current.markStarved();
    		System.out.println("Proccess"+current.getProcessID() +" IS STARVING");
}
		current.setState(PCB.State.RUNNING);
		SystemCall.setTime(currentTime);
		SystemCall.createProcess(current.getProcessID());
		System.out.println("Time:"+currentTime+ "[ Proccess P: " +current.getProcessID() +" | Priority: "+current.getPriority()+" ]");
		current.setStartTime(currentTime);
		ganttChart += String.format("| %-4s ", "P" + current.getProcessID());		
		currentTime+= current.getBurstTime();
		ganttTimes += String.format("%-7d", currentTime);		
		current.setTerminationTime(currentTime);
		current.setTurnAroundTime(current.getTerminationTime()-current.getArrivalTime());
		current.setWaitingTime(current.getTurnAroundTime()-current.getBurstTime());
		
	
		
		
		/*if(current.getWaitingTime()>starvationlimit) {
			current.markStarved();
			System.out.println("Proccess"+current.getProcessID() +" IS STARVING");}*/
		
		
			current.setState(PCB.State.TERMINATED);
			SystemCall.setTime(currentTime);

			SystemCall.terminateProcess(current.getProcessID());
			SystemCall.deallocateMemory(current.getProcessID(), current.getMemoryRequired());
			terminatedProccesses.add(current);
			System.out.println("[ Proccess P: " +current.getProcessID() +" has finished execution ]");

		}
	}






	
	
	/*////////////////////////////////////////////////////////////
	                         Helper Methods
	*/////////////////////////////////////////////////////////////

public void transferProccesses(ArrayList<PCB> workingQueue) {
	synchronized(readyQueue){
		while(!readyQueue.isEmpty()) {
			PCB proccess = readyQueue.remove();
			workingQueue.add(proccess);
		}
	}
}
    

public void waits() {
	try {
		Thread.sleep(30);
	}catch(InterruptedException e) {e.printStackTrace();}
}
	
	
	
	
public void execute(PCB p) {
	p.setStartTime(currentTime);   //proccess starts
	p.setState(PCB.State.RUNNING);
	
	SystemCall.setTime(currentTime);
		SystemCall.createProcess(p.getProcessID());
		ganttChart += String.format("| %-4s ", "P" + p.getProcessID());		
		currentTime += p.getBurstTime();
		ganttTimes += String.format("%-7d", currentTime);	
	
	
	finishExecution(p);
}
	
	public void finishExecution(PCB p) {
		p.setTerminationTime(currentTime);
		p.setTurnAroundTime(p.getTerminationTime()-p.getArrivalTime());
		p.setWaitingTime(p.getTurnAroundTime()-p.getBurstTime());
		p.setState(PCB.State.TERMINATED);
		
		SystemCall.setTime(currentTime);
		
		
		SystemCall.terminateProcess(p.getProcessID());
		SystemCall.deallocateMemory(p.getProcessID(), p.getMemoryRequired());
		terminatedProccesses.add(p);
		System.out.println("At Time:"+currentTime+ "[ Proccess P: " +p.getProcessID() +" Executed from : "+ p.getStartTime() +" to "+p.getTerminationTime() +" Total Burst Time: "+p.getBurstTime()+" ]");
	}
	
	
	public void Aging(ArrayList<PCB> workingQueue) {
		for(PCB proccess : workingQueue) {
			if(proccess.getWaitingTime()>= 4 && proccess.getPriority() > 1) {
				proccess.setPriority(proccess.getPriority() -1);
			}
		}
	}
	public String getGanttChart() {
	return ganttChart;
}
public String getGanttTimes() {
	return ganttTimes;
}
	
	
}
