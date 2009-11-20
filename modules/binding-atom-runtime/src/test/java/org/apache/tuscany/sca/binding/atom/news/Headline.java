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

package org.apache.tuscany.sca.binding.atom.news;

import java.io.Serializable;

public class Headline implements Serializable {
    private static final long serialVersionUID = 2516853242307046775L;

    private String text; 
    private String source; 

    public Headline() { 

    } 

    public Headline(String text, String source) { 
        this.text = text; 
        this.source = text; 
    } 

    public void setText(String text) { 
        this.text = text; 
    } 

    public String getText() { 
        return text; 
    } 

    public void setSoure(String source) { 
        this.source = source; 
    } 

    public String getSource() { 
        return source; 
    } 
}
