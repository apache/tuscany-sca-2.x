package org.apache.tuscany.sca.implementation.bpel.ode;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ode.bpel.iapi.BpelEngineException;
import org.apache.ode.bpel.iapi.ContextException;
import org.apache.ode.bpel.iapi.MessageExchangeContext;
import org.apache.ode.bpel.iapi.MyRoleMessageExchange;
import org.apache.ode.bpel.iapi.PartnerRoleMessageExchange;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.implementation.bpel.impl.ThreadRuntimeComponentContext;
import org.apache.tuscany.sca.runtime.RuntimeComponent;

/**
 * Message Exchange Context information
 * 
 * @version $Rev: 573789 $ $Date: 2007-09-07 23:59:49 -0700 (Fri, 07 Sep 2007) $
 */
public class ODEMessageExchangeContext implements MessageExchangeContext {
    private static final Log __log = LogFactory.getLog(ODEMessageExchangeContext.class);
    
    private EmbeddedODEServer _server;

    public ODEMessageExchangeContext(EmbeddedODEServer _server) {
        this._server = _server;
    }

    public void invokePartner(PartnerRoleMessageExchange partnerRoleMessageExchange) throws ContextException {
        if (__log.isDebugEnabled())
            __log.debug("Invoking a partner operation: " + partnerRoleMessageExchange.getOperationName());
        
        System.out.println(">>> Invoking a partner operation: " + partnerRoleMessageExchange.getOperationName());
        
        RuntimeComponent tuscanyRuntimeComponent = ThreadRuntimeComponentContext.getRuntimeComponent();
        for(ComponentReference componentReference : tuscanyRuntimeComponent.getReferences()) {
            System.out.println("Reference : " + componentReference.getName());
            componentReference.getBindings().get(0);
        }
    }

    public void onAsyncReply(MyRoleMessageExchange myRoleMessageExchange) throws BpelEngineException {
        if (__log.isDebugEnabled())
            __log.debug("Processing an async reply from service " + myRoleMessageExchange.getServiceName());  
    }
}
