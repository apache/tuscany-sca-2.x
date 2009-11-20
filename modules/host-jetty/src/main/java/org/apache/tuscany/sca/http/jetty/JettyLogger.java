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
package org.apache.tuscany.sca.http.jetty;

import java.util.logging.Level;

import org.mortbay.log.Logger;

/**
 * Replaces Jetty's logging mechanism
 * 
 * @version $Rev$ $Date$
 */
public class JettyLogger implements Logger {
    private java.util.logging.Logger jdkLogger;

    public JettyLogger() {
        this(JettyLogger.class.getName());
    }

    public JettyLogger(String name) {
        jdkLogger = java.util.logging.Logger.getLogger(name);
    }

    /**
     * @param jdkLogger
     */
    public JettyLogger(java.util.logging.Logger jdkLogger) {
        super();
        this.jdkLogger = jdkLogger;
    }

    public String getName() {
        return jdkLogger.getName();
    }

    public void debug(String msg) {
        jdkLogger.log(Level.FINE, msg);
    }

    public void debug(String msg, Throwable th) {
        jdkLogger.log(Level.FINE, msg, th);
    }

    public void debug(String msg, Object arg0, Object arg1) {
        jdkLogger.log(Level.FINE, format(msg, arg0, arg1));
    }

    public Logger getLogger(String name) {
        return new JettyLogger(name);
    }

    public void info(String msg) {
        jdkLogger.log(Level.INFO, msg);
    }

    public void info(String msg, Object arg0, Object arg1) {
        jdkLogger.log(Level.INFO, format(msg, arg0, arg1));
    }

    public boolean isDebugEnabled() {
        return jdkLogger.isLoggable(Level.FINE);
    }

    public void setDebugEnabled(boolean enabled) {
        jdkLogger.setLevel(Level.FINE);
    }

    public void warn(String msg) {
        jdkLogger.log(Level.WARNING, msg);
    }

    public void warn(String msg, Object arg0, Object arg1) {
        jdkLogger.log(Level.WARNING, format(msg, arg0, arg1));
    }

    public void warn(String msg, Throwable th) {
        jdkLogger.log(Level.WARNING, msg, th);
    }

    private String format(String msg, Object arg0, Object arg1) {
        int i0 = msg.indexOf("{}");
        int i1 = i0 < 0 ? -1 : msg.indexOf("{}", i0 + 2);

        if (arg1 != null && i1 >= 0)
            msg = msg.substring(0, i1) + arg1 + msg.substring(i1 + 2);
        if (arg0 != null && i0 >= 0)
            msg = msg.substring(0, i0) + arg0 + msg.substring(i0 + 2);
        return msg;
    }
}
