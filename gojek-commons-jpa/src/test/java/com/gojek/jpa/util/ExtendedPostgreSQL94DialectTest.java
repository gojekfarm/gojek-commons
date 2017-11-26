/**
 *
 */
package com.gojek.jpa.util;

import static org.testng.Assert.assertEquals;

import java.sql.Types;

import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class ExtendedPostgreSQL94DialectTest {

	@Test
	public void shouldRegisterArrayType() {
		ExtendedPostgreSQL94Dialect dialect = new ExtendedPostgreSQL94Dialect();
		assertEquals(dialect.getHibernateTypeName(Types.ARRAY), ArrayType.class.getName());
	}
}
