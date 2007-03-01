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
package org.apache.tuscany.sca.plugin.itest.monitor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

import org.osoa.sca.annotations.Service;

import org.apache.maven.plugin.logging.Log;
import org.apache.tuscany.core.monitor.ProxyMonitorFactory;
import org.apache.tuscany.host.MonitorFactory;
import org.apache.tuscany.host.monitor.FormatterRegistry;

/**
 * A factory for monitors that forwards events to a {@link org.apache.maven.plugin.logging.Log}
 *
 * @version $Rev$ $Date$
 */
@Service(interfaces = {MonitorFactory.class, FormatterRegistry.class})
public class MavenLoggingMonitorFactory extends ProxyMonitorFactory {
    private Log log;

    public MavenLoggingMonitorFactory(Log log, Properties levels, Level defaultLevel) {
        Map<String, Object> configProperties = new HashMap<String, Object>();
        configProperties.put("levels", levels);
        configProperties.put("defaultLevel", defaultLevel);
        this.log = log;
        initInternal(configProperties);
    }

    public MavenLoggingMonitorFactory(Log log) {
        this.log = log;
    }

    protected <T> InvocationHandler createInvocationHandler(Class<T> monitorInterface, Map<String, Level> levels) {
        return new LoggingHandler(log, levels);
    }

    private class LoggingHandler implements InvocationHandler {
        private final Log log;
        private final Map<String, Level> methodLevels;

        public LoggingHandler(Log log, Map<String, Level> methodLevels) {
            this.log = log;
            this.methodLevels = methodLevels;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String sourceMethod = method.getName();
            Level level = methodLevels.get(sourceMethod);
            if (level == null) {
                return null;
            }
            if ((Level.FINE.equals(level)
                || Level.FINER.equals(level)
                || Level.FINEST.equals(level)
                || Level.CONFIG.equals(level))
                && log.isDebugEnabled()) {
                if (args != null) {
                    for (Object o : args) {
                        if (o instanceof Throwable) {
                            log.debug(formatException((Throwable) o));
                            break;
                        }
                    }
                }
            } else if (level.equals(Level.INFO.equals(level) && log.isInfoEnabled())) {
                if (args != null) {
                    for (Object o : args) {
                        if (o instanceof Throwable) {
                            log.info(formatException((Throwable) o));
                            break;
                        }
                    }
                }
            } else if (level.equals(Level.WARNING.equals(level) && log.isWarnEnabled())) {
                if (args != null) {
                    for (Object o : args) {
                        if (o instanceof Throwable) {
                            log.warn(formatException((Throwable) o));
                            break;
                        }
                    }
                }
            } else if (level.equals(Level.SEVERE.equals(level) && log.isErrorEnabled())) {
                if (args != null) {
                    for (Object o : args) {
                        if (o instanceof Throwable) {
                            log.error(formatException((Throwable) o));
                            break;
                        }
                    }
                }
            }


            return null;
        }
    }
}
