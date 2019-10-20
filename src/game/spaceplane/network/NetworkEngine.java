package game.spaceplane.network;

import game.spaceplane.helpers.PackageHelper;
import game.spaceplane.physics.Asteroid;
import game.spaceplane.physics.AsteroidL;
import game.spaceplane.physics.AsteroidM;
import game.spaceplane.physics.AsteroidS;
import game.spaceplane.physics.Body;
import game.spaceplane.physics.GunAttack;
import game.spaceplane.physics.Spaceship;

import game.spaceplane.helpers.BinaryHelper;

import java.io.IOException;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import android.util.Log;

public class NetworkEngine {
    DummyClient dc;
    ConcurrentLinkedQueue<DatagramPacket> packetQueue;
    Short clientPort = 4713; // currently unused
    int hostPort = 4712;
    final int MAX_CLIENTS = 1;
    volatile boolean waitingForClients = true;
    DatagramSocket hostSocket;
    InetAddress clientAddress;
    InetAddress hostAddress;
    List<DatagramSocket> clients;
    DatagramSocket client;
    SocketMonitor monitor;
    private boolean DEBUG = false;

    public NetworkEngine() {
        packetQueue = new ConcurrentLinkedQueue<DatagramPacket>();
    }

    // change this after one-client testing
    public void startHost() {
        reset();
        byte[] buffer = new byte[6];
        DatagramPacket pack = new DatagramPacket(buffer, buffer.length);
        try {
            hostSocket = new DatagramSocket(hostPort);
        } catch (SocketException e) {
            Log.d(e.toString(), "Exception:" + e.getMessage(), e.getCause());
        }
        clients = new LinkedList<DatagramSocket>();
        if (DEBUG) {
            clients.add(hostSocket);
        } else {
            // while(waitingForClients){
            try {
                // DatagramSocket newClient = new DatagramSocket(clientPort++);
                hostSocket.receive(pack);

                clients.add(hostSocket);
                clientAddress = pack.getAddress();
                buffer = "hello".getBytes();
                DatagramPacket ack = new DatagramPacket(buffer, buffer.length,
                        clientAddress, hostPort);
                hostSocket.send(ack);
                System.out.println("");
            } catch (SocketTimeoutException e) {
                Log.d(e.toString(), "Exception:" + e.getMessage(), e.getCause());
                System.out.println("Socket timed out.");
            } catch (IOException e) {
                Log.d(e.toString(), "Exception:" + e.getMessage(), e.getCause());
                setWaiting(false);
            }
            // }
            monitor = new HostSocketMonitor(clients, packetQueue);
            monitor.start();
        }
    }

    public void startClient(InetAddress hostAddress) {
        reset();
        try {
            hostSocket = new DatagramSocket(hostPort);
            byte[] buffer = "hello".getBytes();
            DatagramPacket hello = new DatagramPacket(buffer, buffer.length,
                    hostAddress, hostPort);
            hostSocket.send(hello);

            buffer = new byte[4];
            DatagramPacket ack = new DatagramPacket(buffer, buffer.length);
            hostSocket.receive(ack);

            this.hostAddress = hostAddress;

            Log.d("network", "Packet sent.");
            monitor = new ClientSocketMonitor(hostSocket, packetQueue);
            monitor.start();
        } catch (SocketException e) {
            Log.d(e.toString(), "Exception:" + e.getMessage(), e.getCause());
        } catch (IOException e) {
            Log.d(e.toString(), "Exception:" + e.getMessage(), e.getCause());
        }
    }

