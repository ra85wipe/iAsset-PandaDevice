package org.eclipse.contexts;

import org.eclipse.basyx.vab.protocol.http.server.BaSyxContext;

/**
 * BaSyx context that contains no infrastructure
 * 
 * @author kuhn
 *
 */
public class BaSyxExamplesContext_Empty extends BaSyxContext {

	
	/**
	 * Version of serialized instance
	 */
	private static final long serialVersionUID = 1L;


	
	/**
	 * Constructor
	 */
	public BaSyxExamplesContext_Empty() {
		// Invoke base constructor to set up Tomcat server in basys.components context
		super("/basys.examples", "");
	}
}

