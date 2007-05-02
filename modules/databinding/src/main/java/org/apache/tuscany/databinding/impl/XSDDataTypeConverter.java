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

package org.apache.tuscany.databinding.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.TimeZone;
import javax.xml.XMLConstants;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;

/**
 * Utility class for XSD data type conversions
 */
public class XSDDataTypeConverter {
    public static final class Base64Binary {
        private static final char[] S_BASE64CHAR =
        {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',
            'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
            'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4',
            '5', '6', '7', '8', '9', '+', '/'};

        private static final char S_BASE64PAD = '=';

        private static final byte[] S_DECODETABLE = new byte[128];

        static {
            for (int i = 0; i < S_DECODETABLE.length; i++) {
                S_DECODETABLE[i] = Byte.MAX_VALUE; // 127
            }
            for (int i = 0; i < S_BASE64CHAR.length; i++) {
                // 0 to 63
                S_DECODETABLE[S_BASE64CHAR[i]] = (byte) i;
            }
        }

        private Base64Binary() {
        }

        /**
         * 
         */
        public static byte[] decode(char[] data, int off, int len) {
            char[] ibuf = new char[4];
            int ibufcount = 0;
            byte[] obuf = new byte[len / 4 * 3 + 3];
            int obufcount = 0;
            for (int i = off; i < off + len; i++) {
                char ch = data[i];
                if (ch == S_BASE64PAD || ch < S_DECODETABLE.length && S_DECODETABLE[ch] != Byte.MAX_VALUE) {
                    ibuf[ibufcount++] = ch;
                    if (ibufcount == ibuf.length) {
                        ibufcount = 0;
                        obufcount += decode0(ibuf, obuf, obufcount);
                    }
                }
            }
            if (obufcount == obuf.length) {
                return obuf;
            }
            byte[] ret = new byte[obufcount];
            System.arraycopy(obuf, 0, ret, 0, obufcount);
            return ret;
        }

        /**
         * 
         */
        public static void decode(char[] data, int off, int len, OutputStream ostream) throws IOException {
            char[] ibuf = new char[4];
            int ibufcount = 0;
            byte[] obuf = new byte[3];
            for (int i = off; i < off + len; i++) {
                char ch = data[i];
                if (ch == S_BASE64PAD || ch < S_DECODETABLE.length && S_DECODETABLE[ch] != Byte.MAX_VALUE) {
                    ibuf[ibufcount++] = ch;
                    if (ibufcount == ibuf.length) {
                        ibufcount = 0;
                        int obufcount = decode0(ibuf, obuf, 0);
                        ostream.write(obuf, 0, obufcount);
                    }
                }
            }
        }

        /**
         * 
         */
        public static byte[] decode(String data) {
            char[] ibuf = new char[4];
            int ibufcount = 0;
            byte[] obuf = new byte[data.length() / 4 * 3 + 3];
            int obufcount = 0;
            for (int i = 0; i < data.length(); i++) {
                char ch = data.charAt(i);
                if (ch == S_BASE64PAD || ch < S_DECODETABLE.length && S_DECODETABLE[ch] != Byte.MAX_VALUE) {
                    ibuf[ibufcount++] = ch;
                    if (ibufcount == ibuf.length) {
                        ibufcount = 0;
                        obufcount += decode0(ibuf, obuf, obufcount);
                    }
                }
            }
            if (obufcount == obuf.length) {
                return obuf;
            }
            byte[] ret = new byte[obufcount];
            System.arraycopy(obuf, 0, ret, 0, obufcount);
            return ret;
        }

        /**
         * 
         */
        public static void decode(String data, OutputStream ostream) throws IOException {
            char[] ibuf = new char[4];
            int ibufcount = 0;
            byte[] obuf = new byte[3];
            for (int i = 0; i < data.length(); i++) {
                char ch = data.charAt(i);
                if (ch == S_BASE64PAD || ch < S_DECODETABLE.length && S_DECODETABLE[ch] != Byte.MAX_VALUE) {
                    ibuf[ibufcount++] = ch;
                    if (ibufcount == ibuf.length) {
                        ibufcount = 0;
                        int obufcount = decode0(ibuf, obuf, 0);
                        ostream.write(obuf, 0, obufcount);
                    }
                }
            }
        }

