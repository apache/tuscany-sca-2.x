package org.apache.tuscany.sca.binding.corba.impl.reference;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.binding.corba.types.TypeTree;
import org.apache.tuscany.sca.binding.corba.types.TypeTreeCreator;
import org.apache.tuscany.sca.binding.corba.types.util.TypeHelpersProxy;
import org.omg.CORBA.Object;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.OutputStream;

/**
 * Represents single CORBA request
 */
public class DynaCorbaRequest {

	private TypeTree returnTree;
	private List<TypeTree> arguments = new ArrayList<TypeTree>();
	private OutputStream outputStream;
	private ObjectImpl remoteObject;

	/**
	 * Creates request.
	 * 
	 * @param remoteObject
	 *            remote object reference
	 * @param operation
	 *            operation to invoke
	 */
	public DynaCorbaRequest(Object remoteObject, String operation) {
		outputStream = ((ObjectImpl) remoteObject)._request(operation, true);
		this.remoteObject = (ObjectImpl) remoteObject;

	}

	/**
	 * Adds operation argument
	 * 
	 * @param argument
	 */
	public void addArgument(java.lang.Object argument) {
		TypeTree tree = TypeTreeCreator.createTypeTree(argument.getClass());
		TypeHelpersProxy.write(tree.getRootNode(), outputStream, argument);
	}

	/**
	 * Sets return type for operation
	 * 
	 * @param forClass
	 */
	public void setOutputType(Class<?> forClass) {
		returnTree = TypeTreeCreator.createTypeTree(forClass);
	}

	/**
	 * Invokes previously configured request
	 * 
	 * @return
	 */
	public DynaCorbaResponse invoke() {
		DynaCorbaResponse response = new DynaCorbaResponse();
		try {
			InputStream is = remoteObject._invoke(outputStream);
			if (is != null) {
				response.setContent(TypeHelpersProxy.read(returnTree
						.getRootNode(), is));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;
	}

}
