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
package org.apache.tuscany.sca.plugin.itest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.HashMap;
import java.util.logging.Level;
import java.text.MessageFormat;

import org.apache.maven.plugin.logging.Log;

import org.apache.tuscany.core.monitor.ProxyMonitorFactory;

/**
 * @version $Rev$ $Date$
 */
public class MavenMonitorFactory extends ProxyMonitorFactory {
    private final Log log;

    public MavenMonitorFactory(Log log) {
        this.log = log;
        Map<String, Object> configProperties = new HashMap<String, Object>();
        configProperties.put("defaultLevel", Level.FINEST);
        initInternal(configProperties);
    }

    protected <T> InvocationHandler createInvocationHandler(Class<T> monitorInterface, Map<String, Level> levels) {
        ResourceBundle bundle = locateBundle(monitorInterface, bundleName);
        return new MonitorHandler(monitorInterface.getName(), levels, bundle);
    }

    private class MonitorHandler implements InvocationHandler {
        private final String monitorName;
        private final Map<String, Level> methodLevels;
        private final ResourceBundle bundle;

        public MonitorHandler(String monitorName, Map<String, Level> methodLevels, ResourceBundle bundle) {
            this.monitorName = monitorName;
            this.methodLevels = methodLevels;
            this.bundle = bundle;
        }

        public Object invoke(Object object, Method method, Object[] objects) throws Throwable {
            String sourceMethod = method.getName();
            Level level = methodLevels.get(sourceMethod);
            if (level == Level.OFF) {
                return null;
            }
            
            int value = level.intValue();
            if (isLogEnabled(value)) {
                String key = monitorName + '#' + sourceMethod;
                String message;
                if (bundle != null) {
                    message = bundle.getString(key);
                } else {
                    message = null;
                }
                if (message != null) {
                    message = MessageFormat.format(message, objects);
                } else {
                    StringBuilder builder = new StringBuilder();
                    builder.append(key).append(":");
                    for (Object o : objects) {
                        builder.append(' ');
                        if (o instanceof Throwable) {
                            builder.append(formatException((Throwable) o));
                        } else {
                            builder.append(String.valueOf(o));
                        }
                    }
                    message = builder.toString();
                }
                Throwable cause = getFirstException(objects);
                if (cause != null) {
                    if (value >= Level.SEVERE.intValue()) {
                        log.error(message, cause);
                    } else if (value >= Level.WARNING.intValue()) {
                        log.warn(message, cause);
                    } else if (value >= Level.INFO.intValue()) {
                        log.info(message, cause);
                    } else if (value >= Level.FINEST.intValue()) {
                        log.debug(message, cause);
                    }
                } else {
                    if (value >= Level.SEVERE.intValue()) {
                        log.error(message);
                    } else if (value >= Level.WARNING.intValue()) {
                        log.warn(message);
                    } else if (value >= Level.INFO.intValue()) {
                        log.info(message);
                    } else if (value >= Level.FINEST.intValue()) {
                        log.debug(message);
                    }
                }
            }

            return null;
        }

        private boolean isLogEnabled(int value) {
            return log.isDebugEnabled() && value >= Level.FINEST.intValue()
                || log.isInfoEnabled() && value >= Level.INFO.intValue()
                || log.isWarnEnabled() && value >= Level.WARNING.intValue()
                || log.isErrorEnabled() && value >= Level.SEVERE.intValue();
        }

        private Throwable getFirstException(Object[] objects) {
            for (Object object : objects) {
                if (object instanceof Throwable) {
                    return (Throwable) object;
                }
            }
            return null;
        }
    }
}
