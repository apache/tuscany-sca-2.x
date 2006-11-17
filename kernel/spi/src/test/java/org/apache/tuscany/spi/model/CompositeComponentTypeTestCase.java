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
package org.apache.tuscany.spi.model;

import junit.framework.TestCase;

public class CompositeComponentTypeTestCase extends TestCase {
    
    public void testWireCreationAndRetrieval() throws Exception  {
        WireDefinition wire1 = new WireDefinition();
        WireDefinition wire2 = new WireDefinition();
        
        CompositeComponentType composite = new CompositeComponentType();
        CompositeComponentType includedComposite = new CompositeComponentType();
        includedComposite.add(wire1);
        Include compositeInclude = new Include();
        compositeInclude.setIncluded(includedComposite);
        
        composite.add(compositeInclude);
        composite.add(wire1);
        
        assertEquals(1, composite.getDeclaredWires().size());
        assertEquals(wire1, composite.getDeclaredWires().get(0));
        assertEquals(2, composite.getWires().size());
    }

}
