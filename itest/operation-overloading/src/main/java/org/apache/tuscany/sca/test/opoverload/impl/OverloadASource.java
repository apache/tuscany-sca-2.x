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

package org.apache.tuscany.sca.test.opoverload.impl;

import java.util.ArrayList;

import org.apache.tuscany.sca.test.opoverload.OverloadASourceTarget;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

/**
 * 
 */
@Service(OverloadASourceTarget.class)
public class OverloadASource implements OverloadASourceTarget{
    public OverloadASourceTarget overloadASourceTarget;
    /**
     * 
     */
    public OverloadASource()  {
      
    }
    /**
     * @return
     * @see org.apache.tuscany.sca.test.opoverload.OverloadASourceTarget#operationA()
     */
    public String[] operationAall() {
        assert overloadASourceTarget != null : "reference overloadASourceTarget is null";
        ArrayList<String> ret = new ArrayList<String>();
        ret.add(overloadASourceTarget.operationA());
        ret.add(overloadASourceTarget.operationA(11));
        ret.add(overloadASourceTarget.operationA("eleven"));
        ret.add(overloadASourceTarget.operationA(3,"three"));
        ret.add(overloadASourceTarget.operationA("four",4));
        return (String[]) ret.toArray(new String[ret.size()]);
    }
 
       
    /**
     * @return
     * @see org.apache.tuscany.sca.test.opoverload.OverloadASourceTarget#operationA()
     */
    public String operationA() {
        assert overloadASourceTarget != null : "reference overloadASourceTarget is null";
         return overloadASourceTarget.operationA();
    }
    /**
     * @param parm1
     * @return
     * @see org.apache.tuscany.sca.test.opoverload.OverloadASourceTarget#operationA(int)
     */
    public String operationA(int parm1) {
        assert overloadASourceTarget != null : "reference overloadASourceTarget is null";
        return overloadASourceTarget.operationA(parm1);
    }
    /**
     * @param parm1
     * @param parm2
     * @return
     * @see org.apache.tuscany.sca.test.opoverload.OverloadASourceTarget#operationA(int, java.lang.String)
     */
    public String operationA(int parm1, String parm2) {
        assert overloadASourceTarget != null : "reference overloadASourceTarget is null";
        return overloadASourceTarget.operationA(parm1, parm2);
    }
    public String operationA(String string) {
        assert overloadASourceTarget != null : "reference overloadASourceTarget is null";
        return overloadASourceTarget.operationA(string);
    }
    /**
     * @param parm1
     * @param parm2
     * @return
     * @see org.apache.tuscany.sca.test.opoverload.OverloadASourceTarget#operationA(java.lang.String, int)
     */
    public String operationA(String parm1, int parm2) {
        assert overloadASourceTarget != null : "reference overloadASourceTarget is null";
        return overloadASourceTarget.operationA(parm1, parm2);
    }

    @Reference
    public void setOverloadASourceTarget(OverloadASourceTarget overloadASourceTarget) {
        assert overloadASourceTarget != null : "reference overloadASourceTarget is set tonull";
        this.overloadASourceTarget = overloadASourceTarget;
    }

}
