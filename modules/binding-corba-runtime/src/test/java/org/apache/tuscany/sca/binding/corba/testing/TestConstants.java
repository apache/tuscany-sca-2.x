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

package org.apache.tuscany.sca.binding.corba.testing;

/**
 * Some constants and utilities for module tests
 * @version $Rev$ $Date$
 */
public class TestConstants {

    public static final String STR_1 = "Hello world!";
    public static final String STR_2 = "2nd string";
    public static final String STR_3 = "Other string";
    public static final String[] STR_ARR_1 = {"Hello", "World"};
    public static final String[] STR_ARR_2 = {"Another", "string", "array"};
    public static final int INT_1 = 0;

    public static final int TEST1_PORT = 11100;
    public static final String TEST1_HOST = "localhost";

    public static final int TEST2_PORT = 11101;
    public static final String TEST2_HOST = "localhost";
    
    public static final int[][] INT_ARRAY_2_DIM = { {1, 2}, {3, 4}};
    public static final int[][][] INT_ARRAY_3_DIM = { { {1, 2}, {3, 4}}, { {5, 6}, {7, 8}}};

    public static final long TNAMESERV_SPAWN_WAIT = 300;

    /**
     * Tests if given 2D arrays values are equal
     * @param arr1
     * @param arr2
     * @return
     */
    public static boolean are2DimArraysEqual(int[][] arr1, int[][] arr2) {
        int sum = 0;
        int expSum = 0;
        for (int i = 0; i < arr1.length; i++) {
            for (int j = 0; j < arr1[i].length; j++) {
                expSum++;
                if (arr1[i][j] == arr2[i][j]) {
                    sum++;
                }
            }
        }
        return sum == expSum;
    }

    /**
     * Tests if given 3D arrays values are equal
     * @param arr1
     * @param arr2
     * @return
     */
    public static boolean are3DimArraysEqual(int[][][] arr1, int[][][] arr2) {
        int sum = 0;
        int expSum = 0;
        for (int i = 0; i < arr1.length; i++) {
            for (int j = 0; j < arr1[i].length; j++) {
                for (int k = 0; k < arr1[i][j].length; k++) {
                    expSum++;
                    if (arr1[i][j][k] == arr2[i][j][k]) {
                        sum++;
                    }
                }
            }
        }
        return sum == expSum;
    }
}
