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
package account;

import org.osoa.sca.ServiceRuntimeException;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

import calculator.AddService;

@Service(Customer.class)
public class CustomerImpl implements Customer {

    private AddService extEJBService = null;

    public AddService getExtEJBService() {
        return extEJBService;
    }

    @Reference
    public void setExtEJBService(AddService extEJBService) {
        this.extEJBService = extEJBService;
    }

    // this method invokes external EJB through EJB reference binding
    public Double depositAmount(java.lang.String accountNo, Double amount) {
         
        Double total = null;

        System.out.println("In component implementation. Invoking external EJB through EJB reference binding  ");

        try {
            Double balance = extEJBService.add(amount.doubleValue(), 1000); //invoke external ejb through ejb reference binding 
            total =  balance + amount; 
        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }
        return total;
    }

}
