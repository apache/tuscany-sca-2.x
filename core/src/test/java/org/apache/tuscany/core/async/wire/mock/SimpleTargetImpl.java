/**
 *
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
package org.apache.tuscany.core.async.wire.mock;

import java.util.concurrent.CountDownLatch;

public class SimpleTargetImpl implements SimpleTarget {

    private final CountDownLatch startSignal;
    private final CountDownLatch doneSignal;
    
    public SimpleTargetImpl(CountDownLatch startSignal, CountDownLatch doneSignal) {
        this.startSignal = startSignal;
        this.doneSignal = doneSignal;
    }

    public void hello(String message) throws Exception {
        try {
            startSignal.await();
            doneSignal.countDown();
        } catch (InterruptedException ex) {}
    }

    public void goodbye(String message) throws Exception {
        try {
            startSignal.await();
            doneSignal.countDown();
        } catch (InterruptedException ex) {}
    }

    public void echo(String message) throws Exception {
        try {
            startSignal.await();
            doneSignal.countDown();
        } catch (InterruptedException ex) {}
    }


}

