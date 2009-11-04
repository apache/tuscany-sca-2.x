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
package helloworldrest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.oasisopen.sca.annotation.Scope;
import org.oasisopen.sca.annotation.Service;

@Service(HelloWorldService.class)
@Scope("Composite")
@Path("/helloworld")
public class HelloWorldServiceImpl implements HelloWorldService {

    private String name = new String("original!");

    @Path("/setname")
    @PUT
    @Consumes("text/plain")
    public void setName(String name) {
        this.name = name;

    }

    //http://<host>:<port>/helloworld-rest-webapp/HelloWorldService/helloworld/getname
    @Path("/getname")
    @GET
    @Produces("text/plain")
    public String getName() {
        return this.name;
    }

    @POST
    @Path("/postoperation/{name}/")
    @Consumes("text/plain")
    public void postOperationTest(@PathParam("name") String name) {
        this.name = name;
    }

}
