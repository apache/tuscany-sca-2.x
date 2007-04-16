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
package crud;

import org.apache.tuscany.assembly.ComponentService;
import org.apache.tuscany.assembly.Implementation;
import org.apache.tuscany.assembly.impl.ComponentServiceImpl;
import org.apache.tuscany.assembly.impl.ComponentTypeImpl;
import org.apache.tuscany.interfacedef.java.JavaInterface;
import org.apache.tuscany.interfacedef.java.JavaInterfaceContract;
import org.apache.tuscany.interfacedef.java.impl.JavaInterfaceContractImpl;
import org.apache.tuscany.interfacedef.java.impl.JavaInterfaceImpl;

/**
 * @version $$Rev$$ $$Date: 2007-04-03 11:08:56 -0700 (Tue, 03 Apr
 *          2007) $$
 */
public class CRUDImplementation extends ComponentTypeImpl implements Implementation {
    private String directory;

    public CRUDImplementation(String directory) {
        this.directory = directory;
        ComponentService service = createService(CRUD.class);
        getServices().add(service);
    }

    private ComponentService createService(Class<?> type) {
        org.apache.tuscany.assembly.ComponentService service = new ComponentServiceImpl();
        service.setName(type.getSimpleName());
        JavaInterface interfaze = new JavaInterfaceImpl();
        interfaze.setJavaClass(type);
        JavaInterfaceContract interfaceContract = new JavaInterfaceContractImpl();
        interfaceContract.setInterface(interfaze);
        service.setInterfaceContract(interfaceContract);
        return service;
    }

    /**
     * @return the directory
     */
    public String getDirectory() {
        return directory;
    }

    /**
     * @param directory the directory to set
     */
    public void setDirectory(String directory) {
        this.directory = directory;
    }

}
