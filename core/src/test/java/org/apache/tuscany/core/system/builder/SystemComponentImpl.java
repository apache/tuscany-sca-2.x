/**
 * 
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.core.system.builder;

import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.context.AutowireContext;
import org.apache.tuscany.core.context.ConfigurationContext;
import org.apache.tuscany.core.system.annotation.Autowire;
import org.apache.tuscany.core.system.annotation.ParentContext;
import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Property;

/**
 * A system component used for unit testing
 * 
 * @version $Rev$ $Date$
 */
public class SystemComponentImpl {

    @Autowire
    private ConfigurationContext ctx;

    @ParentContext
    private AggregateContext parent;

    @Autowire
    private AutowireContext autowireCtx;

    private ConfigurationContext ctxSetter;

    private AggregateContext parentSetter;

    private AutowireContext autowireCtxSetter;

    public ConfigurationContext getConfigContext() {
        return ctx;
    }

    public AggregateContext getParentContext() {
        return parent;
    }

    public AutowireContext getAutowireContext() {
        return autowireCtx;
    }

    @Autowire
    public void setConfigContext(ConfigurationContext configCtx) {
        ctxSetter = configCtx;
    }

    public ConfigurationContext getConfigContextSetter() {
        return ctxSetter;
    }

    @ParentContext
    public void setParentContex(AggregateContext ctx) {
        parentSetter = ctx;
    }

    public AggregateContext getParentContextSetter() {
        return parentSetter;
    }

    @Autowire
    public void setAutowireContext(AutowireContext ctx) {
        autowireCtxSetter = ctx;
    }

    public AutowireContext getAutowireContextSetter() {
        return autowireCtx;
    }

    private boolean inited;
    
    @Init
    public void init(){
        inited=true;
    }
    
    public boolean initialized(){
        return (inited);
    }

   private boolean destroyed;
    
    @Destroy
    public void destroy(){
        destroyed=true;
    }
    
    public boolean destroyed(){
        return (destroyed);
    }
    
    @Property
    private int testInt;
    
    public int getTestInt(){
        return testInt;
    }

    @Property
    private double testDouble;
    
    public double getTestDouble(){
        return testDouble;
    }

    @Property
    private float testFloat;
    
    public float getTestFloat(){
        return testFloat;
    }
    
    @Property
    private short testShort;
    
    public short getTestShort(){
        return testShort;
    }
    
    @Property
    private boolean testBoolean;
    
    public boolean getTestBoolean(){
        return testBoolean;
    }
    
    @Property
    private byte testByte;
    
    public byte getTestByte(){
        return testByte;
    }
    
    @Property
    private char testChar;
    
    public char getTestChar(){
        return testChar;
    }
    
    @Property
    private String testString;
    
    public String getTestString(){
        return testString;
    }


}
