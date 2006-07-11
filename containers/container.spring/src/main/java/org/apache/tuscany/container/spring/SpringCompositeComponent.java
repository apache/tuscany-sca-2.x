package org.apache.tuscany.container.spring;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Map;

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

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.extension.CompositeComponentExtension;
import org.apache.tuscany.spi.wire.TargetInvoker;

/**
 * A composite implementation responsible for managing Spring application contexts.
 *
 * @version $$Rev$$ $$Date$$
 */
public class SpringCompositeComponent extends CompositeComponentExtension {
    private static final String[] EMPTY_ARRAY = new String[0];
    private ConfigurableApplicationContext springContext;

    /**
     * Creates a new composite
     *
     * @param name          the name of the SCA composite
     * @param springContext the pre-instantiated Spring applicaiton context
     * @param parent        the SCA composite parent
     */
    public SpringCompositeComponent(String name,
                                    ConfigurableApplicationContext springContext,
                                    CompositeComponent parent) {
        super(name, parent);
        SCAApplicationContext scaApplicationContext = new SCAApplicationContext();
        springContext.setParent(scaApplicationContext);
        this.springContext = springContext;
    }

    public TargetInvoker createTargetInvoker(String serviceName, Method method) {
        // Treat the serviceName as the Spring bean name to look up
        return new SpringInvoker(serviceName, method, springContext);
    }

    public void setScopeContainer(ScopeContainer scopeContainer) {
        // not needed
    }

    public ConfigurableApplicationContext getApplicationContext() {
        return springContext;
    }

    public void start() {
        super.start();
        springContext.start();
    }

    public void stop() {
        super.stop();
        springContext.stop();
    }


    /**
     * An inner class is required to act as the Spring application context parent as opposed to implementing
     * the interface since the return types for {@link org.springframework.context.ApplicationContext#getParent()}
     * and {@link org.apache.tuscany.spi.component.CompositeComponent#getParent()} clash
     */
    private class SCAApplicationContext implements ApplicationContext {

        public Object getBean(String name) throws BeansException {
            SCAObject context = (SCAObject) children.get(name); // keep cast due to compiler error
            if (context == null) {
                throw new NoSuchBeanDefinitionException("SCA service not found [" + name + "]");
            }
            return context.getServiceInstance();
        }

        public Object getBean(String name, Class requiredType) throws BeansException {
            SCAObject context = (SCAObject) children.get(name);   // keep cast due to compiler error
            if (context == null) {
                throw new NoSuchBeanDefinitionException("SCA service not found [" + name + "]");
            }
            Class<?> type;
            if (context instanceof Reference) {
                type = ((Reference) context).getInterface();
            } else if (context instanceof Service) {
                type = ((Service) context).getInterface();
            } else {
                throw new AssertionError("Illegal context type [" + name + "]");
            }
            if (requiredType != null && requiredType.isAssignableFrom(type)) {
                // need null check since Spring may pass in a null
                throw new BeanNotOfRequiredTypeException(name, requiredType, type);
            }
            return context.getServiceInstance();
        }

        public boolean containsBean(String name) {
            return children.get(name) != null;
        }

        public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
            return children.get(name) != null;
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

        public Map getBeansOfType(Class type, boolean includePrototypes, boolean includeFactoryBeans)
            throws BeansException {
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

        public ClassLoader getClassLoader() {
            // REVIEW: this is almost certainly flawed, but it's not clear how the SCA runtime's
            // resource loading mechanism is exposed right now.
            return this.getClass().getClassLoader();
        }
    }
}
