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

    private int[] r1 = new int[] {79, 69, 74, 80, 47, 50, 46, 48, 172, 237, 0, 5, 119, 1, 1, 116, 0, 5, 71, 85, 69, 83, 84};
    private int[] r2 = new int[] {79, 69, 74, 80, 47, 50, 46, 48, 172, 237, 0, 5, 119, 1, 13, 118, 114, 0, 58, 111, 114, 103, 46, 97, 112, 97, 99, 104, 101, 46, 103, 101, 114, 111, 110, 105, 109, 111, 46, 115, 97, 109, 112, 108, 101, 115, 46, 98, 97, 110, 107, 46, 101, 106, 98, 46, 66, 97, 110, 107, 77, 97, 110, 97, 103, 101, 114, 70, 97, 99, 97, 100, 101, 72, 111, 109, 101, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 120, 112, 118, 114, 0, 54, 111, 114, 103, 46, 97, 112, 97, 99, 104, 101, 46, 103, 101, 114, 111, 110, 105, 109, 111, 46, 115, 97, 109, 112, 108, 101, 115, 46, 98, 97, 110, 107, 46, 101, 106, 98, 46, 66, 97, 110, 107, 77, 97, 110, 97, 103, 101, 114, 70, 97, 99, 97, 100, 101, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 120, 112, 112, 112, 119, 185, 1, 0, 180, 111, 114, 103, 46, 97, 112, 97, 99, 104, 101, 46, 103, 101, 114, 111, 110, 105, 109, 111, 46, 115, 97, 109, 112, 108, 101, 115, 47, 66, 97, 110, 107, 47, 49, 46, 49, 46, 49, 47, 99, 97, 114, 63, 69, 74, 66, 77, 111, 100, 117, 108, 101, 61, 66, 97, 110, 107, 69, 74, 66, 46, 106, 97, 114, 44, 74, 50, 69, 69, 65, 112, 112, 108, 105, 99, 97, 116, 105, 111, 110, 61, 111, 114, 103, 46, 97, 112, 97, 99, 104, 101, 46, 103, 101, 114, 111, 110, 105, 109, 111, 46, 115, 97, 109, 112, 108, 101, 115, 47, 66, 97, 110, 107, 47, 49, 46, 49, 46, 49, 47, 99, 97, 114, 44, 106, 50, 101, 101, 84, 121, 112, 101, 61, 83, 116, 97, 116, 101, 108, 101, 115, 115, 83, 101, 115, 115, 105, 111, 110, 66, 101, 97, 110, 44, 110, 97, 109, 101, 61, 66, 97, 110, 107, 77, 97, 110, 97, 103, 101, 114, 70, 97, 99, 97, 100, 101, 66, 101, 97, 110, 0, 1};
    private int[] r3 = new int[] {79, 69, 74, 80, 47, 50, 46, 48, 172, 237, 0, 5, 119, 1, 4, 112};
    private int[] r4 = new int[] {79, 69, 74, 80, 47, 50, 46, 48, 172, 237, 0, 5, 119, 1, 4, 115, 114, 0, 16, 106, 97, 118, 97, 46, 108, 97, 110, 103, 46, 68, 111, 117, 98, 108, 101, 128, 179, 194, 74, 41, 107, 251, 4, 2, 0, 1, 68, 0, 5, 118, 97, 108, 117, 101, 120, 114, 0, 16, 106, 97, 118, 97, 46, 108, 97, 110, 103, 46, 78, 117, 109, 98, 101, 114, 134, 172, 149, 29, 11, 148, 224, 139, 2, 0, 0, 120, 112, 64, 143, 106, 204, 204, 204, 204, 205};

    public MockServer(int listen) {
        this.listen = listen;
    }
    
    public void run() {
        try {
            ServerSocket ss = new ServerSocket(listen);

            doExchange(ss.accept(), 15, r1);
            doExchange(ss.accept(), 80, r2);
            doExchange(ss.accept(), 109, r3);
            doExchange(ss.accept(), 163, r4);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doExchange(Socket sin, int readCount, int[] write) throws IOException, InterruptedException {
        InputStream is = sin.getInputStream();
        OutputStream os = sin.getOutputStream();
        readBytes(is, readCount);
        writeBytes(os, write);
        sin.close();
    }

    private void readBytes(InputStream is, int x) throws IOException, InterruptedException {
        for (int i = 0; i < x; i++) {
            is.read();
        }
    }

    private void writeBytes(OutputStream os, int[] bs) throws IOException, InterruptedException {
        for (int i = 0; i < bs.length; i++) {
            os.write(bs[i]);
        }
    }

}
