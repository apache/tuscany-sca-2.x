package org.apache.tuscany.sca.implementation.bpel.ode;

import org.apache.ode.bpel.iapi.BindingContext;
import org.apache.ode.bpel.iapi.EndpointReference;
import org.apache.ode.bpel.iapi.Endpoint;
import org.apache.ode.bpel.iapi.PartnerRoleChannel;
import org.w3c.dom.Document;

import javax.xml.namespace.QName;
import javax.wsdl.PortType;

/**
 * @author Matthieu Riou <mriou@apache.org>
 */
public class ODEBindingContext implements BindingContext {

    private EmbeddedODEServer _server;

    public ODEBindingContext(EmbeddedODEServer _server) {
        this._server = _server;
    }

    public EndpointReference activateMyRoleEndpoint(QName pid, Endpoint endpoint) {
        return new TuscanyEPR();
    }

    public void deactivateMyRoleEndpoint(Endpoint endpoint) {
        // TODO
    }

    public PartnerRoleChannel createPartnerRoleChannel(QName qName, PortType portType, Endpoint endpoint) {
        // TODO
        return null;
    }

    // TODO This should hold something that makes sense for Tuscany so that the process has
    // an address that makes sense from the outside world perspective 
    private class TuscanyEPR implements EndpointReference {
        public Document toXML() {
            return null;
        }
    }
}
