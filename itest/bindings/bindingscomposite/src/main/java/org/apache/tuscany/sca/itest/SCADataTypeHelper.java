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
package org.apache.tuscany.sca.itest;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.Vector;

import org.apache.tuscany.sca.util.SCATestUtilityService;

public class SCADataTypeHelper {

    Date date;
    Random ran;
    SCATestUtilityService scaUtil; // change this to be the service provider

    public SCADataTypeHelper(SCATestUtilityService util) {
        date = new Date();
        ran = new Random(date.getTime());
        scaUtil = util;
    }

    public StringBuffer test_char() throws SCADataTypeHelperException {

        char x = 'a';
        x += ran.nextInt(26); // get a char a-z
        StringBuffer rc = new StringBuffer("\nchar datatype test sending ==> " + x);
        try {
            char y = scaUtil.echo_char(x);
            if (x == y) {
                rc.append("\nchar successfully received ==> " + y);
            } else {
                throw new SCADataTypeHelperException(rc + "\nDatatype exception:   char ==> " + y);
            }
        } catch (Exception e) {
            throw new SCADataTypeHelperException("\nRemote exception from scaUtil.echo_char\n" + e.toString());
        }
        return rc;
    }

    public StringBuffer test_String() throws SCADataTypeHelperException {

        String x = date.toString();
        StringBuffer rc = new StringBuffer("\nString datatype test sending ==> " + x);
        try {
            String y = scaUtil.echo_String(x);
            if (x.equals(y)) {
                rc.append("\nString successfully received ==> " + y);
            } else {
                throw new SCADataTypeHelperException(rc + "\nDatatype exception:   String ==> " + y);
            }
        } catch (Exception e) {
            throw new SCADataTypeHelperException("\nRemote exception from scaUtil.echo_String\n" + e.toString());
        }
        return rc;
    }

    public StringBuffer test_int() throws SCADataTypeHelperException {

        int x = ran.nextInt();
        StringBuffer rc = new StringBuffer("\nint datatype test sending ==> " + x);
        try {
            int y = scaUtil.echo_int(x);
            if (x == y) {
                rc.append("\nint successfully received ==> " + y);
            } else {
                throw new SCADataTypeHelperException(rc + "\nDatatype exception:   int ==> " + y);
            }
        } catch (Exception e) {
            throw new SCADataTypeHelperException("\nRemote exception from scaUtil.echo_int\n" + e.toString());
        }
        return rc;
    }

    public StringBuffer test_boolean() throws SCADataTypeHelperException {

        boolean x = ran.nextBoolean();
        StringBuffer rc = new StringBuffer("\nboolean datatype test sending ==> " + x);
        try {
            boolean y = scaUtil.echo_boolean(x);
            if (x == y) {
                rc.append("\nboolean successfully received ==> " + y);
            } else {
                throw new SCADataTypeHelperException(rc + "\nDatatype exception:   boolean ==> " + y);
            }
        } catch (Exception e) {
            throw new SCADataTypeHelperException("\nRemote exception from scaUtil.echo_boolean\n" + e.toString());
        }
        return rc;
    }

    public StringBuffer test_long() throws SCADataTypeHelperException {

        long x = ran.nextLong();
        StringBuffer rc = new StringBuffer("\nlong datatype test sending ==> " + x);
        try {
            long y = scaUtil.echo_long(x);
            if (x == y) {
                rc.append("\nlong successfully received ==> " + y);
            } else {
                throw new SCADataTypeHelperException(rc + "\nDatatype exception:   long ==> " + y);
            }
        } catch (Exception e) {
            throw new SCADataTypeHelperException("\nRemote exception from scaUtil.echo_long\n" + e.toString());
        }
        return rc;
    }

