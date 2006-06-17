package org.apache.tuscany.core.mock.component;

import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Scope;

@Scope("REQUEST")
public class RequestScopeDestroyOnlyComponent extends SessionScopeComponentImpl {

    boolean destroyed;

    public boolean isDestroyed() {
        return destroyed;
    }

    @Destroy
    public void destroy() {
        destroyed = true;
    }

}
