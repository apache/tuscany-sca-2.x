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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.apache.tuscany.spi.monitor.MonitorFactory;

/**
 * Implementation of a {@link MonitorFactory} that produces implementations that simply return.
 *
 * @version $Rev$ $Date$
 */
public class NullMonitorFactory implements MonitorFactory {
    public <T> T getMonitor(Class<T> monitorInterface) {
        /*
         * This uses a reflection proxy to implement the monitor interface which
         * is a simple but perhaps not very performant solution. Performance
         * might be improved by code generating an implementation with empty methods.
         */
        return monitorInterface.cast(Proxy.newProxyInstance(monitorInterface.getClassLoader(), new Class<?>[]{monitorInterface}, NULL_MONITOR));
    }

    /**
     * Singleton wire hander that does nothing.
     */
    private static final InvocationHandler NULL_MONITOR = new InvocationHandler() {
        public Object invoke(Object proxy, Method method, Object[] args) {
            return null;
        }
    };
}
