//$Id: Session.java,v 1.25 2004/11/17 18:07:22 turin42 Exp $
package net.sf.hibernate;

import net.sf.hibernate.type.Type;
import java.io.Serializable;
import java.sql.Connection;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
/*>>> 
import checkers.inference.reim.quals.*; 
import checkers.inference.sflow.quals.*; 
*/ 

/**
 * The main runtime interface between a Java application and Hibernate. This is the
 * central API class abstracting the notion of a persistence service.<br>
 * <br>
 * The lifecycle of a <tt>Session</tt> is bounded by the beginning and end of a logical
 * transaction. (Long transactions might span several database transactions.)<br>
 * <br>
 * The main function of the <tt>Session</tt> is to offer create, find and delete operations
 * for instances of mapped entity classes. Instances may exist in one of two states:<br>
 * <br>
 * <i>transient:</i> not associated with any <tt>Session</tt><br>
 * <i>persistent:</i> associated with a <tt>Session</tt><br>
 * <br>
 * Transient instances may be made persistent by calling <tt>save()</tt>,
 * <tt>update()</tt> or <tt>saveOrUpdate()</tt>. Persistent instances may be made transient
 * by calling<tt> delete()</tt>. Any instance returned by a <tt>find(), iterate()</tt> or
 * <tt>load()</tt> method is persistent.<br>
 * <br>
 * <tt>save()</tt> results in an SQL <tt>INSERT</tt>, <tt>delete()</tt>
 * in an SQL <tt>DELETE</tt> and <tt>update()</tt> in an SQL <tt>UPDATE</tt>. Changes to
 * <i>persistent</i> instances are detected at flush time and also result in an SQL
 * <tt>UPDATE</tt>.<br>
 * <br>
 * It is not intended that implementors be threadsafe. Instead each thread/transaction
 * should obtain its own instance from a <tt>SessionFactory</tt>.<br>
 * <br>
 * A <tt>Session</tt> instance is serializable if its persistent classes are serializable.<br>
 * <br>
 * A typical transaction should use the following idiom:
 * <pre>
 * Session sess = factory.openSession();
 * Transaction tx;
 * try {
 *     tx = sess.beginTransaction();
 *     //do some work
 *     ...
 *     tx.commit();
 * }
 * catch (Exception e) {
 *     if (tx!=null) tx.rollback();
 *     throw e;
 * }
 * finally {
 *     sess.close();
 * }
 * </pre>
 * <br>
 * If the <tt>Session</tt> throws an exception, the transaction must be rolled back
 * and the session discarded. The internal state of the <tt>Session</tt> might not
 * be consistent with the database after the exception occurs.
 *
 * @see SessionFactory
 * @author Gavin King
 */
public interface Session extends Serializable {
	
	/**
	 * Force the <tt>Session</tt> to flush. Must be called at the end of a
	 * unit of work, before commiting the transaction and closing the
	 * session (<tt>Transaction.commit()</tt> calls this method). <i>Flushing</i>
	 * is the process of synchronising the underlying persistent store with
	 * persistable state held in memory.
	 *
	 * @throws HibernateException
	 */
	public void flush() throws HibernateException;
	
	/**
	 * Set the flush mode. The flush mode determines at which points
	 * Hibernate automatically flushes the session. For a readonly
	 * session, it is reasonable to set the flush mode to
	 * <tt>FlushMode.NEVER</tt> at the start of the session (in
	 * order to achieve some extra performance).
	 *
	 * @see FlushMode
	 * @param flushMode the FlushMode
	 */
	public void setFlushMode(FlushMode flushMode);
	
	/**
	 * Get the current flush mode.
	 *
	 * @return FlushMode
	 */
	public FlushMode getFlushMode();
	
	/**
	 * Get the <tt>SessionFactory</tt> that created this instance.
	 * @see SessionFactory
	 */
	public SessionFactory getSessionFactory();
	
