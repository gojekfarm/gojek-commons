/**
 *
 */
package com.gojek.jpa.util;

import java.sql.Types;

import org.hibernate.dialect.PostgreSQL95Dialect;

/**
 * @author ganeshs
 *
 */
public class ExtendedPostgreSQL94Dialect extends PostgreSQL95Dialect {

	public ExtendedPostgreSQL94Dialect() {
		this.registerHibernateType(Types.ARRAY, ArrayType.class.getName());
	}
}
