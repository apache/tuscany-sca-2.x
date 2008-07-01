/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */

package org.apache.tuscany.sca.binding.ws.wsdlgen;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.wsdl.Definition;
import javax.wsdl.PortType;
import javax.wsdl.WSDLException;
import javax.wsdl.xml.WSDLWriter;
import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.AbstractContract;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.builder.BindingBuilderExtension;
import org.apache.tuscany.sca.assembly.builder.impl.ProblemImpl;
import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ResolverExtension;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceContract;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLDefinition;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLFactory;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterface;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterfaceContract;
import org.apache.tuscany.sca.interfacedef.wsdl.impl.InvalidWSDLException;
import org.apache.tuscany.sca.interfacedef.wsdl.impl.WSDLInterfaceIntrospectorImpl;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.monitor.Problem.Severity;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.IntentAttachPoint;
import org.apache.tuscany.sca.xsd.XSDFactory;

/**
 * @version $Rev$ $Date$
 */
public class BindingWSDLGenerator {
    private static final Logger logger = Logger.getLogger(BindingWSDLGenerator.class.getName());
    private static final QName SOAP12_INTENT = new QName("http://www.osoa.org/xmlns/sca/1.0", "soap.1_2");

    public static boolean printWSDL;     // external code sets this to print generated WSDL

    private BindingWSDLGenerator() {
        // this class has static methods only and cannot be instantiated
    }

    /**
     * Log a warning message.
     * @param problem
     */
    private static void logWarning(Problem problem) {
        Logger problemLogger = Logger.getLogger(problem.getSourceClassName(), problem.getBundleName());
        if (problemLogger != null){
            problemLogger.logp(Level.WARNING, problem.getSourceClassName(), null, problem.getMessageId(), problem.getMessageParams());
        } else {
            logger.severe("Can't get logger " + problem.getSourceClassName()+ " with bundle " + problem.getBundleName());
        }
    }

    /**
     * Report a warning.
     * @param message
     * @param binding
     * @param parameters
     */
    private static void warning(Monitor monitor, String message, WebServiceBinding wsBinding, String... messageParameters) {
        Problem problem = new ProblemImpl(BindingWSDLGenerator.class.getName(), "wsdlgen-validation-messages", Severity.WARNING, wsBinding, message, (Object[])messageParameters);
        if (monitor != null) {
            monitor.problem(problem);
        } else {
            logWarning(problem);
        }
    }

    /**
     * Report an error.
     * @param message
     * @param binding
     * @param parameters
     */
    private static void error(Monitor monitor, String message, WebServiceBinding wsBinding, String... messageParameters) {
        Problem problem = new ProblemImpl(BindingWSDLGenerator.class.getName(), "wsdlgen-validation-messages", Severity.ERROR, wsBinding, message, (Object[])messageParameters);
        if (monitor != null) {
            monitor.problem(problem);
        } else {
            throw new WSDLGenerationException(problem.toString(), null, problem);
        }
    }
  
    /**
     * Report an exception error.
     * @param message
     * @param binding
     * @param exception
     */
    private static void error(Monitor monitor, String message, WebServiceBinding wsBinding, Exception ex) {
        Problem problem = new ProblemImpl(BindingWSDLGenerator.class.getName(), "wsdlgen-validation-messages", Severity.ERROR, wsBinding, message, ex);
        if (monitor != null) {
            monitor.problem(problem);
        } else {
            throw new WSDLGenerationException(problem.toString(), ex, problem);
        }
    }
  
    /**
     * Report a fatal error.
     * @param message
     * @param binding
     * @param exception
     */
    private static void fatal(Monitor monitor, String message, WebServiceBinding wsBinding, String... messageParameters) {
        Problem problem = new ProblemImpl(BindingWSDLGenerator.class.getName(), "wsdlgen-validation-messages", Severity.ERROR,wsBinding, message, (Object[])messageParameters);
        throw new WSDLGenerationException(problem.toString(), null, problem);
    }
  
