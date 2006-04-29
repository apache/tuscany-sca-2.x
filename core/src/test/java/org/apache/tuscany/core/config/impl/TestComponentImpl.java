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
package org.apache.tuscany.core.config.impl;

import java.util.List;

import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

/**
 * @version $$Rev$$ $$Date$$
 */
@Service(interfaces = {TestComponent.class})
public class TestComponentImpl implements TestComponent {

    @Property
    protected String baz;

    @Reference (name="bazRefeference")
    protected TestComponent bazRef;

    @Reference (required = true)
    protected TestComponent wombat;

    @Property
    public void setFoo(String foo){

    }

    @Property(name = "fooRequired",required = true)
    public void setFooRequiredRename(String foo){

    }

    @Reference
    public void bar(String bar){

    }

    @Reference(name ="setBarRequired", required = true)
    public void setBar(List bar){

    }

    @Reference(name ="setBar", required = false)
    public void setBarNonRequired(List bar){

    }

    public void someSetter(String val){

    }

}
