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

package org.apache.tuscany.sca.test.corba.types;

import org.apache.tuscany.sca.test.corba.generated.Color;
import org.apache.tuscany.sca.test.corba.generated.RichStruct;
import org.apache.tuscany.sca.test.corba.generated.UnexpectedException;
import org.apache.tuscany.sca.test.corba.generated.WrongColor;
import org.apache.tuscany.sca.test.corba.generated._ScenarioOneImplBase;

/**
 * @version $Rev$ $Date$
 * Servant for generated interface. Uses generated classes.
 */
public class ScenarioOneServant extends _ScenarioOneImplBase {

    private static final long serialVersionUID = 1L;

    public RichStruct setRichStruct(RichStruct richStruct) throws WrongColor, UnexpectedException {
        if (richStruct.innerStruct.color.value() == Color.red.value()) {
            throw new WrongColor(richStruct.innerStruct.color, Color.green);
        } else if (richStruct.longField == 0) {
            throw new UnexpectedException("Expected richStruct.longField != 0");
        }
        return richStruct;
    }

}
