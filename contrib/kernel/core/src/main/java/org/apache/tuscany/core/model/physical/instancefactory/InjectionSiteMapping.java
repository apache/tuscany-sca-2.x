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
package org.apache.tuscany.core.model.physical.instancefactory;

/**
 * @version $Rev$ $Date$
 */
public class InjectionSiteMapping {

    // SOurce of the mapping
    private InjectionSource source;
    
    // Site of the mapping
    private MemberSite site;

    /**
     * Gets the source of the injection.
     * @return Injection source.
     */
    public InjectionSource getSource() {
        return source;
    }

    /**
     * Sets the source of the injection.
     * @param source Injection source.
     */
    public void setSource(InjectionSource source) {
        this.source = source;
    }

    /**
     * Gets the site of the injection.
     * @return Injection site.
     */
    public MemberSite getSite() {
        return site;
    }

    /**
     * Set the size of the injection.
     * @param site Injection site.
     */
    public void setSite(MemberSite site) {
        this.site = site;
    }
}
