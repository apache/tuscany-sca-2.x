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

package echo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.oasisopen.sca.annotation.Reference;

import bean.TestBean;

public class EchoClientImpl implements Echo {
    @Reference
    protected Echo echoReference;

    public String echo(String msg) {
        return echoReference.echo(msg);
    }

    public int[] echoArrayInt(int[] intArray) {
        throw new UnsupportedOperationException("UNsupported !");
    }

    public String[] echoArrayString(String[] stringArray) {
        throw new UnsupportedOperationException("UNsupported !");
    }

    public TestBean echoBean(TestBean testBean) {
        throw new UnsupportedOperationException("UNsupported !");
    }

    public boolean echoBoolean(boolean param) {
        throw new UnsupportedOperationException("UNsupported !");
    }

    public void echoBusinessException() throws EchoBusinessException {
        throw new UnsupportedOperationException("UNsupported !");
    }

    public int echoInt(int param) {
        throw new UnsupportedOperationException("UNsupported !");
    }

    public List echoList(ArrayList list) {
        throw new UnsupportedOperationException("UNsupported !");
    }

    public Map echoMap(HashMap map) {
        throw new UnsupportedOperationException("UNsupported !");
    }

    public void echoRuntimeException() throws RuntimeException {
        throw new UnsupportedOperationException("UNsupported !");
    }

    public Set echoSet(HashSet set) {
        throw new UnsupportedOperationException("UNsupported !");
    }

    public void get\u03a9\u03bb\u03c0() {
        throw new UnsupportedOperationException("UNsupported !");
    }

}
