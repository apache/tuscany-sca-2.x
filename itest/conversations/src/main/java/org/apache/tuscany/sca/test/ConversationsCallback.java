/**
 * 
 */
package org.apache.tuscany.sca.test;

import org.osoa.sca.annotations.Remotable; 

/**
 * @author lamodeo
 *
 */
public interface ConversationsCallback {
	
	public void   callBackMessage(String aString);
	public void   callBackIncrement(String aString);
	public void   callBackEndSession();

}
