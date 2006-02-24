package org.apache.tuscany.binding.axis.loader;

import java.util.Collection;

import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;

import org.apache.tuscany.binding.axis.assembly.WebServiceAssemblyFactory;
import org.apache.tuscany.binding.axis.assembly.WebServiceBinding;
import org.apache.tuscany.binding.axis.assembly.impl.WebServiceAssemblyFactoryImpl;
import org.apache.tuscany.core.runtime.RuntimeContext;
import org.apache.tuscany.core.system.annotation.Autowire;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.AssemblyModelObject;
import org.apache.tuscany.model.scdl.loader.SCDLModelLoader;
import org.apache.tuscany.model.util.XMLNameUtil;
import org.osoa.sca.annotations.Init;

/**
 * Populates the assembly model from an SCDL model
 */
@org.osoa.sca.annotations.Scope("MODULE")
public class WebServiceSCDLModelLoader implements SCDLModelLoader {
    
    private RuntimeContext runtimeContext;
    private WebServiceAssemblyFactory wsFactory;

    /**
     * Constructs a new WebServiceSCDLModelLoader.
     */
    public WebServiceSCDLModelLoader(AssemblyModelContext modelContext) {
        this.wsFactory=new WebServiceAssemblyFactoryImpl();
    }

    /**
     * @param runtimeContext The runtimeContext to set.
     */
    @Autowire
    public void setRuntimeContext(RuntimeContext runtimeContext) {
        this.runtimeContext = runtimeContext;
    }

    @Init(eager=true)
    public void init() {
        runtimeContext.addLoader(this);
    }

    /**
     * @see org.apache.tuscany.model.scdl.loader.SCDLModelLoader#load(org.apache.tuscany.model.assembly.AssemblyModelContext, java.lang.Object)
     */
    public AssemblyModelObject load(AssemblyModelContext modelContext, Object object) {
        if (object instanceof org.apache.tuscany.model.scdl.WebServiceBinding) {
            org.apache.tuscany.model.scdl.WebServiceBinding scdlBinding=(org.apache.tuscany.model.scdl.WebServiceBinding)object;
            WebServiceBinding binding=wsFactory.createWebServiceBinding();
            binding.setURI(scdlBinding.getUri());
            
            // Get the WSDL port namespace and name
            String portURI=scdlBinding.getPort();
            int h=portURI.indexOf('#');
            String portNamespace=portURI.substring(0,h);
            String portName=portURI.substring(h+1);
            
            // Load the WSDL file
            String packageName=XMLNameUtil.getPackageNameFromNamespace(portNamespace);
            String fileName=XMLNameUtil.getValidNameFromXMLName(portName, false);
            String wsdlURI=packageName+'/'+fileName+".wsdl";
            Definition definition;
            try {
                WSDLReader reader=WSDLFactory.newInstance().newWSDLReader();
                definition = reader.readWSDL(wsdlURI);
            } catch (WSDLException e) {
                throw new IllegalArgumentException(e);
            }
            binding.setWSDLDefinition(definition);

            // Find the specified port
            for (Service service : (Collection<Service>)definition.getServices().values()) {
                Port port=service.getPort(portName);
                if (port!=null) {
                    binding.setWSDLPort(port);
                    break;
                }
            }
            
            return binding;
        } else
            return null;
    }
}
