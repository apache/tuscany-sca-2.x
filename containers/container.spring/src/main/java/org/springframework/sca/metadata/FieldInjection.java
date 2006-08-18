/*
 * Copyright 2002-2006 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.sca.metadata;

import java.lang.reflect.Field;

import org.osoa.sca.annotations.Reference;

/**
 * 
 * @author Rod Johnson
 *
 */
public class FieldInjection extends Injection {
	
	private final Field field;
	
	public FieldInjection(Field field, String lookupName) {
		super(lookupName);
		this.field = field;
	}
	
	public FieldInjection(Field field) {
		Reference annotation = field.getAnnotation(Reference.class);
		
		this.field = field;
		
		if (annotation == null) {
			//throw new IllegalArgumentException("Field " + field + " not annotated");
			return;
		}
		
		if ("".equals(annotation.name())) {
			setLookupName(field.getName());
		}
		else {
			setLookupName(annotation.name());
		}
	}

	@Override
	protected void injectValue(Object target, Object value) {
		try {
			if (!field.isAccessible()) {
				field.setAccessible(true);
			}
			field.set(target, value);
		} 
		catch (IllegalArgumentException ex) {
			// TODO
			throw new UnsupportedOperationException();
		} 
		catch (IllegalAccessException ex) {
			// TODO
			ex.printStackTrace();
			throw new UnsupportedOperationException();
		}
	}

}
