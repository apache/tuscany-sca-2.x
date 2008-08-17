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

import org.apache.tuscany.sca.binding.corba.meta.CorbaUnionElement;
import org.apache.tuscany.sca.binding.corba.meta.CorbaUnionElementType;

public final class InnerUnion {

    @CorbaUnionElement(type = CorbaUnionElementType.option, optionNumber = 1)
    private int x;
   
    @CorbaUnionElement(type = CorbaUnionElementType.option, optionNumber = 2)
    private float y;
    
    @CorbaUnionElement(type = CorbaUnionElementType.discriminator)
    @SuppressWarnings("unused")
    private int discriminator;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        discriminator = 1;
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        discriminator = 2;
        this.y = y;
    }

}
