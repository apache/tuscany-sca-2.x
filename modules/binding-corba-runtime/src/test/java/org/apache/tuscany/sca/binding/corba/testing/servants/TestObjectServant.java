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

package org.apache.tuscany.sca.binding.corba.testing.servants;

import org.apache.tuscany.sca.binding.corba.testing.generated.SimpleStruct;
import org.apache.tuscany.sca.binding.corba.testing.generated.SimpleStructHolder;
import org.apache.tuscany.sca.binding.corba.testing.generated.SomeStruct;
import org.apache.tuscany.sca.binding.corba.testing.generated._TestObjectImplBase;
import org.apache.tuscany.sca.binding.corba.testing.generated.long_seq1Holder;
import org.apache.tuscany.sca.binding.corba.testing.generated.long_seq2Holder;
import org.apache.tuscany.sca.binding.corba.testing.generated.long_seq3Holder;

/**
 * @version $Rev$ $Date$
 */
public class TestObjectServant extends _TestObjectImplBase {

    private static final long serialVersionUID = 1L;

    public int[] setLongSeq1(long_seq1Holder arg) {
        return arg.value;
    }

    public int[][] setLongSeq2(long_seq2Holder arg) {

        return arg.value;
    }

    public int[][][] setLongSeq3(long_seq3Holder arg) {
        return arg.value;
    }

    public SimpleStruct setSimpleStruct(SimpleStructHolder arg) {
        return arg.value;
    }

    public SomeStruct setStruct(SomeStruct arg) {
        return arg;
    }

    public SomeStruct pickStructFromArgs(SomeStruct arg1, SomeStruct arg2, SomeStruct arg3, int structNumber) {
        switch (structNumber) {
            case 1:
                return arg1;
            case 2:
                return arg2;
            case 3:
                return arg3;
            default:
                return arg1;
        }
    }

}
