package control;

public class StoppableThread extends Thread{
	protected boolean running = true;
	public void terminate() {
		this.running = false;
	}
	public void restart() {
		this.running = true;
	}
	
}
