package org.apache.tuscany.container.java.mock.components;

import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Scope;

@Scope("REQUEST")
public class RequestScopeDestroyOnlyComponent extends SessionScopeComponentImpl {

    boolean destroyed = false;

    public boolean isDestroyed() {
        return destroyed;
    }

    @Destroy
    public void destroy() {
        destroyed = true;
    }

}
