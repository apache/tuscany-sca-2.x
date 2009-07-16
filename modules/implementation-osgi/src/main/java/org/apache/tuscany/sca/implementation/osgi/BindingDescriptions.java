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

package org.apache.tuscany.sca.implementation.osgi;

import java.util.List;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.Binding;

/**
 * OSGi RFC 119 SCA binding descriptions
 */
public interface BindingDescriptions extends List<Binding> {
    String OSGI_SD_NS = ServiceDescriptions.OSGI_SD_NS;
    QName BINDINGS_QNAME = new QName(OSGI_SD_NS, "bindings");
}
