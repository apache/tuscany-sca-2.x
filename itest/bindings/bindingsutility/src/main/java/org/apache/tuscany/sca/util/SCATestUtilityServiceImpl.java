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
package org.apache.tuscany.sca.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.Vector;

import org.osoa.sca.annotations.Service;

@Service(SCATestUtilityService.class)
public class SCATestUtilityServiceImpl implements SCATestUtilityService {

    // private SCATestToolCallbackService utilCallBack;

    /**
     * The setter used by the runtime to set the callback reference
     * 
     * @param myServiceCallback
     */

    // @Callback
    // public void setCallback(SCATestToolCallbackService utilCallBack) {
    // this.utilCallBack = utilCallBack;
    // }
    public String ping(String input) {
        System.out.println("Invoking SCATestUtilityServiceImpl.ping()");
        StringBuffer rc = new StringBuffer();

        try {
            // get a systems property object
            Properties sp = System.getProperties();
            rc.append("Pinged SCA Test Utility Service on: ");
            // get operating system info
            rc.append(sp.getProperty("os.name"));
            rc.append(", " + sp.getProperty("os.version"));
            // get network info
            rc.append(" - " + InetAddress.getLocalHost().toString());
        } catch (Exception e) {
            System.out.println("\nException preparing system infomation for ping service reply\n" + e.toString());
            e.printStackTrace();
        }
        return rc.toString();
    }

    public void asyncping() {

        StringBuffer rc = new StringBuffer();

        try {
            // get a systems property object
            Properties sp = System.getProperties();
            rc.append("Pinged SCA Test Utility Service on: ");
            // get operating system info
            rc.append(sp.getProperty("os.name"));
            rc.append(", " + sp.getProperty("os.version"));
            // get network info
            rc.append(" - " + InetAddress.getLocalHost().toString());
        } catch (Exception e) {
            System.out.println("\nException preparing system infomation for ping service reply\n" + e.toString());
            e.printStackTrace();
        }
        // utilCallBack.pingCallBack(rc.toString());
    }

    public int echo_int(int input) {
        int local;
        local = input;
        return local;
    }

    public short echo_short(short input) {
        short local;
        local = input;
        return local;
    }

    public long echo_long(long input) {
        long local;
        local = input;
        return local;
    }

    public float echo_float(float input) {
        float local;
        local = input;
        return local;
    }

    public double echo_double(double input) {
        double local;
        local = input;
        return local;
    }

    public boolean echo_boolean(boolean input) {
        boolean local;
        local = input;
        return local;
    }

    public char echo_char(char input) {
        char local;
        local = input;
        return local;
    }

    public String echo_String(String input) {
        String local;
        local = input;
        return local;
    }

    public BigDecimal echo_BigDecimal(BigDecimal input) {
        BigDecimal local;
        local = input;
        return local;
    }

    public BigInteger echo_BigInteger(BigInteger input) {
        BigInteger local;
        local = input;
        return local;
    }

    public Vector echo_Vector(Vector input) {
        Vector local;
        local = input;
        return local;
    }

    public GregorianCalendar echo_GregorianCalendar(GregorianCalendar input) {
        GregorianCalendar local;
        local = input;
        return local;
    }

}
