/**
 *
 */
package com.gojek.jpa.util;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.apache.commons.beanutils.ConvertUtils;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

/**
 * @author ganeshs
 *
 */
public class ArrayType implements UserType {

	protected static final int[] SQL_TYPES = { Types.ARRAY };

	/**
	 * Return the SQL type codes for the columns mapped by this type. The codes
	 * are defined on <tt>java.sql.Types</tt>.
	 *
	 * @return int[] the typecodes
	 * @see java.sql.Types
	 */
	public final int[] sqlTypes() {
		return SQL_TYPES;
	}

	/**
	 * The class returned by <tt>nullSafeGet()</tt>.
	 *
	 * @return Class
	 */
	public final Class returnedClass() {
		return Object[].class;
	}

	@Override
	public final Object deepCopy(final Object value) throws HibernateException {
		return value;
	}

	@Override
	public final boolean isMutable() {
		return false;
	}

	@Override
	public final Object assemble(final Serializable arg0, final Object arg1) throws HibernateException {
		return null;
	}

	@Override
	public final Serializable disassemble(final Object arg0) throws HibernateException {
		return null;
	}

	@Override
	public final boolean equals(final Object x, final Object y) throws HibernateException {
		if (x == y) {
			return true;
		} else if (x == null || y == null) {
			return false;
		} else {
			return x.equals(y);
		}
	}

	@Override
	public final int hashCode(final Object x) throws HibernateException {
		return x.hashCode();
	}

	@Override
	public final Object replace(
	    final Object original,
	    final Object target,
	    final Object owner) throws HibernateException {
	    return original;
	}

	@Override
	public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
		if (rs.wasNull()) {
			return null;
		}

		Array array = rs.getArray(names[0]);
		if (array == null) {
			return null;
		}
		Object object = array.getArray();
		if (object == null) {
			return null;
		}
		if (object.getClass().getComponentType() == BigDecimal.class) {
			return ConvertUtils.convert(array, Double[].class);
		} else if (object.getClass().getComponentType() == BigInteger.class) {
			return ConvertUtils.convert(object, Long[].class);
		}
		return object;
	}

	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
		if (value == null) {
			st.setNull(index, SQL_TYPES[0]);
		} else {
			String[] castObject = (String[]) value;
			Array array = session.connection().createArrayOf("text", castObject);
			st.setArray(index, array);
		}
	}
}