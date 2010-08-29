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
package calculator;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.oasisopen.sca.annotation.Remotable;

/**
 * The Calculator service interface.
 */
@Remotable
public interface CalculatorService {

    @GET
    @Path("add/{n1}/{n2}")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    String add(@PathParam("n1") String n1, @PathParam("n2") String n2);

    @GET
    @Path("subtract/{n1}/{n2}")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    String subtract(@PathParam("n1") String n1, @PathParam("n2") String n2);

    @GET
    @Path("multiply/{n1}/{n2}")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    String multiply(@PathParam("n1") String n1, @PathParam("n2") String n2);

    @GET
    @Path("divide/{n1}/{n2}")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    String divide(@PathParam("n1") String n1, @PathParam("n2") String n2);

    @GET
    @Path("calculate/{formula}")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    String calculate(@PathParam("formula") String formula);

}