    /**
     * Report a fatal exception error.
     * @param message
     * @param binding
     * @param exception
     */
    private static void fatal(Monitor monitor, String message, WebServiceBinding wsBinding, Exception ex) {
        Problem problem = new ProblemImpl(BindingWSDLGenerator.class.getName(), "wsdlgen-validation-messages", Severity.ERROR, wsBinding, message, ex);
        throw new WSDLGenerationException(problem.toString(), ex, problem);
    }

    /**
     * This method can be called from the binding builder or from the runtime.
     * Report problems and exceptions in the most appropriate way for both
     * of these cases.
     */
    public static void generateWSDL(Component component,
                                    AbstractContract contract,
                                    WebServiceBinding wsBinding,
                                    ExtensionPointRegistry extensionPoints,
                                    Monitor monitor) {
        try {
            createWSDLDocument(component, contract, wsBinding, extensionPoints, monitor);
        } catch (WSDLGenerationException ex) {
            if (ex.getProblem() != null) {
                warning(monitor, "WsdlGenProblem", wsBinding, component.getName(), contract.getName());
                if (monitor != null) {
                    monitor.problem(ex.getProblem());
                } else {
                    throw ex;
                }
            } else if (ex.getCause() instanceof Exception) {
                warning(monitor, "WsdlGenException", wsBinding, component.getName(), contract.getName());
                error(monitor, "UnexpectedException", wsBinding, (Exception)ex.getCause());
            } else { // should never happen
                throw new IllegalStateException(ex);
            }
        } catch (RuntimeException ex) {
            warning(monitor, "WsdlGenException", wsBinding, component.getName(), contract.getName());
            error(monitor, "UnexpectedException", wsBinding, ex);
        }    
    }        

    private static void createWSDLDocument(Component component,
                                           AbstractContract contract,
                                           WebServiceBinding wsBinding,
                                           ExtensionPointRegistry extensionPoints,
                                           Monitor monitor) {
        ModelFactoryExtensionPoint modelFactories = extensionPoints.getExtensionPoint(ModelFactoryExtensionPoint.class);
        DataBindingExtensionPoint dataBindings = extensionPoints.getExtensionPoint(DataBindingExtensionPoint.class);
        WSDLFactory wsdlFactory = modelFactories.getFactory(WSDLFactory.class);
        XSDFactory xsdFactory = modelFactories.getFactory(XSDFactory.class);

        if (contract.getInterfaceContract() == null) {
            // can happen if incorrect component service name
            fatal(monitor, "MissingInterfaceContract", wsBinding, component.getName(), contract.getName()); 
        }

        InterfaceContract icontract = wsBinding.getBindingInterfaceContract();
        if (icontract == null) {
            icontract = contract.getInterfaceContract().makeUnidirectional(false);
            if (icontract instanceof JavaInterfaceContract) {
                ModelResolver resolver = component instanceof ResolverExtension ?
                                             ((ResolverExtension)component).getModelResolver() : null;
                icontract = createWSDLInterfaceContract(
                                    (JavaInterfaceContract)icontract,
                                    requiresSOAP12(wsBinding),
                                    resolver,
                                    dataBindings,
                                    wsdlFactory,
                                    xsdFactory,
                                    monitor);
            } else {
                try {
                    //TUSCANY-2316 Cloning the Interface Contract to avoid overriding data binding information 
                    icontract = (InterfaceContract)icontract.clone();
                } catch (Exception e) {
                    //ignore
                }
            }
            wsBinding.setBindingInterfaceContract(icontract);
        }
        
        // TODO - fix up the conversational flag and operation sequences in case the contract has come from WSDL
        // as we don't yet support requires="conversational" or sca:endConversation annotations
        // in WSDL interface descriptions (see section 1.5.4 of the Assembly Specification V1.0)
        if (contract.getInterfaceContract().getInterface() != null ) {
            icontract.getInterface().setConversational(contract.getInterfaceContract().getInterface().isConversational());
            
            for (Operation operation : icontract.getInterface().getOperations()){
                Operation serviceOperation = null;
                
                for (Operation tmpOp : contract.getInterfaceContract().getInterface().getOperations()){
                    if (operation.getName().equals(tmpOp.getName())) {
                        serviceOperation = tmpOp;
                        break;
                    }
                }
                
                if (serviceOperation != null ){
                    operation.setConversationSequence(serviceOperation.getConversationSequence());
                }
            }
        }

        /*
        // Look at all the Web Service bindings of the SCA service to see if any
        // of them have an existing generated WSDL definitions document.  If found,
        // use it for this binding as well.  If not found, generate a new document.
        Definition definition = null;
        for (Binding binding : contract.getBindings()) {
            if (binding instanceof WebServiceBinding) {
                definition = ((WebServiceBinding)binding).getWSDLDocument();
                if (definition != null) {
                    wsBinding.setWSDLDocument(definition);
                    break;
                }
            }
        }
        */
        // The above code is currently not used.  Instead, we only look
        // for a WSDL definitions document in this binding and don't
        // attempt to share the same document across multiple bindings.

        // generate a WSDL definitions document if needed
        Definition definition = wsBinding.getWSDLDocument();
        if (definition == null) {
            definition = WSDLServiceGenerator.configureWSDLDefinition(wsBinding, component, contract, monitor);
            wsBinding.setWSDLDocument(definition);
        }
    }

