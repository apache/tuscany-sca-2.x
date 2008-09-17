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
package org.apache.tuscany.sca.binding.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;

import org.apache.tuscany.sca.host.embedded.SCADomain;

/**
 * HTTP binding unit tests.
 * 
 * @version $Rev$ $Date$
 */
public class HTTPBindingCacheTestCase extends TestCase {
	// RFC 822 date time
	protected static final SimpleDateFormat dateFormat = new SimpleDateFormat(
			"EEE, dd MMM yyyy HH:mm:ss Z");

	// Request with no predicates in header.
	private static final String REQUEST1 = "{0} /httpbinding/{1} HTTP/1.0\n"
			+ "Host: localhost\n" + "Content-Type: text/xml\n"
			+ "Connection: close\n" + "Content-Length: {2}" + "\n\n{3}";

	// Request with predicates in header
	private static final String REQUEST2 = "{0} /httpbinding/{1} HTTP/1.0\n"
			+ "Host: localhost\n" + "Content-Type: text/xml\n" + "{2}: {3}\n" // predicate (If-Match, If-None-Match, If-Modified-Since, If-NotModified-Since): value (date or ETag)
			+ "Connection: close\n" + "Content-Length: {4}" + "\n\n{5}";

	private static final int HTTP_PORT = 8085;

	private SCADomain scaDomain;

	@Override
	protected void setUp() throws Exception {
		scaDomain = SCADomain.newInstance("testCache.composite");
	}

	@Override
	protected void tearDown() throws Exception {
		scaDomain.close();
	}

	/**
	 * Test invoking a POJO get method implementation using the HTTP binding. 
	 * @throws Exception
	 */
	public void testGet() throws Exception {
		Socket client = new Socket("127.0.0.1", HTTP_PORT);
		OutputStream os = client.getOutputStream();
		int index = 0;
		String content = "";
		String request = MessageFormat.format(REQUEST1, "GET", index, content
				.getBytes().length, content);
		os.write(request.getBytes());
		os.flush();

		String document = read(client);
		assertTrue(document.indexOf("<body><p>item=" + index) != -1);
	}

	/**
	 * Test invoking a conditional method implementation using the HTTP binding. 
	 * @throws Exception
	 */
	public void testConditionalGetIfModifiedNegative() throws Exception {
		Socket client = new Socket("127.0.0.1", HTTP_PORT);
		OutputStream os = client.getOutputStream();
		int index = 0;
		String content = "";
		String request = MessageFormat.format(REQUEST2, "GET", index,
				"If-Modified-Since", dateFormat.format(new Date(0)), content
						.getBytes().length, content);
		os.write(request.getBytes());
		os.flush();

		String document = read(client);
		// Should return item
		assertTrue(document.indexOf("<body><p>item=" + index) != -1);
		// Should return code 304 Not Modified.
		// assertTrue(document.indexOf("HTTP/1.1 304") != -1);
	}

	/**
	 * Test invoking a conditional method implementation using the HTTP binding. 
	 * @throws Exception
	 */
	public void testConditionalGetIfModifiedPositive() throws Exception {
		Socket client = new Socket("127.0.0.1", HTTP_PORT);
		OutputStream os = client.getOutputStream();
		int index = 1;
		String content = "";
		String request = MessageFormat.format(REQUEST2, "GET", index,
				"If-Modified-Since", dateFormat.format(new Date(0)), content
						.getBytes().length, content);
		os.write(request.getBytes());
		os.flush();

		String document = read(client);
		// Should return item
		// assertTrue(document.indexOf("<body><p>item=" + index) != -1);
		// Should return code 304 Not Modified.
		assertTrue(document.indexOf("HTTP/1.1 304") != -1);
	}

	/**
	 * Test invoking a conditional method implementation using the HTTP binding. 
	 * @throws Exception
	 */
	public void testConditionalGetIfUnmodifiedNegative() throws Exception {
		Socket client = new Socket("127.0.0.1", HTTP_PORT);
		OutputStream os = client.getOutputStream();
		int index = 1;
		String content = "";
		String request = MessageFormat.format(REQUEST2, "GET", index,
				"If-Unmodified-Since", dateFormat.format(new Date()), content
						.getBytes().length, content);
		os.write(request.getBytes());
		os.flush();

		String document = read(client);
		// Should return item
		assertTrue(document.indexOf("<body><p>item=" + index) != -1);
		// Should return code 304 Not Modified.
		// assertTrue(document.indexOf("HTTP/1.1 304") != -1);
	}

