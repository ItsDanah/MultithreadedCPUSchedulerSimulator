package os;

public class Memory {
	public final int MAX_MEMORY = 2048;
	private int availableMemory ;
	
	public Memory() {
		this.availableMemory= MAX_MEMORY;
		
	}

	public  synchronized boolean allocateMemory(int size ) {
		if (availableMemory >= size) {
			availableMemory = availableMemory -size;
			return true;
		}
		System.out.println("No Available Memory");
		return false ;
	}
	public synchronized void deallocateMemory( int size ) {
		availableMemory = availableMemory + size ;
		
	}
	public synchronized int getavailableMemory() {
		return availableMemory;
	}
}
