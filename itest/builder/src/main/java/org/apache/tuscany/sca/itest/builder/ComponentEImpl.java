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
package org.apache.tuscany.sca.itest.builder;

import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

/**
 * Implementation class for ComponentD.
 * 
 * @version $Rev: 678875 $ $Date: 2008-07-22 21:01:07 +0100 (Tue, 22 Jul 2008) $
 */
@Service(interfaces={Service3.class})
public class ComponentEImpl implements Service3 {


    public String getGreetings(String name) {
        return "Hello, " + name + " from ComponentE";
    }
}
