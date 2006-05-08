/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package supplychain;

import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

/**
 * This class implements the Warehouse service component.
 */
@Service(Shipper.class)
public class ShipperComponentImpl implements Shipper {
    
    private Customer customer;
    
    @Reference
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    
    public void processShipment(String order) {
        customer.notifyShipment(order + ", shipped");
    }

}
