package org.apache.tuscany.container.js.rhino;

import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.tuscany.binding.axis2.util.AxiomHelper;
import org.apache.xmlbeans.XmlException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.xml.XMLObject;
import org.osoa.sca.ServiceRuntimeException;

import commonj.sdo.helper.TypeHelper;

/**
 * Invokes a JavaScript/E4X function with argument and return values that may be E4X XML objects. When calling the script from Java request arguments
 * that are AXIOM OMElements are converted to E4X XML objects. If the response from the script is an E4X XML object it is converted to an AXIOM
 * OMElement.
 */
public class RhinoE4XScript extends RhinoScript {

    private TypeHelper typeHelper;

    private String serviceNS = "http://helloworld.samples.tuscany.apache.org"; // TODO can't hardcode this!

    public RhinoE4XScript(String scriptName, String script, Map context, ClassLoader cl, TypeHelper typeHelper) {
        super(scriptName, script, context, cl);
        this.typeHelper = typeHelper;
    }

    protected RhinoE4XScript(String scriptName, String script, Scriptable scriptScope, TypeHelper typeHelper) {
        super(scriptName, script, scriptScope);
        this.typeHelper = typeHelper;
    }

    /**
     * Turn args to JS objects and convert any OMElement to E4X XML
     */
    @Override
    protected Object[] processArgs(String functionName, Object arg, Scriptable scope) {
        QName operationQN = new QName(serviceNS, functionName);
        OMElement om = AxiomHelper.toOMElement(typeHelper, (Object[]) arg, operationQN, true);
        try {
            return new Object[] { E4XAXIOMUtils.toScriptableObject(om, scope) };
        } catch (XmlException e) {
            throw new ServiceRuntimeException(e);
        }
    }

    /**
     * Unwrap and convert response
     */
    @Override
    protected Object processResponse(Object response, Class responseClass) {
        if (response instanceof XMLObject) {
            OMElement om = E4XAXIOMUtils.toOMElement((XMLObject) response);
            Object[] resp = AxiomHelper.toObjects(typeHelper, om, true);
            return resp[0];
        } else {
            return super.processResponse(response, responseClass);
        }
    }

    @Override
    public RhinoE4XScript copy() {
        return new RhinoE4XScript(scriptName, script, scriptScope, typeHelper);
    }

}
