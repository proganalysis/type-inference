public class PoolHarness implements Runnable {

  static org.apache.commons.pool.impl.GenericKeyedObjectPool org_apache_commons_pool_impl_GenericKeyedObjectPool_var = null;
  static org.apache.commons.pool.PoolableObjectFactory org_apache_commons_pool_PoolableObjectFactory_var = null;
  static org.apache.commons.pool.impl.SoftReferenceObjectPool org_apache_commons_pool_impl_SoftReferenceObjectPool_var = null;
  static org.apache.commons.pool.KeyedPoolableObjectFactory org_apache_commons_pool_KeyedPoolableObjectFactory_var = null;
  static java.lang.String java_lang_Object_var = null;
  int int_var = 0;
  static org.apache.commons.pool.impl.GenericKeyedObjectPool.Config org_apache_commons_pool_impl_GenericKeyedObjectPool_Config_var = null;
  long long_var = 0;
  static org.apache.commons.pool.impl.StackObjectPool org_apache_commons_pool_impl_StackObjectPool_var = null;
  static SimpleKeyedPoolableObjectFactory SimpleKeyedPoolableObjectFactory_var = null;
  byte byte_var = 0;
  static org.apache.commons.pool.KeyedObjectPool org_apache_commons_pool_KeyedObjectPool_var = null;
  static org.apache.commons.pool.impl.GenericObjectPool.Config org_apache_commons_pool_impl_GenericObjectPool_Config_var = null;
  boolean boolean_var = false;
  static org.apache.commons.pool.impl.StackKeyedObjectPool org_apache_commons_pool_impl_StackKeyedObjectPool_var = null;
  static SimplePoolableObjectFactory SimplePoolableObjectFactory_var = null;
  static org.apache.commons.pool.ObjectPool org_apache_commons_pool_ObjectPool_var = null;
  static org.apache.commons.pool.impl.GenericObjectPool org_apache_commons_pool_impl_GenericObjectPool_var = null;


  public static void main(String[] args) {
      
      try {
	  SimpleKeyedPoolableObjectFactory_var = new SimpleKeyedPoolableObjectFactory();
	  SimplePoolableObjectFactory_var = new SimplePoolableObjectFactory();
	  org_apache_commons_pool_impl_GenericObjectPool_var = new org.apache.commons.pool.impl.GenericObjectPool(org_apache_commons_pool_PoolableObjectFactory_var);
	  org_apache_commons_pool_impl_SoftReferenceObjectPool_var = new org.apache.commons.pool.impl.SoftReferenceObjectPool(org_apache_commons_pool_PoolableObjectFactory_var);
	  org_apache_commons_pool_impl_StackObjectPool_var = new org.apache.commons.pool.impl.StackObjectPool(org_apache_commons_pool_PoolableObjectFactory_var);
	  org_apache_commons_pool_impl_GenericKeyedObjectPool_var = new org.apache.commons.pool.impl.GenericKeyedObjectPool(org_apache_commons_pool_KeyedPoolableObjectFactory_var);
	  org_apache_commons_pool_impl_StackKeyedObjectPool_var = new org.apache.commons.pool.impl.StackKeyedObjectPool(org_apache_commons_pool_KeyedPoolableObjectFactory_var);
	  java_lang_Object_var = new java.lang.String();

	  org_apache_commons_pool_KeyedPoolableObjectFactory_var = SimpleKeyedPoolableObjectFactory_var;
	  org_apache_commons_pool_PoolableObjectFactory_var = SimplePoolableObjectFactory_var;
	  org_apache_commons_pool_ObjectPool_var = org_apache_commons_pool_impl_GenericObjectPool_var;
	  org_apache_commons_pool_ObjectPool_var = org_apache_commons_pool_impl_SoftReferenceObjectPool_var;
	  org_apache_commons_pool_ObjectPool_var = org_apache_commons_pool_impl_StackObjectPool_var;
	  org_apache_commons_pool_KeyedObjectPool_var = org_apache_commons_pool_impl_GenericKeyedObjectPool_var;
	  org_apache_commons_pool_KeyedObjectPool_var = org_apache_commons_pool_impl_StackKeyedObjectPool_var;
      } catch(Exception e) {}
  

    PoolHarness r = new PoolHarness();
    for (int i = 0; i < 10; i++) {
      Thread t = new Thread(r);
      t.start();
    }
  }
  public void run() {
    try {

      while (true) {
        org_apache_commons_pool_ObjectPool_var.returnObject(java_lang_Object_var);
        org_apache_commons_pool_ObjectPool_var.invalidateObject(java_lang_Object_var);
        org_apache_commons_pool_ObjectPool_var.addObject();
        int_var = org_apache_commons_pool_ObjectPool_var.getNumIdle();
        int_var = org_apache_commons_pool_ObjectPool_var.getNumActive();
        org_apache_commons_pool_ObjectPool_var.clear();
        org_apache_commons_pool_ObjectPool_var.close();
        org_apache_commons_pool_ObjectPool_var.setFactory(org_apache_commons_pool_PoolableObjectFactory_var);
        java_lang_Object_var = (java.lang.String) org_apache_commons_pool_KeyedObjectPool_var.borrowObject(java_lang_Object_var);
        org_apache_commons_pool_KeyedObjectPool_var.returnObject(java_lang_Object_var, java_lang_Object_var);
        org_apache_commons_pool_KeyedObjectPool_var.invalidateObject(java_lang_Object_var, java_lang_Object_var);
        org_apache_commons_pool_KeyedObjectPool_var.addObject(java_lang_Object_var);
        int_var = org_apache_commons_pool_KeyedObjectPool_var.getNumIdle(java_lang_Object_var);
        int_var = org_apache_commons_pool_KeyedObjectPool_var.getNumActive(java_lang_Object_var);
        int_var = org_apache_commons_pool_KeyedObjectPool_var.getNumIdle();
        int_var = org_apache_commons_pool_KeyedObjectPool_var.getNumActive();
        org_apache_commons_pool_KeyedObjectPool_var.clear();
        org_apache_commons_pool_KeyedObjectPool_var.clear(java_lang_Object_var);
        org_apache_commons_pool_KeyedObjectPool_var.close();
        org_apache_commons_pool_KeyedObjectPool_var.setFactory(org_apache_commons_pool_KeyedPoolableObjectFactory_var);
      }
    } catch (Throwable e) { }
  }
}