	/**
	 * Get the JDBC connection. Applications are responsible for
	 * calling commit/rollback upon the connection before closing
	 * the <tt>Session</tt>.
	 *
	 * @return the JDBC connection in use by the <tt>Session</tt>
	 * @throws HibernateException if the <tt>Session</tt> is disconnected
	 */
	public Connection connection() throws HibernateException;
	
	/**
	 * Disconnect the <tt>Session</tt> from the current JDBC connection. If
	 * the connection was obtained by Hibernate, close it or return it to the
	 * connection pool. Otherwise return it to the application.<br>
	 * <br>
	 * This is used by applications which require long transactions.
	 *
	 * @return the connection provided by the application or <tt>null</tt>
	 * @throws HibernateException if the <tt>Session</tt> is disconnected
	 * @see Session#reconnect()
	 */
	public Connection disconnect() throws HibernateException;
	
	/**
	 * Obtain a new JDBC connection. This is used by applications which
	 * require long transactions.
	 *
	 * @see Session#disconnect()
	 * @throws HibernateException
	 */
	public void reconnect() throws HibernateException;
	
	/**
	 * Reconnect to the given JDBC connection. This is used by applications
	 * which require long transactions.
	 *
	 * @param connection a JDBC connection
	 * @throws HibernateException if the <tt>Session</tt> is connected
	 * @see Session#disconnect()
	 */
	public void reconnect(Connection connection) throws HibernateException;
	
	/**
	 * End the <tt>Session</tt> by disconnecting from the JDBC connection and
	 * cleaning up. It is not strictly necessary to <tt>close()</tt> the
	 * <tt>Session</tt> but you must at least <tt>disconnect()</tt> it.
	 *
	 * @return the connection provided by the application
	 * or <tt>null</tt>
	 * @throws HibernateException
	 */
	public Connection close() throws HibernateException;
	
	/**
	 * Cancel execution of the current query. May be called from one thread
	 * to stop execution of a query in another thread. Use with care!
	 */
	public void cancelQuery() throws HibernateException;
	
	/**
	 * Check if the <tt>Session</tt> is still open.
	 *
	 * @return boolean
	 */
	public boolean isOpen();
	
	/**
	 * Check if the <tt>Session</tt> is currently connected.
	 *
	 * @return boolean
	 */
	public boolean isConnected();
	
	/**
	 * Does this <tt>Session</tt> contain any changes which must be
	 * synchronized with the database? Would any SQL be executed if
	 * we flushed this session?
	 * 
	 * @return boolean
	 */
	public boolean isDirty() throws HibernateException;
	
	/**
	 * Return the identifier of an entity instance cached by the <tt>Session</tt>, or
	 * throw an exception if the instance is transient or associated with a different
	 * <tt>Session</tt>.
	 *
	 * @param object a persistent instance
	 * @return the identifier
	 * @throws HibernateException if the <tt>Session</tt> is connected
	 */
	public Serializable getIdentifier(Object object) throws HibernateException;
	/**
	 * Check if this instance is associated with this <tt>Session</tt>.
	 * 
	 * @param object an instance of a persistent class
	 * @return true if the given instance is associated with this <tt>Session</tt>
	 */
	public boolean contains(Object object);
	/**
	 * Remove this instance from the session cache. Changes to the instance will
	 * not be synchronized with the database. This operation cascades to associated
	 * instances if the association is mapped with cascade="all" or cascade="all-delete-orphan".
	 * 
	 * @param object a persistent instance
	 * @throws HibernateException
	 */
	public void evict(Object object) throws HibernateException;
	
	/**
	 * Return the persistent instance of the given entity class with the given identifier,
	 * obtaining the specified lock mode, assuming the instance exists.
	 *
	 * @param theClass a persistent class
	 * @param id a valid identifier of an existing persistent instance of the class
	 * @param lockMode the lock level
	 * @return the persistent instance or proxy
	 * @throws HibernateException
	 */
	public Object load(Class theClass, Serializable id, LockMode lockMode) throws HibernateException;
	
