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

import junit.framework.Assert;
import junit.framework.TestCase;
import org.apache.tuscany.model.assembly.Scope;

/**
 * Basic scope strategy tests, including downscope referencing 
 * 
 * @version $Rev$ $Date$
 */
public class DefaultScopeStrategyTestCase extends TestCase {

    public void testDownScopeReferences() throws Exception{
        DefaultScopeStrategy strategy = new DefaultScopeStrategy();
        
        Assert.assertTrue(!strategy.downScopeReference(Scope.MODULE,Scope.MODULE));
        Assert.assertTrue(strategy.downScopeReference(Scope.MODULE,Scope.SESSION));
        Assert.assertTrue(strategy.downScopeReference(Scope.MODULE,Scope.REQUEST));
        Assert.assertTrue(strategy.downScopeReference(Scope.MODULE,Scope.INSTANCE));

        Assert.assertTrue(!strategy.downScopeReference(Scope.SESSION,Scope.MODULE));
        Assert.assertTrue(!strategy.downScopeReference(Scope.SESSION,Scope.SESSION));
        Assert.assertTrue(strategy.downScopeReference(Scope.SESSION,Scope.REQUEST));
        Assert.assertTrue(strategy.downScopeReference(Scope.SESSION,Scope.INSTANCE));

        Assert.assertTrue(!strategy.downScopeReference(Scope.REQUEST,Scope.MODULE));
        Assert.assertTrue(!strategy.downScopeReference(Scope.REQUEST,Scope.SESSION));
        Assert.assertTrue(!strategy.downScopeReference(Scope.REQUEST,Scope.REQUEST));
        Assert.assertTrue(strategy.downScopeReference(Scope.REQUEST,Scope.INSTANCE));

        Assert.assertTrue(!strategy.downScopeReference(Scope.REQUEST,Scope.MODULE));
        Assert.assertTrue(!strategy.downScopeReference(Scope.REQUEST,Scope.SESSION));
        Assert.assertTrue(!strategy.downScopeReference(Scope.REQUEST,Scope.REQUEST));
        Assert.assertTrue(!strategy.downScopeReference(Scope.INSTANCE,Scope.INSTANCE));
    }
}

