package game.spaceplane.network;

import java.io.IOException;
import java.net.*;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ClientSocketMonitor extends SocketMonitor{
        DatagramSocket socket;
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
       
        public ClientSocketMonitor(DatagramSocket hostConnection, ConcurrentLinkedQueue<DatagramPacket> packets){
                socket = hostConnection;
                packetQueue = packets;
                
        }
       
        public synchronized void run(){
        		running = true;
                while(running){
                        try{
                                socket.receive(packet);
                                packetQueue.add(packet);
                        }
                        catch(SocketTimeoutException e){
                                continue;
                        }
                        catch(IOException ioe){
                                ioe.printStackTrace();
                        }
                }
        }
}