    protected static boolean requiresSOAP12(WebServiceBinding wsBinding) {
        if (wsBinding instanceof IntentAttachPoint) {
            List<Intent> intents = ((IntentAttachPoint)wsBinding).getRequiredIntents();
            for (Intent intent : intents) {
                if (SOAP12_INTENT.equals(intent.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Create a WSDLInterfaceContract from a JavaInterfaceContract
     */
    protected static WSDLInterfaceContract createWSDLInterfaceContract(JavaInterfaceContract contract,
                                                                       boolean requiresSOAP12,
                                                                       ModelResolver resolver,
                                                                       DataBindingExtensionPoint dataBindings,
                                                                       WSDLFactory wsdlFactory,
                                                                       XSDFactory xsdFactory,
                                                                       Monitor monitor) {

        WSDLInterfaceContract wsdlContract = wsdlFactory.createWSDLInterfaceContract();
        WSDLInterface wsdlInterface = wsdlFactory.createWSDLInterface();
        wsdlContract.setInterface(wsdlInterface);

        WSDLDefinition wsdlDefinition = wsdlFactory.createWSDLDefinition();
        JavaInterface iface = (JavaInterface)contract.getInterface();

        Definition def = null;
        try {
            Interface2WSDLGenerator wsdlGenerator =
                new Interface2WSDLGenerator(requiresSOAP12, resolver, dataBindings, xsdFactory, monitor);
            def = wsdlGenerator.generate(iface, wsdlDefinition);
        } catch (WSDLException e) {
            throw new WSDLGenerationException(e);
        }

        // for debugging
        if (printWSDL) {
            try {
                System.out.println("Generated WSDL for Java interface " + iface.getName() + " class " + iface.getJavaClass().getName());
                WSDLWriter writer =  javax.wsdl.factory.WSDLFactory.newInstance().newWSDLWriter();
                writer.writeWSDL(def, System.out);
            } catch (WSDLException e) {
                throw new WSDLGenerationException(e);
            }
        }

        wsdlDefinition.setDefinition(def);
        wsdlInterface.setWsdlDefinition(wsdlDefinition);
        wsdlInterface.setRemotable(true);
        wsdlInterface.setConversational(contract.getInterface().isConversational());
        wsdlInterface.setUnresolved(false);
        wsdlInterface.setRemotable(true);
        PortType portType = (PortType)def.getAllPortTypes().values().iterator().next();
        wsdlInterface.setPortType(portType);

        try {
            for (Operation op : iface.getOperations()) {
                javax.wsdl.Operation wsdlOp = portType.getOperation(op.getName(), null, null);
                wsdlInterface.getOperations().add(WSDLInterfaceIntrospectorImpl.getOperation(
                                                      wsdlOp, wsdlDefinition, resolver, xsdFactory));
            }
        } catch (InvalidWSDLException e) {
            throw new WSDLGenerationException(e);
        }

        return wsdlContract;
    }

}
