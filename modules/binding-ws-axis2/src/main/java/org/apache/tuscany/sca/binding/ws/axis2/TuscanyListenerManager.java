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

package org.apache.tuscany.sca.binding.ws.axis2;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.description.TransportInDescription;
import org.apache.axis2.engine.ListenerManager;
import org.apache.axis2.transport.TransportListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A Tuscany specific Axis2 ListenerManager. Created purely as part of
 * TUSCANY-3149 to unregister the ListenerManager from the runtime
 * shutown hook
 */
public class TuscanyListenerManager extends ListenerManager {
	private static final Log log = LogFactory.getLog(TuscanyListenerManager.class);
	
	private ListenerManagerShutdownThread shutdownThread = null;
	
    /**
     * To start all the transports
     */
    public synchronized void start() {
    	
    	ConfigurationContext configctx = getConfigctx();
    	
    	// very nasty! but this is in order to get someone running who keeps 
    	// getting perm gen errors. This will all go away when we move up to Axis2 1.5
    	HashMap startedTransports = null;
    	
    	try {
    		Field field = ListenerManager.class.getDeclaredField("startedTransports");
    		field.setAccessible(true);
    		startedTransports = (HashMap)field.get(this);
    	} catch (Exception ex){
    		ex.printStackTrace();
    	}

        for (Iterator transportNames =
                configctx.getAxisConfiguration().getTransportsIn().values().iterator();
             transportNames.hasNext();) {
            try {
                TransportInDescription transportIn = (TransportInDescription) transportNames.next();
                TransportListener listener = transportIn.getReceiver();
                if (listener != null &&
                    startedTransports.get(transportIn.getName()) == null) {
                    listener.init(configctx, transportIn);
                    listener.start();
                    if (startedTransports.get(transportIn.getName()) == null) {
                        startedTransports.put(transportIn.getName(), listener);
                    }
                }
            } catch (Exception e) {
                log.info(e.getMessage(), e);
            }
        }
        shutdownThread = new ListenerManagerShutdownThread(this);
        Runtime.getRuntime().addShutdownHook(shutdownThread);
    }
    
    public synchronized void stop() throws AxisFault {
    	super.stop();
    	Runtime.getRuntime().removeShutdownHook(shutdownThread);
    }
    
    static class ListenerManagerShutdownThread extends Thread {
        ListenerManager listenerManager;

        public ListenerManagerShutdownThread(ListenerManager listenerManager) {
            super();
            this.listenerManager = listenerManager;
        }

        public void run() {
            try {                
            	listenerManager.stop();
            } catch (AxisFault axisFault) {
                log.error(axisFault.getMessage(), axisFault);
            }
        }
    }    

}
