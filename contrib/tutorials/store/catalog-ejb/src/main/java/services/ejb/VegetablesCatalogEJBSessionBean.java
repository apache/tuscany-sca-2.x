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

package services.ejb;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

public class VegetablesCatalogEJBSessionBean implements SessionBean {
    private static final long serialVersionUID = -7421020241291271838L;
    
    private List<Vegetable> catalog = new ArrayList<Vegetable>();
 
    public VegetablesCatalogEJBSessionBean() {
        catalog.add(new Vegetable("Broccoli", "$2.99"));
        catalog.add(new Vegetable("Asparagus", "$3.55"));
        catalog.add(new Vegetable("Cauliflower", "$1.55"));
    }

    public Vegetable[] get() {   
        Vegetable[] catalogArray = new Vegetable[catalog.size()];
        catalog.toArray(catalogArray);
        return catalogArray;
    }
    
    public void ejbCreate() throws CreateException {
    }
    
    public void ejbActivate() throws EJBException, RemoteException {
    }
    
    public void ejbPassivate() throws EJBException, RemoteException {
    }
    
    public void ejbRemove() throws EJBException, RemoteException {
    }
    
    public void setSessionContext(SessionContext arg0) throws EJBException, RemoteException {
    }
}
