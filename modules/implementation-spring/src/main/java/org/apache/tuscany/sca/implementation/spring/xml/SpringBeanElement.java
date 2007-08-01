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
package org.apache.tuscany.sca.implementation.spring.xml;

import java.util.List;
import java.util.ArrayList;

/**
 * Represents a <bean> element in a Spring application-context
 * - this has id and className attributes
 * - plus zero or more property elements as children
 *
 * @version $Rev: 512919 $ $Date: 2007-02-28 19:32:56 +0000 (Wed, 28 Feb 2007) $
 */
public class SpringBeanElement {

    private String id;
    private String className;
    private List<SpringPropertyElement> properties = new ArrayList<SpringPropertyElement>();

    public SpringBeanElement(String id, String className) {
        this.id = id;
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    public String getId() {
        return id;
    }

    public List<SpringPropertyElement> getProperties() {
        return properties;
    }

    public void addProperty(SpringPropertyElement property) {
        properties.add(property);
    }

} // end class SpringBeanElement
