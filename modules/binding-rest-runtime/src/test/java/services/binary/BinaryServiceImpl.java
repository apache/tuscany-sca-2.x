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

package services.binary;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.activation.DataSource;

import org.oasisopen.sca.annotation.Scope;

/**
 * 
 */
@Scope("COMPOSITE")
public class BinaryServiceImpl implements BinaryService {
    private byte[] content;
    private int length;

    public void create(DataSource dataSource) throws IOException {
        content = new byte[10240];
        InputStream is = dataSource.getInputStream();
        length = is.read(content);
        System.out.println("Content received: " + length);
    }

    public InputStream get() {
        byte[] bytes = new byte[length];
        System.arraycopy(content, 0, bytes, 0, length);
        System.out.println("Content sent: " + length);
        return new ByteArrayInputStream(bytes);
    }

    public void update(InputStream is) throws IOException {
        length = is.read(content);
        System.out.println("Content updated: " + length);
    }

}