	/**
	 * Test invoking a conditional method implementation using the HTTP binding. 
	 * @throws Exception
	 */
	public void testConditionalGetIfUnmodifiedPositive() throws Exception {
		Socket client = new Socket("127.0.0.1", HTTP_PORT);
		OutputStream os = client.getOutputStream();
		int index = 0;
		String content = "";
		String request = MessageFormat.format(REQUEST2, "GET", index,
				"If-Unmodified-Since", dateFormat.format(new Date(0)), content
						.getBytes().length, content);
		os.write(request.getBytes());
		os.flush();

		String document = read(client);
		// Should return item
		// assertTrue(document.indexOf("<body><p>item=" + index) != -1);
		// Should return code 412 PreconditionFailed.
		assertTrue(document.indexOf("HTTP/1.1 412") != -1);
	}

	/**
	 * Test invoking a conditional method implementation using the HTTP binding. 
	 * @throws Exception
	 */
	public void testConditionalGetIfMatchNegative() throws Exception {
		Socket client = new Socket("127.0.0.1", HTTP_PORT);
		OutputStream os = client.getOutputStream();
		int index = 1;
		String content = "";
		String request = MessageFormat.format(REQUEST2, "GET", index,
				"If-Match", "eTagXXX", content.getBytes().length, content);
		os.write(request.getBytes());
		os.flush();

		String document = read(client);
		// Should return item
		// assertTrue(document.indexOf("<body><p>item=" + index) != -1);
		// Should return code 412 precondition failed.
		assertTrue(document.indexOf("HTTP/1.1 412") != -1);
	}

	/**
	 * Test invoking a conditional method implementation using the HTTP binding. 
	 * @throws Exception
	 */
	public void testConditionalGetIfMatchPositive() throws Exception {
		Socket client = new Socket("127.0.0.1", HTTP_PORT);
		OutputStream os = client.getOutputStream();
		int index = 0;
		String content = "";
		String request = MessageFormat.format(REQUEST2, "GET", index,
				"If-Match", "eTagXXX", content.getBytes().length, content);
		os.write(request.getBytes());
		os.flush();

		String document = read(client);
		// Should return item
		assertTrue(document.indexOf("<body><p>item=" + index) != -1);
		// Should return code 412 PreconditionFailed.
		// assertTrue(document.indexOf("HTTP/1.1 412") != -1);
	}

	/**
	 * Test invoking a conditional method implementation using the HTTP binding. 
	 * @throws Exception
	 */
	public void testConditionalGetIfNoneMatchNegative() throws Exception {
		Socket client = new Socket("127.0.0.1", HTTP_PORT);
		OutputStream os = client.getOutputStream();
		int index = 1;
		String content = "";
		String request = MessageFormat.format(REQUEST2, "GET", index,
				"If-None-Match", "eTagXXX", content.getBytes().length, content);
		os.write(request.getBytes());
		os.flush();

		String document = read(client);
		// Should return item
		assertTrue(document.indexOf("<body><p>item=" + index) != -1);
		// Should return code 412 precondition failed.
		// assertTrue(document.indexOf("HTTP/1.1 412") != -1);
	}

	/**
	 * Test invoking a conditional method implementation using the HTTP binding. 
	 * @throws Exception
	 */
	public void testConditionalGetIfNoneMatchPositive() throws Exception {
		Socket client = new Socket("127.0.0.1", HTTP_PORT);
		OutputStream os = client.getOutputStream();
		int index = 0;
		String content = "";
		String request = MessageFormat.format(REQUEST2, "GET", index,
				"If-None-Match", "eTagXXX", content.getBytes().length, content);
		os.write(request.getBytes());
		os.flush();

		String document = read(client);
		// Should return item
		// assertTrue(document.indexOf("<body><p>item=" + index) != -1);
		// Should return code 412 PreconditionFailed.
		assertTrue(document.indexOf("HTTP/1.1 412") != -1);
	}

