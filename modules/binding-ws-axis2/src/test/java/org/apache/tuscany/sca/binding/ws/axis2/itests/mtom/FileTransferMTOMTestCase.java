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

package org.apache.tuscany.sca.binding.ws.axis2.itests.mtom;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import junit.framework.TestCase;
import org.apache.tuscany.sca.binding.ws.axis2.itests.mtom.FileTransferService;
import org.apache.tuscany.sca.host.embedded.SCADomain;

public class FileTransferMTOMTestCase extends TestCase {

    private SCADomain domain;

    public void testFileTransfer() throws Exception {
        // For testing purpose lets try uploading FileTransferClient.java file.
        DataHandler dataHandler = new DataHandler(new FileDataSource("FileTransferClient.java"));
        FileTransferService filetransfer = domain.getService(FileTransferService.class, "FileTransferServiceComponent");
        assertEquals("File uploaded Sucessfully", filetransfer.uploadFile(dataHandler));
    }

    @Override
    protected void setUp() throws Exception {
        domain = SCADomain.newInstance("org/apache/tuscany/sca/binding/ws/axis2/itests/mtom/filetransferservice.composite");
    }
   
    @Override
    protected void tearDown() throws Exception {
        domain.close();
    }

}
