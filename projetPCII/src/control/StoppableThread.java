package control;

public class StoppableThread extends Thread{
	private boolean running = true;
	
	public void terminate() {
		this.running = false;
	}
	
}
