package org.apache.tuscany.core.system.wire;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

import org.apache.tuscany.common.ObjectFactory;
import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.wire.ProxyCreationException;
import org.apache.tuscany.spi.wire.SourceInvocationChain;
import org.apache.tuscany.spi.wire.SourceWire;

/**
 * @version $$Rev$$ $$Date$$
 */
public class SystemSourceWire implements SourceWire {
    private String referenceName;
    private QualifiedName targetName;
    private Class businessInterface;
    private ObjectFactory factory;

    public SystemSourceWire(String referenceName, QualifiedName targetName, Class businessInterface, ObjectFactory factory) {
        this.referenceName = referenceName;
        this.targetName = targetName;
        this.businessInterface = businessInterface;
        this.factory = factory;
    }

    public String getReferenceName() {
        return referenceName;
    }

    public void setReferenceName(String referenceName) {
        this.referenceName = referenceName;
    }

    public QualifiedName getTargetName() {
        return targetName;
    }

    public void setTargetName(QualifiedName targetName) {
        this.targetName = targetName;
    }

    public Object createProxy() throws ProxyCreationException {
        return factory.getInstance();
    }

    public Class getBusinessInterface() {
        return businessInterface;
    }

    public void setBusinessInterface(Class businessInterface) {
        this.businessInterface = businessInterface;
    }

    public Class[] getImplementedInterfaces() {
        return new Class[0];
    }

    public Map getInvocationChains() {
        return Collections.emptyMap();
    }

    public void addInvocationChain(Method method, SourceInvocationChain chains) {
        throw new UnsupportedOperationException();
    }

    public void addInvocationChains(Map chains) {
        throw new UnsupportedOperationException();
    }

    public void addInterface(Class claz) {
        throw new UnsupportedOperationException();
    }

}
