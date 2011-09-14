/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.apache.tuscany.sca.databinding.jaxb;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.util.ValidationEventCollector;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.databinding.PullTransformer;
import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.TransformationException;
import org.apache.tuscany.sca.databinding.BaseTransformer;
import org.w3c.dom.Node;

/**
 *
 * @version $Rev$ $Date$
 */
public class Node2JAXB extends BaseTransformer<Node, Object> implements PullTransformer<Node, Object> {
    private JAXBContextHelper contextHelper;
    private ValidationEventCollector validationEventCollector = new ValidationEventCollector();
    
    public Node2JAXB(ExtensionPointRegistry registry) {
        contextHelper = JAXBContextHelper.getInstance(registry);
    }

    public Object transform(Node source, TransformationContext context) {
        Object response = null;
        if (source == null)
            return null;
        try {
            JAXBContext jaxbContext = contextHelper.createJAXBContext(context, false);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            Object result;
            // TUSCANY-3791
            synchronized(source){
                /* some debug code
                System.setProperty("jaxb.debug", "true");
                unmarshaller.setListener(new DebugListener());
                */
                validationEventCollector.reset();
                unmarshaller.setEventHandler(validationEventCollector);
                result = unmarshaller.unmarshal(source, JAXBContextHelper.getJavaType(context.getTargetDataType()));
            }
            response = JAXBContextHelper.createReturnValue(jaxbContext, context.getTargetDataType(), result);
        } catch (Exception e) {
            throw new TransformationException(e);
        }
        
        if (validationEventCollector.hasEvents()){
            String validationErrors = "";
            for(ValidationEvent event : validationEventCollector.getEvents()){
                validationErrors += "Event: " + event.getMessage() + " ";
            }
            throw new TransformationException(validationErrors);
        }
        return response;
    }

    @Override
    protected Class<Node> getSourceType() {
        return Node.class;
    }

    @Override
    protected Class<Object> getTargetType() {
        return Object.class;
    }

    @Override
    public int getWeight() {
        return 30;
    }

    @Override
    public String getTargetDataBinding() {
        return JAXBDataBinding.NAME;
    }
    
    /* some debug code
    class DebugListener extends Unmarshaller.Listener {
        public void beforeUnmarshal(Object target, Object parent) {
            
        }
        
        public void afterUnmarshal(Object target, Object parent) {
            
        }
    }
    */
}
