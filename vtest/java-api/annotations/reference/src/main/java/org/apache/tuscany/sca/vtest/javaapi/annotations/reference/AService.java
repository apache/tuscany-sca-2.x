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
package org.apache.tuscany.sca.vtest.javaapi.annotations.reference;


/**
 * Simple Service that uses another Service
 */
public interface AService {

    public String getName();
    public String getB1Name();
    public String getB2Name();
    public String getB3Name();
    public String getB4Name();
    public String getB5Name();
    public String getB6Name();
    public String getB7Name();
    public String getB8Name();
    public String getB9Name();
    public String getB10Name();
    public String getB11Name();
    public String getB12Name();
    public String getB13Name(int i);
    public String getB14Name(int i);
    public String getB15Name(int i);
    
    public int getB13Size();
    public int getB14Size();
    public int getB15Size();
    
    public boolean isB7SetterCalled();
    public boolean isB16Null();
    public boolean isB17Null();
    
    public boolean isB4Null(); 
    public boolean isB5Null();
    public boolean isB6Null();
}
