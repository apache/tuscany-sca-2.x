package org.apache.tuscany.binding.axis2.handler;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.ListIterator;

import javax.xml.stream.XMLStreamException;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.context.OperationContext;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.engine.MessageReceiver;
import org.apache.axis2.om.OMAbstractFactory;
import org.apache.axis2.om.OMDocument;
import org.apache.axis2.om.OMElement;
import org.apache.axis2.om.OMFactory;
import org.apache.axis2.om.OMNamespace;
import org.apache.axis2.receivers.AbstractInOutSyncMessageReceiver;
import org.apache.axis2.soap.SOAPBody;
import org.apache.axis2.soap.SOAPEnvelope;
import org.apache.axis2.soap.SOAPFactory;
import org.apache.tuscany.binding.axis2.util.AxiomHelper;
import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.context.EntryPointContext;
import org.apache.tuscany.core.context.InstanceContext;
import org.apache.tuscany.model.assembly.EntryPoint;
import org.apache.wsdl.WSDLConstants;
import org.eclipse.emf.common.util.UniqueEList;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Sequence;
import commonj.sdo.Type;

public class WebServiceEntryPointInOutSyncMessageReceiver extends AbstractInOutSyncMessageReceiver {
    // public static final String MEP_URL = "http://www.w3.org/2004/08/wsdl/tuscany/in-out";
    public static final String MEP_URL = WSDLConstants.MEP_URI_OUT_IN;

    protected final AggregateContext moduleContext;

    protected final EntryPoint entryPoint;
    protected final EntryPointContext entryPointContext;

    /**
     * Field log
     */

    /**
     * Constructor WebServiceEntryPointInOutSyncMessageReceiver
     * 
     * @param entryPoint
     * @param moduleContext
     * @param context 
     */
    public WebServiceEntryPointInOutSyncMessageReceiver(AggregateContext moduleContext, EntryPoint entryPoint, EntryPointContext context) {
        this.moduleContext = moduleContext;
        this.entryPoint = entryPoint;
        this.entryPointContext= context;
    }

    public void invokeBusinessLogic(MessageContext msgContext, MessageContext outMsgContext) throws AxisFault {
        try {
            
            AxisOperation axisOperation = msgContext.getAxisOperation();
            String axisOperationName= axisOperation.getName().getLocalPart();

            OMElement requestOM = msgContext.getEnvelope().getBody().getFirstElement();
            DataObject msgdo = AxiomHelper.toDataObject(requestOM);
            Sequence parmSeq = msgdo.getSequence("mixed");
            ArrayList parms = new ArrayList(parmSeq.size());
            for (int i = 0; i < parmSeq.size(); ++i) {
                Object parmDO = (Object) parmSeq.getValue(i);// parm element
                if (parmDO instanceof DataObject) {

                    Sequence nn = ((DataObject) parmDO).getSequence("mixed");

                    for (int j = 0; j < nn.size(); j++) {

                        Object valueDO = (Object) nn.getValue(j); // data array s
                        if (valueDO instanceof DataObject) {

                            Sequence seqVal = ((DataObject) valueDO).getSequence("mixed");
                            Object seqDO = seqVal.getValue(0);
                            if (seqDO instanceof String) {
                                parms.add(seqDO);
                            } else {
                                parms.add(valueDO); // no sure if this is right?

                            }
                        }
                    }
                }
            }
            Object[] args= parms.toArray(new Object[parms.size()]);
            Class[] argsClazz= new Class[args.length];
            for(int i= args.length -1; i> -1; --i){
                argsClazz[i]= args[i].getClass();
                
            }
            Class clazz = entryPoint.getConfiguredService().getService().getServiceContract().getInterface();
            Method operationMethod= clazz.getMethod(axisOperationName, argsClazz);
            
             
            InvocationHandler handler = (InvocationHandler) entryPointContext.getInstance(null);
           
         
            Object response = handler.invoke(null, operationMethod,  args);
            // response.getClass();
            //TODO how do I form the response .. the only way is to look at the schema ..yuk
            
          
            //SOAPFactory sf=  new SOAP11Factory();
            SOAPFactory fac = getSOAPFactory(msgContext);
           // SOAPEnvelope soapenv = sf.createSOAPEnvelope();
            SOAPEnvelope soapenv = fac.getDefaultEnvelope();
            SOAPBody soapbody = soapenv.getBody();
            OMNamespace respNS = fac.createOMNamespace(axisOperation.getName().getNamespaceURI(), "");
          
            OMElement se = fac.createOMElement(axisOperationName+"Response",respNS,soapbody);
            
            soapbody.addChild(se);
            OMElement returnEl= fac.createOMElement(axisOperationName+"Return",respNS,soapbody);
            returnEl.setText(response.toString());
            se.addChild(returnEl);
            // se.addChild(sf.createText(response, true));
            outMsgContext.setEnvelope(soapenv);
            // outMsgContext.setAxisOperation(axisOperation);
            // outMsgContext.setAxisService(msgContext.getAxisService());
         

        } catch (Exception e) {
            e.printStackTrace();
            throw new AxisFault("Error creating DataObject from Soapenvelope. " + e.getClass() + " " + e.getMessage(), e);

        } catch (Throwable e) {
            
            e.printStackTrace();
            throw new AxisFault("Error creating DataObject from Soapenvelope. " + e.getClass() + " " + e.getMessage(), e);
        }

    }

}
