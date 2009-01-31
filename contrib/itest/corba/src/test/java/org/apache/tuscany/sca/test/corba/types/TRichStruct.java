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
 * User provided interface representation for RichStruct type.
 */
public final class TRichStruct {

    public TInnerStruct innerStruct;
    public String[] stringSequence;
    public int longField;

    public TRichStruct() {

    }

    public TRichStruct(TInnerStruct a1, String[] a2, int a3) {
        innerStruct = a1;
        stringSequence = a2;
        longField = a3;
    }

}
