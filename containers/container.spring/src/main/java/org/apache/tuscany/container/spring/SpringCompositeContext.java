package org.apache.tuscany.container.spring;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Map;

import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.context.Context;
import org.apache.tuscany.spi.context.ReferenceContext;
import org.apache.tuscany.spi.context.ScopeContext;
import org.apache.tuscany.spi.context.ServiceContext;
import org.apache.tuscany.spi.extension.CompositeContextExtension;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.SourceWire;
import org.apache.tuscany.spi.wire.InvocationChain;
import org.apache.tuscany.spi.wire.SourceInvocationChain;
import org.apache.tuscany.core.builder.Connector;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.core.io.Resource;

/**
 * A composite implementation responsible for managing Spring application contexts.
 *
 * @version $$Rev$$ $$Date$$
 */
public class SpringCompositeContext extends CompositeContextExtension {
    private static final String[] EMPTY_ARRAY = new String[0];
    private ConfigurableApplicationContext springContext;
    private SCAApplicationContext scaApplicationContext;
    /**
     * Creates a new composite
     *
     * @param name          the name of the SCA composite
     * @param springContext the pre-instantiated Spring applicaiton context
     * @param parent        the SCA composite parent
     */
    public SpringCompositeContext(String name, ConfigurableApplicationContext springContext, CompositeContext parent) {
        super(name, parent);
        scaApplicationContext = new SCAApplicationContext();
        springContext.setParent(scaApplicationContext);
        this.springContext = springContext;
    }

    public TargetInvoker createTargetInvoker(String serviceName, Method method) {
        return new SpringInvoker(serviceName, method, springContext);
    }

    public void setScopeContext(ScopeContext scopeContext) {

    }

    public ConfigurableApplicationContext getApplicationContext() {
        return springContext;
    }

    /**
     * An inner class is required to act as the Spring application context parent as opposed to implementing
     * the interface since the return types for {@link org.springframework.context.ApplicationContext#getParent()}
     * and {@link org.apache.tuscany.spi.context.CompositeContext#getParent()} clash
     */
    private class SCAApplicationContext implements ApplicationContext {

        public Object getBean(String name) throws BeansException {
            Context context = (Context) children.get(name); // keep cast for compiler error
            if (context == null) {
                throw new NoSuchBeanDefinitionException("SCA service not found [" + name + "]");
            }
            return context.getService();
        }

        public Object getBean(String name, Class requiredType) throws BeansException {
            Context context = (Context) children.get(name);   // keep cast for compiler error
            if (context == null) {
                throw new NoSuchBeanDefinitionException("SCA service not found [" + name + "]");
            }
            Class<?> type;
            if (context instanceof ReferenceContext) {
                type = ((ReferenceContext) context).getInterface();
            } else if (context instanceof ServiceContext) {
                type = ((ServiceContext) context).getInterface();
            } else {
                throw new AssertionError("Illegal context type [" + name + "]");
            }
            if (requiredType != null && requiredType.isAssignableFrom(type)) {
                // need null check since Spring may pass in a null
                throw new BeanNotOfRequiredTypeException(name, requiredType, type);
            }
            return context.getService();
        }

        public boolean containsBean(String name) {
            return (sourceWires.get(name) != null);
        }

        public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
            return sourceWires.get(name) != null;
        }

        public Class getType(String name) throws NoSuchBeanDefinitionException {
            return null;
        }

        public String[] getAliases(String name) throws NoSuchBeanDefinitionException {
            return EMPTY_ARRAY;
        }

        public ApplicationContext getParent() {
            return null;
        }

        public AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException {
            return null;
        }

        public String getDisplayName() {
            return getName();
        }

        public long getStartupDate() {
            return 0;
        }

        public boolean containsBeanDefinition(String beanName) {
            return false;
        }

        public int getBeanDefinitionCount() {
            return 0;
        }

        public String[] getBeanDefinitionNames() {
            return new String[0];
        }

        public String[] getBeanNamesForType(Class type) {
            return new String[0];
        }

        public String[] getBeanNamesForType(Class type, boolean includePrototypes, boolean includeFactoryBeans) {
            return new String[0];
        }

        public Map getBeansOfType(Class type) throws BeansException {
            return null;
        }

        public Map getBeansOfType(Class type, boolean includePrototypes, boolean includeFactoryBeans) throws BeansException {
            return null;
        }

        public BeanFactory getParentBeanFactory() {
            return null;
        }

        public boolean containsLocalBean(String name) {
            return false;
        }

        public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
            return null;
        }

        public String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException {
            return null;
        }

        public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
            return null;
        }

        public void publishEvent(ApplicationEvent event) {

        }

        public Resource[] getResources(String locationPattern) throws IOException {
            return new Resource[0];
        }

        public Resource getResource(String location) {
            return null;
        }
    }
}
