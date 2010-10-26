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

package org.apache.tuscany.sca.test.corba.types;

/**
 * @version $Rev$ $Date$
 * User provided enum representation for Color type.
 */
public class TColor {

    private int value;

    public static final int _red = 0;
    public static final int _yellow = 1;
    public static final int _green = 2;

    public static final TColor red = new TColor(_red);
    public static final TColor yellow = new TColor(_yellow);
    public static final TColor green = new TColor(_green);

    public int value() {
        return value;
    }

    public static TColor from_int(int value) {
        switch (value) {
            case 0:
                return red;
            case 1:
                return yellow;
            case 2:
                return green;
        }
        return green;
    }

    protected TColor(int value) {
        this.value = value;
    }
    
    public TColor() {
        
    }

}