    public void sendUpdatesToClients(List<Body> bodies) {
        List<Byte> byteList = new ArrayList<Byte>();
        byteList.clear();

        // asteroids - 5B each
        for (Body b : bodies) {
            if (!b.isShip() && !b.isGun()) {
                Integer i = b.GetAsteroidType();
                Byte asteroidSize = i.byteValue();
                byteList.add(asteroidSize); // 1 byte

                short aPosX = (short) Math.round(b.getPosition().getHead().X);
                byte[] posX = BinaryHelper.makeBytesLittleEndian(aPosX);
                byteList.add(posX[0]); // 2 bytes
                byteList.add(posX[1]); // 3 bytes

                Short aPosY = (short) Math.round(b.getPosition().getHead().Y);
                byte[] posY = BinaryHelper.makeBytesLittleEndian(aPosY);
                byteList.add(posY[0]); // 4 bytes
                byteList.add(posY[1]); // 5 bytes

            }
        }
        // flag - 3B
        byteList.add(Byte.MIN_VALUE);
        byteList.add((byte) 0);
        byteList.add(Byte.MAX_VALUE);
        // ships - 21B
        for (Body b : bodies) {
            if (b.isShip()) {
                Spaceship ship = (Spaceship) b;
                Short playerNo = (short) ship.getPlayerNum();
                byteList.add(playerNo.byteValue()); // 1 byte

                Double sDirX = ship.getDirection().getHead().X;
                byte[] dirX = BinaryHelper.makeBytesLittleEndian(sDirX);
                byteList.add(dirX[0]); // 2 bytes
                byteList.add(dirX[1]); // 3 bytes
                byteList.add(dirX[2]); // 4 bytes
                byteList.add(dirX[3]); // 5 bytes
                byteList.add(dirX[4]); // 6 bytes
                byteList.add(dirX[5]); // 7 bytes
                byteList.add(dirX[6]); // 8 bytes
                byteList.add(dirX[7]); // 9 bytes

                Double sDirY = ship.getDirection().getHead().Y;
                byte[] dirY = BinaryHelper.makeBytesLittleEndian(sDirY);
                byteList.add(dirY[0]); // 10 bytes
                byteList.add(dirY[1]); // 11 bytes
                byteList.add(dirY[2]); // 12 bytes
                byteList.add(dirY[3]); // 13 bytes
                byteList.add(dirY[4]); // 14 bytes
                byteList.add(dirY[5]); // 15 bytes
                byteList.add(dirY[6]); // 16 bytes
                byteList.add(dirY[7]); // 17 bytes

                Short sPosX = (short) Math.round(b.getPosition().getHead().X);
                byte[] posX = BinaryHelper.makeBytesLittleEndian(sPosX);
                byteList.add(posX[0]); // 18 bytes
                byteList.add(posX[1]); // 19 bytes

                Short sPosY = (short) Math.round(b.getPosition().getHead().Y);
                byte[] posY = BinaryHelper.makeBytesLittleEndian(sPosY);
                byteList.add(posY[0]); // 20 bytes
                byteList.add(posY[1]); // 21 bytes

            }

        }
        // flag - 3B
        byteList.add(Byte.MIN_VALUE);
        byteList.add((byte) 0);
        byteList.add(Byte.MAX_VALUE);
        // bullets - 5B each
        for (Body body : bodies) {
            if (body.isGun()) {
                GunAttack gun = (GunAttack) body;

                Short gPosX = (short) Math.round(gun.getPosition().getHead().X);
                byte[] posX = BinaryHelper.makeBytesLittleEndian(gPosX);
                byteList.add(posX[0]); // 1 byte
                byteList.add(posX[1]); // 2 bytes

                Short gPosY = (short) Math.round(gun.getPosition().getHead().Y);
                byte[] posY = BinaryHelper.makeBytesLittleEndian(gPosY);
                byteList.add(posY[0]); // 3 bytes
                byteList.add(posY[1]); // 4 bytes
                
                Integer i = gun.getOwner();
                byteList.add(i.byteValue());
            }

        }
        // flag - 3B
        byteList.add(Byte.MIN_VALUE);
        byteList.add((byte) 0);
        byteList.add(Byte.MAX_VALUE);

        byte[] byteArray = PackageHelper.MakeByteArray(byteList);

        DatagramPacket update = new DatagramPacket(byteArray, byteArray.length,
                clientAddress, hostPort);
        try {
            hostSocket.send(update);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendInputToServer(Boolean rotating, Boolean accelerating,
            Boolean firing) {
        byte[] buffer = new byte[3];

        if (rotating == null)
            buffer[0] = 0;
        else if (rotating)
            buffer[0] = Byte.MAX_VALUE;
        else
            buffer[0] = Byte.MIN_VALUE;

        if (accelerating)
            buffer[1] = Byte.MAX_VALUE;
        else
            buffer[1] = Byte.MIN_VALUE;

        if (firing)
            buffer[2] = Byte.MAX_VALUE;
        else
            buffer[2] = Byte.MIN_VALUE;

        DatagramPacket input = new DatagramPacket(buffer, buffer.length,
                hostAddress, hostPort);
        try {
            hostSocket.send(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public CopyOnWriteArrayList<Boolean> getClientInput() {
        CopyOnWriteArrayList<Boolean> RotAccelFire = new CopyOnWriteArrayList<Boolean>();
        Boolean rotating;
        Boolean accelerating;
        Boolean firing;
        if (!packetQueue.isEmpty()) {
            byte[] data = packetQueue.remove().getData();

            switch (data[0]) {
            case Byte.MAX_VALUE:
                rotating = true;
                break;
            case Byte.MIN_VALUE:
                rotating = false;
                break;
            default:
                rotating = null;
            }

            switch (data[1]) {
            case Byte.MAX_VALUE:
                accelerating = true;
                break;
            case Byte.MIN_VALUE:
                accelerating = false;
                break;
            default:
                accelerating = null;
            }

            switch (data[2]) {
            case Byte.MAX_VALUE:
                firing = true;
                break;
            case Byte.MIN_VALUE:
                firing = false;
                break;
            default:
                firing = null;
            }
        } else {
            rotating = null;
            accelerating = false;
            firing = false;
        }
        RotAccelFire.add(rotating);
        RotAccelFire.add(accelerating);
        RotAccelFire.add(firing);

        return RotAccelFire;

    }

    public CopyOnWriteArrayList<Body> getServerState() {
        CopyOnWriteArrayList<Body> result = null;
        if (!packetQueue.isEmpty()) {
            byte[] data = packetQueue.remove().getData();
            result = new CopyOnWriteArrayList<Body>();

            int i = 0;
            while (true) {
                if (data[i] == Byte.MIN_VALUE) {
                    if (data[i + 1] == 0) {
                        if (data[i + 2] == Byte.MAX_VALUE) {
                            i += 3;
                            break;
                        }
                    }
                }
                Asteroid a = null;
                byte type = data[i];

                byte[] posXB = { data[++i], data[++i] };
                Short posX = BinaryHelper.makeShortLittleEndian(posXB);

                byte[] posYB = { data[++i], data[++i] };
                Short posY = BinaryHelper.makeShortLittleEndian(posYB);

                switch (type) {
                case 1:
                    a = new AsteroidL(posX, posY);
                    break;
                case 2:
                    a = new AsteroidM(posX, posY);
                    break;
                case 3:
                    a = new AsteroidS(posX, posY);
                    break;
                }
                result.add(a);
                i++;
            }

            while (true) {
                if (i < data.length) {
                    if (data[i] == Byte.MIN_VALUE) {
                        if (data[i + 1] == 0) {
                            if (data[i + 2] == Byte.MAX_VALUE) {
                                i += 3;
                                break;
                            }
                        }
                    }
                }
                int playerNo = data[i];

                double sDirX = BinaryHelper.makeDoubleLittleEndian(new byte[] {
                        data[++i], data[++i], data[++i], data[++i], data[++i],
                        data[++i], data[++i], data[++i] });

                double sDirY = BinaryHelper.makeDoubleLittleEndian(new byte[] {
                        data[++i], data[++i], data[++i], data[++i], data[++i],
                        data[++i], data[++i], data[++i] });

                int sPosX = BinaryHelper.makeShortLittleEndian(new byte[] {
                        data[++i], data[++i] });

                int sPosY = BinaryHelper.makeShortLittleEndian(new byte[] {
                        data[++i], data[++i] });

                Spaceship s = new Spaceship(sPosX, sPosY, playerNo);
                s.setForward(sDirX, sDirY);
                result.add(s);
                i++;
            }

            while (true) {
                if (i < data.length - 4) {
                    if (data[i] == Byte.MIN_VALUE) {
                        if (data[i + 1] == 0) {
                            if (data[i + 2] == Byte.MAX_VALUE) {
                                if (i + 3 < data.length) {
                                    i += 3;
                                    break;
                                }
                            }
                        }
                    }
                }
                int posX = BinaryHelper.makeShortLittleEndian(new byte[] {
                        data[i], data[++i] });
                int posY = BinaryHelper.makeShortLittleEndian(new byte[] {
                        data[++i], data[++i] });

                GunAttack g = new GunAttack(posX, posY);
                g.setOwner(data[++i]);
                result.add(g);
                i++;
            }

        }
        return result;

    }

    public static InetAddress makeAddress(String s) {
        try {
            return InetAddress.getByName(s);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<InetAddress> getIP() {
        ArrayList<InetAddress> list = new ArrayList<InetAddress>();
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        list.add(inetAddress);
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("network", ex.toString());
        }
        return list;
    }

    public static InetAddress getLoopbackAddress() {
        InetAddress result = null;
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (inetAddress.isLoopbackAddress()) {
                        result = inetAddress;
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("network", ex.toString());
        }
        return result;
    }

    public static boolean isIPv4(String address) {
        boolean result = true;
        Character c = '`';
        for (int i = 0; i < address.length(); i++) {
            c = address.charAt(i);
            if (Character.isDigit(c)) {
                continue;
            }
            if (c.equals('.')) {
                continue;
            }
            result = false;
            break;
        }
        return result;
    }

    public void setWaiting(boolean waiting) {
        waitingForClients = waiting;
    }

    private void reset() {
        if (hostSocket != null)
            hostSocket.close();
        if (clients != null)
            clients.clear();
        if (monitor != null)
            monitor.kill();
        stopDummyClient();
    }

    public List<DatagramSocket> getClientList() {
        return clients;
    }

    public void startDummyClient() {
        dc = new DummyClient(packetQueue);
        dc.start();
    }

    public void stopDummyClient() {
        if (dc != null) {
            dc.setRunning(false);
        }
    }

}
