package org.apache.tuscany.binding.celtix.loader;

import java.io.IOException;
import java.net.URL;
import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.wsdl.factory.WSDLFactory;

import org.w3c.dom.Element;

import org.apache.tuscany.core.loader.WSDLDefinitionRegistry;

import org.objectweb.celtix.BusException;
import org.objectweb.celtix.wsdl.WSDLManager;

public class TuscanyWSDLManager implements WSDLManager {
    WSDLDefinitionRegistry wsdlDefinitionRegistry;

    public TuscanyWSDLManager(WSDLDefinitionRegistry w) throws BusException {
        wsdlDefinitionRegistry = w;
    }

    public WSDLFactory getWSDLFactory() {
        //Not supported
        return null;
    }

    public ExtensionRegistry getExtenstionRegistry() {
        return wsdlDefinitionRegistry.getExtensionRegistry();
    }

    public Definition getDefinition(URL url) throws WSDLException {
        try {
            return wsdlDefinitionRegistry.loadDefinition(null, url);
        } catch (IOException e) {
            throw new WSDLException(WSDLException.CONFIGURATION_ERROR, e.getMessage());
        }
    }

    public Definition getDefinition(String url) throws WSDLException {
        try {
            //The namespace is the wsdl targetNamesapce, it is only used
            //when the wsdl is created into cache. we are ok here to set it to null.
            return wsdlDefinitionRegistry.loadDefinition(null, new URL(url));
        } catch (IOException e) {
            throw new WSDLException(WSDLException.CONFIGURATION_ERROR, e.getMessage());
        }
    }

    public Definition getDefinition(Element el) throws WSDLException {
        //Not supported
        return null;
    }

    public Definition getDefinition(Class<?> sei) throws WSDLException {
        //Not supported
        return null;
    }

    public void addDefinition(Object key, Definition wsdl) {
        //Not supported
    }

    public void shutdown() {
    }


}