        private static int decode0(char[] ibuf, byte[] obuf, int index) {
            int wp = index;
            int outlen = 3;
            if (ibuf[3] == S_BASE64PAD) {
                outlen = 2;
            }
            if (ibuf[2] == S_BASE64PAD) {
                outlen = 1;
            }
            int b0 = S_DECODETABLE[ibuf[0]];
            int b1 = S_DECODETABLE[ibuf[1]];
            int b2 = S_DECODETABLE[ibuf[2]];
            int b3 = S_DECODETABLE[ibuf[3]];
            switch (outlen) {
                case 1:
                    obuf[wp] = (byte) (b0 << 2 & 0xfc | b1 >> 4 & 0x3);
                    return 1;
                case 2:
                    obuf[wp++] = (byte) (b0 << 2 & 0xfc | b1 >> 4 & 0x3);
                    obuf[wp] = (byte) (b1 << 4 & 0xf0 | b2 >> 2 & 0xf);
                    return 2;
                case 3:
                    obuf[wp++] = (byte) (b0 << 2 & 0xfc | b1 >> 4 & 0x3);
                    obuf[wp++] = (byte) (b1 << 4 & 0xf0 | b2 >> 2 & 0xf);
                    obuf[wp] = (byte) (b2 << 6 & 0xc0 | b3 & 0x3f);
                    return 3;
                default:
                    throw new IllegalArgumentException("The character sequence is not base64 encoded.");
            }
        }

        /**
         * Returns base64 representation of specified byte array.
         */
        public static String encode(byte[] data) {
            return encode(data, 0, data.length);
        }

        /**
         * Returns base64 representation of specified byte array.
         */
        public static String encode(byte[] data, int off, int len) {
            if (len <= 0) {
                return "";
            }
            char[] out = new char[len / 3 * 4 + 4];
            int rindex = off;
            int windex = 0;
            int rest = len - off;
            while (rest >= 3) {
                int i =
                    ((data[rindex] & 0xff) << 16) + ((data[rindex + 1] & 0xff) << 8)
                        + (data[rindex + 2] & 0xff);
                out[windex++] = S_BASE64CHAR[i >> 18];
                out[windex++] = S_BASE64CHAR[(i >> 12) & 0x3f];
                out[windex++] = S_BASE64CHAR[(i >> 6) & 0x3f];
                out[windex++] = S_BASE64CHAR[i & 0x3f];
                rindex += 3;
                rest -= 3;
            }
            if (rest == 1) {
                int i = data[rindex] & 0xff;
                out[windex++] = S_BASE64CHAR[i >> 2];
                out[windex++] = S_BASE64CHAR[(i << 4) & 0x3f];
                out[windex++] = S_BASE64PAD;
                out[windex++] = S_BASE64PAD;
            } else if (rest == 2) {
                int i = ((data[rindex] & 0xff) << 8) + (data[rindex + 1] & 0xff);
                out[windex++] = S_BASE64CHAR[i >> 10];
                out[windex++] = S_BASE64CHAR[(i >> 4) & 0x3f];
                out[windex++] = S_BASE64CHAR[(i << 2) & 0x3f];
                out[windex++] = S_BASE64PAD;
            }
            return new String(out, 0, windex);
        }

