/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.common.monitor.impl;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tuscany.common.monitor.LogLevel;
import org.apache.tuscany.common.monitor.MonitorFactory;

/**
 * A factory for monitors that forwards events to a {@link java.util.logging.Logger Java Logging (JSR47) Logger}.
 *
 * @version $Rev$ $Date$
 * @see java.util.logging
 */
public class JavaLoggingMonitorFactory implements MonitorFactory {
    private final String bundleName;
    private final Level defaultLevel;
    private final Map<String, Level> levels;

    private final Map<Class<?>, WeakReference<?>> proxies = new WeakHashMap<Class<?>, WeakReference<?>>();

    /**
     * Construct a MonitorFactory that will monitor the specified methods at the specified levels
     * and generate messages using java.util.logging.
     * <p/>
     * The supplied Properties can be used to specify custom log levels for specific monitor
     * methods. The key should be the method name in form returned by
     * <code>Class.getName() + '#' + Method.getName()</code> and the value the log level to use
     * as defined by {@link java.util.logging.Level}.
     *
     * @param levels       definition of custom levels for specific monitored methods
     * @param defaultLevel the default log level to use
     * @param bundleName   the name of a resource bundle that will be passed to the logger
     * @see java.util.logging.Logger
     */
    public JavaLoggingMonitorFactory(Properties levels, Level defaultLevel, String bundleName) {
        this.defaultLevel = defaultLevel;
        this.bundleName = bundleName;
        this.levels = new HashMap<String, Level>(levels.size());
        for (Map.Entry<Object, Object> entry : levels.entrySet()) {
            String method = (String) entry.getKey();
            String level = (String) entry.getValue();
            try {
                this.levels.put(method, Level.parse(level));
            } catch (IllegalArgumentException e) {
                throw new InvalidLevelException(method, level);
            }
        }
    }

    public synchronized <T> T getMonitor(Class<T> monitorInterface) {
        T proxy = getCachedMonitor(monitorInterface);
        if (proxy == null) {
            proxy = createMonitor(monitorInterface, bundleName);
            proxies.put(monitorInterface, new WeakReference<T>(proxy));
        }
        return proxy;
    }

    private <T>T getCachedMonitor(Class<T> monitorInterface) {
        WeakReference<?> ref = proxies.get(monitorInterface);
        return (ref != null) ? monitorInterface.cast(ref.get()) : null;
    }

    private <T>T createMonitor(Class<T> monitorInterface, String bundleName) {
        String className = monitorInterface.getName();
        Logger logger = Logger.getLogger(className, bundleName);
        Method[] methods = monitorInterface.getMethods();
        Map<String, Level> levels = new HashMap<String, Level>(methods.length);
        for (Method method : methods) {
            String key = className + '#' + method.getName();
            Level level = this.levels.get(key);

            // if not specified the in config properties, look for an annotation on the method
            if (level == null) {
                LogLevel annotation = method.getAnnotation(LogLevel.class);
                if (annotation != null && annotation.value() != null) {
                    try {
                        level = Level.parse(annotation.value());
                    } catch (IllegalArgumentException e) {
                        // bad value, just use the default
                        level = defaultLevel;
                    }
                }
            }
            if (level == null) {
                level = defaultLevel;
            }
            levels.put(method.getName(), level);
        }
        InvocationHandler handler = new LoggingHandler(logger, levels);
        return monitorInterface.cast(Proxy.newProxyInstance(monitorInterface.getClassLoader(), new Class<?>[]{monitorInterface}, handler));
    }

    private static final class LoggingHandler implements InvocationHandler {
        private final Logger logger;
        private final Map<String, Level> methodLevels;

        public LoggingHandler(Logger logger, Map<String, Level> methodLevels) {
            this.logger = logger;
            this.methodLevels = methodLevels;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String sourceMethod = method.getName();
            Level level = methodLevels.get(sourceMethod);
            if (level != null && logger.isLoggable(level)) {
                // construct the key for the resource bundle
                String className = logger.getName();
                String key = className + '#' + sourceMethod;

                // if the only argument is a Throwable use the special logger for it
                if (args != null && args.length == 1 && args[0] instanceof Throwable) {
                    logger.logp(level, className, sourceMethod, key, (Throwable) args[0]);
                } else {
                    logger.logp(level, className, sourceMethod, key, args);
                }
            }
            return null;
        }
    }
}
