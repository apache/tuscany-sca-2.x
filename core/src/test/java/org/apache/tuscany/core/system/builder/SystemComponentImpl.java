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

import org.apache.tuscany.core.context.CompositeContext;
import org.apache.tuscany.core.context.AutowireContext;
import org.apache.tuscany.core.context.ConfigurationContext;
import org.apache.tuscany.core.system.annotation.Autowire;
import org.apache.tuscany.core.system.annotation.ParentContext;
import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Scope;

/**
 * A system component used for unit testing
 * 
 * @version $Rev$ $Date$
 */
@Scope("MODULE")
public class SystemComponentImpl {

    @Autowire
    protected ConfigurationContext ctx;

    @ParentContext
    protected CompositeContext parent;

    @Autowire
    protected AutowireContext autowireCtx;

    private ConfigurationContext ctxSetter;

    private CompositeContext parentSetter;

    private AutowireContext autowireCtxSetter;

    public ConfigurationContext getConfigContext() {
        return ctx;
    }

    public CompositeContext getParentContext() {
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
    public void setParentContex(CompositeContext ctx) {
        parentSetter = ctx;
    }

    public CompositeContext getParentContextSetter() {
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
    protected int testInt;

    public int getTestInt(){
        return testInt;
    }

    @Property
    protected double testDouble;

    public double getTestDouble(){
        return testDouble;
    }

    @Property
    protected float testFloat;

    public float getTestFloat(){
        return testFloat;
    }

    @Property
    protected short testShort;

    public short getTestShort(){
        return testShort;
    }

    @Property
    protected boolean testBoolean;

    public boolean getTestBoolean(){
        return testBoolean;
    }

    @Property
    protected byte testByte;

    public byte getTestByte(){
        return testByte;
    }

    @Property
    protected char testChar;

    public char getTestChar(){
        return testChar;
    }

    @Property
    protected String testString;

    public String getTestString(){
        return testString;
    }


}
