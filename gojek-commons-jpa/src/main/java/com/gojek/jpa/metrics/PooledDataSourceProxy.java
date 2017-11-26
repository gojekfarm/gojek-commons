/**
 *
 */
package com.gojek.jpa.metrics;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Logger;

import javax.persistence.EntityManagerFactory;

import org.activejpa.jpa.EntityManagerProvider;
import org.hibernate.c3p0.internal.C3P0ConnectionProvider;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.internal.SessionFactoryImpl;

import com.mchange.v2.c3p0.PooledDataSource;

/**
 * NOTE: Lazily get the data source from the entity manager provider. This class is useful when EntityManagerFactory is not intialized at the time of constructing the datasource. 
 *
 * @author ganeshs
 *
 */
public class PooledDataSourceProxy implements PooledDataSource {
	
	private PooledDataSource dataSource;
	
	private EntityManagerProvider provider;
	
	/**
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#close()
	 */
	public void close() throws SQLException {
		getDataSource().close();
	}

	/**
	 * @param arg0
	 * @throws SQLException
	 * @deprecated
	 * @see com.mchange.v2.c3p0.PooledDataSource#close(boolean)
	 */
	public void close(boolean arg0) throws SQLException {
		getDataSource().close(arg0);
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#getAllUsers()
	 */
	public Collection getAllUsers() throws SQLException {
		return getDataSource().getAllUsers();
	}

	/**
	 * @param out
	 * @throws SQLException
	 * @see javax.sql.CommonDataSource#setLogWriter(java.io.PrintWriter)
	 */
	public void setLogWriter(PrintWriter out) throws SQLException {
		getDataSource().setLogWriter(out);
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see javax.sql.DataSource#getConnection()
	 */
	public Connection getConnection() throws SQLException {
		return getDataSource().getConnection();
	}

	/**
	 * @param username
	 * @param password
	 * @return
	 * @throws SQLException
	 * @see javax.sql.DataSource#getConnection(java.lang.String, java.lang.String)
	 */
	public Connection getConnection(String username, String password) throws SQLException {
		return getDataSource().getConnection(username, password);
	}

	/**
	 * @return
	 * @see com.mchange.v2.c3p0.PooledDataSource#getDataSourceName()
	 */
	public String getDataSourceName() {
		return getDataSource().getDataSourceName();
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#getEffectivePropertyCycle(java.lang.String, java.lang.String)
	 */
	public float getEffectivePropertyCycle(String arg0, String arg1) throws SQLException {
		return getDataSource().getEffectivePropertyCycle(arg0, arg1);
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#getEffectivePropertyCycleDefaultUser()
	 */
	public float getEffectivePropertyCycleDefaultUser() throws SQLException {
		return getDataSource().getEffectivePropertyCycleDefaultUser();
	}

	/**
	 * @return
	 * @see com.mchange.v2.c3p0.PooledDataSource#getExtensions()
	 */
	public Map getExtensions() {
		return getDataSource().getExtensions();
	}

	/**
	 * @return
	 * @see com.mchange.v2.c3p0.PooledDataSource#getIdentityToken()
	 */
	public String getIdentityToken() {
		return getDataSource().getIdentityToken();
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#getLastAcquisitionFailure(java.lang.String, java.lang.String)
	 */
	public Throwable getLastAcquisitionFailure(String arg0, String arg1) throws SQLException {
		return getDataSource().getLastAcquisitionFailure(arg0, arg1);
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#getLastAcquisitionFailureDefaultUser()
	 */
	public Throwable getLastAcquisitionFailureDefaultUser() throws SQLException {
		return getDataSource().getLastAcquisitionFailureDefaultUser();
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#getLastCheckinFailure(java.lang.String, java.lang.String)
	 */
	public Throwable getLastCheckinFailure(String arg0, String arg1) throws SQLException {
		return getDataSource().getLastCheckinFailure(arg0, arg1);
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#getLastCheckinFailureDefaultUser()
	 */
	public Throwable getLastCheckinFailureDefaultUser() throws SQLException {
		return getDataSource().getLastCheckinFailureDefaultUser();
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#getLastCheckoutFailure(java.lang.String, java.lang.String)
	 */
	public Throwable getLastCheckoutFailure(String arg0, String arg1) throws SQLException {
		return getDataSource().getLastCheckoutFailure(arg0, arg1);
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#getLastCheckoutFailureDefaultUser()
	 */
	public Throwable getLastCheckoutFailureDefaultUser() throws SQLException {
		return getDataSource().getLastCheckoutFailureDefaultUser();
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#getLastConnectionTestFailure(java.lang.String, java.lang.String)
	 */
	public Throwable getLastConnectionTestFailure(String arg0, String arg1) throws SQLException {
		return getDataSource().getLastConnectionTestFailure(arg0, arg1);
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#getLastConnectionTestFailureDefaultUser()
	 */
	public Throwable getLastConnectionTestFailureDefaultUser() throws SQLException {
		return getDataSource().getLastConnectionTestFailureDefaultUser();
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#getLastIdleTestFailure(java.lang.String, java.lang.String)
	 */
	public Throwable getLastIdleTestFailure(String arg0, String arg1) throws SQLException {
		return getDataSource().getLastIdleTestFailure(arg0, arg1);
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#getLastIdleTestFailureDefaultUser()
	 */
	public Throwable getLastIdleTestFailureDefaultUser() throws SQLException {
		return getDataSource().getLastIdleTestFailureDefaultUser();
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see javax.sql.CommonDataSource#getLogWriter()
	 */
	public PrintWriter getLogWriter() throws SQLException {
		return getDataSource().getLogWriter();
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see javax.sql.CommonDataSource#getLoginTimeout()
	 */
	public int getLoginTimeout() throws SQLException {
		return getDataSource().getLoginTimeout();
	}

	/**
	 * @return
	 * @throws SQLException
	 * @deprecated
	 * @see com.mchange.v2.c3p0.PooledDataSource#getNumBusyConnections()
	 */
	public int getNumBusyConnections() throws SQLException {
		return getDataSource().getNumBusyConnections();
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#getNumBusyConnections(java.lang.String, java.lang.String)
	 */
	public int getNumBusyConnections(String arg0, String arg1) throws SQLException {
		return getDataSource().getNumBusyConnections(arg0, arg1);
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#getNumBusyConnectionsAllUsers()
	 */
	public int getNumBusyConnectionsAllUsers() throws SQLException {
		return getDataSource().getNumBusyConnectionsAllUsers();
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#getNumBusyConnectionsDefaultUser()
	 */
	public int getNumBusyConnectionsDefaultUser() throws SQLException {
		return getDataSource().getNumBusyConnectionsDefaultUser();
	}

	/**
	 * @return
	 * @throws SQLException
	 * @deprecated
	 * @see com.mchange.v2.c3p0.PooledDataSource#getNumConnections()
	 */
	public int getNumConnections() throws SQLException {
		return getDataSource().getNumConnections();
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#getNumConnections(java.lang.String, java.lang.String)
	 */
	public int getNumConnections(String arg0, String arg1) throws SQLException {
		return getDataSource().getNumConnections(arg0, arg1);
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#getNumConnectionsAllUsers()
	 */
	public int getNumConnectionsAllUsers() throws SQLException {
		return getDataSource().getNumConnectionsAllUsers();
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#getNumConnectionsDefaultUser()
	 */
	public int getNumConnectionsDefaultUser() throws SQLException {
		return getDataSource().getNumConnectionsDefaultUser();
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#getNumFailedCheckinsDefaultUser()
	 */
	public long getNumFailedCheckinsDefaultUser() throws SQLException {
		return getDataSource().getNumFailedCheckinsDefaultUser();
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#getNumFailedCheckoutsDefaultUser()
	 */
	public long getNumFailedCheckoutsDefaultUser() throws SQLException {
		return getDataSource().getNumFailedCheckoutsDefaultUser();
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#getNumFailedIdleTestsDefaultUser()
	 */
	public long getNumFailedIdleTestsDefaultUser() throws SQLException {
		return getDataSource().getNumFailedIdleTestsDefaultUser();
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#getNumHelperThreads()
	 */
	public int getNumHelperThreads() throws SQLException {
		return getDataSource().getNumHelperThreads();
	}

	/**
	 * @return
	 * @throws SQLException
	 * @deprecated
	 * @see com.mchange.v2.c3p0.PooledDataSource#getNumIdleConnections()
	 */
	public int getNumIdleConnections() throws SQLException {
		return getDataSource().getNumIdleConnections();
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#getNumIdleConnections(java.lang.String, java.lang.String)
	 */
	public int getNumIdleConnections(String arg0, String arg1) throws SQLException {
		return getDataSource().getNumIdleConnections(arg0, arg1);
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#getNumIdleConnectionsAllUsers()
	 */
	public int getNumIdleConnectionsAllUsers() throws SQLException {
		return getDataSource().getNumIdleConnectionsAllUsers();
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#getNumIdleConnectionsDefaultUser()
	 */
	public int getNumIdleConnectionsDefaultUser() throws SQLException {
		return getDataSource().getNumIdleConnectionsDefaultUser();
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#getNumThreadsAwaitingCheckout(java.lang.String, java.lang.String)
	 */
	public int getNumThreadsAwaitingCheckout(String arg0, String arg1) throws SQLException {
		return getDataSource().getNumThreadsAwaitingCheckout(arg0, arg1);
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#getNumThreadsAwaitingCheckoutDefaultUser()
	 */
	public int getNumThreadsAwaitingCheckoutDefaultUser() throws SQLException {
		return getDataSource().getNumThreadsAwaitingCheckoutDefaultUser();
	}

	/**
	 * @return
	 * @throws SQLException
	 * @deprecated
	 * @see com.mchange.v2.c3p0.PooledDataSource#getNumUnclosedOrphanedConnections()
	 */
	public int getNumUnclosedOrphanedConnections() throws SQLException {
		return getDataSource().getNumUnclosedOrphanedConnections();
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#getNumUnclosedOrphanedConnections(java.lang.String, java.lang.String)
	 */
	public int getNumUnclosedOrphanedConnections(String arg0, String arg1) throws SQLException {
		return getDataSource().getNumUnclosedOrphanedConnections(arg0, arg1);
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#getNumUnclosedOrphanedConnectionsAllUsers()
	 */
	public int getNumUnclosedOrphanedConnectionsAllUsers() throws SQLException {
		return getDataSource().getNumUnclosedOrphanedConnectionsAllUsers();
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#getNumUnclosedOrphanedConnectionsDefaultUser()
	 */
	public int getNumUnclosedOrphanedConnectionsDefaultUser() throws SQLException {
		return getDataSource().getNumUnclosedOrphanedConnectionsDefaultUser();
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#getNumUserPools()
	 */
	public int getNumUserPools() throws SQLException {
		return getDataSource().getNumUserPools();
	}

	/**
	 * @return
	 * @throws SQLFeatureNotSupportedException
	 * @see javax.sql.CommonDataSource#getParentLogger()
	 */
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return getDataSource().getParentLogger();
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#getStartTimeMillisDefaultUser()
	 */
	public long getStartTimeMillisDefaultUser() throws SQLException {
		return getDataSource().getStartTimeMillisDefaultUser();
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#getStatementCacheNumCheckedOut(java.lang.String, java.lang.String)
	 */
	public int getStatementCacheNumCheckedOut(String arg0, String arg1) throws SQLException {
		return getDataSource().getStatementCacheNumCheckedOut(arg0, arg1);
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#getStatementCacheNumCheckedOutDefaultUser()
	 */
	public int getStatementCacheNumCheckedOutDefaultUser() throws SQLException {
		return getDataSource().getStatementCacheNumCheckedOutDefaultUser();
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#getStatementCacheNumCheckedOutStatementsAllUsers()
	 */
	public int getStatementCacheNumCheckedOutStatementsAllUsers() throws SQLException {
		return getDataSource().getStatementCacheNumCheckedOutStatementsAllUsers();
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#getStatementCacheNumConnectionsWithCachedStatements(java.lang.String, java.lang.String)
	 */
	public int getStatementCacheNumConnectionsWithCachedStatements(String arg0, String arg1) throws SQLException {
		return getDataSource().getStatementCacheNumConnectionsWithCachedStatements(arg0, arg1);
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#getStatementCacheNumConnectionsWithCachedStatementsAllUsers()
	 */
	public int getStatementCacheNumConnectionsWithCachedStatementsAllUsers() throws SQLException {
		return getDataSource().getStatementCacheNumConnectionsWithCachedStatementsAllUsers();
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#getStatementCacheNumConnectionsWithCachedStatementsDefaultUser()
	 */
	public int getStatementCacheNumConnectionsWithCachedStatementsDefaultUser() throws SQLException {
		return getDataSource().getStatementCacheNumConnectionsWithCachedStatementsDefaultUser();
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#getStatementCacheNumStatements(java.lang.String, java.lang.String)
	 */
	public int getStatementCacheNumStatements(String arg0, String arg1) throws SQLException {
		return getDataSource().getStatementCacheNumStatements(arg0, arg1);
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#getStatementCacheNumStatementsAllUsers()
	 */
	public int getStatementCacheNumStatementsAllUsers() throws SQLException {
		return getDataSource().getStatementCacheNumStatementsAllUsers();
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#getStatementCacheNumStatementsDefaultUser()
	 */
	public int getStatementCacheNumStatementsDefaultUser() throws SQLException {
		return getDataSource().getStatementCacheNumStatementsDefaultUser();
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#getStatementDestroyerNumActiveThreads()
	 */
	public int getStatementDestroyerNumActiveThreads() throws SQLException {
		return getDataSource().getStatementDestroyerNumActiveThreads();
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#getStatementDestroyerNumConnectionsInUse(java.lang.String, java.lang.String)
	 */
	public int getStatementDestroyerNumConnectionsInUse(String arg0, String arg1) throws SQLException {
		return getDataSource().getStatementDestroyerNumConnectionsInUse(arg0, arg1);
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#getStatementDestroyerNumConnectionsInUseAllUsers()
	 */
	public int getStatementDestroyerNumConnectionsInUseAllUsers() throws SQLException {
		return getDataSource().getStatementDestroyerNumConnectionsInUseAllUsers();
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#getStatementDestroyerNumConnectionsInUseDefaultUser()
	 */
	public int getStatementDestroyerNumConnectionsInUseDefaultUser() throws SQLException {
		return getDataSource().getStatementDestroyerNumConnectionsInUseDefaultUser();
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#getStatementDestroyerNumConnectionsWithDeferredDestroyStatements(java.lang.String, java.lang.String)
	 */
	public int getStatementDestroyerNumConnectionsWithDeferredDestroyStatements(String arg0, String arg1)
			throws SQLException {
		return getDataSource().getStatementDestroyerNumConnectionsWithDeferredDestroyStatements(arg0, arg1);
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#getStatementDestroyerNumConnectionsWithDeferredDestroyStatementsAllUsers()
	 */
	public int getStatementDestroyerNumConnectionsWithDeferredDestroyStatementsAllUsers() throws SQLException {
		return getDataSource().getStatementDestroyerNumConnectionsWithDeferredDestroyStatementsAllUsers();
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#getStatementDestroyerNumConnectionsWithDeferredDestroyStatementsDefaultUser()
	 */
	public int getStatementDestroyerNumConnectionsWithDeferredDestroyStatementsDefaultUser() throws SQLException {
		return getDataSource().getStatementDestroyerNumConnectionsWithDeferredDestroyStatementsDefaultUser();
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#getStatementDestroyerNumDeferredDestroyStatements(java.lang.String, java.lang.String)
	 */
	public int getStatementDestroyerNumDeferredDestroyStatements(String arg0, String arg1) throws SQLException {
		return getDataSource().getStatementDestroyerNumDeferredDestroyStatements(arg0, arg1);
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#getStatementDestroyerNumDeferredDestroyStatementsAllUsers()
	 */
	public int getStatementDestroyerNumDeferredDestroyStatementsAllUsers() throws SQLException {
		return getDataSource().getStatementDestroyerNumDeferredDestroyStatementsAllUsers();
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#getStatementDestroyerNumDeferredDestroyStatementsDefaultUser()
	 */
	public int getStatementDestroyerNumDeferredDestroyStatementsDefaultUser() throws SQLException {
		return getDataSource().getStatementDestroyerNumDeferredDestroyStatementsDefaultUser();
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#getStatementDestroyerNumIdleThreads()
	 */
	public int getStatementDestroyerNumIdleThreads() throws SQLException {
		return getDataSource().getStatementDestroyerNumIdleThreads();
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#getStatementDestroyerNumTasksPending()
	 */
	public int getStatementDestroyerNumTasksPending() throws SQLException {
		return getDataSource().getStatementDestroyerNumTasksPending();
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#getStatementDestroyerNumThreads()
	 */
	public int getStatementDestroyerNumThreads() throws SQLException {
		return getDataSource().getStatementDestroyerNumThreads();
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#getThreadPoolNumActiveThreads()
	 */
	public int getThreadPoolNumActiveThreads() throws SQLException {
		return getDataSource().getThreadPoolNumActiveThreads();
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#getThreadPoolNumIdleThreads()
	 */
	public int getThreadPoolNumIdleThreads() throws SQLException {
		return getDataSource().getThreadPoolNumIdleThreads();
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#getThreadPoolNumTasksPending()
	 */
	public int getThreadPoolNumTasksPending() throws SQLException {
		return getDataSource().getThreadPoolNumTasksPending();
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#getThreadPoolSize()
	 */
	public int getThreadPoolSize() throws SQLException {
		return getDataSource().getThreadPoolSize();
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#getUpTimeMillisDefaultUser()
	 */
	public long getUpTimeMillisDefaultUser() throws SQLException {
		return getDataSource().getUpTimeMillisDefaultUser();
	}

	/**
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#hardReset()
	 */
	public void hardReset() throws SQLException {
		getDataSource().hardReset();
	}

	/**
	 * @param iface
	 * @return
	 * @throws SQLException
	 * @see java.sql.Wrapper#isWrapperFor(java.lang.Class)
	 */
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return getDataSource().isWrapperFor(iface);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#sampleLastAcquisitionFailureStackTrace(java.lang.String, java.lang.String)
	 */
	public String sampleLastAcquisitionFailureStackTrace(String arg0, String arg1) throws SQLException {
		return getDataSource().sampleLastAcquisitionFailureStackTrace(arg0, arg1);
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#sampleLastAcquisitionFailureStackTraceDefaultUser()
	 */
	public String sampleLastAcquisitionFailureStackTraceDefaultUser() throws SQLException {
		return getDataSource().sampleLastAcquisitionFailureStackTraceDefaultUser();
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#sampleLastCheckinFailureStackTrace(java.lang.String, java.lang.String)
	 */
	public String sampleLastCheckinFailureStackTrace(String arg0, String arg1) throws SQLException {
		return getDataSource().sampleLastCheckinFailureStackTrace(arg0, arg1);
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#sampleLastCheckinFailureStackTraceDefaultUser()
	 */
	public String sampleLastCheckinFailureStackTraceDefaultUser() throws SQLException {
		return getDataSource().sampleLastCheckinFailureStackTraceDefaultUser();
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#sampleLastCheckoutFailureStackTrace(java.lang.String, java.lang.String)
	 */
	public String sampleLastCheckoutFailureStackTrace(String arg0, String arg1) throws SQLException {
		return getDataSource().sampleLastCheckoutFailureStackTrace(arg0, arg1);
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#sampleLastCheckoutFailureStackTraceDefaultUser()
	 */
	public String sampleLastCheckoutFailureStackTraceDefaultUser() throws SQLException {
		return getDataSource().sampleLastCheckoutFailureStackTraceDefaultUser();
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#sampleLastConnectionTestFailureStackTrace(java.lang.String, java.lang.String)
	 */
	public String sampleLastConnectionTestFailureStackTrace(String arg0, String arg1) throws SQLException {
		return getDataSource().sampleLastConnectionTestFailureStackTrace(arg0, arg1);
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#sampleLastConnectionTestFailureStackTraceDefaultUser()
	 */
	public String sampleLastConnectionTestFailureStackTraceDefaultUser() throws SQLException {
		return getDataSource().sampleLastConnectionTestFailureStackTraceDefaultUser();
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#sampleLastIdleTestFailureStackTrace(java.lang.String, java.lang.String)
	 */
	public String sampleLastIdleTestFailureStackTrace(String arg0, String arg1) throws SQLException {
		return getDataSource().sampleLastIdleTestFailureStackTrace(arg0, arg1);
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#sampleLastIdleTestFailureStackTraceDefaultUser()
	 */
	public String sampleLastIdleTestFailureStackTraceDefaultUser() throws SQLException {
		return getDataSource().sampleLastIdleTestFailureStackTraceDefaultUser();
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#sampleStatementCacheStatus(java.lang.String, java.lang.String)
	 */
	public String sampleStatementCacheStatus(String arg0, String arg1) throws SQLException {
		return getDataSource().sampleStatementCacheStatus(arg0, arg1);
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#sampleStatementCacheStatusDefaultUser()
	 */
	public String sampleStatementCacheStatusDefaultUser() throws SQLException {
		return getDataSource().sampleStatementCacheStatusDefaultUser();
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#sampleStatementDestroyerStackTraces()
	 */
	public String sampleStatementDestroyerStackTraces() throws SQLException {
		return getDataSource().sampleStatementDestroyerStackTraces();
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#sampleStatementDestroyerStatus()
	 */
	public String sampleStatementDestroyerStatus() throws SQLException {
		return getDataSource().sampleStatementDestroyerStatus();
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#sampleThreadPoolStackTraces()
	 */
	public String sampleThreadPoolStackTraces() throws SQLException {
		return getDataSource().sampleThreadPoolStackTraces();
	}

	/**
	 * @return
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#sampleThreadPoolStatus()
	 */
	public String sampleThreadPoolStatus() throws SQLException {
		return getDataSource().sampleThreadPoolStatus();
	}

	/**
	 * @param arg0
	 * @see com.mchange.v2.c3p0.PooledDataSource#setDataSourceName(java.lang.String)
	 */
	public void setDataSourceName(String arg0) {
		getDataSource().setDataSourceName(arg0);
	}

	/**
	 * @param arg0
	 * @see com.mchange.v2.c3p0.PooledDataSource#setExtensions(java.util.Map)
	 */
	public void setExtensions(Map arg0) {
		getDataSource().setExtensions(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#softReset(java.lang.String, java.lang.String)
	 */
	public void softReset(String arg0, String arg1) throws SQLException {
		getDataSource().softReset(arg0, arg1);
	}

	/**
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#softResetAllUsers()
	 */
	public void softResetAllUsers() throws SQLException {
		getDataSource().softResetAllUsers();
	}

	/**
	 * @throws SQLException
	 * @see com.mchange.v2.c3p0.PooledDataSource#softResetDefaultUser()
	 */
	public void softResetDefaultUser() throws SQLException {
		getDataSource().softResetDefaultUser();
	}

	/**
	 * @param iface
	 * @return
	 * @throws SQLException
	 * @see java.sql.Wrapper#unwrap(java.lang.Class)
	 */
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return getDataSource().unwrap(iface);
	}

	/**
	 * @param seconds
	 * @throws SQLException
	 * @see javax.sql.CommonDataSource#setLoginTimeout(int)
	 */
	public void setLoginTimeout(int seconds) throws SQLException {
		getDataSource().setLoginTimeout(seconds);
	}

	/**
	 * @param provider
	 */
	public PooledDataSourceProxy(EntityManagerProvider provider) {
		this.provider = provider;
	}
	
	/**
	 * @return
	 */
	protected PooledDataSource getDataSource() {
		if (dataSource == null) {
			EntityManagerFactory factory = this.provider.getEntityManagerFactory();
			dataSource = getDataSource(factory);
		}
		return dataSource;
	}
	
	/**
	 * @param factory
	 * @return
	 */
	protected PooledDataSource getDataSource(EntityManagerFactory factory) {
		if (! (factory instanceof SessionFactoryImpl)) {
			throw new IllegalStateException("Entity Manager Factory is not an instanceof SessionFactoryImpl");
		}
		SessionFactoryImpl impl = (SessionFactoryImpl) factory;
		ConnectionProvider provider = impl.getServiceRegistry().getService(ConnectionProvider.class);
		if (! (provider instanceof C3P0ConnectionProvider)) {
			throw new IllegalStateException("Hibernate connection provider is not of type DatasourceConnectionProviderImpl");
		}
		return provider.unwrap(PooledDataSource.class);
	}

}