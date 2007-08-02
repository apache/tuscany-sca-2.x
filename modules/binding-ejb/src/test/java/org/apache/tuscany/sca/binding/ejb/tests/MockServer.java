/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */

package org.apache.tuscany.sca.binding.ejb.tests;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class MockServer implements Runnable {

    private int listen;
    byte seq[][] =
        {
         {79, 69, 74, 80, 47, 51, 46, 48, 1, -84, -19, 0, 5, 119, 58, 1, 27, 0, 54, 47, 104, 101, 108, 108, 111, 45,
          97, 100, 100, 115, 101, 114, 118, 105, 99, 101, 47, 65, 100, 100, 83, 101, 114, 118, 105, 99, 101, 66, 101,
          97, 110, 47, 99, 97, 108, 99, 117, 108, 97, 116, 111, 114, 46, 65, 100, 100, 83, 101, 114, 118, 105, 99, 101,
          112},

         {79, 69, 74, 80, 47, 50, 46, 48, -84, -19, 0, 5, 119, 3, 1, 13, 1, 118, 114, 0, 25, 99, 97, 108, 99, 117, 108,
          97, 116, 111, 114, 46, 65, 100, 100, 83, 101, 114, 118, 105, 99, 101, 72, 111, 109, 101, 0, 0, 0, 0, 0, 0, 0,
          0, 0, 0, 0, 120, 112, 118, 114, 0, 21, 99, 97, 108, 99, 117, 108, 97, 116, 111, 114, 46, 65, 100, 100, 83,
          101, 114, 118, 105, 99, 101, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 120, 112, 112, 112, 119, 38, 7, 0, 31, 104,
          101, 108, 108, 111, 45, 97, 100, 100, 115, 101, 114, 118, 105, 99, 101, 47, 65, 100, 100, 83, 101, 114, 118,
          105, 99, 101, 66, 101, 97, 110, -1, -1, 0, 0},

         {79, 69, 74, 80, 47, 51, 46, 48, 0, -84, -19, 0, 5, 119, 1, 10, 116, 0, 31, 104, 101, 108, 108, 111, 45, 97,
          100, 100, 115, 101, 114, 118, 105, 99, 101, 47, 65, 100, 100, 83, 101, 114, 118, 105, 99, 101, 66, 101, 97,
          110, 119, 2, -1, -1, 112, 119, 1, 1, 112, 118, 114, 0, 25, 99, 97, 108, 99, 117, 108, 97, 116, 111, 114, 46,
          65, 100, 100, 83, 101, 114, 118, 105, 99, 101, 72, 111, 109, 101, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 120, 112,
          119, 9, 0, 6, 99, 114, 101, 97, 116, 101, 0},
         {79, 69, 74, 80, 47, 50, 46, 48, -84, -19, 0, 5, 119, 2, 1, 4, 112},

         {79, 69, 74, 80, 47, 51, 46, 48, 0, -84, -19, 0, 5, 119, 1, 23, 116, 0, 31, 104, 101, 108, 108, 111, 45, 97,
          100, 100, 115, 101, 114, 118, 105, 99, 101, 47, 65, 100, 100, 83, 101, 114, 118, 105, 99, 101, 66, 101, 97,
          110, 119, 2, -1, -1, 112, 119, 1, 1, 112, 118, 114, 0, 21, 99, 97, 108, 99, 117, 108, 97, 116, 111, 114, 46,
          65, 100, 100, 83, 101, 114, 118, 105, 99, 101, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 120, 112, 119, 24, 0, 3, 97,
          100, 100, 2, 4, 64, 89, 0, 0, 0, 0, 0, 0, 4, 64, -113, 64, 0, 0, 0, 0, 0},
         {79, 69, 74, 80, 47, 50, 46, 48, -84, -19, 0, 5, 119, 2, 1, 4, 115, 114, 0, 16, 106, 97, 118, 97, 46, 108, 97,
          110, 103, 46, 68, 111, 117, 98, 108, 101, -128, -77, -62, 74, 41, 107, -5, 4, 2, 0, 1, 68, 0, 5, 118, 97,
          108, 117, 101, 120, 114, 0, 16, 106, 97, 118, 97, 46, 108, 97, 110, 103, 46, 78, 117, 109, 98, 101, 114,
          -122, -84, -107, 29, 11, -108, -32, -117, 2, 0, 0, 120, 112, 64, -111, 48, 0, 0, 0, 0, 0}

        };

    public MockServer(int listen) {
        this.listen = listen;
    }

    public void run() {
        try {
            ServerSocket ss = new ServerSocket(listen);
            for (int i = 0; i < seq.length; i += 2) {
                // System.out.println("Processing request[" + i/2 + "]");
                doExchange(ss.accept(), seq[i], seq[i + 1]);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doExchange(Socket socket, byte[] read, byte[] write) throws IOException, InterruptedException {
        Thread t2 = readBytes(socket, read.length);
        Thread t1 = writeBytes(socket, write);
        t1.join();
        t2.join();
        socket.close();
    }

    private Thread readBytes(Socket socket, int x) throws IOException, InterruptedException {
        byte[] buf = new byte[x];
        Thread t = new Reader(socket, buf);
        t.start();
        return t;
    }

    private Thread writeBytes(Socket socket, byte[] bs) throws IOException, InterruptedException {
        Thread t = new Writer(socket, bs);
        t.start();
        return t;
    }

    private static class Reader extends Thread {

        private InputStream is;
        private byte[] buf;

        Reader(Socket socket, byte[] buf) throws IOException {
            this.is = socket.getInputStream();
            this.buf = buf;
        }

        public void run() {
            try {
                int totalSize = buf.length;
                int readSize = 0;
                int offset = 0;
                while (totalSize > 0 && (readSize = is.read(buf, offset, totalSize)) != -1) {
                    offset += readSize;
                    totalSize -= readSize;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private static class Writer extends Thread {

        private OutputStream os;
        private byte[] buf;

        Writer(Socket socket, byte[] buf) throws IOException {
            this.os = socket.getOutputStream();
            this.buf = buf;
        }

        public void run() {
            try {
                os.write(buf);
                os.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
