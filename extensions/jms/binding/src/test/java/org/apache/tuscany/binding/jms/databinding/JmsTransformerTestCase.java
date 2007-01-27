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

package org.apache.tuscany.binding.jms.databinding;

import junit.framework.TestCase;

/**
 * Test cases for transformers
 */
public class JmsTransformerTestCase extends TestCase {
    private Input2JmsInputTransformer t1;
    private JmsInput2InputTransformer t2;
    private Output2JmsOutputTransformer t3;
    private JmsOutput2OutputTransformer t4;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        t1 = new Input2JmsInputTransformer();
        t2 = new JmsInput2InputTransformer();
        t3 = new Output2JmsOutputTransformer();
        t4 = new JmsOutput2OutputTransformer();
    }

    public void testInput() {
        Object[] args1 = new Object[] {1, "ABC", 1.0d};
        Object[] args2 = t1.transform(args1, null);
        assertEquals(1, args2.length);
        assertTrue(args2[0] instanceof byte[]);
        Object[] args3 = t2.transform(args2, null);
        for (int i = 0; i < args3.length; i++) {
            assertEquals(args1[i], args3[i]);
        }

    }

    public void testOutput() {
        Object args1 = "ABC";
        Object args2 = t3.transform(args1, null);
        assertTrue(args2 instanceof byte[]);
        Object args3 = t4.transform(args2, null);
        assertEquals(args1, args3);
    }

}
