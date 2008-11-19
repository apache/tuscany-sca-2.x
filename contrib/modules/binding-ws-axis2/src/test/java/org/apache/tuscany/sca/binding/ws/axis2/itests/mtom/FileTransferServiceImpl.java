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

import org.osoa.sca.annotations.Service;
import javax.activation.DataHandler;
import java.io.File;
import java.io.FileOutputStream;

/**
 * This class implements the HelloWorld service.
 */
@Service(FileTransferService.class)
public class FileTransferServiceImpl implements FileTransferService {

    public String uploadFile(DataHandler attachment) throws Exception {
        
        //OMText binaryNode = (OMText) (attachment.getFirstElement()).getFirstOMChild();
        //DataHandler dataHandler = (DataHandler) binaryNode.getDataHandler();        
        
        // Use this code to save the file we have received.
        /*DataHandler dataHandler = attachment;
        
        File file = new File("transferedfile.java");
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        dataHandler.writeTo(fileOutputStream);        
        fileOutputStream.flush();
        fileOutputStream.close();*/
        
        System.out.println(attachment.getContentType());
        
        return "File uploaded Sucessfully";
    }

}