    public StringBuffer test_short() throws SCADataTypeHelperException {

        short x = (short)ran.nextInt();
        StringBuffer rc = new StringBuffer("\nshort datatype test sending ==> " + x);
        try {
            short y = scaUtil.echo_short(x);
            if (x == y) {
                rc.append("\nshort successfully received ==> " + y);
            } else {
                throw new SCADataTypeHelperException(rc + "\nDatatype exception:   short ==> " + y);
            }
        } catch (Exception e) {
            throw new SCADataTypeHelperException("\nRemote exception from scaUtil.echo_short\n" + e.toString());
        }
        return rc;
    }

    public StringBuffer test_float() throws SCADataTypeHelperException {

        float x = ran.nextFloat();
        StringBuffer rc = new StringBuffer("\nfloat datatype test sending ==> " + x);
        try {
            float y = scaUtil.echo_float(x);
            if (x == y) {
                rc.append("\nfloat successfully received ==> " + y);
            } else {
                throw new SCADataTypeHelperException(rc + "\nDatatype exception:   float ==> " + y);
            }
        } catch (Exception e) {
            throw new SCADataTypeHelperException("\nRemote exception from scaUtil.echo_float " + e.toString());
        }
        return rc;
    }

    public StringBuffer test_double() throws SCADataTypeHelperException {

        double x = ran.nextDouble();
        StringBuffer rc = new StringBuffer("\ndouble datatype test sending ==> " + x);
        try {
            double y = scaUtil.echo_double(x);
            if (x == y) {
                rc.append("\ndouble successfully received ==> " + y);
            } else {
                throw new SCADataTypeHelperException(rc + "\nDatatype exception:   double ==> " + y);
            }
        } catch (Exception e) {
            throw new SCADataTypeHelperException("\nRemote exception from scaUtil.echo_double\n" + e.toString());
        }
        return rc;
    }

    public StringBuffer test_BigInteger() throws SCADataTypeHelperException {

        BigInteger x = new BigInteger(ran.nextInt(32) + 32, ran); // between
        // 32 - 64
        // bitLength
        StringBuffer rc = new StringBuffer("\nBigInteger datatype test sending ==> " + x);
        try {
            BigInteger y = scaUtil.echo_BigInteger(x);
            if (x.equals(y)) {
                rc.append("\nBigInteger successfully received ==> " + y);
            } else {
                throw new SCADataTypeHelperException("\n" + rc + "\nDatatype exception:   BigInteger ==> " + y);
            }
        } catch (Exception e) {
            throw new SCADataTypeHelperException("\nRemote exception from scaUtil.echo_BigInteger\n" + e.toString());
        }
        return rc;
    }

    public StringBuffer test_BigDecimal() throws SCADataTypeHelperException {

        BigDecimal x = new BigDecimal(ran.nextDouble());
        StringBuffer rc = new StringBuffer("\nBigDecimal datatype test sending ==> " + x);
        try {
            BigDecimal y = scaUtil.echo_BigDecimal(x);
            if (x.equals(y)) {
                rc.append("\nBigDecimal successfully received ==> " + y);
            } else {
                throw new SCADataTypeHelperException("\n" + rc + "\nDatatype exception:   BigDecimal ==> " + y);
            }
        } catch (Exception e) {
            throw new SCADataTypeHelperException("\nRemote exception from scaUtil.echo_BigDecimal\n" + e.toString());
        }
        return rc;
    }

