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
package org.apache.tuscany.implementation.java.monitor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.osoa.sca.annotations.Service;


/**
 * A factory for monitors that forwards events to a {@link java.util.logging.Logger Java Logging (JSR47) Logger}.
 *
 * @version $Rev$ $Date$
 * @see java.util.logging
 */
@Service(interfaces = {MonitorFactory.class, FormatterRegistry.class})
public class JavaLoggingMonitorFactory extends ProxyMonitorFactory {

    /**
     * Construct a MonitorFactory that will monitor the specified methods at the specified levels and generate messages
     * using java.util.logging.
     * <p/>
     * The supplied Properties can be used to specify custom log levels for specific monitor methods. The key should be
     * the method name in form returned by <code>Class.getName() + '#' + Method.getName()</code> and the value the log
     * level to use as defined by {@link java.util.logging.Level}.
     *
     * @param levels       definition of custom levels for specific monitored methods, may be null or empty.
     * @param defaultLevel the default log level to use
     * @param bundleName   the name of a resource bundle that will be passed to the logger
     * @see java.util.logging.Logger
     */
    public JavaLoggingMonitorFactory(Properties levels, Level defaultLevel, String bundleName) {
        Map<String, Object> configProperties = new HashMap<String, Object>();
        configProperties.put("levels", levels);
        configProperties.put("defaultLevel", defaultLevel);
        configProperties.put("bundleName", bundleName);
        initInternal(configProperties);
    }

    /**
     * Constructs a MonitorFactory that needs to be subsequently configured via a call to {@link #initialize}.
     */
    public JavaLoggingMonitorFactory() {
    }

    protected <T> InvocationHandler createInvocationHandler(Class<T> monitorInterface,
                                                            Map<String, Level> levels) {
        ResourceBundle bundle = locateBundle(monitorInterface, bundleName);
        Logger logger = Logger.getLogger(monitorInterface.getName());
        return new LoggingHandler(logger, levels, bundle);
    }

    private class LoggingHandler implements InvocationHandler {
        private final Logger logger;
        private final Map<String, Level> methodLevels;
        private final ResourceBundle bundle;

        public LoggingHandler(Logger logger,
                              Map<String, Level> methodLevels,
                              ResourceBundle bundle
        ) {
            this.logger = logger;
            this.methodLevels = methodLevels;
            this.bundle = bundle;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String sourceMethod = method.getName();
            Level level = methodLevels.get(sourceMethod);
            if (level != null && logger.isLoggable(level)) {
                // construct the key for the resource bundle
                String className = logger.getName();
                String key = className + '#' + sourceMethod;

                LogRecord logRecord = new LogRecord(level, key);
                logRecord.setLoggerName(className);
                logRecord.setSourceClassName(className);
                logRecord.setSourceMethodName(sourceMethod);
                logRecord.setParameters(args);
                if (args != null) {
                    for (Object o : args) {
                        if (o instanceof Throwable) {
                            logRecord.setMessage(formatException((Throwable) o));
                            break;
                        }
                    }
                }
                logRecord.setResourceBundle(bundle);
                logger.log(logRecord);
            }
            return null;
        }
    }
}
