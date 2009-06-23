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
package org.apache.tuscany.sca.binding.jsonp.impl;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.binding.jsonp.JSONPBinding;

/**
 * Represents a binding to an RMI service.
 *
 * @version $Rev: 718858 $ $Date: 2008-11-19 05:27:58 +0000 (Wed, 19 Nov 2008) $
 */
public class JSONPBindingImpl implements JSONPBinding {

    private String name;
    private String uri;

    public String getName() {
        return name;
    }

    public String getURI() {
        return uri;
    }

    public void setURI(String uri) {
        this.uri = uri;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public boolean isUnresolved() {
        return false;
    }

    public void setUnresolved(boolean arg0) {
    }

    public QName getType() {
        return TYPE;
    }

}
