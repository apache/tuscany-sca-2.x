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
package org.apache.tuscany.sca.core.invocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.lang.reflect.Constructor;
import java.util.WeakHashMap;

public class SCAProxy extends Proxy 
{
     protected SCAProxy (InvocationHandler handler) {
         super(handler);
     }
     
     // This is a cache containing the proxy class constructor for each business interface.
     // This improves performance compared to calling Proxy.newProxyInstance()
     // every time that a proxy is needed.
     private static WeakHashMap cache = new WeakHashMap<Class, Object>();
     
     public static Object newProxyInstance(ClassLoader classloader, Class aclass[], InvocationHandler invocationhandler)
        throws IllegalArgumentException
    {
        try {
            if(invocationhandler == null)
                throw new NullPointerException();
            // Lookup cached constructor.  aclass[0] is the reference's business interface.
            Constructor proxyCTOR;
            synchronized(cache) {
                proxyCTOR = (Constructor) cache.get(aclass[0]);
            }
            if(proxyCTOR == null) {
                Class proxyClass = getProxyClass(classloader, aclass);
                proxyCTOR = proxyClass.getConstructor(constructorParams);
                synchronized(cache){
                    cache.put(aclass[0],proxyCTOR);
                }
            }
            return proxyCTOR.newInstance(new Object[] { invocationhandler });
        }
        catch(NoSuchMethodException e) {
            throw new InternalError(e.toString());
        }
        catch(IllegalAccessException e) {
            throw new InternalError(e.toString());
        }
        catch (InstantiationException e) {
            throw new InternalError(e.toString());
        }
        catch (InvocationTargetException e) {
            throw new InternalError(e.toString());
        }
    }
    
    private static final Class constructorParams[] = { InvocationHandler.class };

}