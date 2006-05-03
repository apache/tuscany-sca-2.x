/**
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.binding.axis2.util;

public class ClassLoaderHelper {

    private static ThreadLocal<ClassLoader> applicationClassLoader = new ThreadLocal<ClassLoader>() {
        protected synchronized ClassLoader initialValue() {
            return Thread.currentThread().getContextClassLoader();
        }
    };

    public static void initApplicationClassLoader() {
        applicationClassLoader.set(Thread.currentThread().getContextClassLoader());
    }

    public static ClassLoader setApplicationClassLoader() {
        ClassLoader oldCL = null;
        ClassLoader cl = applicationClassLoader.get();
        if (cl != Thread.currentThread().getContextClassLoader()) {
            oldCL = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(cl);
        }
        return oldCL;
    }

    public static ClassLoader setSystemClassLoader() {
        ClassLoader oldCL = null;
        ClassLoader cl = ClassLoaderHelper.class.getClassLoader();
        if (cl != Thread.currentThread().getContextClassLoader()) {
            oldCL = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(cl);
        }
        return oldCL;
    }
    
    public static void applicationInvoke(Runnable runnable) {
        ClassLoader currentCL = Thread.currentThread().getContextClassLoader();
        ClassLoader appCL = applicationClassLoader.get();
        invoke(runnable, currentCL, appCL);
    }

    public static void systemInvoke(Runnable runnable) {
        ClassLoader currentCL = Thread.currentThread().getContextClassLoader();
        ClassLoader sysCL = ClassLoaderHelper.class.getClassLoader();
        invoke(runnable, currentCL, sysCL);
    }

    private static void invoke(Runnable runnable, ClassLoader old, ClassLoader newCL) {
        try {
            if (newCL != old) {
                Thread.currentThread().setContextClassLoader(newCL);
            }

            runnable.run();

        }finally {
            if (newCL != old) {
                Thread.currentThread().setContextClassLoader(old);
            }
        }
    }
}
