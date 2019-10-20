package game.spaceplane.network;

import java.io.IOException;
import java.net.*;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class HostSocketMonitor extends SocketMonitor{
        List<DatagramSocket> socketList;

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
       
        public HostSocketMonitor(List<DatagramSocket> sockets, ConcurrentLinkedQueue<DatagramPacket> packets){
                socketList = sockets;
                packetQueue = packets;
        }
       
        public void run(){
    			running = true;
//                for(DatagramSocket socket : socketList){
//                        try {
//                                socket.setSoTimeout(100);
//                        } catch (SocketException e1){
//                                System.out.println("Invalid socket");
//                        }
//                }
                while(running){
                        for(DatagramSocket socket : socketList){
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
}
