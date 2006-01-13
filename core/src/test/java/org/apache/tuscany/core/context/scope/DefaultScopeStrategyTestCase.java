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
package org.apache.tuscany.core.context.scope;

import org.apache.tuscany.model.assembly.ScopeEnum;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Basic scope strategy tests, including downscope referencing 
 * 
 * @version $Rev$ $Date$
 */
public class DefaultScopeStrategyTestCase extends TestCase {

    public void testDownScopeReferences() throws Exception{
        DefaultScopeStrategy strategy = new DefaultScopeStrategy();
        
        Assert.assertTrue(!strategy.downScopeReference(ScopeEnum.MODULE,ScopeEnum.MODULE));
        Assert.assertTrue(strategy.downScopeReference(ScopeEnum.MODULE,ScopeEnum.SESSION));
        Assert.assertTrue(strategy.downScopeReference(ScopeEnum.MODULE,ScopeEnum.REQUEST));
        Assert.assertTrue(strategy.downScopeReference(ScopeEnum.MODULE,ScopeEnum.INSTANCE));

        Assert.assertTrue(!strategy.downScopeReference(ScopeEnum.SESSION,ScopeEnum.MODULE));
        Assert.assertTrue(!strategy.downScopeReference(ScopeEnum.SESSION,ScopeEnum.SESSION));
        Assert.assertTrue(strategy.downScopeReference(ScopeEnum.SESSION,ScopeEnum.REQUEST));
        Assert.assertTrue(strategy.downScopeReference(ScopeEnum.SESSION,ScopeEnum.INSTANCE));

        Assert.assertTrue(!strategy.downScopeReference(ScopeEnum.REQUEST,ScopeEnum.MODULE));
        Assert.assertTrue(!strategy.downScopeReference(ScopeEnum.REQUEST,ScopeEnum.SESSION));
        Assert.assertTrue(!strategy.downScopeReference(ScopeEnum.REQUEST,ScopeEnum.REQUEST));
        Assert.assertTrue(strategy.downScopeReference(ScopeEnum.REQUEST,ScopeEnum.INSTANCE));

        Assert.assertTrue(!strategy.downScopeReference(ScopeEnum.REQUEST,ScopeEnum.MODULE));
        Assert.assertTrue(!strategy.downScopeReference(ScopeEnum.REQUEST,ScopeEnum.SESSION));
        Assert.assertTrue(!strategy.downScopeReference(ScopeEnum.REQUEST,ScopeEnum.REQUEST));
        Assert.assertTrue(!strategy.downScopeReference(ScopeEnum.INSTANCE,ScopeEnum.INSTANCE));
    }
}

