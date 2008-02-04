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
package sample;

import junit.framework.Assert;

import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

/**
 * Implementation of a simple service
 */
@Service(C.class)
public class CImpl implements C {

    /**
     * Reference to X
     */
    private X xRef;
    
    /**
     * Reference to Y
     */
    private Y yRef;

    /**
     * Setter for refX
     * 
     * @param x Reference to X
     */
    @Reference(name="refX")
    protected void setX(X x)
    {
        System.out.println("Setting X on CImpl to " + x);
        xRef = x;
    }
    
    /**
     * Setter for refY
     * 
     * @param y Reference to Y
     */
    @Reference(name="refY")
    protected void setY(Y y)
    {
        System.out.println("Setting Y on CImpl to " + y);
        yRef = y;
    }
    
    /**
     * Simple operation that uses the injected references to X and Y
     * 
     * @return "C:cOp() - xResult = " + xRef.xOP() + " yResult = " + yRef.yOp();
     */
    public String cOp() {
        Assert.assertNotNull(xRef);
        Assert.assertNotNull(yRef);
        
        String xResult = xRef.xOp();
        String yResult = yRef.yOp();
        
        return "C:cOp() - xResult = " + xResult + " yResult = " + yResult;
    }
}
