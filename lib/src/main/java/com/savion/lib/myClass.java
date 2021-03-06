package com.savion.lib;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

/**
 * Created by Administrator on 2017-11-23.
 */
public class myClass {
    public static void main(String args[]) throws IOException {
        //为了简单起见，所有的异常信息都往外抛
        int port = 8899;
        //定义一个ServerSocket监听在端口8899上
        ServerSocket server = new ServerSocket(port);
        while (true) {
            //server尝试接收其他Socket的连接请求，server的accept方法是阻塞式的
            Socket socket = server.accept();
            //每接收到一个Socket就建立一个新的线程来处理它
            new Thread(new Task(socket)).start();
        }
    }

    /**
     * 用来处理Socket请求的
     */
    static class Task implements Runnable {

        private Socket socket;
        private ParseTemp parseTemp;
        private byte temstart = 0;
        private byte humstart = 10;
        private byte[] bytes = new byte[6];

        public Task(Socket socket) {
            this.socket = socket;
            parseTemp = new ParseTemp();
        }

        public void run() {
            try {
//                handleSocket();
                receiveData();
                sendFakeData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void receiveData(){
        }


        private void sendFakeData() {
            while (true) {
                try {
                    Thread.sleep(1000);
                    byte[] receive = new byte[1024];
                    int length = socket.getInputStream().read(receive);
                    System.out.println("receive:");
                    for (byte b:receive){
                        System.out.print(b+"-");
                    }
                    System.out.println("==========");


                    byte[] ints = parseTemp.calBytes(new byte[]{1, 5, 6, 0, humstart,0,temstart});
                    socket.getOutputStream().write(ints);

//                    Arrays.asList(ints).stream().forEach(s -> System.out.print(s + ","));
                    System.out.println("send:");
                    for (byte b:ints){
                        System.out.print(b+"-");
                    }
                    System.out.println("==========");
                    temstart+=10;
                    humstart+=10;
                } catch (InterruptedException e) {
                    System.out.println("InterruptedException");
                    //e.printStackTrace();
                } catch (IOException e) {
                    System.out.println("IOException");
                    //e.printStackTrace();
                }
            }
        }
        /**
         * 跟客户端Socket进行通信
         *
         * @throws Exception
         */
        private void handleSocket() throws Exception {
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "GBK"));
            StringBuilder sb = new StringBuilder();
            String temp;
            int index;
            byte[] bytes=new byte[1024];
            while ((temp = br.readLine()) != null) {
                System.out.println(temp);
                if ((index = temp.indexOf("eof")) != -1) {//遇到eof时就结束接收
                    sb.append(temp.substring(0, index));
                }
                sb.append(temp);
            }
            System.out.println("客户端: " + sb);
            //读完后写一句
            Writer writer = new OutputStreamWriter(socket.getOutputStream(), "UTF-8");
            writer.write("你好，客户端。");
            writer.write("eof\n");
            writer.flush();
            writer.close();
            br.close();
            socket.close();
        }
    }
}