	/**
	 * Return the persistent instance of the given entity class with the given identifier,
	 * assuming that the instance exists.
	 * <br><br>
	 * You should not use this method to determine if an instance exists (use <tt>find()</tt>
	 * instead). Use this only to retrieve an instance that you assume exists, where non-existence
	 * would be an actual error.
	 *
	 * @param theClass a persistent class
	 * @param id a valid identifier of an existing persistent instance of the class
	 * @return the persistent instance or proxy
	 * @throws HibernateException
	 */
	public Object load(Class theClass, Serializable id) throws HibernateException;
	
	/**
	 * Read the persistent state associated with the given identifier into the given transient 
	 * instance.
	 *
	 * @param object an "empty" instance of the persistent class
	 * @param id a valid identifier of an existing persistent instance of the class
	 * @throws HibernateException
	 */
	public void load(Object object, Serializable id) throws HibernateException;
	
	/**
	 * Persist all reachable transient objects, reusing the current identifier 
	 * values. Note that this will not trigger the Interceptor of the Session.
	 * 
	 * @param object a transient instance of a persistent class
	 */
	public void replicate(Object object, ReplicationMode replicationMode) throws HibernateException;
	
	/**
	 * Persist the given transient instance, first assigning a generated identifier. (Or
	 * using the current value of the identifier property if the <tt>assigned</tt>
	 * generator is used.)
	 *
	 * @param object a transient instance of a persistent class
	 * @return the generated identifier
	 * @throws HibernateException
	 */
	public Serializable save(Object object) throws HibernateException;
	
	/**
	 * Persist the given transient instance, using the given identifier.
	 *
	 * @param object a transient instance of a persistent class
	 * @param id an unused valid identifier
	 * @throws HibernateException
	 */
	public void save(Object object, Serializable id) throws HibernateException;
	
	/**
	 * Either <tt>save()</tt> or <tt>update()</tt> the given instance, depending upon the value of
	 * its identifier property. By default the instance is always saved. This behaviour may be
	 * adjusted by specifying an <tt>unsaved-value</tt> attribute of the identifier property
	 * mapping.
	 *
	 * @see Session#save(java.lang.Object)
	 * @see Session#update(Object object, Serializable id)
	 * @param object a transient instance containing new or updated state
	 * @throws HibernateException
	 */
	public void saveOrUpdate(Object object) throws HibernateException;
	
	/**
	 * Update the persistent instance with the identifier of the given transient
	 * instance. If there is a persistent instance with the same identifier,
	 * an exception is thrown.<br>
	 * <br>
	 * If the given transient instance has a <tt>null</tt> identifier, an exception
	 * will be thrown.<br>
	 * <br>
	 *
	 * @param object a transient instance containing updated state
	 * @throws HibernateException
	 */
	public void update(Object object) throws HibernateException;
	
	/**
	 * Update the persistent state associated with the given identifier. An exception
	 * is thrown if there is a persistent instance with the same identifier in the
	 * current session.<br>
	 * <br>
	 *
	 * @param object a transient instance containing updated state
	 * @param id identifier of persistent instance
	 * @throws HibernateException
	 */
	public void update(Object object, Serializable id) throws HibernateException;
	
	/**
	 * Copy the state of the given object onto the persistent object with the same
	 * identifier. If there is no persistent instance currently associated with 
	 * the session, it will be loaded. Return the persistent instance. If the 
	 * given instance is unsaved or does not exist in the database, save it and 
	 * return it as a newly persistent instance. Otherwise, the given instance
	 * does not become associated with the session.
	 * 
	 * @param object a transient instance with state to be copied
	 * @return an updated persistent instance
	 */
	public Object saveOrUpdateCopy(Object object) throws HibernateException;
	
	/**
	 * Copy the state of the given object onto the persistent object with the 
	 * given identifier. If there is no persistent instance currently associated 
	 * with the session, it will be loaded. Return the persistent instance. If
	 * there is no database row with the given identifier, save the given instance
	 * and return it as a newly persistent instance. Otherwise, the given instance
	 * does not become associated with the session.
	 * 
	 * @param object a persistent or transient instance with state to be copied
	 * @param id the identifier of the instance to copy to
	 * @return an updated persistent instance
	 */
	public Object saveOrUpdateCopy(Object object, Serializable id) throws HibernateException;
	
