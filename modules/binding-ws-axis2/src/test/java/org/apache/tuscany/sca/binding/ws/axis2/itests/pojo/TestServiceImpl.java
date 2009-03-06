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
package org.apache.tuscany.sca.binding.ws.axis2.itests.pojo;

import java.util.Arrays;

import org.oasisopen.sca.annotation.Service;

@Service(TestService.class)
public class TestServiceImpl implements TestService {

    public boolean printData(Data data) {
        if (data == null) {
            System.out.println("data is null");
            return false;
        }

        StringBuilder sb = new StringBuilder(256);
        sb.append("Data:\n");
        Data2[] a = data.getA();
        if (a != null) {
            for (int i = 0; i < a.length; i++) {
                sb.append("  a[");
                sb.append(i);
                sb.append("] = ");
                sb.append(Arrays.asList(a[i].getAsdf()));
                sb.append("\n");
            }
        } else {
            sb.append("  a = null");
        }
        sb.append("\n  b = ");
        sb.append(data.getB());
        sb.append("\n  c = ");
        sb.append(data.getC());
        sb.append("\n");

        System.out.println(sb.toString());

        return true;
    }

}
