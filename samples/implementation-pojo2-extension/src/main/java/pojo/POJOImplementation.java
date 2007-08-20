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

package pojo;

import java.lang.reflect.Method;
import java.util.Map;

import org.apache.tuscany.sca.assembly.Implementation;


/**
 * Represents a POJO implementation in an SCA assembly.
 *
 * @version $Rev$ $Date$
 */
public interface POJOImplementation extends Implementation {
    
    /**
     * Returns the POJO class name
     * @return
     */
    public String getPOJOName();

    /**
     * Sets the POJO class name
     * @param pojoName
     */
    public void setPOJOName(String pojoName);
    
    /**
     * Returns the POJO class.
     * @return
     */
    public Class<?> getPOJOClass();
    
    /**
     * Sets the POJO class.
     * @param pojoClass
     */
    public void setPOJOClass(Class<?> pojoClass);
  
    /**
     * Returns the POJO's methods.
     * @return
     */
    public Map<String, Method> getMethods();

}
