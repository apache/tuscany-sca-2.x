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
package org.apache.tuscany.core.monitor;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.apache.tuscany.spi.monitor.LogLevel;
import org.apache.tuscany.spi.monitor.MonitorFactory;

/**
 * A factory for monitors that forwards events to a {@link java.util.logging.Logger Java Logging (JSR47) Logger}.
 *
 * @version $Rev$ $Date$
 * @see java.util.logging
 */
public class JavaLoggingMonitorFactory implements MonitorFactory {
    private String bundleName;
    private Level defaultLevel;
    private Map<String, Level> levels;

    private Map<Class<?>, WeakReference<?>> proxies = new WeakHashMap<Class<?>, WeakReference<?>>();

    /**
     * Construct a MonitorFactory that will monitor the specified methods at the specified levels and generate messages
     * using java.util.logging.
     * <p/>
     * The supplied Properties can be used to specify custom log levels for specific monitor methods. The key should be
     * the method name in form returned by <code>Class.getName() + '#' + Method.getName()</code> and the value the log
     * level to use as defined by {@link java.util.logging.Level}.
     *
     * @param levels       definition of custom levels for specific monitored methods
     * @param defaultLevel the default log level to use
     * @param bundleName   the name of a resource bundle that will be passed to the logger
     * @see java.util.logging.Logger
     */
    public JavaLoggingMonitorFactory(Properties levels, Level defaultLevel, String bundleName) {
        Map<String,Object> configProperties = new HashMap<String,Object>();
        configProperties.put("levels", levels);
        configProperties.put("defaultLevel", defaultLevel);
        configProperties.put("bundleName", bundleName);
        initialize(configProperties);
    }

    /**
     * Constructs a MonitorFactory that needs to be subsequently configured via a call to {@link #initialize}.
     */
    public JavaLoggingMonitorFactory() {
    }

    public void initialize(Map<String,Object> configProperties) {
        try {
            this.defaultLevel = (Level) configProperties.get("defaultLevel");
            this.bundleName = (String) configProperties.get("bundleName");
            Properties levels = (Properties) configProperties.get("levels");

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
        catch (ClassCastException cce) {
            throw new IllegalArgumentException(cce.getLocalizedMessage());
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
        Logger logger = Logger.getLogger(className);
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

        ResourceBundle bundle = locateBundle(monitorInterface, bundleName);

        InvocationHandler handler = new LoggingHandler(logger, levels, bundle);
        return monitorInterface
            .cast(Proxy.newProxyInstance(monitorInterface.getClassLoader(), new Class<?>[]{monitorInterface}, handler));
    }

    private static <T>ResourceBundle locateBundle(Class<T> monitorInterface, String bundleName) {
        Locale locale = Locale.getDefault();
        ClassLoader cl = monitorInterface.getClassLoader();
        String packageName = monitorInterface.getPackage().getName();
        while (true) {
            try {
                return ResourceBundle.getBundle(packageName + '.' + bundleName, locale, cl);
            } catch (MissingResourceException e) {
                //ok
            }
            int index = packageName.lastIndexOf('.');
            if (index == -1) {
                break;
            }
            packageName = packageName.substring(0, index);
        }
        try {
            return ResourceBundle.getBundle(bundleName, locale, cl);
        } catch (Exception e) {
            return null;
        }
    }

    private static final class LoggingHandler implements InvocationHandler {
        private final Logger logger;
        private final Map<String, Level> methodLevels;
        private final ResourceBundle bundle;

        public LoggingHandler(Logger logger, Map<String, Level> methodLevels, ResourceBundle bundle) {
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
                            logRecord.setThrown((Throwable) o);
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
