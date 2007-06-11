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

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Displays the bytes flowing across a Socket connection.
 * Used to get the read count and reply data for the MockServer
 */
public class SocketTracer implements Runnable {

    private int listen;
    private int send;

    SocketTracer(int listen, int send) {
        this.listen = listen;
        this.send = send;
    }

    public void run() {
        try {
            ServerSocket ss = new ServerSocket(listen);
            while (true) {
                Socket sin = ss.accept();

                Socket sout = new Socket("localhost", send);
                
                Thread st = new Thread(new Send(sin, sout));
                st.start();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

class Send implements Runnable {

    Socket sin;
    Socket sout;
    
    Send(Socket sin, Socket sout) {
        this.sin = sin;
        this.sout = sout;
    }

    public void run() {
        try {

            Reply rr = new Reply(sout.getInputStream(), sin.getOutputStream());
            Thread rt = new Thread(rr);
            rt.start();

            OutputStream outout = sout.getOutputStream();

            InputStream is = sin.getInputStream();
            int i = 0;
            while ((i = is.read()) != -1) {
                System.out.println("out: " + i);
                outout.write(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class Reply implements Runnable {

    InputStream is;
    OutputStream outout;

    Reply(InputStream is, OutputStream outout) {
        this.is = is;
        this.outout = outout;
    }

    public void run() {
        try {
            int i = 0;
            while ((i = is.read()) != -1) {
                System.out.println("reply: " + i);
                outout.write(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
