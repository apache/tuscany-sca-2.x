package org.apache.tuscany.container.spring;

import org.springframework.beans.factory.xml.NamespaceHandler;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.BeanDefinitionDecorator;
import org.w3c.dom.Element;

/**
 * @version $$Rev$$ $$Date$$
 */
public class SCANamespaceHandler implements NamespaceHandler {
    public void init() {

    }

    public BeanDefinitionParser findParserForElement(Element element) {
        return null;
    }

    public BeanDefinitionDecorator findDecoratorForElement(Element element) {
        return null;
    }
}
