package org.apache.tuscany.sca.aspectj;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class UserTimingAspect {	
	@Pointcut
    public void timedCall() {
		
    }    
    	    
    @Around("timedCall()")
    public Object timedSection(ProceedingJoinPoint jp) throws Throwable {
        System.out.println("Timing Around timedSection jp=" + jp);
        long start = System.currentTimeMillis();
        try {
            return jp.proceed();
        } finally {
            long end = System.currentTimeMillis();
            System.out.println("Timing Around timedSection Roundtrip is " + (end - start) + "ms for jp.getSignature=" + jp.getSignature());
        }
    }
}
