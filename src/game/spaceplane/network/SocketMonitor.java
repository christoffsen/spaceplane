package game.spaceplane.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class SocketMonitor extends Thread{
		volatile boolean running;
        ConcurrentLinkedQueue<DatagramPacket> inQueue;
        ConcurrentLinkedQueue<DatagramPacket> outQueue;
        ConcurrentLinkedQueue<DatagramPacket> packetQueue;
        byte[] buffer = new byte[256];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        
        public void kill(){
        	running = false;
        }
}
