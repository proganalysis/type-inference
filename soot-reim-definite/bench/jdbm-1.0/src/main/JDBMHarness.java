public class JDBMHarness implements Runnable {


  java.lang.String java_lang_String_var = null;
  int int_var = 0;
  java.lang.Object java_lang_Object_var = null;
  long long_var = 0;
  static jdbm.RecordManager jdbm_RecordManager_var = null;
  static jdbm.recman.BaseRecordManager jdbm_recman_BaseRecordManager_var = null;
  jdbm.helper.Serializer jdbm_helper_Serializer_var = null;

  public JDBMHarness() {
    try {
      jdbm_recman_BaseRecordManager_var = new jdbm.recman.BaseRecordManager(java_lang_String_var);
      jdbm_RecordManager_var = jdbm_recman_BaseRecordManager_var;
    } catch (Throwable t) {}
  }

  public static void main(String[] args) {
    JDBMHarness r = new JDBMHarness();
    for (int i = 0; i < 10; i++) {
      Thread t = new Thread(r);
      t.start();
    }
  }

  public void run() {
    try {
      long_var = jdbm_RecordManager_var.insert(java_lang_Object_var);
      long_var = jdbm_RecordManager_var.insert(java_lang_Object_var, jdbm_helper_Serializer_var);
      jdbm_RecordManager_var.delete(long_var);
      jdbm_RecordManager_var.update(long_var, java_lang_Object_var);
      jdbm_RecordManager_var.update(long_var, java_lang_Object_var, jdbm_helper_Serializer_var);
      java_lang_Object_var = jdbm_RecordManager_var.fetch(long_var);
      java_lang_Object_var = jdbm_RecordManager_var.fetch(long_var, jdbm_helper_Serializer_var);
      jdbm_RecordManager_var.close();
      int_var = jdbm_RecordManager_var.getRootCount();
      long_var = jdbm_RecordManager_var.getRoot(int_var);
      jdbm_RecordManager_var.setRoot(int_var, long_var);
      jdbm_RecordManager_var.commit();
      jdbm_RecordManager_var.rollback();
      long_var = jdbm_RecordManager_var.getNamedObject(java_lang_String_var);
      jdbm_RecordManager_var.setNamedObject(java_lang_String_var, long_var);
    } catch (Throwable e) { }
  }
}