	/**
	 * Remove a persistent instance from the datastore. The argument may be
	 * an instance associated with the receiving <tt>Session</tt> or a transient
	 * instance with an identifier associated with existing persistent state.
	 *
	 * @param object the instance to be removed
	 * @throws HibernateException
	 */
	public void delete(Object object) throws HibernateException;
	
	/**
	 * Execute a query.
	 *
	 * @param query a query expressed in Hibernate's query language
	 * @return a distinct list of instances (or arrays of instances)
	 * @throws HibernateException
	 */
	public List find(/*@Tainted*/ String query) throws HibernateException;
			
	/**
	 * Execute a query with bind parameters.
	 *
	 * Bind a value to a "?" parameter in the query string.
	 *
	 * @param query the query string
	 * @param value a value to be bound to a "?" placeholder (JDBC IN parameter).
	 * @param type the Hibernate type of the value
	 * @see Hibernate for access to <tt>Type</tt> instances
	 * @return a distinct list of instances (or arrays of instances)
	 * @throws HibernateException
	 */
	public List find(/*@Tainted*/ String query, Object value, Type type) throws HibernateException;
	
	/**
	 * Execute a query with bind parameters.
	 *
	 * Binding an array of values to "?" parameters in the query string.
	 *
	 * @param query the query string
	 * @param values an array of values to be bound to the "?" placeholders (JDBC IN parameters).
	 * @param types an array of Hibernate types of the values
	 * @see Hibernate for access to <tt>Type</tt> instances
	 * @return a distinct list of instances
	 * @throws HibernateException
	 */
	public List find(/*@Tainted*/ String query, Object[] values, Type[] types) throws HibernateException;
	
	/**
	 * Execute a query and return the results in an iterator. If the query has multiple
	 * return values, values will be returned in an array of type <tt>Object[].</tt><br>
	 * <br>
	 * Entities returned as results are initialized on demand. The first SQL query returns
	 * identifiers only. So <tt>iterate()</tt> is usually a less efficient way to retrieve
	 * objects than <tt>find()</tt>.
	 *
	 * @param query the query string
	 * @return an iterator
	 * @throws HibernateException
	 */
	public Iterator iterate(/*@Tainted*/ String query) throws HibernateException;
	
	/**
	 * Execute a query and return the results in an iterator. Write the given value to "?"
	 * in the query string. If the query has multiple return values, values will be returned
	 * in an array of type <tt>Object[]</tt>.<br>
	 * <br>
	 * Entities returned as results are initialized on demand. The first SQL query returns
	 * identifiers only. So <tt>iterate()</tt> is usually a less efficient way to retrieve
	 * objects than <tt>find()</tt>.
	 *
	 * @param query the query string
	 * @param value a value to be witten to a "?" placeholder in the query string
	 * @param type the hibernate type of value
	 * @return an iterator
	 * @throws HibernateException
	 */
	public Iterator iterate(/*@Tainted*/ String query, Object value, Type type) throws HibernateException;
	
	/**
	 * Execute a query and return the results in an iterator. Write the given values to "?"
	 * in the query string. If the query has multiple return values, values will be returned
	 * in an array of type <tt>Object[]</tt>.<br>
	 * <br>
	 * Entities returned as results are initialized on demand. The first SQL query returns
	 * identifiers only. So <tt>iterate()</tt> is usually a less efficient way to retrieve
	 * objects than <tt>find()</tt>.
	 *
	 * @param query the query string
	 * @param values a list of values to be written to "?" placeholders in the query
	 * @param types a list of Hibernate types of the values
	 * @return an iterator
	 * @throws HibernateException
	 */
	public Iterator iterate(/*@Tainted*/ String query, Object[] values, Type[] types) throws HibernateException;
	
