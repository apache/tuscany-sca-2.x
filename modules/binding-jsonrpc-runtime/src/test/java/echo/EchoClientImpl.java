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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.oasisopen.sca.annotation.AllowsPassByReference;
import org.oasisopen.sca.annotation.Reference;

import bean.TestBean;

@AllowsPassByReference
public class EchoClientImpl implements Echo {
    @Reference
    @AllowsPassByReference
    protected Echo echoReference;

    public String echo(String msg) {
        return echoReference.echo(msg);
    }

    public void echoVoid() {
        echoReference.echoVoid();
    }

    public void echoRuntimeException() throws RuntimeException {
        echoReference.echoRuntimeException();
    }

    public void echoBusinessException() throws EchoBusinessException {
        echoReference.echoBusinessException();
    }

    public int echoInt(int param) {
        return echoReference.echoInt(param);
    }

    public double echoDouble(double param) {
        return echoReference.echoDouble(param);
    }

    public boolean echoBoolean(boolean param) {
        return echoReference.echoBoolean(param);
    }

    public Map echoMap(HashMap map) {
        return echoReference.echoMap(map);
    }

    public TestBean echoBean(TestBean testBean) {
        return echoReference.echoBean(testBean);
    }

    public List echoList(ArrayList list) {
        return echoReference.echoList(list);
    }

    public String[] echoArrayString(String[] stringArray) {
        return echoReference.echoArrayString(stringArray);
    }

    public int[] echoArrayInt(int[] intArray) {
        return echoReference.echoArrayInt(intArray);
    }

    public Set echoSet(HashSet set) {
        return echoReference.echoSet(set);
    }

    public void get\u03a9\u03bb\u03c0() {
        echoReference.get\u03a9\u03bb\u03c0();
    }

    public BigDecimal echoBigDecimal(BigDecimal param) {
        return echoReference.echoBigDecimal(param);
    }


}
