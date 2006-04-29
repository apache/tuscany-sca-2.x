package org.apache.tuscany.core.extension.config.extensibility;

import java.lang.reflect.Method;

import org.apache.tuscany.core.extension.config.JavaExtensibilityElement;
import org.apache.tuscany.core.injection.MethodEventInvoker;

/**
 * @version $$Rev$$ $$Date$$
 */
public class InvokerExtensibilityElement implements JavaExtensibilityElement {

    private Method method;
    private MethodEventInvoker invoker;

    public InvokerExtensibilityElement(Method m) {
        method = m;
    }

    public MethodEventInvoker getEventInvoker() {
        if (invoker == null) {
            invoker = new MethodEventInvoker(method);
        }
        return invoker;
    }
}
