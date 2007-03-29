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
package org.apache.tuscany.assembly.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A simple print utility class to help print the assembly model.
 *
 *  @version $Rev$ $Date$
 */
public class PrintUtil {
	
	PrintWriter out;
	Set<Object> objs = new HashSet<Object>();
	int level;
	
	public PrintUtil(OutputStream out) {
		this.out = new PrintWriter(new OutputStreamWriter(out), true);
	}
	
	void indent(int level) {
		for (int i=0; i<level; i++) {
			out.print("  ");
		}
	}
	
	public void print(Object obj) {
		if (objs.contains(obj)) {
			indent(level);
			out.println(obj.getClass().getName()+"@"+System.identityHashCode(obj));
		}
		else {
			objs.add(obj);
			try {
				indent(level);
				out.println(obj.getClass().getSimpleName() + " {");
				BeanInfo bi = Introspector.getBeanInfo(obj.getClass());
				for (PropertyDescriptor pd: bi.getPropertyDescriptors()) {
					try {
						Object pv = pd.getReadMethod().invoke(obj);
						if (pv != null) {
							if (pv.getClass().isArray()) {
								pv = Arrays.asList((Object[])pv);
							}
							if (pv instanceof List) {
								if (!((List)pv).isEmpty()) {
									level++;
									indent(level);
									out.println(pd.getName() + "= [");
									for (Object e: (List)pv) {
										level++;
										print(e);
										level--;
									}
									indent(level);
									out.println( " ]");
									level--;
								}
							}
							else {
								Class<?> pvc = pv.getClass();
								if (pvc.isPrimitive() || pvc.getName().startsWith("java.") || pvc.getName().startsWith("javax.") || pvc.isEnum()) {
									if (!pd.getName().equals("class")) {
										if (!(Boolean.FALSE.equals(pv))) {
											indent(level+1);
											out.println(pd.getName() + "=" + pv.toString());
										}
									}
								} else {
									level++;
									indent(level);
									out.println(pd.getName() + "= {" );
									level++;
									print(pv);
									level--;
									indent(level);
									out.println("}");
									level--;
								}
							}
						}
					} catch (Exception e) {}
				}
				indent(level);
				out.println("}");
			} catch (IntrospectionException e) {
				indent(level);
				out.println(e);
			}
		}
	}

}