	/**
	 * Test invoking a POJO get method implementation using the HTTP binding. 
	 * @throws Exception
	 */
	public void testDelete() throws Exception {
		Socket client = new Socket("127.0.0.1", HTTP_PORT);
		OutputStream os = client.getOutputStream();
		int index = 0;
		String content = "";
		String request = MessageFormat.format(REQUEST1, "DELETE", index,
				content.getBytes().length, content);
		os.write(request.getBytes());
		os.flush();

		String document = read(client);
		assertTrue(document.indexOf("deleted item=" + index) != -1);
	}

	/**
	 * Test invoking a conditional method implementation using the HTTP binding. 
	 * @throws Exception
	 */
	public void testConditionalDeleteIfModifiedNegative() throws Exception {
		Socket client = new Socket("127.0.0.1", HTTP_PORT);
		OutputStream os = client.getOutputStream();
		int index = 0;
		String content = "";
		String request = MessageFormat.format(REQUEST2, "DELETE", index,
				"If-Modified-Since", dateFormat.format(new Date(0)), content
						.getBytes().length, content);
		os.write(request.getBytes());
		os.flush();

		String document = read(client);
		// Should return item
		assertTrue(document.indexOf("deleted item=" + index) != -1);
		// Should return code 304 Not Modified.
		// assertTrue(document.indexOf("HTTP/1.1 304") != -1);
	}

	/**
	 * Test invoking a conditional method implementation using the HTTP binding. 
	 * @throws Exception
	 */
	public void testConditionalDeleteIfModifiedPositive() throws Exception {
		Socket client = new Socket("127.0.0.1", HTTP_PORT);
		OutputStream os = client.getOutputStream();
		int index = 1;
		String content = "";
		String request = MessageFormat.format(REQUEST2, "DELETE", index,
				"If-Modified-Since", dateFormat.format(new Date(0)), content
						.getBytes().length, content);
		os.write(request.getBytes());
		os.flush();

		String document = read(client);
		// Should return item
		// assertTrue(document.indexOf("deleted item=" + index) != -1);
		// Should return code 304 Not Modified.
		assertTrue(document.indexOf("HTTP/1.1 304") != -1);
	}

	/**
	 * Test invoking a conditional method implementation using the HTTP binding. 
	 * @throws Exception
	 */
	public void testConditionalDeleteIfUnmodifiedNegative() throws Exception {
		Socket client = new Socket("127.0.0.1", HTTP_PORT);
		OutputStream os = client.getOutputStream();
		int index = 1;
		String content = "";
		String request = MessageFormat.format(REQUEST2, "DELETE", index,
				"If-Unmodified-Since", dateFormat.format(new Date()), content
						.getBytes().length, content);
		os.write(request.getBytes());
		os.flush();

		String document = read(client);
		// Should return item
		assertTrue(document.indexOf("deleted item=" + index) != -1);
		// Should return code 304 Not Modified.
		// assertTrue(document.indexOf("HTTP/1.1 304") != -1);
	}

	/**
	 * Test invoking a conditional method implementation using the HTTP binding. 
	 * @throws Exception
	 */
	public void testConditionalDeleteIfUnmodifiedPositive() throws Exception {
		Socket client = new Socket("127.0.0.1", HTTP_PORT);
		OutputStream os = client.getOutputStream();
		int index = 0;
		String content = "";
		String request = MessageFormat.format(REQUEST2, "DELETE", index,
				"If-Unmodified-Since", dateFormat.format(new Date(0)), content
						.getBytes().length, content);
		os.write(request.getBytes());
		os.flush();

		String document = read(client);
		// Should return item
		// assertTrue(document.indexOf("deleted item=" + index) != -1);
		// Should return code 412 PreconditionFailed.
		assertTrue(document.indexOf("HTTP/1.1 412") != -1);
	}

	/**
	 * Test invoking a conditional method implementation using the HTTP binding. 
	 * @throws Exception
	 */
	public void testConditionalDeleteIfMatchNegative() throws Exception {
		Socket client = new Socket("127.0.0.1", HTTP_PORT);
		OutputStream os = client.getOutputStream();
		int index = 1;
		String content = "";
		String request = MessageFormat.format(REQUEST2, "DELETE", index,
				"If-Match", "eTagXXX", content.getBytes().length, content);
		os.write(request.getBytes());
		os.flush();

		String document = read(client);
		// Should return item
		// assertTrue(document.indexOf("deleted item=" + index) != -1);
		// Should return code 412 precondition failed.
		assertTrue(document.indexOf("HTTP/1.1 412") != -1);
	}

