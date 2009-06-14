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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;

/**
 * Test service implementation that implements a various conditional HTTP
 * methods. For testing, the id==0 items are very old (Date(0)), not modified,
 * and always match ETags and the id==1 items are always brand new (Date()),
 * modified, and never match ETags. Using these ids one can test the
 * LastModified and ETag headers of the requests.
 * 
 * @version $Rev$ $Date$
 */
public class TestBindingCacheImpl {

	/**
	 * Implements the HTTP get method of the collection implementation.
	 * @param id
	 * @return
	 */
	public InputStream get(String id) {
		return new ByteArrayInputStream(
				("<html><body><p>item=" + id + "</body></html>").getBytes());
	}

	/**
	 * Implements the HTTP conditional get method of the collection implementation.
	 * @param id
	 * @return
	 */
	public InputStream conditionalGet(String id, HTTPCacheContext cacheContext)
			throws NotModifiedException, PreconditionFailedException {

		if (cacheContext != null) {
			if (cacheContext.ifModifiedSince) {
				if ((id.equals("1"))
						&& (0 > cacheContext.lastModifiedDate
								.compareTo(new Date())))
					throw new NotModifiedException("item 1 was modified on "
							+ new Date());
			}
			if (cacheContext.ifUnmodifiedSince) {
				if ((id.equals("0"))
						&& (0 > cacheContext.lastModifiedDate
								.compareTo(new Date())))
					throw new PreconditionFailedException(
							"item 0 was modified on " + new Date(0));
			}
			if (cacheContext.ifMatch) {
				if (id.equals("1"))
					throw new PreconditionFailedException(
							"item 1 eTag does not match "
									+ cacheContext.getETag());
			}
			if (cacheContext.ifNoneMatch) {
				if (id.equals("0"))
					throw new PreconditionFailedException(
							"item 0 eTag matches " + cacheContext.getETag());
			}
		}
		return new ByteArrayInputStream(
				("<html><body><p>item=" + id + "</body></html>").getBytes());
	}

	/**
	 * Implements the HTTP delete method of the collection implementation.
	 * @param id
	 * @return
	 */
	public InputStream delete(String id) {
		return new ByteArrayInputStream(
				("<html><body><p>deleted item=" + id + "</body></html>")
						.getBytes());
	}

	/**
	 * Implements the HTTP conditional delete method of the collection implementation.
	 * @param id
	 * @return
	 */
	public InputStream conditionalDelete(String id, HTTPCacheContext cacheContext)
			throws NotModifiedException, PreconditionFailedException {

		if (cacheContext != null) {
			if (cacheContext.ifModifiedSince) {
				if ((id.equals("1"))
						&& (0 > cacheContext.lastModifiedDate
								.compareTo(new Date())))
					throw new NotModifiedException("item 1 was modified on "
							+ new Date());
			}
			if (cacheContext.ifUnmodifiedSince) {
				if ((id.equals("0"))
						&& (0 > cacheContext.lastModifiedDate
								.compareTo(new Date())))
					throw new PreconditionFailedException(
							"item 0 was modified on " + new Date(0));
			}
			if (cacheContext.ifMatch) {
				if (id.equals("1"))
					throw new PreconditionFailedException(
							"item 1 eTag does not match "
									+ cacheContext.getETag());
			}
			if (cacheContext.ifNoneMatch) {
				if (id.equals("0"))
					throw new PreconditionFailedException(
							"item 0 eTag matches " + cacheContext.getETag());
			}
		}
		return new ByteArrayInputStream(
				("<html><body><p>deleted item=" + id + "</body></html>")
						.getBytes());
	}

	/**
	 * Implements the HTTP post method of the collection implementation.
	 * @param id
	 * @return
	 */
	public InputStream post() {
		int id = (new java.util.Random()).nextInt(Integer.MAX_VALUE);
		return new ByteArrayInputStream(
				("<html><body><p>posted item=" + id + "</body></html>")
						.getBytes());
	}

	/**
	 * Implements the HTTP conditional post method of the collection implementation.
	 * @param id
	 * @return
	 */
	public HTTPCacheContext conditionalPost(HTTPCacheContext cacheContext)
			throws NotModifiedException, PreconditionFailedException {
		String id = "" + (new java.util.Random()).nextInt(Integer.MAX_VALUE);

		if (cacheContext != null) {
			if (cacheContext.ifModifiedSince) {
				if (0 >= cacheContext.lastModifiedDate.compareTo(new Date(0)))
					throw new NotModifiedException("item was modified on "
							+ new Date());
			}
			if (cacheContext.ifUnmodifiedSince) {
				if ((0 >= cacheContext.lastModifiedDate.compareTo(new Date(0))))
					throw new PreconditionFailedException(
							"item was modified on " + new Date(0));
			}
			if (cacheContext.ifMatch) {
				if (cacheContext.getETag().equalsIgnoreCase("ETagNoneMatch"))
					throw new PreconditionFailedException(
							"item eTag does not match "
									+ cacheContext.getETag());
			}
			if (cacheContext.ifNoneMatch) {
				if (cacheContext.getETag().equalsIgnoreCase("ETagMatch"))
					throw new PreconditionFailedException("item eTag matches "
							+ cacheContext.getETag());
			}
		}

		// Return the ETag and LastModfied fields by serialize to a byte array
		HTTPCacheContext returnContext = new HTTPCacheContext();
		returnContext.setETag( "ETag" + (new java.util.Random()).nextInt(Integer.MAX_VALUE) );
		returnContext.setLastModified( new Date() );
		return returnContext;
	}

	/**
	 * Implements the HTTP update/put method of the collection implementation.
	 * @param id
	 * @return
	 */
	public InputStream put(String id) {
		return new ByteArrayInputStream(
				("<html><body><p>updated item=" + id + "</body></html>")
						.getBytes());
	}

	/**
	 * Implements the HTTP conditional update/put method of the collection implementation.
	 * @param id
	 * @return
	 */
	public InputStream conditionalPut(String id, HTTPCacheContext cacheContext)
			throws NotModifiedException, PreconditionFailedException {

		if (cacheContext != null) {
			if (cacheContext.ifModifiedSince) {
				if ((id.equals("1"))
						&& (0 > cacheContext.lastModifiedDate
								.compareTo(new Date())))
					throw new NotModifiedException("item 1 was modified on "
							+ new Date());
			}
			if (cacheContext.ifUnmodifiedSince) {
				if ((id.equals("0"))
						&& (0 > cacheContext.lastModifiedDate
								.compareTo(new Date())))
					throw new PreconditionFailedException(
							"item 0 was modified on " + new Date(0));
			}
			if (cacheContext.ifMatch) {
				if (id.equals("1"))
					throw new PreconditionFailedException(
							"item 1 eTag does not match "
									+ cacheContext.getETag());
			}
			if (cacheContext.ifNoneMatch) {
				if (id.equals("0"))
					throw new PreconditionFailedException(
							"item 0 eTag matches " + cacheContext.getETag());
			}
		}
		
		return new ByteArrayInputStream(
				("<html><body><p>updated item=" + id + "</body></html>")
						.getBytes());
	}

}
