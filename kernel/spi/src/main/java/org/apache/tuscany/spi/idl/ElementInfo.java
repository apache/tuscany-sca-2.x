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

package org.apache.tuscany.spi.idl;

import javax.xml.namespace.QName;

/**
 * An abstraction of XML schema elements
 */
public class ElementInfo {
    private QName name;
    private TypeInfo type;

    /**
     * @param name
     * @param type
     */
    public ElementInfo(QName name, TypeInfo type) {
        super();
        this.name = name;
        this.type = type;
    }

    /**
     * @return the name
     */
    public QName getQName() {
        return name;
    }

    /**
     * @return the type
     */
    public TypeInfo getType() {
        return type;
    }
}
