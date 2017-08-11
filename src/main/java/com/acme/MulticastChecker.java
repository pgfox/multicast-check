package com.acme;

import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * MulticastChecker - simple broadcast/listener to ensure multicast possible
 * in the env. 
 */
public class MulticastChecker {

    public static void main(String[] args) throws Exception {

        MulticastChecker theApp = new MulticastChecker();
        theApp.runCheck();

    }


    private void runCheck() throws Exception {


        ExecutorService executorService = Executors.newFixedThreadPool(2);

        try {

            String multicastAddress = System.getProperty("MULTICAST_ADDRESS", "231.7.7.7");
            int groupPort = Integer.parseInt(System.getProperty("GROUP_PORT", "9876"));
            String clientID = System.getProperty("CLIENT_ID");

            if (clientID == null) {
                System.out.println("system property -DCLIENT_ID=XXXX is required");
                System.exit(0);
            }

            int sendDelay = Integer.parseInt(System.getProperty("SEND_DELAY", "5"));


            System.out.println("Using MULTICAST_ADDRESS " + multicastAddress);
            System.out.println("Using GROUP_PORT " + groupPort);
            System.out.println("Using CLIENT_ID " + clientID);
            System.out.println("Using SEND_DELAY " + sendDelay);

            InetAddress group = InetAddress.getByName(multicastAddress);


            // create a broadcaster
            MulticastBroadcaster broadcaster = new MulticastBroadcaster(group, groupPort, clientID, sendDelay);
            executorService.submit(broadcaster);


            // create a listener
            MulticastListener multicastListener = new MulticastListener(group, groupPort, clientID);
            executorService.submit(multicastListener);


            System.out.println("Broadcaster and Listener STARTED. Press enter to finish.");
            System.in.read();
            System.out.println("Broadcaster and Listener STOPPING.");
        } finally {

            // cleanup
            if (executorService != null) {
                executorService.shutdownNow();
            }
        }


    }


    class MulticastBroadcaster implements Runnable {

        private final DatagramPacket packet;
        private final String clientID;
        private final DatagramSocket socket;
        private final int sendDelay;


        MulticastBroadcaster(InetAddress group, int port, String clientID, int sendDelay) throws Exception {


            this.clientID = clientID;
            byte[] message = this.clientID.getBytes();
            this.packet = new DatagramPacket(message, message.length, group, port);

            this.socket = new DatagramSocket();
            this.sendDelay = sendDelay;


        }

        public void run() {

            DatagramSocket socket = null;

            try {

                socket = new DatagramSocket();

                while (true) {
                    socket.send(packet);
                    System.out.println(new Date() + " BROADCASTER: Packet sent with client ID " + clientID);
                    Thread.sleep(TimeUnit.SECONDS.toMillis(sendDelay));
                }
            } catch (InterruptedIOException ex) {
                System.out.println(" BROADCASTER: Received an interrupt, broadcaster " + clientID + " stopping.");
            } catch (Throwable ex) {
                ex.printStackTrace(System.out);
            } finally {
                socket.close();
            }


        }
    }

    class MulticastListener implements Runnable {

        private final InetAddress group;
        private final int port;
        private final String clientID;

        MulticastListener(InetAddress group, int port, String clientID) throws Exception {
            this.group = group;
            this.port = port;
            this.clientID = clientID;
        }

        public void run() {

            MulticastSocket socket = null;

            try {

                socket = new MulticastSocket(port);
                socket.joinGroup(group);

                while (true) {
                    byte[] buf = new byte[256];
                    DatagramPacket recv = new DatagramPacket(buf, buf.length);
                    socket.receive(recv);

                    System.out.println(new Date()+" LISTENER: Received packet from " + new String(recv.getData()));
                }

            } catch (InterruptedIOException ex) {
                System.out.println("LISTENER: Received an interrupt, listener " + clientID + " stopping.");
            } catch (Exception ex) {
                ex.printStackTrace(System.out);
            } finally {

                if (socket != null) {
                    try {
                        socket.leaveGroup(group);
                    } catch (Exception ex) {
                        ex.printStackTrace(System.out);
                    }
                    socket.close();
                }


            }


        }
    }


}
