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

/**
 * Overview of Apache Tuscany Application Programming Interface.
 *
 * This package contains classes and annotations intended for use by application code.
 * In general, the programming models supported by Apache Tuscany are designed to be
 * non-intrusive. The goal is for application code to be portable to any framework
 * supporting an Inversion of Control style programming model and as such does
 * not require application code to inherit from framework classes or to implement
 * framework specific interfaces. Where additional information is required by the
 * framework, Java annotations are used.
 *
 * <h1>Monitoring Framework</h1>
 *
 * Apache Tuscany provides a monitoring framework that application code can use to
 * send management events to the host's logging framework in a manner that is
 * independent of the actual framework used by the runtime environment. This is
 * the same infrastructure as used by Tuscany itself, allowing events from
 * applications, the Apache Tuscany runtime and the host itself to all be handled
 * by the host's logging infrastructure. These events can be combined in one stream
 * or seperated as supported by the host infrastructure.
 *
 * To use this framework, application code should define a component-specific interface
 * defining the monitoring events that it wishes to send and mark a field, setter or
 * constructor with the {@link org.apache.tuscany.api.annotation.Monitor @Monitor} annotation.
 * The framework will inject a implementation of the monitoring interface that dispatches
 * events to the underlying infrastructure.
 *
 * <pre>
 * public class MyComponent {
 *     private final MyComponentMonitor monitor;
 *
 *     public MyComponent(@Monitor MyComponentMonitor monitor) {
 *         this.monitor = monitor;
 *     }
 *
 *     public void start() {
 *         monitor.started();
 *     }
 *
 *     public void stop() {
 *         monitor.stopped();
 *     }
 *
 *     public interface MyComponentMonitor {
 *         void started();
 *         void stopped();
 *     }
 * }
 * </pre>
 *
 * The {@link org.apache.tuscany.api.annotation.LogLevel @LogLevel} annotation
 * can be used to provide a hint for the logging level to associate with the event.
 * The actual level used is determined by the configuration of the monitoring framework.
 *
 * For performance reasons, the objects passed as parameters should typically be instances
 * that are already in use by the application code; operations that allocate new objects
 * (such as string concatenation) should be avoided.
 *
 * <h1></h1> 
 */
package org.apache.tuscany.api;