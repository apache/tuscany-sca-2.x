package org.apache.tuscany.core.monitor;

import org.apache.tuscany.spi.monitor.MonitorFactory;

import java.util.Map;

/**
 * Helper for creating MonitorFactory instances.
 * 
 * @version $$Rev: $$ $$Date: $$
 */

public final class MonitorFactoryUtil {
    /**
     * Hide the constructor
     */
    private MonitorFactoryUtil() {
    }

    /**
     * Creates a MonitorFactory instance of the specified type.
     * @param name fully qualified classname of the desired MonitorFactory type
     * @param props collection of initialization properties
     * @return a configured MonitorFactory instance, or null if the factory could not be instantiated.
     */
    public static MonitorFactory createMonitorFactory(String name, Map<String, Object> props) {
        Class<? extends MonitorFactory> clazz;
        try {
            clazz = (Class<? extends MonitorFactory>) Class.forName(name);
        } catch (ClassNotFoundException cnfe) {
            return null;
        } catch (ClassCastException cce) {
            return null;
        }

        return createMonitorFactory(clazz, props);
    }

    /**
     * Creates a MonitorFactory instance of the specified type.
     * @param mfc class of the desired MonitorFactory type
     * @param props collection of initialization properties
     * @return a configured MonitorFactory instance, or null if the factory could not be instantiated.
     */
    public static MonitorFactory createMonitorFactory(Class<? extends MonitorFactory> mfc, Map<String, Object> props) {
        MonitorFactory mf;
        try {
            mf = mfc.newInstance();
            mf.initialize(props);
        } catch (InstantiationException e) {
            throw new AssertionError(e);
        } catch (IllegalAccessException e) {
            throw new AssertionError(e);
        }
        // allow IllegalArgumentException to propogate out

        return mf;
    }
}