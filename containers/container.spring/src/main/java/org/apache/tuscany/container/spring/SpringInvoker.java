package org.apache.tuscany.container.spring;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.tuscany.spi.component.TargetException;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.InvocationRuntimeException;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

/**
 * Dispatches to an operation on a Spring bean
 *
 * @version $$Rev$$ $$Date$$
 */
public class SpringInvoker implements TargetInvoker {
    private ApplicationContext springContext;
    private boolean cacheable = true; // default to true since Spring handles resolution
    private String beanName;
    private Method method;
    private Object bean;

    public SpringInvoker(String beanName, Method method, ApplicationContext context) {
        this.beanName = beanName;
        this.method = method;
        springContext = context;
    }

    public Object invokeTarget(Object object) throws InvocationTargetException {
        if (bean == null) {
            try {
                bean = springContext.getBean(beanName);
            } catch (BeansException e) {
                throw new TargetException(e);
            }
        }
        try {
            return method.invoke(bean, (Object[]) object);
        } catch (IllegalAccessException e) {
            throw new InvocationTargetException(e);
        }
    }

    public Message invoke(Message msg) throws InvocationRuntimeException {
        TargetInvoker invoker = msg.getTargetInvoker();
        if (invoker == null) {
            throw new InvocationRuntimeException("No target invoker specified on message");
        }
        try {
            Object resp = invokeTarget(msg.getBody());
            msg.setBody(resp);
        } catch (InvocationTargetException e) {
            msg.setBody(e.getCause());
        } catch (Throwable e) {
            msg.setBody(e);
        }
        return msg;
    }

    public boolean isCacheable() {
        return cacheable;
    }

    public void setCacheable(boolean cacheable) {
        this.cacheable = cacheable;
    }

    public boolean isOptimizable() {
        return false;
    }


    public SpringInvoker clone() throws CloneNotSupportedException {
        SpringInvoker invoker = (SpringInvoker) super.clone();
        invoker.bean = null;
        return invoker;
    }


}
