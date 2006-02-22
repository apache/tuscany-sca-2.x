package org.apache.tuscany.container.js.assembly.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.tuscany.common.resource.loader.ResourceLoader;
import org.apache.tuscany.container.js.assembly.JavaScriptImplementation;
import org.apache.tuscany.container.js.rhino.RhinoInvoker;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.ComponentType;
import org.apache.tuscany.model.assembly.ModelInitException;
import org.apache.tuscany.model.assembly.impl.ComponentImplementationImpl;

public class JavaScriptImplementationImpl extends ComponentImplementationImpl implements JavaScriptImplementation {

    private ResourceLoader resourceLoader;

    public JavaScriptImplementationImpl() {
        super();
    }

    public ResourceLoader getResourceLoader() {
        return null;
    }

    public void initialize(AssemblyModelContext modelContext) throws ModelInitException {
        if (isInitialized()) {
            return;
        }
        this.resourceLoader = modelContext.getResourceLoader();
        if(resourceLoader == null){
            throw new ModelInitException("No resource loader set on model context");
        }
        getScriptFile();

        // Initialize the component type
        ComponentType componentType = getComponentType();
        if (componentType == null) {
            try {
                componentType = createComponentType(modelContext);
            } catch (IOException e) {
                throw new ModelInitException("Error retrieving component type file",e);
            }
            setComponentType(componentType);
        }

        super.initialize(modelContext);

    }

    String script;

    public String getScriptFile() {
        return script;
    }

    public void setScriptFile(String fn) {
        script = fn;
    }

    // TODO remove this signature
    public RhinoInvoker getRhinoInvoker() {
        return null;
    }

    private String scriptCode;

    public String getScript() throws ModelInitException {
        if (scriptCode != null) {
            return scriptCode;
        }
        try {
            URL url = resourceLoader.getResource(getScriptFile());
            if (url == null) {
                ModelInitException ce = new ModelInitException("Script not found");
                ce.setIdentifier(getScriptFile());
                throw ce;
            }
            InputStream inputStream = url.openStream();
            try {
                StringBuffer sb = new StringBuffer();
                int n = 0;
                while ((n = inputStream.read()) != -1) {
                    sb.append((char) n);
                }
                scriptCode = sb.toString();
                return scriptCode;
            } finally {
                inputStream.close();
            }
        } catch (IOException e) {
            ModelInitException ce = new ModelInitException("Error reading script file",e);
            ce.setIdentifier(getScriptFile());
            throw ce;
        }
    }

    /**
     * Create the component type
     * 
     * @param modelContext
     * @param implementationClass
     */
    private ComponentType createComponentType(AssemblyModelContext modelContext) throws IOException{
        String prefix = script.substring(0,script.lastIndexOf('.'));
        URL componentTypeFile = resourceLoader.getResource(prefix + ".componentType");
        if (componentTypeFile != null) {
            return modelContext.getAssemblyLoader().getComponentType(componentTypeFile.toString());
        } else {
            // TODO we could introspect the JavaScript source
            return modelContext.getAssemblyFactory().createComponentType();
        }
    }
    
    
}
