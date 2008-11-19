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
package org.apache.tuscany.sca.policy.authentication.token;

import java.security.Principal;


/**
 *
 * @version $Rev$ $Date$
 */
public class TokenPrincipal implements Principal {

    private String name;

    public TokenPrincipal(String name){
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
        
        this.name = name;
    }
    
    public String getName() {
        return name;
    }    
    
    @Override
    public int hashCode() {
        return name.hashCode();
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    
    @Override
    public boolean equals(Object principal) {
        if (principal == null)
            return false;
        if (this == principal)
            return true;
        if (getClass() != principal.getClass())
            return false;
        final TokenPrincipal other = (TokenPrincipal)principal;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name)){
            return false;
        }
            
        return true;
    }
   
}
