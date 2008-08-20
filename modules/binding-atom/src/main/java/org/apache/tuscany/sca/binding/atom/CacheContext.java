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

package org.apache.tuscany.sca.binding.atom;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A class to store cache settings for Atom and HTTP requests and responses.
 */
public class CacheContext {
    private static final SimpleDateFormat RFC822DateFormat = new SimpleDateFormat( "EEE, dd MMM yyyy HH:mm:ss Z" ); // RFC 822 date time
	public String eTag;
	public String lastModified;
	public Date lastModifiedDate;
	public String [] predicates;

	/**
	 * An ETag is a unique ID for an item. It changes when
	 * a field in the item or the update date changes.
	 * See HTTP specification for how ETags work:
	 * http://tools.ietf.org/html/rfc2616
	 * @return the eTag
	 */
	public String getETag() {
		return eTag;
	}
	/**
	 * @param tag the eTag to set
	 */
	public void setETag(String tag) {
		eTag = tag;
	}
	/**
	 * The LastModified date is the time the item was last 
	 * changed. See HTTP specification for how ETags work:
	 * http://tools.ietf.org/html/rfc2616
	 * @return the lastModified
	 */
	public String getLastModified() {
		return lastModified;
	}
	/**
	 * The LastModified date is the time the item was last 
	 * changed. See HTTP specification for how ETags work:
	 * http://tools.ietf.org/html/rfc2616
	 * @return the lastModified
	 */
	public Date getLastModifiedAsDate() {
		return lastModifiedDate;
	}
	/**
	 * @param lastModified the lastModified to set
	 */
	public void setLastModified(String lastModified) throws java.text.ParseException {
		this.lastModified = lastModified;
		// Catch date formatting on input to help debugging.
  		lastModifiedDate = RFC822DateFormat.parse( lastModified );
	}
	
	/**
	 * Predicates are statements that work in conjunction with
	 * ETags and LastModified dates to determine if a precondition
	 * or postcondition is satisfied.
	 * See HTTP specification for how predicates wrk:
	 * http://tools.ietf.org/html/rfc2616
	 * Example predicats in HTTP include If-Match, If-None-Match,
	 * If-Modified-Since, If-Unmodified-Since, If-Range.
	 * @return the predicates
	 */
	public String[] getPredicates() {
		return predicates;
	}
	/**
	 * @param predicates the predicates to set
	 */
	public void setPredicates(String[] predicates) {
		this.predicates = predicates;
	}	
	
    public String toString() {
		StringBuffer sb = new StringBuffer(", predicates=");
		if (predicates == null) {
			sb.append("null");
        } else if ( predicates.length == 0 ){
			sb.append("length=0");
		} else { 
			for (int i = 0; i < predicates.length; i++) {
				if (i > 0)
					sb.append(", ");
				sb.append(predicates[i]);
			}
		}

		return "eTag=" + eTag + ", lastModified=" + lastModified
				+ sb.toString();
	}
}
