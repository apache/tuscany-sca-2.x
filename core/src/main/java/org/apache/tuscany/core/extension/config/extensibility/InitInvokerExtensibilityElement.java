package org.apache.tuscany.core.extension.config.extensibility;

import java.lang.reflect.Method;

/**
 * @version $$Rev$$ $$Date$$
 */
public class InitInvokerExtensibilityElement extends InvokerExtensibilityElement{

    private boolean eager;

    public InitInvokerExtensibilityElement(Method m, boolean eager) {
        super(m);
        this.eager = eager;
    }

    public boolean isEager() {
        return eager;
    }

}
