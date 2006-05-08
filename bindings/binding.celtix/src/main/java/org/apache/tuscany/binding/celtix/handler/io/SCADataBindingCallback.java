package org.apache.tuscany.binding.celtix.handler.io;

import org.w3c.dom.Node;
import commonj.sdo.helper.TypeHelper;
import org.objectweb.celtix.bindings.DataReader;
import org.objectweb.celtix.bindings.DataWriter;
import org.objectweb.celtix.bus.bindings.AbstractWSDLOperationDataBindingCallback;
import org.objectweb.celtix.bus.bindings.WSDLOperationInfo;
import org.objectweb.celtix.context.ObjectMessageContext;

public class SCADataBindingCallback extends AbstractWSDLOperationDataBindingCallback {
    TypeHelper typeHelper;
    boolean hasInOut;

    public SCADataBindingCallback(WSDLOperationInfo op, TypeHelper helper, boolean inout) {
        super(op);
        typeHelper = helper;
        hasInOut = inout;
    }

    public TypeHelper getTypeHelper() {
        return typeHelper;
    }

    public boolean hasInOut() {
        return hasInOut;
    }

    public Mode getMode() {
        return Mode.PARTS;
    }

    public Class<?>[] getSupportedFormats() {
        return new Class<?>[]{Node.class};
    }

    @SuppressWarnings("unchecked")
    public <T> DataWriter<T> createWriter(Class<T> cls) {
        if (cls == Node.class) {
            return (DataWriter<T>)new NodeDataWriter(this);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T> DataReader<T> createReader(Class<T> cls) {
        if (cls == Node.class) {
            return (DataReader<T>)new NodeDataReader(this);
        }
        //REVISIT - need to figure out what to do with Faults
        return null;
    }

    public void initObjectContext(ObjectMessageContext octx) {
        //REVISIT - this is only used server side, must be implemented for server side to work        
    }

}
