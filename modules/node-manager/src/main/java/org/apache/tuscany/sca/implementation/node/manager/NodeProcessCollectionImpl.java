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

package org.apache.tuscany.sca.implementation.node.manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.tuscany.sca.data.collection.Entry;
import org.apache.tuscany.sca.data.collection.Item;
import org.apache.tuscany.sca.data.collection.ItemCollection;
import org.apache.tuscany.sca.data.collection.LocalItemCollection;
import org.apache.tuscany.sca.data.collection.NotFoundException;
import org.apache.tuscany.sca.node.launcher.NodeLauncher;
import org.osoa.sca.ServiceRuntimeException;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

/**
 * Implementation of a node process collection service. 
 *
 * @version $Rev$ $Date$
 */
@Scope("COMPOSITE")
@Service(interfaces={ItemCollection.class, LocalItemCollection.class})
public class NodeProcessCollectionImpl implements ItemCollection, LocalItemCollection {

    private static final Logger logger = Logger.getLogger(NodeProcessCollectionImpl.class.getName());    

    private List<SCANodeVM> nodeVMs = new ArrayList<SCANodeVM>();

    /**
     * Initialize the component.
     */
    @Init
    public void initialize() {
    }
    
    public Entry<String, Item>[] getAll() {
        logger.fine("getAll");
        
        // Return all the running VMs
        List<Entry<String, Item>> entries = new ArrayList<Entry<String, Item>>();
        for (SCANodeVM vm: nodeVMs) {
            entries.add(entry(vm));
        }
        return entries.toArray(new Entry[entries.size()]);
    }

    public Item get(String key) throws NotFoundException {
        logger.fine("get " + key);

        // Return the specified VM
        SCANodeVM vm = vm(key);
        if (vm == null) {
            throw new NotFoundException();
        }
        
        return item(vm);
    }

    public String post(String key, Item item) {
        logger.fine("post " + key);

        // If the VM is already running just return it
        SCANodeVM vm = vm(key);
        if (vm != null) {
            if (vm.isAlive()) {
                return key;
            } else {
                // Remove dead VM entry
                try {
                    vm.stop();
                } catch (InterruptedException e) {
                    throw new ServiceRuntimeException(e);
                }
                nodeVMs.remove(vm);
            }
        }

        // Start a new VM and add it to the collection
        vm = new SCANodeVM(key);
        nodeVMs.add(0, vm);
        try {
            vm.start();
        } catch (IOException e) {
            throw new ServiceRuntimeException(e);
        }
        
        return key;
    }

    public void put(String key, Item item) throws NotFoundException {
        throw new UnsupportedOperationException();
    }

    public void delete(String key) throws NotFoundException {
        logger.fine("delete " + key);
        
        // Stop a VM and remove it from the collection
        SCANodeVM vm = vm(key);
        if (vm != null) {
            try {
                vm.stop();
            } catch (InterruptedException e) {
                throw new ServiceRuntimeException(e);
            }
            nodeVMs.remove(vm);
        } else {
            //throw new NotFoundException();
        }
    }
    
    public Entry<String, Item>[] query(String queryString) {
        logger.fine("query " + queryString);
        
        if (queryString.startsWith("node=")) {
            
            // Return the log for the specified VM
            String key = queryString.substring(queryString.indexOf('=') + 1);
            List<Entry<String, Item>> entries = new ArrayList<Entry<String, Item>>();
            for (SCANodeVM vm: nodeVMs) {
                if (vm.getNodeName().equals(key)) {
                    entries.add(entry(vm));
                }
            }
            return entries.toArray(new Entry[entries.size()]);
            
        } else {
            throw new UnsupportedOperationException();
        }
    }
    
    /**
     * Returns the specified VM.
     * 
     * @param key
     * @return
     */
    private SCANodeVM vm(String key) {
        for (SCANodeVM vm: nodeVMs) {
            if (key.equals(vm.getNodeName())) {
                return vm;
            }
        }
        return null;
    }

    /**
     * Returns an entry representing a VM.
     * 
     * @param vm
     * @return
     */
    private static Entry<String, Item> entry(SCANodeVM vm) {
        Entry<String, Item> entry = new Entry<String, Item>();
        entry.setKey(vm.getNodeName());
        entry.setData(item(vm));
        return entry;
    }
    
    /**
     * Returns an item representing a VM.
     * 
     * @param vm
     * @return
     */
    private static Item item(SCANodeVM vm) {
        Item item = new Item();
        String key = vm.getNodeName();
        item.setTitle(title(key));
        item.setLink("/node-config/" + vm.getNodeName());
        item.setContents("<span id=\"log\" style=\"white-space: nowrap; font-size: small\">" + vm.getLog().toString() + "</span>");
        return item;
    }
    
    /**
     * Represent a child Java VM running an SCA node.
     */
    private static class SCANodeVM {
        private String nodeName;
        private StringBuffer log;
        private Process process;
        private Thread monitor;
        private int status;
        
        SCANodeVM(String nodeName) {
            log = new StringBuffer();
            this.nodeName =nodeName;
        }
        
        /**
         * Starts a node in a new VM.
         */
        private void start() throws IOException {

            // Determine the node configuration URI
            String nodeConfigurationURI = NodeManagerUtil.nodeConfigurationURI(nodeName);
            
            // Build the Java VM command line
            Properties props = System.getProperties();
            String java = props.getProperty("java.home") + "/bin/java";
            String cp = props.getProperty("java.class.path");
            String main = NodeLauncher.class.getName();
            final List<String> command = new ArrayList<String>();
            command.add(java);
            command.add("-cp");
            command.add(cp);
            
            // Propagate TUSCANY properties
            String tuscanyHome = props.getProperty("TUSCANY_HOME");
            if (tuscanyHome != null) {
                command.add("-DTUSCANY_HOME=" + tuscanyHome);
            }
            String tuscanyPath = props.getProperty("TUSCANY_PATH");
            if (tuscanyPath != null) {
                command.add("-DTUSCANY_PATH=" + tuscanyPath);
            }

            // Specify the main class and parameters
            command.add(main);
            command.add(nodeConfigurationURI);
            
            logger.info("Starting " + "java " + main + " " + nodeConfigurationURI);
            
            // Start the VM
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.redirectErrorStream(true);
            process = builder.start();
            
            logger.info("Started " + process);
            
            // Start a thread to monitor the process
            final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            monitor = new Thread(new Runnable() {
                public void run() {
                    try {
                        for (;;) {
                            String s = reader.readLine();
                            if (s != null) {
                                logger.info(s);
                                log.append(s + "<br>");
                            } else {
                                break;
                            }
                        }
                        status = process.waitFor();
                    } catch (IOException e) {
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            monitor.start();
        }

        /**
         * Returns the composite used to start this VM.
         * @return
         */
        String getNodeName() {
            return nodeName;
        }
        
        /**
         * Returns the log for this VM.
         * 
         * @return
         */
        StringBuffer getLog() {
            return log;
        }

        /**
         * Returns true if the VM is alive
         * 
         * @return
         */
        private boolean isAlive() {
            return monitor.isAlive();
        }
        
        /**
         * Returns the VM status code.
         * @return
         */
        int getStatus() {
            return status;
        }

        /**
         * Stops the VM.
         * 
         * @throws InterruptedException
         */
        private void stop() throws InterruptedException {
            logger.info("Stopping " + process);
            
            process.destroy();
            monitor.join();
            
            logger.info("Stopped " + process);
        }
    }
    
    /**
     * Returns a node title.
     * 
     * @param key
     * @return
     */
    private static String title(String key) {
        return key;
    }

}
