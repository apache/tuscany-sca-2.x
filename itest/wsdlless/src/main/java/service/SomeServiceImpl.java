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
package service;

import org.osoa.sca.annotations.Service;

@Service(SomeService.class)
public class SomeServiceImpl implements SomeService {

    public AnObject getUsingString(String stringParam) {
        System.out.println("Param value:" + stringParam);

        return getAnObject(stringParam);
    }

    private AnObject getAnObject(String stringParam) {
        return new AnObject(stringParam + "123", 123);
    }

    public AnObject getUsingMoreComplexObject(MoreComplexObject moreComplexParam) {
        System.out.println("Param value:" + moreComplexParam.getStringParam());

        return getAnObject(moreComplexParam.getStringParam());
    }

}