	/**
	 * Test invoking a conditional method implementation using the HTTP binding. 
	 * @throws Exception
	 */
	public void testConditionalDeleteIfMatchPositive() throws Exception {
		Socket client = new Socket("127.0.0.1", HTTP_PORT);
		OutputStream os = client.getOutputStream();
		int index = 0;
		String content = "";
		String request = MessageFormat.format(REQUEST2, "DELETE", index,
				"If-Match", "eTagXXX", content.getBytes().length, content);
		os.write(request.getBytes());
		os.flush();

		String document = read(client);
		// Should return item
		assertTrue(document.indexOf("deleted item=" + index) != -1);
		// Should return code 412 PreconditionFailed.
		// assertTrue(document.indexOf("HTTP/1.1 412") != -1);
	}

	/**
	 * Test invoking a conditional method implementation using the HTTP binding. 
	 * @throws Exception
	 */
	public void testConditionalDeleteIfNoneMatchNegative() throws Exception {
		Socket client = new Socket("127.0.0.1", HTTP_PORT);
		OutputStream os = client.getOutputStream();
		int index = 1;
		String content = "";
		String request = MessageFormat.format(REQUEST2, "DELETE", index,
				"If-None-Match", "eTagXXX", content.getBytes().length, content);
		os.write(request.getBytes());
		os.flush();

		String document = read(client);
		// Should return item
		assertTrue(document.indexOf("deleted item=" + index) != -1);
		// Should return code 412 precondition failed.
		// assertTrue(document.indexOf("HTTP/1.1 412") != -1);
	}

	/**
	 * Test invoking a conditional method implementation using the HTTP binding. 
	 * @throws Exception
	 */
	public void testConditionalDeleteIfNoneMatchPositive() throws Exception {
		Socket client = new Socket("127.0.0.1", HTTP_PORT);
		OutputStream os = client.getOutputStream();
		int index = 0;
		String content = "";
		String request = MessageFormat.format(REQUEST2, "DELETE", index,
				"If-None-Match", "eTagXXX", content.getBytes().length, content);
		os.write(request.getBytes());
		os.flush();

		String document = read(client);
		// Should return item
		// assertTrue(document.indexOf("deleted item=" + index) != -1);
		// Should return code 412 PreconditionFailed.
		assertTrue(document.indexOf("HTTP/1.1 412") != -1);
	}

	/**
	 * Test invoking a POJO get method implementation using the HTTP binding. 
	 * @throws Exception
	 */
	public void testPost() throws Exception {
		Socket client = new Socket("127.0.0.1", HTTP_PORT);
		OutputStream os = client.getOutputStream();
		int index = 0;
		String content = "";
		String request = MessageFormat.format(REQUEST1, "POST", index, content
				.getBytes().length, content);
		os.write(request.getBytes());
		os.flush();

		String document = read(client);
		assertTrue(document.indexOf("HTTP/1.1 200 OK") != -1);
	}

	/**
	 * Test invoking a conditional method implementation using the HTTP binding. 
	 * @throws Exception
	 */
	public void testConditionalPostIfModifiedNegative() throws Exception {
		Socket client = new Socket("127.0.0.1", HTTP_PORT);
		OutputStream os = client.getOutputStream();
		int index = 0;
		String content = "";
		String request = MessageFormat.format(REQUEST2, "POST", index,
				"If-Modified-Since", dateFormat.format(new Date()), content
						.getBytes().length, content);
		os.write(request.getBytes());
		os.flush();

		String document = read(client);
		// Should return code 200 OK
		// assertTrue(document.indexOf("posted item=" + index) != -1);
		assertTrue(document.indexOf("HTTP/1.1 200 OK") != -1);
		// Should return code 304 Not Modified.
		// assertTrue(document.indexOf("HTTP/1.1 304") != -1);
	}

