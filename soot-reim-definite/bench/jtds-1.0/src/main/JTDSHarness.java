public class JTDSHarness implements Runnable {

  static java.sql.Savepoint java_sql_Savepoint_var = null;
  static java.lang.String java_lang_String_var = null;
  static java.sql.SQLWarning java_sql_SQLWarning_var = null;
  static java.util.Properties java_util_Properties_var = null;
  static java.util.Map java_util_Map_var = null;
  static int int_var = 0;
  static net.sourceforge.jtds.jdbc.Driver net_sourceforge_jtds_jdbc_Driver_var = null;
  static java.sql.CallableStatement java_sql_CallableStatement_var = null;
  static java.lang.String[] java_lang_StringAB_var = new java.lang.String[1];
  static net.sourceforge.jtds.jdbcx.PooledConnection net_sourceforge_jtds_jdbcx_PooledConnection_var = null;
  static java.sql.Connection java_sql_Connection_var = null;
  static java.sql.DriverPropertyInfo[] java_sql_DriverPropertyInfoAB_var = new java.sql.DriverPropertyInfo[1];
  static java.sql.Statement java_sql_Statement_var = null;
  static java.sql.Driver java_sql_Driver_var = null;
  static boolean boolean_var = false;
  static java.sql.PreparedStatement java_sql_PreparedStatement_var = null;
  static int[] intAB_var = new int[1];
  static java.sql.DatabaseMetaData java_sql_DatabaseMetaData_var = null;


  public static void main(String[] args) {

      try {
	  net_sourceforge_jtds_jdbc_Driver_var = new net.sourceforge.jtds.jdbc.Driver();
	  java_sql_Driver_var = net_sourceforge_jtds_jdbc_Driver_var;
      } catch (Exception e) {}


    JTDSHarness r = new JTDSHarness();
    for (int i = 0; i < 10; i++) {
      Thread t = new Thread(r);
      t.start();
    }
  }

  public void run() {
    try {

      while (true) {

        // call sites
	 
        java_sql_Connection_var = java_sql_Driver_var.connect(java_lang_String_var, java_util_Properties_var);

	
        java_sql_Statement_var = java_sql_Connection_var.createStatement();
        java_sql_PreparedStatement_var = java_sql_Connection_var.prepareStatement(java_lang_String_var);        
	java_sql_CallableStatement_var = java_sql_Connection_var.prepareCall(java_lang_String_var);
        java_lang_String_var = java_sql_Connection_var.nativeSQL(java_lang_String_var);
        java_sql_Connection_var.setAutoCommit(boolean_var);
        boolean_var = java_sql_Connection_var.getAutoCommit();
	
        java_sql_Connection_var.commit();
        java_sql_Connection_var.rollback();
        java_sql_Connection_var.close();
        
	boolean_var = java_sql_Connection_var.isClosed();
        java_sql_DatabaseMetaData_var = java_sql_Connection_var.getMetaData();
        java_sql_Connection_var.setReadOnly(boolean_var);
        boolean_var = java_sql_Connection_var.isReadOnly();
        java_sql_Connection_var.setCatalog(java_lang_String_var);
        java_lang_String_var = java_sql_Connection_var.getCatalog();
        java_sql_Connection_var.setTransactionIsolation(int_var);
        int_var = java_sql_Connection_var.getTransactionIsolation();
        java_sql_SQLWarning_var = java_sql_Connection_var.getWarnings();
        java_sql_Connection_var.clearWarnings();
        java_sql_Statement_var = java_sql_Connection_var.createStatement(int_var, int_var);
        java_sql_PreparedStatement_var = java_sql_Connection_var.prepareStatement(java_lang_String_var, int_var, int_var);
        java_sql_CallableStatement_var = java_sql_Connection_var.prepareCall(java_lang_String_var, int_var, int_var);
        java_util_Map_var = java_sql_Connection_var.getTypeMap();
        java_sql_Connection_var.setTypeMap(java_util_Map_var);
        java_sql_Connection_var.setHoldability(int_var);
        int_var = java_sql_Connection_var.getHoldability();
        java_sql_Savepoint_var = java_sql_Connection_var.setSavepoint();
        java_sql_Savepoint_var = java_sql_Connection_var.setSavepoint(java_lang_String_var);
        java_sql_Connection_var.rollback(java_sql_Savepoint_var);
        java_sql_Connection_var.releaseSavepoint(java_sql_Savepoint_var);
        java_sql_Statement_var = java_sql_Connection_var.createStatement(int_var, int_var, int_var);
        java_sql_PreparedStatement_var = java_sql_Connection_var.prepareStatement(java_lang_String_var, int_var, int_var, int_var);
        java_sql_CallableStatement_var = java_sql_Connection_var.prepareCall(java_lang_String_var, int_var, int_var, int_var);
        java_sql_PreparedStatement_var = java_sql_Connection_var.prepareStatement(java_lang_String_var, int_var);
        java_sql_PreparedStatement_var = java_sql_Connection_var.prepareStatement(java_lang_String_var, intAB_var);
        java_sql_PreparedStatement_var = java_sql_Connection_var.prepareStatement(java_lang_String_var, java_lang_StringAB_var);
	
      }
    } catch (Throwable e) { }
  }
}

