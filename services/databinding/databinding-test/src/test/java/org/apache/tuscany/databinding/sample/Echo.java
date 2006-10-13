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
package org.apache.tuscany.databinding.sample;

import org.apache.tuscany.api.annotation.DataType;
import org.osoa.sca.annotations.Remotable;
import org.osoa.sca.annotations.Service;

import com.example.ipo.jaxb.PurchaseOrderType;


/**
 * @version $Rev$ $Date$
 */
@Remotable
@Service
public interface Echo {
    @DataType(name="javax.xml.bind.JAXBElement")
    PurchaseOrderType echoJAXB(PurchaseOrderType po);

    @DataType(name="commonj.sdo.DataObject")
    com.example.ipo.sdo.PurchaseOrderType echoSDO(com.example.ipo.sdo.PurchaseOrderType po);
    
    @DataType(name="org.apache.xmlbeans.XmlObject")
    com.example.ipo.xmlbeans.PurchaseOrderType echoXMLBeans(com.example.ipo.xmlbeans.PurchaseOrderType po);
}