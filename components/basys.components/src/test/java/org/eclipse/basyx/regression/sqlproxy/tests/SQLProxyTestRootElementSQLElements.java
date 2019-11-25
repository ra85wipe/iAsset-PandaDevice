package org.eclipse.basyx.regression.sqlproxy.tests;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Map;

import org.eclipse.basyx.tools.sqlproxy.SQLMap;
import org.eclipse.basyx.tools.sqlproxy.SQLRootElement;



/**
 * Test SQL root element implementation, its creation, and dropping
 * 
 * @author kuhn
 *
 */
public class SQLProxyTestRootElementSQLElements {

	
	/**
	 * Store SQL root element reference
	 * - An SQL root element is the main gateway to a SQL database
	 */
	protected SQLRootElement sqlRootElement = null;

	
	
	/**
	 * Test basic operations
	 */
	@Test @SuppressWarnings("unchecked")
	public void test() throws Exception {
		// Create SQL root element
		sqlRootElement = new SQLRootElement(SQLConfig.SQLUSER, SQLConfig.SQLPW,  "//localhost/basyx-map?", "org.postgresql.Driver", "jdbc:postgresql:", "root_el_01");
		// - Create new table in database for root element
		sqlRootElement.createRootTable();

		
		// Create map
		Map<String, Object> rootMap = sqlRootElement.createMap(0);
		// - Create contained collection
		Collection<Object>  contCol = sqlRootElement.createCollection(sqlRootElement.getNextIdentifier());
		// - Create contained map
		Map<String, Object> contMap = sqlRootElement.createMap(sqlRootElement.getNextIdentifier());
		// - Add elements to root map
		rootMap.put("a", 13);
		rootMap.put("b", contCol);
		rootMap.put("c", contMap);
		// - Add elements to contained elements
		contCol.add('a');
		contCol.add('b');
		contCol.add(7);
		contMap.put("1", true);
		contMap.put("2", false);
		
		
		// Check map contents
		assertTrue((int) rootMap.get("a") == 13);
		assertTrue(rootMap.get("b") instanceof Collection);
		assertTrue(rootMap.get("c") instanceof Map);
		
		
		// Access root map via new reference
		Map<String, Object> rootMap2 = new SQLMap(sqlRootElement, 0);
		// - Access contained elements
		Collection<Object>  contCol1 = (Collection<Object>)  rootMap2.get("b");
		Map<String, Object> contMap1 = (Map<String, Object>) rootMap2.get("c");
		// Check sizes
		assertTrue(contCol1.size() == 3);
		assertTrue(contMap1.size() == 2);
		
		
		// Add another contained collection
		contMap.put("3", sqlRootElement.createCollection(sqlRootElement.getNextIdentifier()));
		// - Get contained elements
		Collection<Object> contCol2 = (Collection<Object>) contMap.get("3");
		// - Add elements
		contCol2.add("x");

		
		// Access contained elements
		Collection<Object> contCol3 = (Collection<Object>) contMap.get("3");
		// Check sizes
		assertTrue(contCol3.size() == 1);


		// Drop tables
		sqlRootElement.dropTable(0);
		sqlRootElement.dropTable(1);
		sqlRootElement.dropTable(2);
		sqlRootElement.dropTable(3);

		// Drop table for root element (= delete it)
		sqlRootElement.dropRootTable();
	}
}
