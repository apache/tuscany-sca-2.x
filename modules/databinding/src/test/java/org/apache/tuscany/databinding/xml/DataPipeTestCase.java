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

package org.apache.tuscany.databinding.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.sca.databinding.impl.DOMHelper;
import org.apache.tuscany.sca.databinding.impl.PipedTransformer;
import org.apache.tuscany.sca.databinding.xml.Node2Writer;
import org.apache.tuscany.sca.databinding.xml.StreamDataPipe;
import org.apache.tuscany.sca.databinding.xml.Writer2ReaderDataPipe;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Test case for DataPipe
 */
public class DataPipeTestCase extends TestCase {

    public final void testStreamPipe() throws IOException {
        byte[] bytes = new byte[] {1, 2, 3};
        StreamDataPipe pipe = new StreamDataPipe();
        Assert.assertSame(OutputStream.class, pipe.getSourceType());
        Assert.assertSame(InputStream.class, pipe.getTargetType());
        OutputStream os = pipe.getSink();
        os.write(bytes);
        byte[] newBytes = new byte[16];
        int count = pipe.getResult().read(newBytes);
        Assert.assertEquals(3, count);
        for (int i = 0; i < bytes.length; i++) {
            Assert.assertEquals(bytes[i], newBytes[i]);
        }
    }

    public final void testWriter2ReaderPipe() throws IOException {
        String str = "ABC";
        Writer2ReaderDataPipe pipe = new Writer2ReaderDataPipe();
        Assert.assertSame(Writer.class, pipe.getSourceType());
        Assert.assertSame(Reader.class, pipe.getTargetType());
        pipe.getSink().write(str);
        char[] buf = new char[16];
        int count = pipe.getResult().read(buf);
        Assert.assertEquals(3, count);
        for (int i = 0; i < str.length(); i++) {
            Assert.assertEquals(str.charAt(i), buf[i]);
        }
    }

    public final void testPiped() throws Exception {
        Node2Writer node2Writer = new Node2Writer();
        Writer2ReaderDataPipe pipe = new Writer2ReaderDataPipe();
        PipedTransformer<Node, Writer, Reader> transformer =
            new PipedTransformer<Node, Writer, Reader>(node2Writer, pipe);
        Document document = DOMHelper.newDocument();
        Element element = document.createElementNS("http://ns1", "root");
        document.appendChild(element);
        Reader reader = transformer.transform(document, null);
        Assert.assertEquals(transformer.getWeight(), node2Writer.getWeight() + pipe.getWeight());
        Assert.assertEquals(transformer.getSourceDataBinding(), node2Writer.getSourceDataBinding());
        Assert.assertEquals(transformer.getTargetDataBinding(), pipe.getTargetDataBinding());
        char[] buf = new char[120];
        int count = reader.read(buf);
        String xml = new String(buf, 0, count);
        Assert.assertTrue(xml.contains("<root xmlns=\"http://ns1\"/>"));
    }

}
