package org.apache.tuscany.container.spring.config;

import org.apache.tuscany.spi.model.CompositeComponentType;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
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

//    private CompositeComponentType componentType;

    public ScaServiceBeanDefinitionParser(CompositeComponentType componentType) {
//        this.componentType = componentType;
    }

    public BeanDefinition parse(Element element, ParserContext parserContext) {
        RootBeanDefinition beanDef = new RootBeanDefinition();
        beanDef.setBeanClass(SCAService.class);

        String name = element.getAttribute(NAME_ATTRIBUTE);
        String type = element.getAttribute(TYPE_ATTRIBUTE);
        String target = element.getAttribute(TARGET_ATTRIBUTE);

        ConstructorArgumentValues ctorArgs = beanDef.getConstructorArgumentValues();
        ctorArgs.addIndexedArgumentValue(0, name);
        ctorArgs.addIndexedArgumentValue(1, type);
        ctorArgs.addIndexedArgumentValue(2, target);

        // create a bean definition holder to be able to register the
        // bean definition with the bean definition registry
        // (obtained through the ParserContext).  Use name as key.
        BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDef, name);

        // register the BeanDefinitionHolder (which contains the bean definition)
        // with the BeanDefinitionRegistry
        BeanDefinitionReaderUtils.registerBeanDefinition(holder, parserContext.getRegistry());

        return beanDef;
    }
}
