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
package org.apache.servicemix.sca.tuscany;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tuscany.common.monitor.MonitorFactory;

public class CommonsLoggingMonitorFactory implements MonitorFactory {

    private final Map<Class<?>, WeakReference<?>> proxies = new WeakHashMap<Class<?>, WeakReference<?>>();
    
    public CommonsLoggingMonitorFactory() {
    }

    public <T> T getMonitor(Class<T> monitorInterface) {
        T proxy = getCachedMonitor(monitorInterface);
        if (proxy == null) {
            proxy = createMonitor(monitorInterface);
            proxies.put(monitorInterface, new WeakReference<T>(proxy));
        }
        return proxy;
    }

    private <T>T getCachedMonitor(Class<T> monitorInterface) {
        WeakReference<T> ref = (WeakReference<T>) proxies.get(monitorInterface);
        return (ref != null) ? ref.get() : null;
    }

    private <T>T createMonitor(Class<T> monitorInterface) {
        String className = monitorInterface.getName();
        Log logger = LogFactory.getLog(className);
        InvocationHandler handler = new LoggingHandler(logger);
        return (T) Proxy.newProxyInstance(monitorInterface.getClassLoader(), new Class<?>[]{monitorInterface}, handler);
    }

    private static final class LoggingHandler implements InvocationHandler {
        private final Log logger;

        public LoggingHandler(Log logger) {
            this.logger = logger;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String sourceMethod = method.getName();
            if (logger.isDebugEnabled()) {
                // if the only argument is a Throwable use the special logger for it
                if (args != null && args.length == 1 && args[0] instanceof Throwable) {
                    logger.debug(sourceMethod, (Throwable) args[0]);
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append(sourceMethod);
                    sb.append("(");
                    for (int i = 0; i < args.length; i++) {
                        if (i > 0) {
                            sb.append(", ");
                        }
                        sb.append(args[i]);
                    }
                    sb.append(")");
                    logger.debug(sb.toString());
                }
            }
            return null;
        }
    }
}
