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

package org.apache.tuscany.databinding.impl;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.databinding.Transformer;
import org.apache.tuscany.databinding.TransformerRegistry;
import org.apache.tuscany.databinding.impl.TransformerRegistryImpl;

/**
 * 
 */
public class TransformerRegistryImplTestCase extends TestCase {
    private TransformerRegistry registry;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        registry = new TransformerRegistryImpl();
    }

    public void testRegisterTransformer1() {
        Transformer transformer = createMock(Transformer.class);
        registry.registerTransformer("a", "b", 10, transformer);
        Transformer t = registry.getTransformer("a", "b");
        Assert.assertSame(t, transformer);
    }

    public void testRegisterTransformerTransformer() {
        Transformer transformer = createMock(Transformer.class);
        expect(transformer.getSourceDataBinding()).andReturn("a");
        expect(transformer.getTargetDataBinding()).andReturn("b");
        expect(transformer.getWeight()).andReturn(10);
        replay(transformer);
        registry.registerTransformer(transformer);
        Transformer t = registry.getTransformer("a", "b");
        Assert.assertSame(t, transformer);
    }

    public void testUnregisterTransformer() {
        Transformer transformer = createMock(Transformer.class);
        registry.registerTransformer("a", "b", 10, transformer);
        boolean result = registry.unregisterTransformer("a", "b");
        Assert.assertTrue(result);
        Transformer t = registry.getTransformer("a", "b");
        Assert.assertNull(t);
    }

    public void testGetTransformerChain() {
        Transformer t1 = createMock(Transformer.class);
        expect(t1.getSourceDataBinding()).andReturn("a");
        expect(t1.getTargetDataBinding()).andReturn("b");
        expect(t1.getWeight()).andReturn(10);
        replay(t1);
        Transformer t2 = createMock(Transformer.class);
        expect(t2.getSourceDataBinding()).andReturn("b");
        expect(t2.getTargetDataBinding()).andReturn("c");
        expect(t2.getWeight()).andReturn(20);
        replay(t2);

        Transformer t3 = createMock(Transformer.class);
        expect(t3.getSourceDataBinding()).andReturn("a");
        expect(t3.getTargetDataBinding()).andReturn("c");
        expect(t3.getWeight()).andReturn(120);
        replay(t3);

        registry.registerTransformer(t1);
        registry.registerTransformer(t2);
        registry.registerTransformer(t3);

        List<Transformer> l1 = registry.getTransformerChain("a", "b");
        Assert.assertTrue(l1.size() == 1 && l1.get(0) == t1);
        List<Transformer> l2 = registry.getTransformerChain("a", "c");
        Assert.assertTrue(l2.size() == 2 && l2.get(0) == t1 && l2.get(1) == t2);
        List<Transformer> l3 = registry.getTransformerChain("a", "d");
        Assert.assertNull(l3);

    }

}
