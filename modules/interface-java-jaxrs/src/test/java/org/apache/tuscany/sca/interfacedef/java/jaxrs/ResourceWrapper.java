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

package org.apache.tuscany.sca.interfacedef.java.jaxrs;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("myURI")
@Produces({"application/xml", "application/json"})
@Consumes({"application/xml", "application/json"})
public class ResourceWrapper implements Resource {
    public static Resource delegate;

    public ResourceWrapper() {
        super();
    }

    public String get() {
        return delegate.get();
    }

    public void create(long id, String value) {
        delegate.create(id, value);
    }

    public void delete() {
        delegate.delete();
    }

    public void update(String value) {
        delegate.update(value);
    }

    @Override
    public String getList(List<String> names) {
        return delegate.getList(names);
    }

}
