package org.apache.tuscany.sca.implementation.bpel.ode;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ode.bpel.iapi.BpelEngineException;
import org.apache.ode.bpel.iapi.ContextException;
import org.apache.ode.bpel.iapi.MessageExchangeContext;
import org.apache.ode.bpel.iapi.MyRoleMessageExchange;
import org.apache.ode.bpel.iapi.PartnerRoleMessageExchange;

/**
 * Message Exchange Context information
 * 
 * @version $Rev$ $Date$
 */
public class ODEMessageExchangeContext implements MessageExchangeContext {
    private static final Log __log = LogFactory.getLog(ODEMessageExchangeContext.class);
    
    private EmbeddedODEServer _server;

    public ODEMessageExchangeContext(EmbeddedODEServer _server) {
        this._server = _server;
    }

    public void invokePartner(PartnerRoleMessageExchange partnerRoleMessageExchange) throws ContextException {
        if (__log.isDebugEnabled()) {
            __log.debug("Invoking a partner operation: " + partnerRoleMessageExchange.getOperationName());
        }
        
        ODEExternalService scaService = new ODEExternalService(_server);
        scaService.invoke(partnerRoleMessageExchange);
    }

    public void onAsyncReply(MyRoleMessageExchange myRoleMessageExchange) throws BpelEngineException {
        if (__log.isDebugEnabled()) {
            __log.debug("Processing an async reply from service " + myRoleMessageExchange.getServiceName());
        }
    }
 }