	/**
	 * Test invoking a conditional method implementation using the HTTP binding. 
	 * @throws Exception
	 */
	public void testConditionalPostIfModifiedPositive() throws Exception {
		Socket client = new Socket("127.0.0.1", HTTP_PORT);
		OutputStream os = client.getOutputStream();
		int index = 1;
		String content = "";
		String request = MessageFormat.format(REQUEST2, "POST", index,
				"If-Modified-Since", dateFormat.format(new Date(0)), content
						.getBytes().length, content);
		os.write(request.getBytes());
		os.flush();

		String document = read(client);
		// Should return item
		// assertTrue(document.indexOf("posted item=" + index) != -1);
		// Should return code 304 Not Modified.
		assertTrue(document.indexOf("HTTP/1.1 304") != -1);
	}

	/**
	 * Test invoking a conditional method implementation using the HTTP binding. 
	 * @throws Exception
	 */
	public void testConditionalPostIfUnmodifiedNegative() throws Exception {
		Socket client = new Socket("127.0.0.1", HTTP_PORT);
		OutputStream os = client.getOutputStream();
		int index = 1;
		String content = "";
		String request = MessageFormat.format(REQUEST2, "POST", index,
				"If-Unmodified-Since", dateFormat.format(new Date()), content
						.getBytes().length, content);
		os.write(request.getBytes());
		os.flush();

		String document = read(client);
		// Should return code 200 OK
		assertTrue(document.indexOf("HTTP/1.1 200 OK") != -1);
		// Should return code 304 Not Modified.
		// assertTrue(document.indexOf("HTTP/1.1 304") != -1);
	}

	/**
	 * Test invoking a conditional method implementation using the HTTP binding. 
	 * @throws Exception
	 */
	public void testConditionalPostIfUnmodifiedPositive() throws Exception {
		Socket client = new Socket("127.0.0.1", HTTP_PORT);
		OutputStream os = client.getOutputStream();
		int index = 0;
		String content = "";
		String request = MessageFormat.format(REQUEST2, "POST", index,
				"If-Unmodified-Since", dateFormat.format(new Date(0)), content
						.getBytes().length, content);
		os.write(request.getBytes());
		os.flush();

		String document = read(client);
		// Should return item
		// assertTrue(document.indexOf("posted item=" + index) != -1);
		// Should return code 412 PreconditionFailed.
		assertTrue(document.indexOf("HTTP/1.1 412") != -1);
	}

	/**
	 * Test invoking a conditional method implementation using the HTTP binding. 
	 * @throws Exception
	 */
	public void testConditionalPostIfMatchNegative() throws Exception {
		Socket client = new Socket("127.0.0.1", HTTP_PORT);
		OutputStream os = client.getOutputStream();
		int index = 1;
		String content = "";
		String request = MessageFormat.format(REQUEST2, "POST", index,
				"If-Match", "eTagMatch", content.getBytes().length, content);
		os.write(request.getBytes());
		os.flush();

		String document = read(client);
		// Should return code 200 OK.
		assertTrue(document.indexOf("HTTP/1.1 200 OK") != -1);
		// Should return code 412 precondition failed.
		// assertTrue(document.indexOf("HTTP/1.1 412") != -1);
	}

	/**
	 * Test invoking a conditional method implementation using the HTTP binding. 
	 * @throws Exception
	 */
	public void testConditionalPostIfMatchPositive() throws Exception {
		Socket client = new Socket("127.0.0.1", HTTP_PORT);
		OutputStream os = client.getOutputStream();
		int index = 0;
		String content = "";
		String request = MessageFormat
				.format(REQUEST2, "POST", index, "If-Match", "eTagNoneMatch",
						content.getBytes().length, content);
		os.write(request.getBytes());
		os.flush();

		String document = read(client);
		// Should return item
		// assertTrue(document.indexOf("posted item=" + index) != -1);
		// Should return code 412 PreconditionFailed.
		assertTrue(document.indexOf("HTTP/1.1 412") != -1);
	}

