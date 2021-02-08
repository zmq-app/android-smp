package com.itheima.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class TcpStreamClient {
    private static final String connect_ip = "127.0.0.1";
    private static final int connect_port = 9999;
    private static final int sendMaxCounts = 5;
    private static final int delayMs = 2000;

    public void start() {
        Socket client = null;
        try {
            client = new Socket(connect_ip,connect_port);
            System.out.println("Client Socket is create,connect_port = "+connect_port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (client == null) {
            System.err.println("Client Socket is null...");
            return;
        }
        new Thread(new ClientSendRunnable(client)).start();
        new Thread(new ClientRecvRunnable(client)).start();
    }

    static class ClientSendRunnable implements Runnable {
        private Socket mConnSocket;
        private DataOutputStream writeServerStream;
        private boolean isStop = false;
        private int sendServerNum = 1;

        public ClientSendRunnable(Socket connSocket) {
            this.mConnSocket = connSocket;
            try {
                writeServerStream = new DataOutputStream(mConnSocket.getOutputStream());
            } catch (IOException e) {
                mConnSocket = null;
                writeServerStream = null;
                System.err.println("new DataOutputStream is failed...");
            }
        }

        private void sendStringMsg(String msg) {
            try {
                writeServerStream.writeUTF(msg);
                writeServerStream.flush();
            } catch (IOException e) {
                isStop = true;
                System.err.println("sendStringMsg is failed...");
            }
        }

        private void sendBytesMsg(byte[] msg) {
            if (msg != null) {
                try {
                    writeServerStream.write(msg);
                    writeServerStream.flush();
                } catch (IOException e) {
                    isStop = true;
                    System.err.println("sendBytesMsg is failed...");
                }
            }
        }

        /** client send FIN package **/
        private void clientCloseTcpStream() {
            if (isStop && (mConnSocket != null)) {
                try {
                    mConnSocket.close();
                    System.out.println("TcpStreamClient is exited...");
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    isStop = true;
                    mConnSocket = null;
                }
            }
        }

        /**
         * SystemClock.sleep(millis);内部会调用Thread.sleep方法,同时捕获线程中断异常
         * SystemClock使用了android.os包,内部计算自启动的毫秒数(uptimeMillis)
         * @param millis
         */
        private void sleep(long millis) {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            while(!isStop && (sendServerNum <= sendMaxCounts)) {
                sendStringMsg("Hello World,index = "+sendServerNum);
                sendBytesMsg(new byte[] {0x02,0x03,0x05});
                sendServerNum++;
                sleep(delayMs);
            }
            if (sendServerNum > sendMaxCounts) {
                isStop = true;
            }
            clientCloseTcpStream();
        }
    }

    static class ClientRecvRunnable implements Runnable {
        private Socket mConnSocket;
        private DataInputStream readServerStream;
        private boolean isStop = false;
        private int replyClientNum = 0;

        public ClientRecvRunnable(Socket connSocket) {
            this.mConnSocket = connSocket;
            try {
                readServerStream = new DataInputStream(mConnSocket.getInputStream());
            } catch (IOException e) {
                mConnSocket = null;
                readServerStream = null;
                System.err.println("new DataInputStream is failed...");
            }
        }

        private String receiveStringMsg() {
            String msg = null;
            try {
                msg = readServerStream.readUTF();
            } catch (IOException e) {
                System.err.println("receiveStringMsg is failed...");
                isStop = true;
            }
            return msg;
        }

        /**
         * SystemClock.sleep(millis);内部会调用Thread.sleep方法,同时捕获线程中断异常
         * SystemClock使用了android.os包,内部计算自启动的毫秒数(uptimeMillis)
         * @param millis
         */
        private void sleep(long millis) {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            while(!isStop) {
                String msg = receiveStringMsg();
                if (msg != null) {
                    System.out.println("TcpStreamClient receive message = "+msg);
                    replyClientNum++;
                }
                if (replyClientNum == sendMaxCounts) {
                    isStop = true;
                }
                sleep(delayMs);
            }
        }
    }

    /**
     * 通过终端输入命令"netstat -ano | grep 9999",查看此TCP半连接状态
     * tcp6       0      0 :::9999                 :::*                    LISTEN (server listen fd)
     * tcp6       0      0 127.0.0.1:9999          127.0.0.1:36440         CLOSE_WAIT (server)
     * tcp6       0      0 127.0.0.1:36440         127.0.0.1:9999          FIN_WAIT2  (client)
     * @param args
     */
    public static void main(String[] args) {
        TcpStreamClient tcpClient = new TcpStreamClient();
        tcpClient.start();
    }
}
