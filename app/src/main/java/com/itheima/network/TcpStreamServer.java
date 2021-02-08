package com.itheima.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class TcpStreamServer {
    private List<TcpChannel> mTcpChannelList = new CopyOnWriteArrayList<>();
    private int connectClientNum = 0;
    private static final int listen_port = 9999;
    private static final int delayMs = 2000;

    static class TcpChannel implements Runnable {
        private ServerSocket mServer;
        private Socket mClient;
        private DataInputStream clientReadStream = null;  //server读信息
        private DataOutputStream clientWriteStream = null; //server写信息
        private boolean isStop = false;

        public TcpChannel(ServerSocket server,Socket client) {
            this.mServer = server;
            this.mClient = client;
            try {
                this.clientReadStream = new DataInputStream(mClient.getInputStream());
                this.clientWriteStream = new DataOutputStream(mClient.getOutputStream());
            } catch (IOException e) {
                System.err.println("new DataInputStream or DataOutputStream is failed...");
            }
        }

        private String receiveStringMsg() {
            String msg = null;
            if (clientReadStream != null) {
                try {
                    msg = clientReadStream.readUTF();
                    System.out.println("receive message = "+msg);
                } catch (IOException e) {
                }
            }
            return msg;
        }

        private byte[] receiveBytesMsg() {
            byte[] msg = new byte[3];
            if (clientReadStream != null) {
                try {
                    clientReadStream.read(msg,0,msg.length);
                } catch (IOException e) {
                    msg = null;
                }
            } else {
                msg = null;
            }
            return msg;
        }

        private void replyStringMsg(String msg) {
            if (clientWriteStream != null) {
                try {
                    clientWriteStream.writeUTF(msg);
                    clientWriteStream.flush();
                } catch (IOException e) {
                    System.out.println("replyStringMsg is failed...");
                    isStop = true;
                }
            }
        }

        /** server send FIN package **/
        private void serverCloseTcpStream(){
            if (isStop && (mServer != null)) {
                try {
                    mServer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    isStop = true;
                    mServer = null;
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
            while (!isStop) {
                /* 接收并返回String类型的消息 */
                String msg = receiveStringMsg();
                if (msg != null) {
                    System.out.println("TcpStreamServer receive string message = "+msg);
                    replyStringMsg(msg);
                }

                /* 接收并返回byte字节数组类型的消息 */
                byte[] content = receiveBytesMsg();
                if ((content != null) && (content.length > 0)) {
                    System.out.print("TcpStreamServer receive bytes message = ");
                    for (int i=0; i<content.length; i++) {
                        System.out.print(content[i]+" ");
                    }
                    System.out.print('\n');
                }

                if ((msg == null) || (content == null)) {
                    /* recv系统调用返回值为0,即捕获IOException异常,返回的msg=null,则说明client断开连接了 */
                    isStop = true;
                }
                sleep(delayMs);
            }
            serverCloseTcpStream();
        }
    }

    public void start() {
        ServerSocket server = null;
        try {
            server = new ServerSocket(listen_port);
            System.out.println("Server Socket is create,listen_port = "+listen_port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (server == null) {
            System.err.println("ServerSocket is null...");
            return;
        }

        while (true) {
            Socket client;
            try {
                client = server.accept();
                connectClientNum++;
            } catch (IOException e) {
                client = null;
                connectClientNum = 0;
            }
            if ((client == null) && (connectClientNum == 0)) {
                System.out.println("TcpStreamServer is exited...");
                break;
            }

            TcpChannel chanel = new TcpChannel(server,client);
            mTcpChannelList.add(chanel);
            new Thread(chanel).start();
        }
    }

    /**
     * 通过终端输入命令"netstat -ano | grep 9999",查看此TCP建立连接传送数据的状态
     * tcp6       0      0 :::9999                 :::*                    LISTEN (server listen fd)
     * tcp6       0      0 127.0.0.1:9999          127.0.0.1:36972         ESTABLISHED (server)
     * tcp6       0      0 127.0.0.1:36954         127.0.0.1:9999          TIME_WAIT   (上次tcp连接通道最后的释放过程,client TIME_WAIT)
     * tcp6       0      0 127.0.0.1:36972         127.0.0.1:9999          ESTABLISHED (client)
     * @param args
     */
    public static void main(String[] args) {
        TcpStreamServer tcpServer = new TcpStreamServer();
        tcpServer.start();
    }
}