        /**
         * Outputs base64 representation of the specified byte array to a byte stream.
         */
        public static void encode(byte[] data, int off, int len, OutputStream ostream) throws IOException {
            if (len <= 0) {
                return;
            }
            byte[] out = new byte[4];
            int rindex = off;
            int rest = len - off;
            while (rest >= 3) {
                int i =
                    ((data[rindex] & 0xff) << 16) + ((data[rindex + 1] & 0xff) << 8)
                        + (data[rindex + 2] & 0xff);
                out[0] = (byte) S_BASE64CHAR[i >> 18];
                out[1] = (byte) S_BASE64CHAR[(i >> 12) & 0x3f];
                out[2] = (byte) S_BASE64CHAR[(i >> 6) & 0x3f];
                out[3] = (byte) S_BASE64CHAR[i & 0x3f];
                ostream.write(out, 0, 4);
                rindex += 3;
                rest -= 3;
            }
            if (rest == 1) {
                int i = data[rindex] & 0xff;
                out[0] = (byte) S_BASE64CHAR[i >> 2];
                out[1] = (byte) S_BASE64CHAR[(i << 4) & 0x3f];
                out[2] = (byte) S_BASE64PAD;
                out[3] = (byte) S_BASE64PAD;
                ostream.write(out, 0, 4);
            } else if (rest == 2) {
                int i = ((data[rindex] & 0xff) << 8) + (data[rindex + 1] & 0xff);
                out[0] = (byte) S_BASE64CHAR[i >> 10];
                out[1] = (byte) S_BASE64CHAR[(i >> 4) & 0x3f];
                out[2] = (byte) S_BASE64CHAR[(i << 2) & 0x3f];
                out[3] = (byte) S_BASE64PAD;
                ostream.write(out, 0, 4);
            }
        }

        /**
         * Outputs base64 representation of the specified byte array to a character stream.
         */
        public static void encode(byte[] data, int off, int len, Writer writer) throws IOException {
            if (len <= 0) {
                return;
            }
            char[] out = new char[4];
            int rindex = off;
            int rest = len - off;
            int output = 0;
            while (rest >= 3) {
                int i =
                    ((data[rindex] & 0xff) << 16) + ((data[rindex + 1] & 0xff) << 8)
                        + (data[rindex + 2] & 0xff);
                out[0] = S_BASE64CHAR[i >> 18];
                out[1] = S_BASE64CHAR[(i >> 12) & 0x3f];
                out[2] = S_BASE64CHAR[(i >> 6) & 0x3f];
                out[3] = S_BASE64CHAR[i & 0x3f];
                writer.write(out, 0, 4);
                rindex += 3;
                rest -= 3;
                output += 4;
                if (output % 76 == 0) {
                    writer.write("\n");
                }
            }
            if (rest == 1) {
                int i = data[rindex] & 0xff;
                out[0] = S_BASE64CHAR[i >> 2];
                out[1] = S_BASE64CHAR[(i << 4) & 0x3f];
                out[2] = S_BASE64PAD;
                out[3] = S_BASE64PAD;
                writer.write(out, 0, 4);
            } else if (rest == 2) {
                int i = ((data[rindex] & 0xff) << 8) + (data[rindex + 1] & 0xff);
                out[0] = S_BASE64CHAR[i >> 10];
                out[1] = S_BASE64CHAR[(i >> 4) & 0x3f];
                out[2] = S_BASE64CHAR[(i << 2) & 0x3f];
                out[3] = S_BASE64PAD;
                writer.write(out, 0, 4);
            }
        }
    }

    /**
     * <p/>
     * Utility class for xs:hexbinary. </p>
     */
    public static final class HexBinary {
        private HexBinary() {
        }

        /**
         * Converts the string <code>pValue</code> into an array of hex bytes.
         */
        public static byte[] decode(String pValue) {
            if ((pValue.length() % 2) != 0) {
                throw new IllegalArgumentException("A HexBinary string must have even length.");
            }
            byte[] result = new byte[pValue.length() / 2];
            int j = 0;
            int i = 0;
            while (i < pValue.length()) {
                byte b;
                char c = pValue.charAt(i++);
                char d = pValue.charAt(i++);
                if (c >= '0' && c <= '9') {
                    b = (byte) ((c - '0') << 4);
                } else if (c >= 'A' && c <= 'F') {
                    b = (byte) ((c - 'A' + 10) << 4);
                } else if (c >= 'a' && c <= 'f') {
                    b = (byte) ((c - 'a' + 10) << 4);
                } else {
                    throw new IllegalArgumentException("Invalid hex digit: " + c);
                }
                if (d >= '0' && d <= '9') {
                    b += (byte) (d - '0');
                } else if (d >= 'A' && d <= 'F') {
                    b += (byte) (d - 'A' + 10);
                } else if (d >= 'a' && d <= 'f') {
                    b += (byte) (d - 'a' + 10);
                } else {
                    throw new IllegalArgumentException("Invalid hex digit: " + d);
                }
                result[j++] = b;
            }
            return result;
        }

