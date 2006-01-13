/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.common.io.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

/**
 * String encoded with UTF-8
 *
 */
public class UTF8String {
    public static final String UTF8 = "UTF-8";
    private String string;

    /**
     *
     */
    public UTF8String(String str) {
        super();
        this.string = str;
    }

    public UTF8String(byte[] bytes) {
        this(toString(bytes));
    }

    public static String toString(byte[] bytes) {
        try {
            if (bytes == null)
                return null;
            return new String(bytes, UTF8);
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(e.getMessage());
        }
    }

    public static byte[] getBytes(String str) {
        try {
            if (str == null)
                return null;
            return str.getBytes(UTF8);
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(e.getMessage());
        }
    }

    public ByteArrayInputStream getInputStream() {
        return new ByteArrayInputStream(getBytes());
    }

    public static ByteArrayInputStream getInputStream(String str) {
        return new ByteArrayInputStream(getBytes(str));
    }

    public static String toString(ByteArrayOutputStream bos) {
        try {
            return bos.toString(UTF8);
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(e.getMessage());
        }
    }

    public byte[] getBytes() {
        try {
            if (string == null)
                return null;
            return string.getBytes(UTF8);
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(e.getMessage());
        }
    }

    public String toString() {
        return string;
    }

    public int hashCode() {
        return (string == null) ? 0 : string.hashCode();
    }

    public boolean equals(Object object) {
        if (!(object instanceof UTF8String))
            return false;
        UTF8String s = (UTF8String) object;
        if (string == s.string)
            return true;
        if (string == null || s.string == null)
            return false;
        return string.equals(s.string);
    }
}