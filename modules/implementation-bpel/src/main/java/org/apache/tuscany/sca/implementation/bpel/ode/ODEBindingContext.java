package org.apache.tuscany.sca.implementation.bpel.ode;

import javax.wsdl.PortType;
import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ode.bpel.iapi.BindingContext;
import org.apache.ode.bpel.iapi.Endpoint;
import org.apache.ode.bpel.iapi.EndpointReference;
import org.apache.ode.bpel.iapi.PartnerRoleChannel;

/**
 * Binding Context information
 * 
 * @version $Rev$ $Date$
 */
public class ODEBindingContext implements BindingContext {
    protected final Log __log = LogFactory.getLog(getClass());

    public ODEBindingContext() {

    }

    public EndpointReference activateMyRoleEndpoint(QName pid, Endpoint endpoint) {
        // This will be needed when we support callBacks
        if (__log.isDebugEnabled()) {
            __log.debug("Activating MyRole Endpoint : " + pid + " - " + endpoint.serviceName);
        }
        
        QName processName = getProcessName(pid);
        
        return new TuscanyEPR(processName, endpoint);
    }

    public void deactivateMyRoleEndpoint(Endpoint endpoint) {
        if (__log.isDebugEnabled()) {
            __log.debug("Deactivate MyRole Endpoint : " + endpoint.serviceName);
        }

    }

    public PartnerRoleChannel createPartnerRoleChannel(QName pid, PortType portType, Endpoint endpoint) {
        if (__log.isDebugEnabled()) {
            __log.debug("Create PartnerRole channel : " + pid + " - " + portType.getQName() + " - "+ endpoint.serviceName);
        }

        QName processName = getProcessName(pid);
        return new TuscanyPRC(processName, pid, portType, endpoint);
    }
    
    /**
     * Helper method to retrieve the BPEL process name from a processID (where processID have version concatenated to it)
     * @param pid
     * @return QName the BPEL process name
     */
    private static QName getProcessName(QName pid) {
        String processName = pid.getLocalPart().substring(0, pid.getLocalPart().lastIndexOf("-"));
        return new QName(pid.getNamespaceURI(), processName);
    }
}
