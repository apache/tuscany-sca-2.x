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

package sampleasync.impl;

import static java.lang.System.out;

import java.util.concurrent.ExecutionException;

import javax.xml.ws.Response;

import org.oasisopen.sca.annotation.Reference;

import sampleasync.Upper;
import sampleasync.UpperAsyncReference;

/**
 * Sample service interface.
 * 
 * @version $Rev$ $Date$
 */
public class UpperJavaAsyncReferenceImpl implements Upper {
    
    @Reference
    UpperAsyncReference upper;
    
    public String upper(String s) {
        out.println("UpperAsyncReferenceImpl.upper(" + s + ")");
        
        // async poll
        Response<String> response = upper.upperAsync(s);
        
        while (!response.isDone()){
            System.out.println("Waiting for poll");
            try {
                Thread.sleep(500);
            } catch (Exception ex) {
                // do nothing
            }
        }
        
        String result = null;
        
        try {
            result = response.get();
            System.out.println("Async client poll patern: result = " + result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return result;
    }
}
