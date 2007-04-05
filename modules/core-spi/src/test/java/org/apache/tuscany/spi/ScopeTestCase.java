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
package org.apache.tuscany.spi;

import org.apache.tuscany.spi.Scope;

import junit.framework.TestCase;

/**
 * @version $Rev$ $Date$
 */
public class ScopeTestCase extends TestCase {

    public void testEquals() throws Exception {
        Scope scope = new Scope("COMPOSITE");
        assertTrue(scope.equals(Scope.COMPOSITE));
    }

    public void testEqualsNew() throws Exception {
        Scope foo = new Scope("foo");
        Scope foo2 = new Scope("FOO");
        assertTrue(foo.equals(foo2));
    }

    public void testNotEquals() throws Exception {
        Scope foo = new Scope("BAR");
        Scope foo2 = new Scope("FOO");
        assertFalse(foo.equals(foo2));
    }

    public void testNotEqualsDifferent() throws Exception {
        Scope foo = new Scope("FOO");
        assertFalse(foo.equals(new Bar("FOO")));
    }

    public class Bar {
        private String scope;

        public Bar(String scope) {
            this.scope = scope;
        }
    }


}
