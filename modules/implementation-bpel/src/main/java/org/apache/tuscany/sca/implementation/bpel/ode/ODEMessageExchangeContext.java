package org.apache.tuscany.sca.implementation.bpel.ode;

import org.apache.ode.bpel.iapi.*;
import org.apache.ode.utils.DOMUtils;

/**
 * @author Matthieu Riou <mriou@apache.org>
 */
public class ODEMessageExchangeContext implements MessageExchangeContext {
    private EmbeddedODEServer _server;

    public ODEMessageExchangeContext(EmbeddedODEServer _server) {
        this._server = _server;
    }

    public void invokePartner(PartnerRoleMessageExchange partnerRoleMessageExchange) throws ContextException {
        // TODO necessary to invoke an external service
    }

    public void onAsyncReply(MyRoleMessageExchange myRoleMessageExchange) throws BpelEngineException {
        // TODO necessary to get the reply when is returned asynchronously (in a different thread)
    }
}
