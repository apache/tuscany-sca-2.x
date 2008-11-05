package org.apache.tuscany.sca.contribution.processor;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

@SuppressWarnings("unused")
public class TuscanyNamespaceContext implements NamespaceContext {

	private Stack<ArrayList<ArrayList<String>>> context = null;

	public TuscanyNamespaceContext(Stack<ArrayList<ArrayList<String>>> context){
		this.context = context;
	}

	public String getNamespaceURI(String prefix) {
		if (prefix == null) {
			throw new IllegalArgumentException();
		}
		return (String) getResult("getNSUri",prefix);
	}

	public String getPrefix(String namespaceURI) {
		if (namespaceURI == null) {
			throw new IllegalArgumentException();
		}
		return (String) getResult("getPrefix",namespaceURI);
	}

	@SuppressWarnings("unchecked")
	public Iterator<String> getPrefixes(String namespaceURI) {
		if (namespaceURI == null) {
			throw new IllegalArgumentException();
		}
		
		Iterator<String> iterator = new Itr<String>((Iterator<String>) getResult("getPrefixes",namespaceURI));
		return iterator;
	}
	
	/*
	 * Generic method to Iterate through the Stack and return required result(s) 
	 */
	private Object getResult(String operation,String arg){
		
		ArrayList<ArrayList<String>> contextList = null;
		Iterator<String> prefItr = null;
		Iterator<String> uriItr = null;
		
		List<String> list = new ArrayList<String>();;
		
		String toCompare = null;
		
		String tempPrefix = null;
		String tempUri = null ;
		
		for(int i  = context.size()-1; i>=0;i--){
			contextList = context.get(i);
			prefItr = ((ArrayList<String>)contextList.get(0)).iterator();
			uriItr = ((ArrayList<String>)contextList.get(1)).iterator();
			for(int j = 0;uriItr.hasNext();j++){
				tempPrefix = (String) prefItr.next();
				tempUri = (String) uriItr.next();
				if(operation.equalsIgnoreCase("getNSUri")){
					toCompare = tempPrefix;
				}
				else if(operation.equalsIgnoreCase("getPrefix")){
					toCompare = tempUri;
				}
				else if(operation.equalsIgnoreCase("getPrefixes")){
					toCompare = tempUri;
				}
				if(toCompare != null && arg.equalsIgnoreCase(toCompare)){
					if(operation.equalsIgnoreCase("getNSUri")){
						return tempUri;
					}
					else if(operation.equalsIgnoreCase("getPrefix")){
						return tempPrefix;
					}
					else if(operation.equalsIgnoreCase("getPrefixes")){
						list.add(tempPrefix);
						
					}
					
				}
			}
		}
		
		if(operation.equalsIgnoreCase("getPrefixes")){
			return list.iterator();
		}
		
		return null;
	}

	/*
	 * Custom implementation of the Iterator interface to override the behavior of the remove() method.
	 * The iterator should not be modifiable and invocation of the remove() method should throw UnsupportedOperationException. 
	 */
	private class Itr<E> implements Iterator<E>{
		Iterator<E> iterator = null;

		Itr(Iterator<E> inputItr){
			this.iterator = inputItr;
		}
		
		public boolean hasNext() {
			return this.iterator.hasNext();
		}

		public E next() {
			return (E) this.iterator.next();
		}

		public void remove() {
			throw new  UnsupportedOperationException();
		}
	} //end of class Itr<E>
	
	
} //end of Class
