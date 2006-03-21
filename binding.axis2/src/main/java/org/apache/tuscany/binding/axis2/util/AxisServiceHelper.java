package org.apache.tuscany.binding.axis2.util;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap.SOAPOperation;
import javax.xml.namespace.QName;

import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.OutInAxisOperation;
import org.apache.axis2.description.OutOnlyAxisOperation;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.i18n.Messages;

/**
 * createClientSideAxisService copied from AxisService and changed to use a Definition instead of URL to WSDL
 * TODO: The Axis2 guys are going to add this method so this class can be deleted when they do and we move to a new Axis2 build. See JIRA AXIS2-507.
 */
public class AxisServiceHelper {

    public static AxisService createClientSideAxisService(Definition wsdlDefinition, QName wsdlServiceName, String portName, Options options) throws AxisFault {
        AxisService axisService;
        try {

            axisService = new AxisService();

            Service wsdlService;
            if (wsdlServiceName != null) {
                wsdlService = wsdlDefinition.getService(wsdlServiceName);
                if (wsdlService == null) {
                    throw new AxisFault(Messages.getMessage("servicenotfoundinwsdl", wsdlServiceName.getLocalPart()));
                }

            } else {
                Collection col = wsdlDefinition.getServices().values();
                if (col != null && col.size() > 0) {
                    wsdlService = (Service) col.iterator().next();
                    if (wsdlService == null) {
                        throw new AxisFault(Messages.getMessage("noservicefoundinwsdl"));
                    }
                } else {
                    throw new AxisFault(Messages.getMessage("noservicefoundinwsdl"));
                }
            }
            axisService.setName(wsdlService.getQName().getLocalPart());

            Port port;
            if (portName != null) {
                port = wsdlService.getPort(portName);
                if (port == null) {
                    throw new AxisFault(Messages.getMessage("noporttypefoundfor", portName));
                }
            } else {
                Collection ports = wsdlService.getPorts().values();
                if (ports != null && ports.size() > 0) {
                    port = (Port) ports.iterator().next();
                    if (port == null) {
                        throw new AxisFault(Messages.getMessage("noporttypefound"));
                    }
                } else {
                    throw new AxisFault(Messages.getMessage("noporttypefound"));
                }
            }
            List exteElemts = port.getExtensibilityElements();
            if (exteElemts != null) {
                Iterator extItr = exteElemts.iterator();
                while (extItr.hasNext()) {
                    Object extensibilityElement = extItr.next();
                    if (extensibilityElement instanceof SOAPAddress) {
                        SOAPAddress address = (SOAPAddress) extensibilityElement;
                        options.setTo(new EndpointReference(address.getLocationURI()));
                    }
                }
            }

            Binding binding = port.getBinding();
            Iterator bindingOperations = binding.getBindingOperations().iterator();
            while (bindingOperations.hasNext()) {
                BindingOperation bindingOperation = (BindingOperation) bindingOperations.next();
                AxisOperation axisOperation;
                if (bindingOperation.getBindingInput() == null && bindingOperation.getBindingOutput() != null) {
                    axisOperation = new OutOnlyAxisOperation();
                } else {
                    axisOperation = new OutInAxisOperation();
                }
                axisOperation.setName(new QName(bindingOperation.getName()));
                List list = bindingOperation.getExtensibilityElements();
                if (list != null) {
                    Iterator exteElements = list.iterator();
                    while (exteElements.hasNext()) {
                        Object extensibilityElement = exteElements.next();
                        if (extensibilityElement instanceof SOAPOperation) {
                            SOAPOperation soapOp = (SOAPOperation) extensibilityElement;
                            axisOperation.addParameter(new Parameter(AxisOperation.SOAP_ACTION, soapOp.getSoapActionURI()));
                        }
                    }
                }
                axisService.addOperation(axisOperation);
            }

        } catch (IOException e) {
            throw new AxisFault("IOException" + e.getMessage());
        }
        return axisService;
    }

}
