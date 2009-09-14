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
package org.apache.tuscany.sca.binding.jms;

import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

/**
 * This class implements the HelloWorld service.
 */
@Service(ExceptionService.class)
public class ExceptionServiceClient implements ExceptionService {

    private ExceptionService service;

    @Reference
    public void setService(ExceptionService service) {
        this.service = service;
    }

    public void throwChecked() throws CheckedExcpetion {
        service.throwChecked();
    }

    public void throwChecked2Args() throws CheckedExcpetion2Args {
        service.throwChecked2Args();
    }

    public void throwCheckedChained() throws CheckedExcpetionChained {
        service.throwCheckedChained();
    }

    public void throwCheckedNoArgs() throws CheckedExcpetionNoArgs {
        service.throwCheckedNoArgs();
    }

    public void throwUnChecked() {
        service.throwUnChecked();
    }

}
