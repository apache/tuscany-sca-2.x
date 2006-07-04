package org.apache.tuscany.container.spring;

import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.loader.ComponentTypeLoader;
import org.apache.tuscany.spi.model.CompositeComponentType;
import org.apache.tuscany.spi.component.CompositeComponent;

import org.apache.tuscany.container.spring.config.SCABeanDefinitionReader;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.Resource;

/**
 * Loads a component type for a Spring <code>ApplicationContext</code>. The implementation creates a new
 * instance of a Spring application context which is configured with SCA namespace handlers for generating
 * component type information
 *
 * @version $$Rev$$ $$Date$$
 */
public class SpringComponentTypeLoader implements ComponentTypeLoader<SpringImplementation> {

    public void load(CompositeComponent<?> parent, SpringImplementation implementation,
                     DeploymentContext deploymentContext) {
        Resource resource = null; //FIXME
        CompositeComponentType componentType = new CompositeComponentType();
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader = new SCABeanDefinitionReader(beanFactory, componentType);
        reader.loadBeanDefinitions(resource);
        GenericApplicationContext ctx = new GenericApplicationContext(beanFactory);
        ctx.refresh();
        implementation.setComponentType(componentType);
        implementation.setApplicationContext(ctx);
    }
}
