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

package org.apache.tuscany.sca.contribution.resource.impl;

import org.apache.tuscany.sca.contribution.resource.ResourceExport;
import org.apache.tuscany.sca.contribution.resource.ResourceImport;
import org.apache.tuscany.sca.contribution.resource.ResourceImportExportFactory;

/**
 * Resource Import/Export model factory implementation
 * 
 * @version $Rev$ $Date$
 */
public class ResourceImportExportFactoryImpl implements ResourceImportExportFactory {

    public ResourceImport createResourceImport() {
        return new ResourceImportImpl();
    }
    
    public ResourceExport createResourceExport() {
        return new ResourceExportImpl();
    }

}
