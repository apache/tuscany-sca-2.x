package org.apache.tuscany.implementation.spring;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.tuscany.sca.core.invocation.ThreadMessageContext;
import org.apache.tuscany.sca.interfacedef.ConversationSequence;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.impl.JavaInterfaceUtil;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.scope.InstanceWrapper;

import org.apache.tuscany.implementation.spring.xml.SpringBeanElement;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.beans.BeansException;

/**
 * Initial implementation of a Spring bean invoker
 * @author MikeEdwards
 *
 */
public class SpringInvoker implements Invoker {
	
	private Method					theMethod;
	private Object					bean;
	private SpringBeanElement		beanElement;
	private boolean					badInvoker = false;
	
	/**
	 * SpringInvoker constructor
	 * @param component - the Spring component to invoke
	 * @param service - the service to invoke
	 * @param operation - the operation to invoke
	 */
    public SpringInvoker( RuntimeComponent component,
    					  AbstractApplicationContext springContext,
    					  RuntimeComponentService service, 
    					  Operation operation) {

        // From the component and the service, identify the Spring Bean which is the target
        SpringImplementation theImplementation = (SpringImplementation) component.getImplementation();
        beanElement = theImplementation.getBeanFromService( service.getService() );
        
        if( beanElement == null ) {
        	badInvoker = true;
        	return;
        }
        // Now load the class for the bean and get the method relating to the operation....
        try {
        	bean = springContext.getBean( beanElement.getId() );
            Class<?> beanClass = bean.getClass();
            theMethod = JavaInterfaceUtil.findMethod( beanClass, operation );
            System.out.println("SpringInvoker - found method " + theMethod.getName() );
        } catch ( BeansException e ) {
        	badInvoker = true;
        } catch ( NoSuchMethodException e ) {
        	badInvoker = true;
        }
    } // end constructor SpringInvoker

    private Object doInvoke(Object payload) throws SpringInvocationException {
    	if( badInvoker ) throw new SpringInvocationException("Spring invoker incorrectly configured");
    	// Invoke the method on the Spring bean using the payload, returning the results
        try {
            Object ret;

            if (payload != null && !payload.getClass().isArray()) {
                ret = theMethod.invoke(bean, payload);
            } else {
                ret = theMethod.invoke(bean, (Object[])payload);
            }
            return ret;
        } catch (InvocationTargetException e) {
            throw new SpringInvocationException( e.getMessage() );
        } catch (Exception e) {
            throw new SpringInvocationException( e.getMessage() );
        }

    } // end method doInvoke

    /**
     * @param msg the message to invoke on the target bean
     */
    public Message invoke(Message msg) {
        try {
            Object resp = doInvoke(msg.getBody());
            msg.setBody(resp);
        } catch (SpringInvocationException e) {
            msg.setFaultBody(e.getCause());
        }
        System.out.println("Spring Invoker - invoke called");
        return msg;
    } // end method invoke


} // end class SpringInvoker
