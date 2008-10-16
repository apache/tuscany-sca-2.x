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

package org.apache.tuscany.sca.aspectj;

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * SimpleTraceAspect performs tracing of method signatures, arguments, and
 * return values. All Tuscany methods, constructors, and statics are traced.
 * 
 * @version $Rev$ $Date$
 */
@Aspect
public class SimpleTracingAspect extends TracingAspect {

    @Pointcut("execution(public * org.apache.tuscany.sca..*.*(..))")
    // @Pointcut("call(* org.apache.tuscany.sca..*(..))")
    protected void entry() {
    }

    @Pointcut("within(org.apache.tuscany.sca..*) && !within(org.apache.tuscany.sca.aspectj..*Aspect)")
    protected void withinScope() {
    }

    @Override
    protected void startLog() {
        System.out.println(">>> ----------------------------------------------------");
    }

    @Override
    protected void completeLog() {
        System.out.println("<<< ----------------------------------------------------");
    }

    @Override
    protected void logEnter(JoinPoint jp) {
        System.out.println("> logEnter jp.getSignature=" + jp.getSignature());
        java.lang.Object[] args = jp.getArgs();
        if (( args != null ) && ( args.length > 0 )) {
       	// See http://www.eclipse.org/aspectj/doc/released/progguide/pitfalls-infiniteLoops.html
           // System.out.println("Logging anyMethodCall before jp.getArgs=" + Arrays.asList(args));
           System.out.print("  logEnter jp.getArgs(" + args.length + ")=[" );
           for ( int i = 0; i < args.length; i++ ){
        	  if ( i > 0 ) System.out.print( ",");
        	  System.out.print( args[ i ]);
           }
           System.out.println("]" );
        }
    }

    @Override
    protected void logExit(JoinPoint jp, Object result) {
        // Note that result is null for methods with void return.
        System.out.println("< logExit jp.getSignature=" + jp.getSignature() +", result=" + result );        
    }

    @Override
    protected void logThrowable(JoinPoint jp, Throwable throwable) {
    	while ( throwable.getCause() != null )
    		throwable = throwable.getCause();
        System.out.println("! logThrowable jp.getSignature=" + jp.getSignature() + ", throwable=" + throwable);
        // System.out.println("! logThowable stackTrace=" ); 
        // throwable.printStackTrace( System.out );
    }

}
