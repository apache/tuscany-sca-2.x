/*
 * Copyright 2002-2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.apache.tuscany.sca.implementation.spring.namespace;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.namespace.QName;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Handler for the &lt;sca:&gt; namespace in an application context
 * 
 * @version $Rev$ $Date$
 */
public class ScaNamespaceHandler extends NamespaceHandlerSupport {

    public ScaNamespaceHandler() {
    }

    @Override
    public void init() {
        registerBeanDefinitionParser("reference", new ScaReferenceBeanDefinitionParser());
        registerBeanDefinitionParser("service", new ScaServiceBeanDefinitionParser());
        registerBeanDefinitionParser("property", new ScaPropertyBeanDefinitionParser());
    }

    private static String getNamespaceURI(Element element, String prefix) {
        if (element == null) {
            return null;
        }
        String name = ("".equals(prefix)) ? "xmlns" : "xmlns:" + prefix;
        String ns = element.getAttribute(name);
        if (ns != null && !"".equals(ns)) {
            return ns;
        }
        Node parent = element.getParentNode();
        if (parent instanceof Element) {
            return getNamespaceURI((Element)parent, prefix);
        } else {
            return null;
        }
    }

    public static List<QName> resolve(Element element, String listOfNames) {
        List<QName> qnames = new ArrayList<QName>();
        StringTokenizer tokenizer = new StringTokenizer(listOfNames);
        while (tokenizer.hasMoreTokens()) {
            String qname = tokenizer.nextToken();
            String prefix = "";
            String local = qname;
            int index = qname.indexOf(':');
            if (index != -1) {
                local = qname.substring(index + 1);
                prefix = qname.substring(0, index);
            }
            String ns = getNamespaceURI(element, prefix);
            if (ns != null) {
                qnames.add(new QName(ns, local, prefix));
            } else {
                throw new IllegalArgumentException("Prefix " + prefix + "is not bound to a namespace");
            }
        }
        return qnames;
    }

    public static String getAttribute(Element element, String name) {
        String attr = element.getAttributeNS(null, name);
        if ("".equals(attr)) {
            return null;
        } else {
            return attr;
        }
    }
}
