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

package sample;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.xml.Constants;

/**
 * A test policy that can be applied at various places along the message
 * handling chain
 * 
 * @version $Rev: 916315 $ $Date: 2010-02-25 15:12:44 +0000 (Thu, 25 Feb 2010) $
 */
public class ImplementationGenericPolicy {
    public static final QName NAME = new QName(Constants.SCA11_TUSCANY_NS, "implementationGenericPolicy");

    private String name;
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public QName getSchemaName() {
        return NAME;
    }

    public boolean isUnresolved() {
        return false;
    }

    public void setUnresolved(boolean unresolved) {
        // it's always resolved
    }
   
}