	/**
	 * Apply a filter to a persistent collection. A filter is a Hibernate query that may refer to
	 * <tt>this</tt>, the collection element. Filters allow efficient access to very large lazy
	 * collections. (Executing the filter does not initialize the collection.)
	 *
	 * @param collection a persistent collection to filter
	 * @param filter a filter query string
	 * @return Collection the resulting collection
	 * @throws HibernateException
	 */
	public Collection filter(Object collection, String filter) throws HibernateException;
	
	/**
	 * Apply a filter to a persistent collection. A filter is a Hibernate query that may refer to
	 * <tt>this</tt>, the collection element.
	 *
	 * @param collection a persistent collection to filter
	 * @param filter a filter query string
	 * @param value a value to be witten to a "?" placeholder in the query string
	 * @param type the hibernate type of value
	 * @return Collection
	 * @throws HibernateException
	 */
	public Collection filter(Object collection, String filter, Object value, Type type) throws HibernateException;
	
	/**
	 * Apply a filter to a persistent collection.
	 *
	 * Bind the given parameters to "?" placeholders. A filter is a Hibernate query that
	 * may refer to <tt>this</tt>, the collection element.
	 *
	 * @param collection a persistent collection to filter
	 * @param filter a filter query string
	 * @param values a list of values to be written to "?" placeholders in the query
	 * @param types a list of Hibernate types of the values
	 * @return Collection
	 * @throws HibernateException
	 */
	public Collection filter(Object collection, String filter, Object[] values, Type[] types) throws HibernateException;
	
	/**
	 * Delete all objects returned by the query. Return the number of objects deleted.
	 *
	 * @param query the query string
	 * @return the number of instances deleted
	 * @throws HibernateException
	 */
	public int delete(/*@Tainted*/ String query) throws HibernateException;
	
	/**
	 * Delete all objects returned by the query. Return the number of objects deleted.
	 *
	 * @param query the query string
	 * @param value a value to be witten to a "?" placeholder in the query string.
	 * @param type the hibernate type of value.
	 * @return the number of instances deleted
	 * @throws HibernateException
	 */
	public int delete(/*@Tainted*/ String query, Object value, Type type) throws HibernateException;
	
	/**
	 * Delete all objects returned by the query. Return the number of objects deleted.
	 *
	 * @param query the query string
	 * @param values a list of values to be written to "?" placeholders in the query.
	 * @param types a list of Hibernate types of the values
	 * @return the number of instances deleted
	 * @throws HibernateException
	 */
	public int delete(/*@Tainted*/ String query, Object[] values, Type[] types) throws HibernateException;
	
	/**
	 * Obtain the specified lock level upon the given object. This may be used to
	 * perform a version check (<tt>LockMode.READ</tt>), to upgrade to a pessimistic 
	 * lock (<tt>LockMode.UPGRADE</tt>), or to simply reassociate a transient instance 
	 * with a session (<tt>LockMode.NONE</tt>).
	 *
	 * @param object a persistent or transient instance
	 * @param lockMode the lock level
	 * @throws HibernateException
	 */
	public void lock(Object object, LockMode lockMode) throws HibernateException;
	
	/**
	 * Re-read the state of the given instance from the underlying database. It is
	 * inadvisable to use this to implement long-running sessions that span many
	 * business tasks. This method is, however, useful in certain special circumstances.
	 * For example
	 * <ul>
	 * <li>where a database trigger alters the object state upon insert or update
	 * <li>after executing direct SQL (eg. a mass update) in the same session
	 * <li>after inserting a <tt>Blob</tt> or <tt>Clob</tt>
	 * </ul>
	 *
	 * @param object a persistent or transient instance
	 * @throws HibernateException
	 */
	public void refresh(Object object) throws HibernateException;
	
	/**
	 * Re-read the state of the given instance from the underlying database, with
	 * the given <tt>LockMode</tt>. It is inadvisable to use this to implement 
	 * long-running sessions that span many business tasks. This method is, however, 
	 * useful in certain special circumstances.
	 *
	 * @param object a persistent or transient instance
	 * @param lockMode the lock mode to use
	 * @throws HibernateException
	 */
	public void refresh(Object object, LockMode lockMode) throws HibernateException;
	
