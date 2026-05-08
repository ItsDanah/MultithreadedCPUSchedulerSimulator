// ─────────────── This class simulates the RAM ─────────────────────

public class Memory {
	public final int MAX_MEMORY = 2048; // size of memory
	private int availableMemory ; // free memory

	public Memory() {
		this.availableMemory= MAX_MEMORY; // initially memory is fully available
	}

	// ── memory allocation and deallocation ──────────────────────────────────
	public  synchronized boolean allocateMemory(int size ) {
		if (availableMemory >= size) { 
			availableMemory = availableMemory -size; // reserves memory
			return true; // memory successfully allocated
		}
		System.out.println("No Available Memory");
		return false ; // failed to allocate memory
	}

	public synchronized void deallocateMemory( int size ) {
		availableMemory = availableMemory + size ; // frees memory after process termination
	}

	// ── setters and getters ──────────────────────────────────
	public synchronized int getavailableMemory() {
		return availableMemory;
	}
}
