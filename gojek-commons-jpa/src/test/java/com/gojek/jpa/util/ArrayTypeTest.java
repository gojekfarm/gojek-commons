/**
 *
 */
package com.gojek.jpa.util;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class ArrayTypeTest {

	private ArrayType arrayType;
	
	@BeforeMethod
	public void setup() {
		arrayType = new ArrayType();
	}

	@Test
	public void shouldGetArrayFromResultSet() throws HibernateException, SQLException {
		String[] expected = new String[] {"test1", "test2"};
		ResultSet rs = mock(ResultSet.class);
		Array array = mock(Array.class);
		when(array.getArray()).thenReturn(expected);
		when(rs.getArray("some_name")).thenReturn(array);
		assertEquals(arrayType.nullSafeGet(rs, new String[] {"some_name"}, mock(SharedSessionContractImplementor.class), null), expected);
	}
	
	@Test
	public void shouldReturnNullIfResultFieldIsNull() throws HibernateException, SQLException {
		ResultSet rs = mock(ResultSet.class);
		when(rs.getArray("some_name")).thenReturn(null);
		assertNull(arrayType.nullSafeGet(rs, new String[] {"some_name"}, mock(SharedSessionContractImplementor.class), null));
	}
	
	@Test
	public void shouldReturnNullIfResultArrayIsNull() throws HibernateException, SQLException {
		ResultSet rs = mock(ResultSet.class);
		Array array = mock(Array.class);
		when(array.getArray()).thenReturn(null);
		when(rs.getArray("some_name")).thenReturn(array);
		assertNull(arrayType.nullSafeGet(rs, new String[] {"some_name"}, mock(SharedSessionContractImplementor.class), null));
	}
	
	@Test
	public void shouldSetArrayToStatement() throws HibernateException, SQLException {
		String[] value = new String[] {"test1", "test2"};
		SharedSessionContractImplementor session = mock(SharedSessionContractImplementor.class);
		Connection connection = mock(Connection.class);
		Array array = mock(Array.class);
		when(connection.createArrayOf("text", value)).thenReturn(array);
		when(session.connection()).thenReturn(connection);
		PreparedStatement st = mock(PreparedStatement.class);
		arrayType.nullSafeSet(st, value, 0, session);
		verify(st).setArray(0, array);
	}
	
	@Test
	public void shouldSetNullArrayToStatement() throws HibernateException, SQLException {
		SharedSessionContractImplementor session = mock(SharedSessionContractImplementor.class);
		PreparedStatement st = mock(PreparedStatement.class);
		arrayType.nullSafeSet(st, null, 0, session);
		verify(st).setNull(0, Types.ARRAY);
	}
}
