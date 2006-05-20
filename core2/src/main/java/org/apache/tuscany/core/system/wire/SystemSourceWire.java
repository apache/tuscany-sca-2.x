package org.apache.tuscany.core.system.wire;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.spi.wire.SourceInvocationChain;
import org.apache.tuscany.spi.wire.SourceWire;
import org.apache.tuscany.spi.wire.TargetWire;

/**
 * @version $$Rev$$ $$Date$$
 */
public class SystemSourceWire<T> implements SourceWire<T> {
    private String referenceName;
    private QualifiedName targetName;
    private Class<T> businessInterface;
    private TargetWire<T> targetWire;

    public SystemSourceWire(String referenceName, QualifiedName targetName, Class<T> businessInterface) {
        this.referenceName = referenceName;
        this.targetName = targetName;
        this.businessInterface = businessInterface;
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

    public T getTargetService() throws TargetException {
        if (targetWire == null) {
            throw new TargetException("Target wire not connected to source wire");
        }
        return targetWire.getTargetService();
    }

    public Class<T> getBusinessInterface() {
        return businessInterface;
    }

    public void setBusinessInterface(Class<T> businessInterface) {
        this.businessInterface = businessInterface;
    }

    public Class[] getImplementedInterfaces() {
        return new Class[0];
    }

    public Map<Method, SourceInvocationChain> getInvocationChains() {
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

    public void setTargetWire(TargetWire<T> wire) {
        targetWire = wire;
    }

}
