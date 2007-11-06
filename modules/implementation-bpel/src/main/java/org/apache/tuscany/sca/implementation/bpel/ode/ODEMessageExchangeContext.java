package org.apache.tuscany.sca.implementation.bpel.ode;

import org.apache.ode.bpel.iapi.BpelEngineException;
import org.apache.ode.bpel.iapi.ContextException;
import org.apache.ode.bpel.iapi.MessageExchangeContext;
import org.apache.ode.bpel.iapi.MyRoleMessageExchange;
import org.apache.ode.bpel.iapi.PartnerRoleMessageExchange;

/**
 * Message Exchange Context information
 * 
 * @version $Rev: 573789 $ $Date: 2007-09-07 23:59:49 -0700 (Fri, 07 Sep 2007) $
 */
public class ODEMessageExchangeContext implements MessageExchangeContext {
    private EmbeddedODEServer _server;

    public ODEMessageExchangeContext(EmbeddedODEServer _server) {
        this._server = _server;
    }

    public void invokePartner(PartnerRoleMessageExchange partnerRoleMessageExchange) throws ContextException {

    }

    public void onAsyncReply(MyRoleMessageExchange myRoleMessageExchange) throws BpelEngineException {

    }
}