        /**
         * Converts the byte array <code>pHexBinary</code> into a string.
         */
        public static String encode(byte[] pHexBinary) {
            StringBuffer result = new StringBuffer();
            for (int i = 0; i < pHexBinary.length; i++) {
                byte b = pHexBinary[i];
                byte c = (byte) ((b & 0xf0) >> 4);
                if (c <= 9) {
                    result.append((char) ('0' + c));
                } else {
                    result.append((char) ('A' + c - 10));
                }
                c = (byte) (b & 0x0f);
                if (c <= 9) {
                    result.append((char) ('0' + c));
                } else {
                    result.append((char) ('A' + c - 10));
                }
            }
            return result.toString();
        }

        /**
         * Creates a clone of the given byte array.
         */
        public static byte[] getClone(byte[] pHexBinary) {
            byte[] result = new byte[pHexBinary.length];
            System.arraycopy(pHexBinary, 0, result, 0, pHexBinary.length);
            return result;
        }
    }

    public class XSDDateFormat extends XSDDateTimeFormat {
        private static final long serialVersionUID = -1629412916827246627L;

        /**
         * Creates a new instance.
         */
        public XSDDateFormat() {
            super(true, false);
        }
    }

    /**
     * <p/>
     * An instance of {@link java.text.Format}, which may be used to parse and format <code>xs:dateTime</code> values.
     * </p>
     */
    public static class XSDDateTimeFormat extends Format {
        private static final long serialVersionUID = -1148332471737068969L;

        final boolean parseDate;

        final boolean parseTime;

        /**
         * Creates a new instance.
         */
        public XSDDateTimeFormat() {
            this(true, true);
        }

        XSDDateTimeFormat(boolean pParseDate, boolean pParseTime) {
            parseDate = pParseDate;
            parseTime = pParseTime;
        }

        private void append(StringBuffer pBuffer, int pNum, int pMinLen) {
            String s = Integer.toString(pNum);
            for (int i = s.length(); i < pMinLen; i++) {
                pBuffer.append('0');
            }
            pBuffer.append(s);
        }

        public StringBuffer format(Object pCalendar, StringBuffer pBuffer, FieldPosition pPos) {
            assert pCalendar != null : "The Calendar argument must not be null.";
            assert pBuffer != null : "The StringBuffer argument must not be null.";
            assert pPos != null : "The FieldPosition argument must not be null.";

            Calendar cal = (Calendar) pCalendar;
            if (parseDate) {
                int year = cal.get(Calendar.YEAR);
                if (year < 0) {
                    pBuffer.append('-');
                    year = -year;
                }
                append(pBuffer, year, 4);
                pBuffer.append('-');
                append(pBuffer, cal.get(Calendar.MONTH) + 1, 2);
                pBuffer.append('-');
                append(pBuffer, cal.get(Calendar.DAY_OF_MONTH), 2);
                if (parseTime) {
                    pBuffer.append('T');
                }
            }
            if (parseTime) {
                append(pBuffer, cal.get(Calendar.HOUR_OF_DAY), 2);
                pBuffer.append(':');
                append(pBuffer, cal.get(Calendar.MINUTE), 2);
                pBuffer.append(':');
                append(pBuffer, cal.get(Calendar.SECOND), 2);
                int millis = cal.get(Calendar.MILLISECOND);
                if (millis > 0) {
                    pBuffer.append('.');
                    append(pBuffer, millis, 3);
                }
            }
            TimeZone tz = cal.getTimeZone();
            // JDK 1.4: int offset = tz.getOffset(cal.getTimeInMillis());
            int offset = cal.get(Calendar.ZONE_OFFSET);
            if (tz.inDaylightTime(cal.getTime())) {
                offset += cal.get(Calendar.DST_OFFSET);
            }
            if (offset == 0) {
                pBuffer.append('Z');
            } else {
                if (offset < 0) {
                    pBuffer.append('-');
                    offset = -offset;
                } else {
                    pBuffer.append('+');
                }
                int minutes = offset / (60 * 1000);
                int hours = minutes / 60;
                minutes -= hours * 60;
                append(pBuffer, hours, 2);
                pBuffer.append(':');
                append(pBuffer, minutes, 2);
            }
            return pBuffer;
        }

