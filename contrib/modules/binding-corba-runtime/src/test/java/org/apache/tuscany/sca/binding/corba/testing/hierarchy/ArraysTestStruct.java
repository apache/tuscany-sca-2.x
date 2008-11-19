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

package org.apache.tuscany.sca.binding.corba.testing.hierarchy;

import org.apache.tuscany.sca.binding.corba.meta.CorbaArray;

public final class ArraysTestStruct {

    public ArraysTestStruct() {

    }

    public ArraysTestStruct(String[] field1, int[][] field2, float[][][] field3) {
        this.field1 = field1;
        this.field2 = field2;
        this.field3 = field3;
    }

    @CorbaArray( {2})
    public String[] field1;

    @CorbaArray( {2, 4})
    public int[][] field2;

    @CorbaArray( {2, 4, 2})
    public float[][][] field3;

}