	/**
	 * Test invoking a conditional method implementation using the HTTP binding. 
	 * @throws Exception
	 */
	public void testConditionalPostIfNoneMatchNegative() throws Exception {
		Socket client = new Socket("127.0.0.1", HTTP_PORT);
		OutputStream os = client.getOutputStream();
		int index = 1;
		String content = "";
		String request = MessageFormat.format(REQUEST2, "POST", index,
				"If-None-Match", "eTagNoneMatch", content.getBytes().length,
				content);
		os.write(request.getBytes());
		os.flush();

		String document = read(client);
		// Should return code 200 OK
		assertTrue(document.indexOf("HTTP/1.1 200 OK") != -1);
		// Should return code 412 precondition failed.
		// assertTrue(document.indexOf("HTTP/1.1 412") != -1);
	}

	/**
	 * Test invoking a conditional method implementation using the HTTP binding. 
	 * @throws Exception
	 */
	public void testConditionalPostIfNoneMatchPositive() throws Exception {
		Socket client = new Socket("127.0.0.1", HTTP_PORT);
		OutputStream os = client.getOutputStream();
		int index = 0;
		String content = "";
		String request = MessageFormat.format(REQUEST2, "POST", index,
				"If-None-Match", "eTagMatch", content.getBytes().length,
				content);
		os.write(request.getBytes());
		os.flush();

		String document = read(client);
		// Should return item
		// assertTrue(document.indexOf("posted item=" + index) != -1);
		// Should return code 412 PreconditionFailed.
		assertTrue(document.indexOf("HTTP/1.1 412") != -1);
	}

	/**
	 * Test invoking a POJO get method implementation using the HTTP binding. 
	 * @throws Exception
	 */
	public void testPut() throws Exception {
		Socket client = new Socket("127.0.0.1", HTTP_PORT);
		OutputStream os = client.getOutputStream();
		int index = 0;
		String content = "";
		String request = MessageFormat.format(REQUEST1, "PUT", index, content
				.getBytes().length, content);
		os.write(request.getBytes());
		os.flush();

		String document = read(client);
		assertTrue(document.indexOf("updated item=" + index) != -1);
	}

	/**
	 * Test invoking a conditional method implementation using the HTTP binding. 
	 * @throws Exception
	 */
	public void testConditionalPutIfModifiedNegative() throws Exception {
		Socket client = new Socket("127.0.0.1", HTTP_PORT);
		OutputStream os = client.getOutputStream();
		int index = 0;
		String content = "";
		String request = MessageFormat.format(REQUEST2, "PUT", index,
				"If-Modified-Since", dateFormat.format(new Date(0)), content
						.getBytes().length, content);
		os.write(request.getBytes());
		os.flush();

		String document = read(client);
		// Should return item
		assertTrue(document.indexOf("updated item=" + index) != -1);
		// Should return code 304 Not Modified.
		// assertTrue(document.indexOf("HTTP/1.1 304") != -1);
	}

	/**
	 * Test invoking a conditional method implementation using the HTTP binding. 
	 * @throws Exception
	 */
	public void testConditionalPutIfModifiedPositive() throws Exception {
		Socket client = new Socket("127.0.0.1", HTTP_PORT);
		OutputStream os = client.getOutputStream();
		int index = 1;
		String content = "";
		String request = MessageFormat.format(REQUEST2, "PUT", index,
				"If-Modified-Since", dateFormat.format(new Date(0)), content
						.getBytes().length, content);
		os.write(request.getBytes());
		os.flush();

		String document = read(client);
		// Should return item
		// assertTrue(document.indexOf("updated item=" + index) != -1);
		// Should return code 304 Not Modified.
		assertTrue(document.indexOf("HTTP/1.1 304") != -1);
	}

	/**
	 * Test invoking a conditional method implementation using the HTTP binding. 
	 * @throws Exception
	 */
	public void testConditionalPutIfUnmodifiedNegative() throws Exception {
		Socket client = new Socket("127.0.0.1", HTTP_PORT);
		OutputStream os = client.getOutputStream();
		int index = 1;
		String content = "";
		String request = MessageFormat.format(REQUEST2, "PUT", index,
				"If-Unmodified-Since", dateFormat.format(new Date()), content
						.getBytes().length, content);
		os.write(request.getBytes());
		os.flush();

		String document = read(client);
		// Should return item
		assertTrue(document.indexOf("updated item=" + index) != -1);
		// Should return code 304 Not Modified.
		// assertTrue(document.indexOf("HTTP/1.1 304") != -1);
	}

