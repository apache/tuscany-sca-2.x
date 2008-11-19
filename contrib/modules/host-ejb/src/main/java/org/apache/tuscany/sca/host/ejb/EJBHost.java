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
 * Interface implemented by host environments that allow EJBs
 * to be registered. 
 * <p/> 
 * This interface allows a system service to register an EJB session
 * bean to handle inbound requests.
 * 
 * @version $Rev$ $Date$
 */
public interface EJBHost {
    
    /**
     * Add an EJB session bean.
     * 
     * @param ejbName the EJB name
     * @param sessionBean the EJB session bean descriptor
     * @throws EJBRegistrationException
     */
    void addSessionBean(String ejbName, EJBSessionBean sessionBean) throws EJBRegistrationException;

    /**
     * Remove an EJB session bean.
     * 
     * @param ejbName the EJB name
     * @return the EJB session bean descriptor that was registered under that name
     * @throws EJBRegistrationException
     */
    EJBSessionBean removeSessionBean(String ejbName) throws EJBRegistrationException;

    /**
     * Returns the EJB session bean descriptor registered under
     * the given EJB name.
     * 
     * @param ejbName the EJB name
     * @return the EJB session bean descriptor
     * @throws EJBRegistrationException
     */
    EJBSessionBean getSessionBean(String ejbName) throws EJBRegistrationException;

}
