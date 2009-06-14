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

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

/**
 * A class to store cache settings for Atom and HTTP requests and responses.
 * 
 * Predicates are statements that work in conjunction with
 * ETags and LastModified dates to determine if a precondition
 * or postcondition is satisfied.
 * See HTTP specification for how predicates wrk:
 * http://tools.ietf.org/html/rfc2616
 * Example predicates in HTTP include If-Match, If-None-Match,
 * If-Modified-Since, If-Unmodified-Since, If-Range.

 */
public class HTTPCacheContext {
    public static final SimpleDateFormat RFC822DateFormat = new SimpleDateFormat( "EEE, dd MMM yyyy HH:mm:ss Z" ); // RFC 822 date time
    
    public boolean enabled;
	public String eTag;
	public String lastModified;
	public Date lastModifiedDate;
	public boolean ifModifiedSince;
	public boolean ifUnmodifiedSince;
	public boolean ifMatch;
	public boolean ifNoneMatch;
	public boolean ifRange;

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
		enabled = true;
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
		enabled = true;
	}
	
	/**
	 * @param lastModified the lastModified to set
	 */
	public void setLastModified(Date updated) {
		this.lastModified = RFC822DateFormat.format( updated );
  		lastModifiedDate = updated;
		enabled = true;
	}
	
	/**
	 * @return the ifModifedSince
	 */
	public boolean isIfModifiedSince() {
		return ifModifiedSince;
	}
	/**
	 * @param ifModifedSince the ifModifedSince to set
	 */
	public void setIfModifiedSince(boolean ifModifiedSince) {
		this.ifModifiedSince = ifModifiedSince;
		if ( ifModifiedSince )
			enabled = true;
	}
	/**
	 * @return the ifUnModifiedSince
	 */
	public boolean isIfUnmodifiedSince() {
		return ifUnmodifiedSince;
	}
	/**
	 * @param ifUnModifiedSince the ifUnModifiedSince to set
	 */
	public void setIfUnmodifiedSince(boolean ifUnmodifiedSince) {
		this.ifUnmodifiedSince = ifUnmodifiedSince;
		if ( ifUnmodifiedSince )
			enabled = true;
	}
	/**
	 * @return the ifMatch
	 */
	public boolean isIfMatch() {
		return ifMatch;
	}
	/**
	 * @param ifMatch the ifMatch to set
	 */
	public void setIfMatch(boolean ifMatch) {
		this.ifMatch = ifMatch;
		if ( ifMatch )
			enabled = true;
	}
	/**
	 * @return the ifNoneMatch
	 */
	public boolean isIfNoneMatch() {
		return ifNoneMatch;
	}
	/**
	 * @param ifNoneMatch the ifNoneMatch to set
	 */
	public void setIfNoneMatch(boolean ifNoneMatch) {
		this.ifNoneMatch = ifNoneMatch;
		if ( ifNoneMatch )
			enabled = true;
	}
	/**
	 * @return the ifRange
	 */
	public boolean isIfRange() {
		return ifRange;
	}
	/**
	 * @param ifRange the ifRange to set
	 */
	public void setIfRange(boolean ifRange) {
		this.ifRange = ifRange;
		if ( ifRange )
			enabled = true;
	}

    public String toString() {
    	final String PREDPREFIX = ", predicates=";
		StringBuffer sb = new StringBuffer(PREDPREFIX);
		if ( ifMatch || ifNoneMatch || ifModifiedSince || ifUnmodifiedSince || ifRange ) {
			if ( ifMatch ) {
				if ( sb.length() > PREDPREFIX.length() ) sb.append( ", ");
				sb.append("If-Match");
			}
			if ( ifNoneMatch ) {
				if ( sb.length() > PREDPREFIX.length() ) sb.append( ", ");
				sb.append("If-None-Match");
			}
			if ( ifModifiedSince ) {
				if ( sb.length() > PREDPREFIX.length() ) sb.append( ", ");
				sb.append("If-Modified-Since");
			}
			if ( ifUnmodifiedSince ) {
				if ( sb.length() > PREDPREFIX.length() ) sb.append( ", ");
				sb.append("If-UnModified-Since");
			}
			if ( ifRange ) {
				if ( sb.length() > PREDPREFIX.length() ) sb.append( ", ");
				sb.append("If-Range");
			}
		} else {
			sb.append("null");
		}

		return "eTag=" + eTag + ", lastModified=" + lastModified
				+ sb.toString();
	}

    /**
     * Gets the cache context information (ETag, LastModified, predicates) from the Http request.
     * @param request
     * @return
     */
    public static HTTPCacheContext getCacheContextFromRequest( HttpServletRequest request ) throws java.text.ParseException {
    	HTTPCacheContext context = new HTTPCacheContext();
    	
    	String eTag = request.getHeader( "If-Match" );    	
    	if ( eTag != null ) {
    	   context.setETag( eTag );
    	   context.setIfMatch( true );
    	}
    	eTag = request.getHeader( "If-None-Match" );    	
    	if ( eTag != null ) {
    	   context.setETag( eTag );
    	   context.setIfNoneMatch( true );
    	}
        String lastModifiedString = request.getHeader( "If-Modified-Since" );        
    	if ( lastModifiedString != null ) {
     	   context.setLastModified( lastModifiedString );
    	   context.setIfModifiedSince( true );
     	}
        lastModifiedString = request.getHeader( "If-Unmodified-Since" );        
    	if ( lastModifiedString != null ) {
     	   context.setLastModified( lastModifiedString );
    	   context.setIfUnmodifiedSince( true );
     	}
        lastModifiedString = request.getHeader( "If-Range" );        
    	if ( lastModifiedString != null ) {
     	   context.setLastModified( lastModifiedString );
    	   context.setIfRange( true );
     	}
    	return context;
    }
	/**
	 * Enabled is true whenever ETag, LastModified, or predicate is set.
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}
	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
