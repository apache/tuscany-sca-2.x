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

package itest;

import org.oasisopen.sca.annotation.Destroy;
import org.oasisopen.sca.annotation.Init;
import org.oasisopen.sca.annotation.Scope;

@Scope("COMPOSITE")
public class InitCompositeScopeException implements Service {

    public static boolean initRun;
    public static boolean destroyRun;
    public static boolean doitRun;
    public static int count = 0;
    
    public void doit() {
        doitRun = true;
        if (!initRun) {
            throw new RuntimeException("initRun false");
        }
        if (destroyRun) {
            throw new RuntimeException("destroyRun true");
        }
    }
    
    @Init
    public void init() {
        initRun = true;
        if (count++ < 1) {
            throw new RuntimeException("bang");
        }
    }
    
    @Destroy
    public void destroy() {
        destroyRun = true;   
    }

}
