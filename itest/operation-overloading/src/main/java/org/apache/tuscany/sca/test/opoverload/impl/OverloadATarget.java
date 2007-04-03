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

import org.apache.tuscany.sca.test.opoverload.OverloadASourceTarget;
import org.osoa.sca.annotations.Service;

/**
 * 
 */
@Service(OverloadASourceTarget.class)
public class OverloadATarget implements OverloadASourceTarget {

    /**
     * 
     */
    public OverloadATarget() {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.tuscany.sca.test.opoverload.OverloadASourceTarget#operationA(int)
     */
    public String operationA(int parm1) {
        final String ret = opName + parm1;
        out(ret);
        return ret;

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.tuscany.sca.test.opoverload.OverloadASourceTarget#operationA(int,
     *      java.lang.String)
     */
    public String operationA(int parm1, String parm2) {
        final String ret = opName + parm1 + parm2;
        out(ret);
        return ret;

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.tuscany.sca.test.opoverload.OverloadASourceTarget#operationA()
     */
    public String operationA() {
        out(opName);
        return opName;

    }

    public String operationA(String parm1, int parm2) {
        final String ret = opName + parm1 + parm2;
        out(ret);
        return ret;
    }

    public String operationA(String parm1) {
        final String ret = opName + parm1;
        out(ret);
        return ret;
    }

    private void out(String msg) {

        java.lang.System.out.println(msg);
    }

    public String[] operationAall() {
        throw new IllegalArgumentException("not supported");
       
    }

}