    private StringBuffer test_Vector() throws SCADataTypeHelperException {

        Vector vector = new Vector();
        vector.addElement("DanW");
        vector.addElement(new Float(2000F));
        vector.addElement(new Short((short)11));
        StringBuffer rc = new StringBuffer("\nVector datatype test sending ==> " + vector.toString());
        boolean passed = true;
        try {
            Vector y = scaUtil.echo_Vector(vector);
            if (((String)y.elementAt(0)).equals("DanW")) {
                rc.append("\nVector element 0 string " + y.elementAt(0) + " successfully recieved");
            } else {
                passed = false;
            }
            if ((((Float)y.elementAt(1)).equals((Float)vector.elementAt(1)))) {
                rc.append("\nVector element 1 Float " + y.elementAt(1) + " successfully recieved");
            } else {
                passed = false;
            }
            if (y.elementAt(2).equals(vector.elementAt(2))) {
                rc.append("\nVector element 2 (Short) " + y.elementAt(2) + " successfully received");
            } else {
                passed = false;
            }
            if (!passed) {
                //
                // one of the tests failed
                //
                rc.append("\nVector element 0 (String) " + y.elementAt(0) + " should be \"DanW\"");
                rc.append("\nVector element 1 (Float) " + y.elementAt(1) + " should be \"2000F\"");
                rc.append("\nVector element 2 (Short) " + y.elementAt(2) + " should be \"11\"");
                throw new SCADataTypeHelperException("\n" + rc + "\nDataType exception: Vector ==> " + y);
            }
        } catch (Exception e) {
            throw new SCADataTypeHelperException("\nRemote exception from scaUtil.echo_Vector\n" + e.toString());
        }
        return rc;
    }

    public StringBuffer test_GregorianCalendar() throws SCADataTypeHelperException {

        GregorianCalendar x = new GregorianCalendar();
        StringBuffer rc = new StringBuffer("\nGregorianCalendar datatype test sending ==> " + x.toString());
        try {
            GregorianCalendar y = scaUtil.echo_GregorianCalendar(x);
            if (x.equals(y)) {
                rc.append("\nGregorianCalendar successfully received ==> " + y);
            } else {
                throw new SCADataTypeHelperException("\n" + rc + "\nDatatype exception:   GregorianCalendar ==> " + y);
            }
        } catch (Exception e) {
            throw new SCADataTypeHelperException("\nRemote exception from scaUtil.echo_GregorianCalendar\n" + e
                .toString());
        }
        return rc;
    }

    public StringBuffer doDataType() {

        StringBuffer rc = new StringBuffer();
        try {
            rc.append(test_boolean());
        } catch (SCADataTypeHelperException e) {
            rc.append("\n" + e.toString() + "\n");
        }
        try {
            rc.append(test_char());
        } catch (SCADataTypeHelperException e) {
            rc.append("\n" + e.toString() + "\n");
        }
        try {
            rc.append(test_String());
        } catch (SCADataTypeHelperException e) {
            rc.append("\n" + e.toString() + "\n");
        }
        try {
            rc.append(test_int());
        } catch (SCADataTypeHelperException e) {
            rc.append("\n" + e.toString());
        }
        try {
            rc.append(test_long());
        } catch (SCADataTypeHelperException e) {
            rc.append("\n" + e.toString() + "\n");
        }
        try {
            rc.append(test_short());
        } catch (SCADataTypeHelperException e) {
            rc.append("\n" + e.toString() + "\n");
        }
        try {
            rc.append(test_float());
        } catch (SCADataTypeHelperException e) {
            rc.append("\n" + e.toString() + "\n");
        }
        try {
            rc.append(test_double());
        } catch (SCADataTypeHelperException e) {
            rc.append("\n" + e.toString() + "\n");
        }
        try {
            rc.append(test_BigInteger());
        } catch (SCADataTypeHelperException e) {
            rc.append("\n" + e.toString() + "\n");
        }
        try {
            rc.append(test_BigDecimal());
        } catch (SCADataTypeHelperException e) {
            rc.append("\n" + e.toString() + "\n");
        }
        try {
            rc.append(test_Vector());
        } catch (SCADataTypeHelperException e) {
            rc.append("\n" + e.toString() + "\n");
        }
        try {
            rc.append(test_GregorianCalendar());
        } catch (SCADataTypeHelperException e) {
            rc.append("\n" + e.toString() + "\n");
        } catch (Exception e) {
            rc.append("\n" + e.toString() + "\n");
        }

        return rc;
    }
}
