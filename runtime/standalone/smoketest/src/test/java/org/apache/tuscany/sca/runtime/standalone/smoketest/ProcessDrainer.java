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
package org.apache.tuscany.sca.runtime.standalone.smoketest;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Stream drainer used for draining the out and err streams of an external 
 * process to avoid buffer overflow and deadlock.
 * 
 * @version $Revision$ $Date$
 *
 */
public class ProcessDrainer {
    
    // Default time out in seconds
    private static final int DEFAULT_TIMEOUT = 60;
    
    // Process whose streams are being drained
    private Process process;
    
    // Timeout to wait for the extrenal process to whutdown
    private int timeout = DEFAULT_TIMEOUT;
    
    // Executor that is scheduling the draining
    private ExecutorService executor = Executors.newFixedThreadPool(2);
    
    /*
     * Initializes the process to drained.
     */
    private ProcessDrainer(Process process) {
        this.process = process;
    }
    
    /*
     * Initializes the process to drained.
     */
    private ProcessDrainer(Process process, int timeout) {
        this.process = process;
        this.timeout = timeout;
    }
    
    /**
     * Creates a new instance of the stream drainer for the processed.
     * @param process Process to be drained.
     * @return An instance of the stream drainer.
     */
    public static ProcessDrainer newInstance(Process process) {
        return new ProcessDrainer(process);
    }
    
    /**
     * Creates a new instance of the stream drainer for the processed.
     * @param process Process to be drained.
     * @param timeout Timeout before which teh drainer stops.
     * @return An instance of the stream drainer.
     */
    public static ProcessDrainer newInstance(Process process, int timeout) {
        return new ProcessDrainer(process, timeout);
    }
    
    /**
     * Starts draining the stream.
     */
    public void drain() {
        
        executor.execute(new StreamDrainer(process.getErrorStream()));
        executor.execute(new StreamDrainer(process.getInputStream()));
        
        executor.shutdown();
        try {
            executor.awaitTermination(timeout, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            return;
        }
        
    }
    
    /*
     * Scheduled stream drainer.
     */
    private class StreamDrainer implements Runnable {
        
        // Stream to be drained
        private final InputStream inputStream;
        
        /*
         * Initializes the stream to drained.
         */
        private StreamDrainer(InputStream inputStream) {
            this.inputStream = inputStream;
        }
        
        /*
         * Starts draing the stream.
         */
        public void run() {
            try {
                int count = 0;
                byte[] buffer = new byte[4096];
                count = inputStream.read(buffer);
                while (count != -1) {
                    count = inputStream.read(buffer);
                }
            } catch (IOException ex) {
            }
        }
        
    }

}
