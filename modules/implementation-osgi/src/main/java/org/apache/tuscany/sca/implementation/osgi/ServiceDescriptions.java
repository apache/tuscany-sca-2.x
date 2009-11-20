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

/**
 * OSGi RFC 119 service descriptions
 */
public interface ServiceDescriptions extends List<ServiceDescription> {

    String REMOTE_SERVICE_FOLDER = "OSGI-INF/remote-service";
    String OSGI_SD_NS = "http://www.osgi.org/xmlns/sd/v1.0.0";
    QName SERVICE_DESCRIPTIONS_QNAME = new QName(OSGI_SD_NS, "service-descriptions");
    QName SERVICE_DESCRIPTION_QNAME = new QName(OSGI_SD_NS, "service-description");
    String REMOTE_SERVICE_HEADER = "Remote-Service";
}
