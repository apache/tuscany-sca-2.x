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

package org.apache.tuscany.sca.vtest.javaapi.annotations.property.impl;

import java.util.List;

import org.apache.tuscany.sca.vtest.javaapi.annotations.property.AService;
import org.osoa.sca.annotations.Service;
import org.osoa.sca.annotations.Property;

@Service(AService.class)
public class AServiceImpl implements AService {

	@Property
	protected String p1;			// simple Java type injected via field
	
	@Property
	public String p2;				// simple Java type injected via field

	protected String p3;			// simple Java type injected via setter
	
	public String p4;				// simple Java type injected via setter

	protected String p5;			// simple Java type injected via constructor parameter
	
	public String p6;				// simple Java type injected via constructor parameter

	@Property(required=true)
	protected AObject p7;			// complex Java type injected via field
	
	@Property
	public AObject p8;				// complex Java type injected via field

	protected AObject p9;			// complex Java type injected via setter
	
	public AObject p10;				// complex Java type injected via setter

	protected AObject p11;			// complex Java type injected via constructor parameter
	
	public AObject p12;				// complex Java type injected via constructor parameter

	@Property(name="pFifteen")
	protected String p15;			// injected via field with different name

	protected String p16;			// injected via setter with different name

	@Property(name="p17", required=false)
	public String p17;				// injected via field but not defined in composite

	public String p18;				// injected via setter but not defined in composite

	@Property(name="p19", required=true)
	public List<String> p19;		// a List and injected via field with no element

	public List<String> p20;		// a List and injected via setter

	@Property(required=false)
	public Integer[] p21;			// an array and injected via field
	
    public String getName() {
        return "AService";
    }
    
    public AServiceImpl(
    		@Property(name = "p5") String p5, 
    		@Property(name = "p6") String p6,
    		@Property(name = "p11") AObject p11,
    		@Property(name = "p12") AObject p12
    		) {
    	super();
    	this.p5 = p5;
    	this.p6 = p6;
    	this.p11 = p11;
    	this.p12 = p12;
    }

    @Property
    public void setP3(String p3) {
		this.p3 = p3;
	}

    @Property(required=true)
    public void setP4(String p4) {
		this.p4 = p4;
	}

    @Property
    public void setP9(AObject p9) {
		this.p9 = p9;
	}

    @Property
    public void setP10(AObject p10) {
		this.p10 = p10;
	}
    
    @Property(name="pSixteen")
    public void setP16(String p16) {
		this.p16 = p16;
	}
    
	@Property(name="p18", required=false)
    public void setP18(String p18) {
		this.p18 = p18;
	}
    
	@Property(name="p20", required=true)
	public void setP20(List<String> p20) {
		this.p20 = p20;
	}

	public String getP1() {
		return p1;
	}

	public String getP2() {
		return p2;
	}

	public String getP3() {
		return p3;
	}

	public String getP4() {
		return p4;
	}

	public String getP5() {
		return p5;
	}

	public String getP6() {
		return p6;
	}

	public String getP7AString() {
		return p7.aString;
	}

	public int getP7BInt() {
		return p7.bInt;
	}
	
	public String getP8AString() {
		return p8.aString;
	}

	public int getP8BInt() {
		return p8.bInt;
	}
	
	public String getP9AString() {
		return p9.aString;
	}

	public int getP9BInt() {
		return p9.bInt;
	}
	
	public String getP10AString() {
		return p10.aString;
	}

	public int getP10BInt() {
		return p10.bInt;
	}

	public String getP11AString() {
		return p11.aString;
	}

	public int getP11BInt() {
		return p11.bInt;
	}

	public String getP12AString() {
		return p12.aString;
	}

	public int getP12BInt() {
		return p12.bInt;
	}

	public String getP15() {
		return p15;
	}

	public String getP16() {
		return p16;
	}

	public String getP17() {
		return p17;
	}

	public String getP18() {
		return p18;
	}

	public int getP19Size() {
		return p19.size();
	}

	public String getP20(int i) {
		return p20.get(i);
	}

	public int getP20Size() {
		return p20.size();
	}

	public int getP21(int i) {
		return p21[i].intValue();
	}

	public int getP21Size() {
		return p21.length;
	}

}



