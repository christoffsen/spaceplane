package game.spaceplane.network;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DummyClient extends Thread{
	InetAddress localhost;
	ConcurrentLinkedQueue<DatagramPacket> queue;
	private volatile boolean running = true;
	byte [] buffer = {Byte.MIN_VALUE, Byte.MIN_VALUE, Byte.MAX_VALUE};
	
	public DummyClient(ConcurrentLinkedQueue<DatagramPacket> packets){
		queue = packets;
	}
	
	@Override
	public void run() {
		while(running){
			queue.add(new DatagramPacket(buffer, buffer.length));
			try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
					continue;
			}
		}
		
	}
	
	public void setRunning(boolean bool){
		running = bool;
	}

}
