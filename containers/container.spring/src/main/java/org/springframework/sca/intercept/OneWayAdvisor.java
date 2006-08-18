/*
 * Copyright 2002-2006 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.sca.intercept;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ReflectiveMethodInvocation;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.core.task.TaskExecutor;
import org.springframework.sca.metadata.ServiceMetadata;

/**
 * An AOP Alliance MethodInterceptor, rather than AspectJ aspect, as there's no value in typed pointcuts. Oh, if it were
 * only annotations...
 *
 * @author Rod Johnson
 */

public class OneWayAdvisor extends DefaultPointcutAdvisor {

    private TaskExecutor taskExecutor;

    private final ServiceMetadata smd;

    public OneWayAdvisor(final ServiceMetadata aSmd, TaskExecutor taskExecutor) {
        this.smd = aSmd;
        setPointcut(new StaticMethodMatcherPointcut() {
            public boolean matches(Method method, Class targetClass) {
                for (Method m : smd.getOneWayMethods()) {
                    if (m.getName().equals(method.getName())) {
                        return true;
                    }
                }
                return false;
            }
        });
        setAdvice(new OneWayInterceptor());
        this.taskExecutor = taskExecutor;
    }


    private class OneWayInterceptor implements MethodInterceptor {
        public Object invoke(MethodInvocation mi) throws Throwable {
            try {
                // TODO this is not right
                ReflectiveMethodInvocation rmi = (ReflectiveMethodInvocation) mi;
                final MethodInvocation clone = rmi.invocableClone();
                System.out.println("EXECUTE DEFERRED");
                taskExecutor.execute(new Runnable() {
                    public void run() {
                        try {
                            clone.proceed();
                        } catch (Throwable ex) {
                            // TODO
                            throw new UnsupportedOperationException();
                        }
                    }
                });
            } catch (Throwable t) {
                t.printStackTrace();
            } finally {
                return null;
            }
        }
    }

}
