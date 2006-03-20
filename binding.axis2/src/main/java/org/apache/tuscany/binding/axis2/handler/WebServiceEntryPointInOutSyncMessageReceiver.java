package org.apache.tuscany.binding.axis2.handler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import javax.wsdl.Part;
import javax.xml.namespace.QName;

import commonj.sdo.helper.TypeHelper;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.receivers.AbstractInOutSyncMessageReceiver;
import org.apache.ws.commons.om.OMElement;
import org.apache.ws.commons.soap.SOAPBody;
import org.apache.ws.commons.soap.SOAPEnvelope;
import org.apache.ws.commons.soap.SOAPFactory;
import org.apache.wsdl.WSDLConstants;

import org.apache.tuscany.binding.axis2.util.AxiomHelper;
import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.context.EntryPointContext;
import org.apache.tuscany.model.assembly.EntryPoint;

public class WebServiceEntryPointInOutSyncMessageReceiver extends AbstractInOutSyncMessageReceiver {
    public static final String MEP_URL = WSDLConstants.MEP_URI_IN_OUT;

    protected final AggregateContext moduleContext;

    protected final EntryPoint entryPoint;
    protected final EntryPointContext entryPointContext;

    private final WebServicePortMetaData wsdlPortInfo;
    private final TypeHelper typeHelper;
    private final ClassLoader classLoader;
    private final Class<?> serviceInterface;

    /**
     * Constructor WebServiceEntryPointInOutSyncMessageReceiver
     *
     * @param entryPoint
     * @param moduleContext
     * @param context
     * @param wsdlPortInfo
     */
    public WebServiceEntryPointInOutSyncMessageReceiver(AggregateContext moduleContext, EntryPoint entryPoint, EntryPointContext context, WebServicePortMetaData wsdlPortInfo) {
        this.moduleContext = moduleContext;
        this.entryPoint = entryPoint;
        this.entryPointContext = context;
        this.wsdlPortInfo = wsdlPortInfo;
        typeHelper = entryPoint.getAggregate().getAssemblyModelContext().getTypeHelper();
        classLoader = entryPoint.getAggregate().getAssemblyModelContext().getApplicationResourceLoader().getClassLoader();
        serviceInterface = entryPoint.getConfiguredService().getService().getServiceContract().getInterface();
    }

    public void invokeBusinessLogic(MessageContext msgContext, MessageContext outMsgContext) throws AxisFault {
        // set application classloader onto the thread
        ClassLoader ccl = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(classLoader);

            // get operation name from request message
            AxisOperation axisOperation = msgContext.getAxisOperation();
            String axisOperationName = axisOperation.getName().getLocalPart();

            // de-serialize request parameters to objects
            OMElement requestOM = msgContext.getEnvelope().getBody().getFirstElement();
            Object[] args = AxiomHelper.toObjects(typeHelper, requestOM);

            // map the operation and arguments to the service method
            Class<?>[] argsClazz = new Class[args.length];
            for (int i = args.length - 1; i > -1; --i) {
                argsClazz[i] = args[i].getClass();

            }
            Method operationMethod = serviceInterface.getMethod(axisOperationName, argsClazz);

            // invoke the proxy's InvocationHandler
            // FIXME we should be invoking the Tuscany pipeline rather than the proxy
            InvocationHandler handler = (InvocationHandler) entryPointContext.getImplementationInstance();
            Object response = handler.invoke(null, operationMethod, args);

            // construct the response message
            SOAPFactory fac = getSOAPFactory(msgContext);
            SOAPEnvelope soapenv = fac.getDefaultEnvelope();
            SOAPBody soapbody = soapenv.getBody();

            // serialize the invocation respose into the message
            QName responseTypeQN = getResponseTypeName(operationMethod.getName());
            OMElement responseOM = AxiomHelper.toOMElement(typeHelper, new Object[]{response}, responseTypeQN);
            soapbody.addChild(responseOM);

            outMsgContext.setEnvelope(soapenv);
            outMsgContext.getOperationContext().setProperty(Constants.RESPONSE_WRITTEN, Constants.VALUE_TRUE);
        } catch (Exception e) {
            e.printStackTrace();
            throw new AxisFault("Error creating DataObject from Soapenvelope. " + e.getClass() + ' ' + e.getMessage(), e);
        } catch (Throwable e) {
            e.printStackTrace();
            throw new AxisFault("Error creating DataObject from Soapenvelope. " + e.getClass() + ' ' + e.getMessage(), e);
        } finally {
            Thread.currentThread().setContextClassLoader(ccl);
        }
    }

    protected QName getResponseTypeName(String operationName) {
        WebServiceOperationMetaData op = wsdlPortInfo.getOperationMetaData(operationName);
        Part part = op.getOutputPart(0);
        return part.getElementName();
    }


}
