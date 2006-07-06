package org.apache.tuscany.container.spring.config;

import org.apache.tuscany.spi.model.CompositeComponentType;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.w3c.dom.Element;

/**
 * Processes <code>service</code> elements in a Spring configuration
 *
 * @version $$Rev$$ $$Date$$
 */
public class ScaServiceBeanDefinitionParser implements BeanDefinitionParser {

    public static final String SERVICE_ELEMENT = "service";
    private static final String NAME_ATTRIBUTE = "name";
    private static final String TYPE_ATTRIBUTE = "type";
    private static final String TARGET_ATTRIBUTE = "target";

    public ScaServiceBeanDefinitionParser() {
    }

    public BeanDefinition parse(Element element, ParserContext parserContext) {

        String name = element.getAttribute(NAME_ATTRIBUTE);
        String type = element.getAttribute(TYPE_ATTRIBUTE);
        String targetName = element.getAttribute(TARGET_ATTRIBUTE);

        BeanDefinitionBuilder proxyBean = BeanDefinitionBuilder.rootBeanDefinition(ProxyFactoryBean.class);
        proxyBean.addPropertyReference("target", targetName);
        proxyBean.addPropertyValue("proxyInterfaces", type);

        // REVIEW: May need to account for singleton-ness of the target?  (ie, if target is singleton=false,
        // perhaps the proxy should also be singleton=false).

        parserContext.getRegistry().registerBeanDefinition(name, proxyBean.getBeanDefinition());

        // REVIEW: It may make sense to register an additional bean to capture/expose the presence &
        // attributes of the service element itself.  Such code as:
        
        /*
        BeanDefinitionBuilder serviceBean = BeanDefinitionBuilder.rootBeanDefinition(SCAService.class);
        serviceBean.addConstructorArg(name);
        serviceBean.addConstructorArg(type);
        serviceBean.addConstructorArg(targetName);

        parserContext.getRegistry().registerBeanDefinition(name, serviceBean.getBeanDefinition());
        */

        return null;
    }
}