	/**
	 * Test invoking a conditional method implementation using the HTTP binding. 
	 * @throws Exception
	 */
	public void testConditionalPutIfUnmodifiedPositive() throws Exception {
		Socket client = new Socket("127.0.0.1", HTTP_PORT);
		OutputStream os = client.getOutputStream();
		int index = 0;
		String content = "";
		String request = MessageFormat.format(REQUEST2, "PUT", index,
				"If-Unmodified-Since", dateFormat.format(new Date(0)), content
						.getBytes().length, content);
		os.write(request.getBytes());
		os.flush();

		String document = read(client);
		// Should return item
		// assertTrue(document.indexOf("updated item=" + index) != -1);
		// Should return code 412 PreconditionFailed.
		assertTrue(document.indexOf("HTTP/1.1 412") != -1);
	}

	/**
	 * Test invoking a conditional method implementation using the HTTP binding. 
	 * @throws Exception
	 */
	public void testConditionalPutIfMatchNegative() throws Exception {
		Socket client = new Socket("127.0.0.1", HTTP_PORT);
		OutputStream os = client.getOutputStream();
		int index = 1;
		String content = "";
		String request = MessageFormat.format(REQUEST2, "PUT", index,
				"If-Match", "eTagXXX", content.getBytes().length, content);
		os.write(request.getBytes());
		os.flush();

		String document = read(client);
		// Should return item
		// assertTrue(document.indexOf("updated item=" + index) != -1);
		// Should return code 412 precondition failed.
		assertTrue(document.indexOf("HTTP/1.1 412") != -1);
	}

	/**
	 * Test invoking a conditional method implementation using the HTTP binding. 
	 * @throws Exception
	 */
	public void testConditionalPutIfMatchPositive() throws Exception {
		Socket client = new Socket("127.0.0.1", HTTP_PORT);
		OutputStream os = client.getOutputStream();
		int index = 0;
		String content = "";
		String request = MessageFormat.format(REQUEST2, "PUT", index,
				"If-Match", "eTagXXX", content.getBytes().length, content);
		os.write(request.getBytes());
		os.flush();

		String document = read(client);
		// Should return item
		assertTrue(document.indexOf("updated item=" + index) != -1);
		// Should return code 412 PreconditionFailed.
		// assertTrue(document.indexOf("HTTP/1.1 412") != -1);
	}

	/**
	 * Test invoking a conditional method implementation using the HTTP binding. 
	 * @throws Exception
	 */
	public void testConditionalPutIfNoneMatchNegative() throws Exception {
		Socket client = new Socket("127.0.0.1", HTTP_PORT);
		OutputStream os = client.getOutputStream();
		int index = 1;
		String content = "";
		String request = MessageFormat.format(REQUEST2, "PUT", index,
				"If-None-Match", "eTagXXX", content.getBytes().length, content);
		os.write(request.getBytes());
		os.flush();

		String document = read(client);
		// Should return item
		assertTrue(document.indexOf("updated item=" + index) != -1);
		// Should return code 412 precondition failed.
		// assertTrue(document.indexOf("HTTP/1.1 412") != -1);
	}

	/**
	 * Test invoking a conditional method implementation using the HTTP binding. 
	 * @throws Exception
	 */
	public void testConditionalPutIfNoneMatchPositive() throws Exception {
		Socket client = new Socket("127.0.0.1", HTTP_PORT);
		OutputStream os = client.getOutputStream();
		int index = 0;
		String content = "";
		String request = MessageFormat.format(REQUEST2, "PUT", index,
				"If-None-Match", "eTagXXX", content.getBytes().length, content);
		os.write(request.getBytes());
		os.flush();

		String document = read(client);
		// Should return item
		// assertTrue(document.indexOf("updated item=" + index) != -1);
		// Should return code 412 PreconditionFailed.
		assertTrue(document.indexOf("HTTP/1.1 412") != -1);
	}

	/**
	 * Read response stream from the given socket.
	 * @param socket
	 * @return
	 * @throws IOException
	 */
	private static String read(Socket socket) throws IOException {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(socket
					.getInputStream()));
			StringBuffer sb = new StringBuffer();
			String str;
			while ((str = reader.readLine()) != null) {
				sb.append(str);
			}
			return sb.toString();
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}
}
