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

package org.apache.tuscany.sca.host.ejb;


/**
 * Default implementation of an extensible EJB host.
 * 
 * @version $Rev$ $Date$
 */
public class ExtensibleEJBHost implements EJBHost {
    
    private EJBHostExtensionPoint ejbHosts;
    
    public ExtensibleEJBHost(EJBHostExtensionPoint ejbHosts) {
        this.ejbHosts = ejbHosts;
    }
    
    public void addSessionBean(String ejbName, EJBSessionBean ejbClass) throws EJBRegistrationException {
        if (ejbHosts.getEJBHosts().isEmpty()) {
            throw new EJBRegistrationException("No EJB host available");
        }

        // TODO implement selection of the correct EJB host based on the mapping
        // For now just select the first one
        getDefaultEJBHost().addSessionBean(ejbName, ejbClass);
    }

    public EJBSessionBean removeSessionBean(String ejbName) throws EJBRegistrationException {
        if (ejbHosts.getEJBHosts().isEmpty()) {
            throw new EJBRegistrationException("No EJB host available");
        }

        // TODO implement selection of the correct EJB host based on the mapping
        // For now just select the first one
        return getDefaultEJBHost().removeSessionBean(ejbName);
    }
    
    public EJBSessionBean getSessionBean(String ejbName) throws EJBRegistrationException {
        if (ejbHosts.getEJBHosts().isEmpty()) {
            throw new EJBRegistrationException("No EJB host available");
        }

        // TODO implement selection of the correct EJB host based on the mapping
        // For now just select the first one
        return getDefaultEJBHost().getSessionBean(ejbName);
    }
    
    private EJBHost getDefaultEJBHost() {
        return ejbHosts.getEJBHosts().get(0);
    }
}
