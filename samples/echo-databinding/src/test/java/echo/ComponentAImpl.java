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
package echo;

import org.apache.axiom.om.OMElement;
import org.osoa.sca.annotations.Constructor;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;

/**
 * @version $Rev$ $Date$
 */
public class ComponentAImpl implements Interface1 {

    private Interface1 componentBReference;
    
    private String prefix;
    
    private String prefix1;
    
    private String bar;
    
    private OMElement omProperty;

    @Constructor
    public ComponentAImpl(@Reference(name = "componentBReference", required = true)
    Interface1 componentBReference) {
        this.componentBReference = componentBReference;
    }

    public String call(String msg) {
        String request = msg + " [" + msg.getClass().getName() + "]";
        System.out.println("ComponentA --> Received message: " + request);
        Object ret = componentBReference.call(msg);
        String response = ret + " [" + ret.getClass().getName() + "]";
        System.out.println("ComponentA --> Returned message: " + response);
        return (String) ret;
    }

    public String call1(String msg) {
        String request = msg + " [" + msg.getClass().getName() + "]";
        System.out.println("ComponentA --> Received message: " + request);
        Object ret = componentBReference.call1(msg);
        String response = ret + " [" + ret.getClass().getName() + "]";
        System.out.println("ComponentA --> Returned message: " + response);
        return (String) ret;
    }

    @Property(name="prefix")
    public void setPrefix(String prefix) {
        System.out.println("[Property] prefix: " + prefix);
        this.prefix = prefix;
    }

    @Property(name="prefix1")
    public void setPrefix1(String prefix1) {
        System.out.println("[Property] prefix1: " + prefix1);
        this.prefix1 = prefix1;
    }  
    
    /**
     * @param bar the bar to set
     */
    @Property(name="bar")
    public void setBar(String bar) {
        System.out.println("[Property] bar: " + bar);
        this.bar = bar;
    }

    /**
     * @param omProperty the omProperty to set
     */
    @Property(name="omProperty")
    public void setOmProperty(OMElement omProperty) {
        this.omProperty = omProperty;
    }

}
