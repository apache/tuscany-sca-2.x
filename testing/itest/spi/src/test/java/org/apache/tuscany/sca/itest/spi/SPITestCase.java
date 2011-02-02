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

package org.apache.tuscany.sca.itest.spi;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class SPITestCase {

    
    @Before
    public void setUp() throws Exception {   
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void diffSPIClasses(){
        File spiSafeFile = new File("spi-safe.txt");
        File spiFile = new File("spi.txt");
        
        String spiSafeMD5 = null;
        String spiMD5 = null;
        
        try {
            spiSafeMD5 = getMD5Digest(spiSafeFile);
            spiMD5 = getMD5Digest(spiFile);
        } catch(Exception ex) {
            ex.printStackTrace();
            Assert.fail(ex.toString());
        }
        
        System.out.println("SPI Safe MD5 = " + spiSafeMD5);
        System.out.println("SPI MD5      = " + spiMD5);
        
        Assert.assertEquals(spiSafeMD5, spiMD5);
    }
    
    private String getMD5Digest(File file) 
        throws NoSuchAlgorithmException, 
               FileNotFoundException,
               IOException {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        
        FileInputStream fileInputStream = new FileInputStream(file);
        DataInputStream dataInputStream = new DataInputStream(fileInputStream);
        byte[] fileBytes = new byte[(int)file.length()];
        dataInputStream.readFully(fileBytes);
        messageDigest.update(fileBytes);
        byte[] md5Digest = messageDigest.digest();
        BigInteger m5DigestInteger = new BigInteger(1,md5Digest);
        return m5DigestInteger.toString(16);
    }
}
