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

package org.apache.tuscany.sca.test.exceptions.impl;

import org.apache.tuscany.sca.test.exceptions.Checked;
import org.apache.tuscany.sca.test.exceptions.ExceptionHandler;
import org.apache.tuscany.sca.test.exceptions.ExceptionThrower;
import org.apache.tuscany.sca.test.exceptions.UnChecked;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;

@Scope("COMPOSITE")
public class ExceptionHandlerImpl implements ExceptionHandler {
    static final String INIT = "INIT";

    private ExceptionThrower exceptionThrower;

    private String theGood;

    private Checked theBad;

    private UnChecked theUgly;

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.tuscany.sca.test.exceptions.impl.ExceptionHandler#testing()
     */
    public void testing() {

        assert exceptionThrower != null : "'exceptionThrower' never wired";
        String result = INIT;
        try {
            theGood = result = exceptionThrower.theGood();
            assert result == ExceptionThrower.SO_THEY_SAY;
        } catch (Throwable e) {
            assert result == INIT;
            assert false;
            e.printStackTrace();
        }

        result = INIT;
        try {
            result = exceptionThrower.theBad();
            // incredible
            assert false : "Expected 'Check' Exception";

        } catch (Checked e) {
            // This is good...
            assert result == INIT;
            theBad = e;
        } catch (Throwable t) {
            // This is not so good.
            t.printStackTrace();
            assert result == INIT;
            assert false : "Got wrong exception '" + t.getClass().getName();
        }

        result = INIT;
        try {
            result = exceptionThrower.theUgly();
            // incredible
            assert false : "Expected 'UnCheck' Exception";

        } catch (Checked e) {
            // This is not so good...
            assert false : "Got wrong exception '" + e.getClass().getName();
            assert result == INIT;
        } catch (UnChecked e) {
            theUgly = e;

        } catch (Throwable t) {
            // This is not good.
            assert false;
            assert result == INIT;

            System.out.println(ExceptionThrower.SO_THEY_SAY + " " + INIT);
        }

    }

    @Reference 
    public void setExceptionThrower(ExceptionThrower exceptionThrower) {
        this.exceptionThrower = exceptionThrower;
    }

    public String getTheGood() {
        return theGood;
    }

    public Checked getTheBad() {
        return theBad;
    }

    public UnChecked getTheUgly() {
        return theUgly;
    }

    public ExceptionThrower getExceptionThrower() {
        return exceptionThrower;
    }

}