        private int parseInt(String pString, int offset, StringBuffer pDigits) {
            int length = pString.length();
            int pOffset = offset;
            pDigits.setLength(0);
            while (pOffset < length) {
                char c = pString.charAt(pOffset);
                if (Character.isDigit(c)) {
                    pDigits.append(c);
                    ++pOffset;
                } else {
                    break;
                }
            }
            return pOffset;
        }

        public Object parseObject(String pString, ParsePosition pParsePosition) {
            assert pString != null : "The String argument must not be null.";
            assert pParsePosition != null : "The ParsePosition argument must not be null.";
            int offset = pParsePosition.getIndex();
            int length = pString.length();

            boolean isMinus = false;
            StringBuffer digits = new StringBuffer();
            int year = 0;
            int month = 0;
            int mday = 0;
            if (parseDate) {
                // Sign
                if (offset < length) {
                    char c = pString.charAt(offset);
                    if (c == '+') {
                        ++offset;
                    } else if (c == '-') {
                        ++offset;
                        isMinus = true;
                    }
                }

                offset = parseInt(pString, offset, digits);
                if (digits.length() < 4) {
                    pParsePosition.setErrorIndex(offset);
                    return null;
                }
                year = Integer.parseInt(digits.toString());

                if (offset < length && pString.charAt(offset) == '-') {
                    ++offset;
                } else {
                    pParsePosition.setErrorIndex(offset);
                    return null;
                }

                offset = parseInt(pString, offset, digits);
                if (digits.length() != 2) {
                    pParsePosition.setErrorIndex(offset);
                    return null;
                }
                month = Integer.parseInt(digits.toString());

                if (offset < length && pString.charAt(offset) == '-') {
                    ++offset;
                } else {
                    pParsePosition.setErrorIndex(offset);
                    return null;
                }

                offset = parseInt(pString, offset, digits);
                if (digits.length() != 2) {
                    pParsePosition.setErrorIndex(offset);
                    return null;
                }
                mday = Integer.parseInt(digits.toString());

                if (parseTime) {
                    if (offset < length && pString.charAt(offset) == 'T') {
                        ++offset;
                    } else {
                        pParsePosition.setErrorIndex(offset);
                        return null;
                    }
                }
            } else {
                year = month = mday = 0;
            }

            int hour = 0;
            int minute = 0;
            int second = 0;
            int millis = 0;
            if (parseTime) {
                offset = parseInt(pString, offset, digits);
                if (digits.length() != 2) {
                    pParsePosition.setErrorIndex(offset);
                    return null;
                }
                hour = Integer.parseInt(digits.toString());

                if (offset < length && pString.charAt(offset) == ':') {
                    ++offset;
                } else {
                    pParsePosition.setErrorIndex(offset);
                    return null;
                }

                offset = parseInt(pString, offset, digits);
                if (digits.length() != 2) {
                    pParsePosition.setErrorIndex(offset);
                    return null;
                }
                minute = Integer.parseInt(digits.toString());

                if (offset < length && pString.charAt(offset) == ':') {
                    ++offset;
                } else {
                    pParsePosition.setErrorIndex(offset);
                    return null;
                }

                offset = parseInt(pString, offset, digits);
                if (digits.length() != 2) {
                    pParsePosition.setErrorIndex(offset);
                    return null;
                }
                second = Integer.parseInt(digits.toString());

                if (offset < length && pString.charAt(offset) == '.') {
                    ++offset;
                    offset = parseInt(pString, offset, digits);
                    if (digits.length() > 0) {
                        millis = Integer.parseInt(digits.toString());
                    } else {
                        millis = 0;
                    }
                } else {
                    millis = 0;
                }
            } else {
                hour = minute = second = millis = 0;
            }

            digits.setLength(0);
            digits.append("GMT");
            if (offset < length) {
                char c = pString.charAt(offset);
                if (c == 'Z') {
                    // Ignore UTC, it is the default
                    ++offset;
                } else if (c == '+' || c == '-') {
                    digits.append(c);
                    ++offset;
                    for (int i = 0; i < 5; i++) {
                        if (offset >= length) {
                            pParsePosition.setErrorIndex(offset);
                            return null;
                        }
                        c = pString.charAt(offset);
                        if ((i != 2 && Character.isDigit(c)) || (i == 2 && c == ':')) {
                            digits.append(c);
                        } else {
                            pParsePosition.setErrorIndex(offset);
                            return null;
                        }
                        ++offset;
                    }
                }
            }

            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(digits.toString()));
            cal.set(isMinus ? -year : year, parseDate ? month - 1 : month, mday, hour, minute, second);
            cal.set(Calendar.MILLISECOND, millis);
            pParsePosition.setIndex(offset);
            return cal;
        }
    }

    public static class XSDTimeFormat extends XSDDateTimeFormat {
        private static final long serialVersionUID = 1346506860724640517L;

        /**
         * Creates a new instance.
         */
        public XSDTimeFormat() {
            super(false, true);
        }
    }

    private static final long MAX_UNSIGNED_INT = (((long) Integer.MAX_VALUE) * 2) + 1;

    private static final int MAX_UNSIGNED_SHORT = Short.MAX_VALUE * 2 + 1;

    public String parseAnySimpleType(String value) {
        return value;
    }

    public byte[] parseBase64Binary(String value) {
        return Base64Binary.decode(value);
    }

    public boolean parseBoolean(String value) {
        return Boolean.valueOf(value).booleanValue();
    }

    public byte parseByte(String value) {
        return Byte.parseByte(value);
    }

    public Calendar parseDate(String value) {
        XSDDateFormat format = new XSDDateFormat();
        ParsePosition pos = new ParsePosition(0);
        Calendar cal = (Calendar) format.parseObject(value, pos);
        if (cal == null) {
            throw new IllegalArgumentException("Failed to parse date " + value + " at:"
                + value.substring(pos.getErrorIndex()));
        }
        return cal;
    }

    public Calendar parseDateTime(String value) {
        XSDDateTimeFormat format = new XSDDateTimeFormat();
        ParsePosition pos = new ParsePosition(0);
        Calendar cal = (Calendar) format.parseObject(value, pos);
        if (cal == null) {
            throw new IllegalArgumentException("Failed to parse dateTime " + value + " at:"
                + value.substring(pos.getErrorIndex()));
        }
        return cal;
    }

    public BigDecimal parseDecimal(String value) {
        return new BigDecimal(value);
    }

    public double parseDouble(String value) {
        if ("INF".equals(value)) {
            return Double.POSITIVE_INFINITY;
        } else if ("-INF".equals(value)) {
            return Double.NEGATIVE_INFINITY;
        } else if ("NaN".equals(value)) {
            return Double.NaN;
        } else {
            return Double.parseDouble(value);
        }
    }

    public Duration parseDuration(String pDuration) {
        try {
            return DatatypeFactory.newInstance().newDuration(pDuration);
        } catch (DatatypeConfigurationException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public float parseFloat(String value) {
        if ("INF".equals(value)) {
            return Float.POSITIVE_INFINITY;
        } else if ("-INF".equals(value)) {
            return Float.NEGATIVE_INFINITY;
        } else if ("NaN".equals(value)) {
            return Float.NaN;
        } else {
            return Float.parseFloat(value);
        }
    }

    public byte[] parseHexBinary(String value) {
        return HexBinary.decode(value);
    }

    public int parseInt(String value) {
        return Integer.parseInt(value);
    }

    public BigInteger parseInteger(String value) {
        return new BigInteger(value);
    }

    public long parseLong(String value) {
        return Long.parseLong(value);
    }

    public QName parseQName(String value, NamespaceContext context) {
        int offset = value.indexOf(':');
        String uri;
        String localName;
        switch (offset) {
            case -1:
                localName = value;
                uri = context.getNamespaceURI("");
                if (uri == null) {
                    // Should not happen, indicates an error in the
                    // NamespaceContext
                    // implementation
                    throw new IllegalArgumentException("The default prefix is not bound.");
                }
                break;
            case 0:
                throw new IllegalArgumentException("Default prefix must be indicated by not using a colon: "
                    + value);
            default:
                String prefix = value.substring(0, offset);
                localName = value.substring(offset + 1);
                uri = context.getNamespaceURI(prefix);
                if (uri == null) {
                    throw new IllegalArgumentException("The prefix " + prefix + " is not bound.");
                }
        }
        return new QName(uri, localName);
    }

    public short parseShort(String value) {
        return Short.parseShort(value);
    }

    public String parseString(String value) {
        return value;
    }

    public Calendar parseTime(String value) {
        XSDTimeFormat format = new XSDTimeFormat();
        ParsePosition pos = new ParsePosition(0);
        Calendar cal = (Calendar) format.parseObject(value, pos);
        if (cal == null) {
            throw new IllegalArgumentException("Failed to parse time " + value + " at:"
                + value.substring(pos.getErrorIndex()));
        }
        return cal;
    }

    public long parseUnsignedInt(String value) {
        long l = Long.parseLong(value);
        if (l < 0) {
            throw new IllegalArgumentException("Failed to parse UnsignedInt " + value
                + ": result is negative");
        }
        if (l > MAX_UNSIGNED_INT) {
            throw new IllegalArgumentException("Failed to parse UnsignedInt " + value
                + ": result exceeds maximum value " + MAX_UNSIGNED_INT);
        }
        return l;
    }

    public int parseUnsignedShort(String value) {
        int i = Integer.parseInt(value);
        if (i < 0) {
            throw new IllegalArgumentException("Failed to parse UnsignedShort " + value
                + ": result is negative");
        }
        if (i > MAX_UNSIGNED_SHORT) {
            throw new IllegalArgumentException("Failed to parse UnsignedShort " + value
                + ": result exceeds maximum value " + MAX_UNSIGNED_SHORT);
        }
        return i;
    }

    public String printAnySimpleType(String value) {
        return value;
    }

    public String printBase64Binary(byte[] value) {
        return Base64Binary.encode(value);
    }

    public String printBoolean(boolean value) {
        return (value ? Boolean.TRUE : Boolean.FALSE).toString();
    }

    public String printByte(byte value) {
        return Byte.toString(value);
    }

    public String printDate(Calendar value) {
        return new XSDDateFormat().format(value);
    }

    public String printDateTime(Calendar value) {
        return new XSDDateTimeFormat().format(value);
    }

    public String printDecimal(BigDecimal value) {
        return value.toString();
    }

    public String printDouble(double value) {
        return Double.toString(value);
    }

    public String printDuration(Duration pDuration) {
        return pDuration.toString();
    }

    public String printFloat(float value) {
        return Float.toString(value);
    }

    public String printHexBinary(byte[] value) {
        return HexBinary.encode(value);
    }

    public String printInt(int value) {
        return Integer.toString(value);
    }

    public String printInteger(BigInteger value) {
        return value.toString();
    }

    public String printLong(long value) {
        return Long.toString(value);
    }

    public String printQName(QName value, NamespaceContext context) {
        String prefix = context.getPrefix(value.getNamespaceURI());
        if (prefix == null) {
            throw new IllegalArgumentException("The namespace URI " + value.getNamespaceURI()
                + " is not bound.");
        } else if (XMLConstants.DEFAULT_NS_PREFIX.equals(prefix)) {
            return value.getLocalPart();
        } else {
            return prefix + ":" + value.getLocalPart();
        }
    }

    public String printShort(short value) {
        return Short.toString(value);
    }

    public String printString(String value) {
        return value;
    }

    public String printTime(Calendar value) {
        return new XSDTimeFormat().format(value);
    }

    public String printUnsignedInt(long value) {
        return Long.toString(value);
    }

    public String printUnsignedShort(int value) {
        return Integer.toString(value);
    }
}
