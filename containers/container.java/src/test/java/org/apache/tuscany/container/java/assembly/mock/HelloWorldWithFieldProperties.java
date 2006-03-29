/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.container.java.assembly.mock;

import org.osoa.sca.annotations.Property;

/**
 * @version $Rev$ $Date$
 */
public class HelloWorldWithFieldProperties {
    @Property
    private String text;

    @Property(required = true)
    public Integer text2;

    @Property(name = "foo")
    public int text3;

    @Property(name = "not exposed")
    protected int field4;

    public String hello(String name) {
        return "Hello " + name;
    }
}
