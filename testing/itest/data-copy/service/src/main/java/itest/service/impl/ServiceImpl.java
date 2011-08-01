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

package itest.service.impl;

import java.util.List;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.w3c.dom.Node;

import commonj.sdo.DataObject;

import static org.junit.Assert.assertEquals;

import itest.privatecopy.intf.ServiceIntf;
import itest.privatecopy.types.Name;


public class ServiceImpl implements ServiceIntf {

    @Override
    public Name greet(Name name) {
        Name retVal = new Name();
        retVal.setFirstName("Hi " + name.getFirstName());
        retVal.setLastName("Ms. " + name.getLastName());
        return retVal;        
    }

    @Override
    public boolean areNamesTheSameObjects(Name name1, Name name2) {
        return name1 == name2;
    }

    @Override
    public String greetJSON(JSONObject name) {
        try {
            String firstName = name.getString("firstName");
            String lastName = name.getString("lastName");
            assertEquals("Jason", firstName);
            assertEquals("Nosaj", lastName);
        } catch (JSONException exc) {
            throw new RuntimeException(exc);
        }
        return "good";
    }


    @Override
    public String greetDOM(Node name) {
	return "fail";
    }
    
    @Override
    public Node returnDOM(Node name) {
        return name;
    }

}