	/**
	 * Determine the current lock mode of the given object.
	 *
	 * @param object a persistent instance
	 * @return the current lock mode
	 * @throws HibernateException
	 */
	public LockMode getCurrentLockMode(Object object) throws HibernateException;
	
	/**
	 * Begin a unit of work and return the associated <tt>Transaction</tt> object.
	 * If a new underlying transaction is required, begin the transaction. Otherwise
	 * continue the new work in the context of the existing underlying transaction.
	 * The class of the returned <tt>Transaction</tt> object is determined by the
	 * property <tt>hibernate.transaction_factory</tt>.
	 *
	 * @return a Transaction instance
	 * @throws HibernateException
	 * @see Transaction
	 */
	public Transaction beginTransaction() throws HibernateException;
	
	/**
	 * Create a new <tt>Criteria</tt> instance, for the given entity class.
	 * 
	 * @param persistentClass
	 * @return Criteria
	 */
	public Criteria createCriteria(Class persistentClass);
	
	/**
	 * Create a new instance of <tt>Query</tt> for the given query string.
	 *
	 * @param queryString a Hibernate query
	 * @return Query
	 * @throws HibernateException
	 */
	public Query createQuery(/*@Tainted*/ String queryString) throws HibernateException;
	
	/**
	 * Create a new instance of <tt>Query</tt> for the given collection and filter string.
	 *
	 * @param collection a persistent collection
	 * @param queryString a Hibernate query
	 * @return Query
	 * @throws HibernateException
	 */
	public Query createFilter(Object collection, String queryString) throws HibernateException;
	
	/**
	 * Obtain an instance of <tt>Query</tt> for a named query string defined in the
	 * mapping file.
	 *
	 * @param queryName the name of a query defined externally
	 * @return Query
	 * @throws HibernateException
	 */
	public Query getNamedQuery(String queryName) throws HibernateException;

	/**
	 * Create a new instance of <tt>Query</tt> for the given SQL string.
	 * 
	 * @param sql a query expressed in SQL
	 * @param returnAlias a table alias that appears inside <tt>{}</tt> in the SQL string
	 * @param returnClass the returned persistent class
	 */
	public Query createSQLQuery(/*@Tainted*/ String sql, String returnAlias, Class returnClass);
	/**
	 * Create a new instance of <tt>Query</tt> for the given SQL string.
	 * 
	 * @param sql a query expressed in SQL
	 * @param returnAliases an array of table aliases that appear inside <tt>{}</tt> in the SQL string
	 * @param returnClasses the returned persistent classes
	 */
	public Query createSQLQuery(/*@Tainted*/ String sql, String[] returnAliases, Class[] returnClasses);
	
	/**
	 * Completely clear the session. Evict all loaded instances and cancel all pending
	 * saves, updates and deletions. Do not close open iterators or instances of
	 * <tt>ScrollableResults</tt>.
	 */
	public void clear();
	
	/**
	 * Return the persistent instance of the given entity class with the given identifier,
	 * or null if there is no such persistent instance. (If the instance, or a proxy for the
	 * instance, is already associated with the session, return that instance or proxy.)
	 * 
	 * @param clazz a persistent class
	 * @param id an identifier
	 * @return a persistent instance or null
	 * @throws HibernateException
	 */
	public Object get(Class clazz, Serializable id) throws HibernateException;

	/**
	 * Return the persistent instance of the given entity class with the given identifier,
	 * or null if there is no such persistent instance. Obtain the specified lock mode
	 * if the instance exists.
	 * 
	 * @param clazz a persistent class
	 * @param id an identifier
	 * @param lockMode the lock mode
	 * @return a persistent instance or null
	 * @throws HibernateException
	 */
	public Object get(Class clazz, Serializable id, LockMode lockMode) throws HibernateException;
	
}






