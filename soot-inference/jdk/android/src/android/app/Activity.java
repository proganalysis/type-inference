package android.app;
import checkers.inference.reim.quals.*; 
import checkers.inference.sflow.quals.*; 

public class Activity extends android.view.ContextThemeWrapper implements android.view.LayoutInflater.Factory2, android.view.Window.Callback, android.view.KeyEvent.Callback, android.view.View.OnCreateContextMenuListener, android.content.ComponentCallbacks2 {
    public int DEFAULT_KEYS_DIALER;
    public int DEFAULT_KEYS_DISABLE;
    public int DEFAULT_KEYS_SEARCH_GLOBAL;
    public int DEFAULT_KEYS_SEARCH_LOCAL;
    public int DEFAULT_KEYS_SHORTCUT;
    protected int[] FOCUSED_STATE_SET;
    public int RESULT_CANCELED;
    public int RESULT_FIRST_USER;
    public int RESULT_OK;
    public Activity() { throw new RuntimeException("skeleton method"); }
    public void addContentView(android.view.View arg0, android.view.ViewGroup.LayoutParams arg1) { throw new RuntimeException("skeleton method"); }
    public void closeContextMenu() { throw new RuntimeException("skeleton method"); }
    public void closeOptionsMenu() { throw new RuntimeException("skeleton method"); }
    public android.app.PendingIntent createPendingResult(int arg0, android.content.Intent arg1, int arg2) { throw new RuntimeException("skeleton method"); }
    public void dismissDialog(int arg0) { throw new RuntimeException("skeleton method"); }
    public boolean dispatchGenericMotionEvent(android.view.MotionEvent arg0) { throw new RuntimeException("skeleton method"); }
    public boolean dispatchKeyEvent(android.view.KeyEvent arg0) { throw new RuntimeException("skeleton method"); }
    public boolean dispatchKeyShortcutEvent(android.view.KeyEvent arg0) { throw new RuntimeException("skeleton method"); }
    public boolean dispatchPopulateAccessibilityEvent(android.view.accessibility.AccessibilityEvent arg0) { throw new RuntimeException("skeleton method"); }
    public boolean dispatchTouchEvent(android.view.MotionEvent arg0) { throw new RuntimeException("skeleton method"); }
    public boolean dispatchTrackballEvent(android.view.MotionEvent arg0) { throw new RuntimeException("skeleton method"); }
    public void dump(java.lang.String arg0, java.io.FileDescriptor arg1, java.io.PrintWriter arg2, java.lang.String[] arg3) { throw new RuntimeException("skeleton method"); }
    public android.view.View findViewById(int arg0) { throw new RuntimeException("skeleton method"); }
    public void finish() { throw new RuntimeException("skeleton method"); }
    public void finishActivity(int arg0) { throw new RuntimeException("skeleton method"); }
    public void finishActivityFromChild(android.app.Activity arg0, int arg1) { throw new RuntimeException("skeleton method"); }
    public void finishAffinity() { throw new RuntimeException("skeleton method"); }
    public void finishFromChild(android.app.Activity arg0) { throw new RuntimeException("skeleton method"); }
    public android.app.ActionBar getActionBar() { throw new RuntimeException("skeleton method"); }
    public android.os.IBinder getActivityToken() { throw new RuntimeException("skeleton method"); }
    public android.app.Application getApplication() { throw new RuntimeException("skeleton method"); }
    public android.content.ComponentName getCallingActivity() { throw new RuntimeException("skeleton method"); }
    public java.lang.String getCallingPackage() { throw new RuntimeException("skeleton method"); }
    public int getChangingConfigurations() { throw new RuntimeException("skeleton method"); }
    public android.content.ComponentName getComponentName() { throw new RuntimeException("skeleton method"); }
    public android.view.View getCurrentFocus() { throw new RuntimeException("skeleton method"); }
    public android.app.FragmentManager getFragmentManager() { throw new RuntimeException("skeleton method"); }
    public android.content.Intent getIntent() { throw new RuntimeException("skeleton method"); }
    public java.lang.Object getLastNonConfigurationInstance() { throw new RuntimeException("skeleton method"); }
    public android.view.LayoutInflater getLayoutInflater() { throw new RuntimeException("skeleton method"); }
    public android.app.LoaderManager getLoaderManager() { throw new RuntimeException("skeleton method"); }
    public java.lang.String getLocalClassName() { throw new RuntimeException("skeleton method"); }
    public android.view.MenuInflater getMenuInflater() { throw new RuntimeException("skeleton method"); }
    public android.app.Activity getParent() { throw new RuntimeException("skeleton method"); }
    public android.content.Intent getParentActivityIntent() { throw new RuntimeException("skeleton method"); }
    public android.content.SharedPreferences getPreferences(int arg0) { throw new RuntimeException("skeleton method"); }
    public int getRequestedOrientation() { throw new RuntimeException("skeleton method"); }
    public java.lang.Object getSystemService(java.lang.String arg0) { throw new RuntimeException("skeleton method"); }
    public int getTaskId() { throw new RuntimeException("skeleton method"); }
    public java.lang.CharSequence getTitle() { throw new RuntimeException("skeleton method"); }
    public int getTitleColor() { throw new RuntimeException("skeleton method"); }
    public int getVolumeControlStream() { throw new RuntimeException("skeleton method"); }
    public android.view.Window getWindow() { throw new RuntimeException("skeleton method"); }
    public android.view.WindowManager getWindowManager() { throw new RuntimeException("skeleton method"); }
    public boolean hasWindowFocus() { throw new RuntimeException("skeleton method"); }
    public void invalidateOptionsMenu() { throw new RuntimeException("skeleton method"); }
    public boolean isChangingConfigurations() { throw new RuntimeException("skeleton method"); }
    public boolean isChild() { throw new RuntimeException("skeleton method"); }
    public boolean isDestroyed() { throw new RuntimeException("skeleton method"); }
    public boolean isFinishing() { throw new RuntimeException("skeleton method"); }
    public boolean isImmersive() { throw new RuntimeException("skeleton method"); }
    public boolean isResumed() { throw new RuntimeException("skeleton method"); }
    public boolean isTaskRoot() { throw new RuntimeException("skeleton method"); }
    public android.database.Cursor managedQuery(android.net.Uri arg0, java.lang.String[] arg1, java.lang.String arg2, java.lang.String arg3) { throw new RuntimeException("skeleton method"); }
    public android.database.Cursor managedQuery(android.net.Uri arg0, java.lang.String[] arg1, java.lang.String arg2, java.lang.String[] arg3, java.lang.String arg4) { throw new RuntimeException("skeleton method"); }
    public boolean moveTaskToBack(boolean arg0) { throw new RuntimeException("skeleton method"); }
    public boolean navigateUpTo(android.content.Intent arg0) { throw new RuntimeException("skeleton method"); }
    public boolean navigateUpToFromChild(android.app.Activity arg0, android.content.Intent arg1) { throw new RuntimeException("skeleton method"); }
    public void onActionModeFinished(android.view.ActionMode arg0) { throw new RuntimeException("skeleton method"); }
    public void onActionModeStarted(android.view.ActionMode arg0) { throw new RuntimeException("skeleton method"); }
    protected void onActivityResult(int arg0, int arg1, android.content.Intent arg2) { throw new RuntimeException("skeleton method"); }
    protected void onApplyThemeResource(android.content.res.Resources.Theme arg0, int arg1, boolean arg2) { throw new RuntimeException("skeleton method"); }
    public void onAttachFragment(android.app.Fragment arg0) { throw new RuntimeException("skeleton method"); }
    public void onAttachedToWindow() { throw new RuntimeException("skeleton method"); }
    public void onBackPressed() { throw new RuntimeException("skeleton method"); }
    protected void onChildTitleChanged(android.app.Activity arg0, java.lang.CharSequence arg1) { throw new RuntimeException("skeleton method"); }
    public void onConfigurationChanged(android.content.res.Configuration arg0) { throw new RuntimeException("skeleton method"); }
    public void onContentChanged() { throw new RuntimeException("skeleton method"); }
    public boolean onContextItemSelected(android.view.MenuItem arg0) { throw new RuntimeException("skeleton method"); }
    public void onContextMenuClosed(android.view.Menu arg0) { throw new RuntimeException("skeleton method"); }
    protected void onCreate(android.os.Bundle arg0) { throw new RuntimeException("skeleton method"); }
    public void onCreateContextMenu(android.view.ContextMenu arg0, android.view.View arg1, android.view.ContextMenu.ContextMenuInfo arg2) { throw new RuntimeException("skeleton method"); }
    public java.lang.CharSequence onCreateDescription() { throw new RuntimeException("skeleton method"); }
    protected android.app.Dialog onCreateDialog(int arg0) { throw new RuntimeException("skeleton method"); }
    protected android.app.Dialog onCreateDialog(int arg0, android.os.Bundle arg1) { throw new RuntimeException("skeleton method"); }
    public void onCreateNavigateUpTaskStack(android.app.TaskStackBuilder arg0) { throw new RuntimeException("skeleton method"); }
    public boolean onCreateOptionsMenu(android.view.Menu arg0) { throw new RuntimeException("skeleton method"); }
    public boolean onCreatePanelMenu(int arg0, android.view.Menu arg1) { throw new RuntimeException("skeleton method"); }
    public android.view.View onCreatePanelView(int arg0) { throw new RuntimeException("skeleton method"); }
    public boolean onCreateThumbnail(android.graphics.Bitmap arg0, android.graphics.Canvas arg1) { throw new RuntimeException("skeleton method"); }
    public android.view.View onCreateView(android.view.View arg0, java.lang.String arg1, android.content.Context arg2, android.util.AttributeSet arg3) { throw new RuntimeException("skeleton method"); }
    public android.view.View onCreateView(java.lang.String arg0, android.content.Context arg1, android.util.AttributeSet arg2) { throw new RuntimeException("skeleton method"); }
    protected void onDestroy() { throw new RuntimeException("skeleton method"); }
    public void onDetachedFromWindow() { throw new RuntimeException("skeleton method"); }
    public boolean onGenericMotionEvent(android.view.MotionEvent arg0) { throw new RuntimeException("skeleton method"); }
    public boolean onKeyDown(int arg0, android.view.KeyEvent arg1) { throw new RuntimeException("skeleton method"); }
    public boolean onKeyLongPress(int arg0, android.view.KeyEvent arg1) { throw new RuntimeException("skeleton method"); }
    public boolean onKeyMultiple(int arg0, int arg1, android.view.KeyEvent arg2) { throw new RuntimeException("skeleton method"); }
    public boolean onKeyShortcut(int arg0, android.view.KeyEvent arg1) { throw new RuntimeException("skeleton method"); }
    public boolean onKeyUp(int arg0, android.view.KeyEvent arg1) { throw new RuntimeException("skeleton method"); }
    public void onLowMemory() { throw new RuntimeException("skeleton method"); }
    public boolean onMenuItemSelected(int arg0, android.view.MenuItem arg1) { throw new RuntimeException("skeleton method"); }
    public boolean onMenuOpened(int arg0, android.view.Menu arg1) { throw new RuntimeException("skeleton method"); }
    public boolean onNavigateUp() { throw new RuntimeException("skeleton method"); }
    public boolean onNavigateUpFromChild(android.app.Activity arg0) { throw new RuntimeException("skeleton method"); }
    protected void onNewIntent(android.content.Intent arg0) { throw new RuntimeException("skeleton method"); }
    public boolean onOptionsItemSelected(android.view.MenuItem arg0) { throw new RuntimeException("skeleton method"); }
    public void onOptionsMenuClosed(android.view.Menu arg0) { throw new RuntimeException("skeleton method"); }
    public void onPanelClosed(int arg0, android.view.Menu arg1) { throw new RuntimeException("skeleton method"); }
    protected void onPause() { throw new RuntimeException("skeleton method"); }
    protected void onPostCreate(android.os.Bundle arg0) { throw new RuntimeException("skeleton method"); }
    protected void onPostResume() { throw new RuntimeException("skeleton method"); }
    protected void onPrepareDialog(int arg0, android.app.Dialog arg1) { throw new RuntimeException("skeleton method"); }
    protected void onPrepareDialog(int arg0, android.app.Dialog arg1, android.os.Bundle arg2) { throw new RuntimeException("skeleton method"); }
    public void onPrepareNavigateUpTaskStack(android.app.TaskStackBuilder arg0) { throw new RuntimeException("skeleton method"); }
    public boolean onPrepareOptionsMenu(android.view.Menu arg0) { throw new RuntimeException("skeleton method"); }
    public boolean onPreparePanel(int arg0, android.view.View arg1, android.view.Menu arg2) { throw new RuntimeException("skeleton method"); }
    protected void onRestart() { throw new RuntimeException("skeleton method"); }
    protected void onRestoreInstanceState(android.os.Bundle arg0) { throw new RuntimeException("skeleton method"); }
    protected void onResume() { throw new RuntimeException("skeleton method"); }
    public java.lang.Object onRetainNonConfigurationInstance() { throw new RuntimeException("skeleton method"); }
    protected void onSaveInstanceState(android.os.Bundle arg0) { throw new RuntimeException("skeleton method"); }
    public boolean onSearchRequested() { throw new RuntimeException("skeleton method"); }
    protected void onStart() { throw new RuntimeException("skeleton method"); }
    protected void onStop() { throw new RuntimeException("skeleton method"); }
    protected void onTitleChanged(java.lang.CharSequence arg0, int arg1) { throw new RuntimeException("skeleton method"); }
    public boolean onTouchEvent(android.view.MotionEvent arg0) { throw new RuntimeException("skeleton method"); }
    public boolean onTrackballEvent(android.view.MotionEvent arg0) { throw new RuntimeException("skeleton method"); }
    public void onTrimMemory(int arg0) { throw new RuntimeException("skeleton method"); }
    public void onUserInteraction() { throw new RuntimeException("skeleton method"); }
    protected void onUserLeaveHint() { throw new RuntimeException("skeleton method"); }
    public void onWindowAttributesChanged(android.view.WindowManager.LayoutParams arg0) { throw new RuntimeException("skeleton method"); }
    public void onWindowFocusChanged(boolean arg0) { throw new RuntimeException("skeleton method"); }
    public android.view.ActionMode onWindowStartingActionMode(android.view.ActionMode.Callback arg0) { throw new RuntimeException("skeleton method"); }
    public void openContextMenu(android.view.View arg0) { throw new RuntimeException("skeleton method"); }
    public void openOptionsMenu() { throw new RuntimeException("skeleton method"); }
    public void overridePendingTransition(int arg0, int arg1) { throw new RuntimeException("skeleton method"); }
    public void recreate() { throw new RuntimeException("skeleton method"); }
    public void registerForContextMenu(android.view.View arg0) { throw new RuntimeException("skeleton method"); }
    public void removeDialog(int arg0) { throw new RuntimeException("skeleton method"); }
    public boolean requestWindowFeature(int arg0) { throw new RuntimeException("skeleton method"); }
    public void runOnUiThread(java.lang.Runnable arg0) { throw new RuntimeException("skeleton method"); }
    public void setContentView(int arg0) { throw new RuntimeException("skeleton method"); }
    public void setContentView(android.view.View arg0) { throw new RuntimeException("skeleton method"); }
    public void setContentView(android.view.View arg0, android.view.ViewGroup.LayoutParams arg1) { throw new RuntimeException("skeleton method"); }
    public void setDefaultKeyMode(int arg0) { throw new RuntimeException("skeleton method"); }
    public void setFeatureDrawable(int arg0, android.graphics.drawable.Drawable arg1) { throw new RuntimeException("skeleton method"); }
    public void setFeatureDrawableAlpha(int arg0, int arg1) { throw new RuntimeException("skeleton method"); }
    public void setFeatureDrawableResource(int arg0, int arg1) { throw new RuntimeException("skeleton method"); }
    public void setFeatureDrawableUri(int arg0, android.net.Uri arg1) { throw new RuntimeException("skeleton method"); }
    public void setFinishOnTouchOutside(boolean arg0) { throw new RuntimeException("skeleton method"); }
    public void setImmersive(boolean arg0) { throw new RuntimeException("skeleton method"); }
    public void setIntent(android.content.Intent arg0) { throw new RuntimeException("skeleton method"); }
    public void setPersistent(boolean arg0) { throw new RuntimeException("skeleton method"); }
    public void setProgress(int arg0) { throw new RuntimeException("skeleton method"); }
    public void setProgressBarIndeterminate(boolean arg0) { throw new RuntimeException("skeleton method"); }
    public void setProgressBarIndeterminateVisibility(boolean arg0) { throw new RuntimeException("skeleton method"); }
    public void setProgressBarVisibility(boolean arg0) { throw new RuntimeException("skeleton method"); }
    public void setRequestedOrientation(int arg0) { throw new RuntimeException("skeleton method"); }
    public void setResult(int arg0) { throw new RuntimeException("skeleton method"); }
    public void setResult(int arg0, @Safe android.content.Intent arg1) { throw new RuntimeException("skeleton method"); }
    public void setSecondaryProgress(int arg0) { throw new RuntimeException("skeleton method"); }
    public void setTitle(int arg0) { throw new RuntimeException("skeleton method"); }
    public void setTitle(java.lang.CharSequence arg0) { throw new RuntimeException("skeleton method"); }
    public void setTitleColor(int arg0) { throw new RuntimeException("skeleton method"); }
    public void setVisible(boolean arg0) { throw new RuntimeException("skeleton method"); }
    public void setVolumeControlStream(int arg0) { throw new RuntimeException("skeleton method"); }
    public boolean shouldUpRecreateTask(android.content.Intent arg0) { throw new RuntimeException("skeleton method"); }
    public void showDialog(int arg0) { throw new RuntimeException("skeleton method"); }
    public boolean showDialog(int arg0, android.os.Bundle arg1) { throw new RuntimeException("skeleton method"); }
    public android.view.ActionMode startActionMode(android.view.ActionMode.Callback arg0) { throw new RuntimeException("skeleton method"); }
    public void startActivities(@Safe android.content.Intent[] arg0) { throw new RuntimeException("skeleton method"); }
    public void startActivities(@Safe android.content.Intent[] arg0, android.os.Bundle arg1) { throw new RuntimeException("skeleton method"); }
    public void startActivity(@Safe android.content.Intent arg0) { throw new RuntimeException("skeleton method"); }
    public void startActivity(@Safe android.content.Intent arg0, android.os.Bundle arg1) { throw new RuntimeException("skeleton method"); }
    public void startActivityAsUser(@Safe android.content.Intent arg0, android.os.Bundle arg1, android.os.UserHandle arg2) { throw new RuntimeException("skeleton method"); }
    public void startActivityAsUser(@Safe android.content.Intent arg0, android.os.UserHandle arg1) { throw new RuntimeException("skeleton method"); }
    public void startActivityForResult(@Safe android.content.Intent arg0, int arg1) { throw new RuntimeException("skeleton method"); }
    public void startActivityForResult(@Safe android.content.Intent arg0, int arg1, android.os.Bundle arg2) { throw new RuntimeException("skeleton method"); }
    public void startActivityFromChild(android.app.Activity arg0, @Safe android.content.Intent arg1, int arg2) { throw new RuntimeException("skeleton method"); }
    public void startActivityFromChild(android.app.Activity arg0, @Safe android.content.Intent arg1, int arg2, android.os.Bundle arg3) { throw new RuntimeException("skeleton method"); }
    public void startActivityFromFragment(android.app.Fragment arg0, @Safe android.content.Intent arg1, int arg2) { throw new RuntimeException("skeleton method"); }
    public void startActivityFromFragment(android.app.Fragment arg0, @Safe android.content.Intent arg1, int arg2, android.os.Bundle arg3) { throw new RuntimeException("skeleton method"); }
    public boolean startActivityIfNeeded(@Safe android.content.Intent arg0, int arg1) { throw new RuntimeException("skeleton method"); }
    public boolean startActivityIfNeeded(@Safe android.content.Intent arg0, int arg1, android.os.Bundle arg2) { throw new RuntimeException("skeleton method"); }
    public void startIntentSender(android.content.IntentSender arg0, @Safe android.content.Intent arg1, int arg2, int arg3, int arg4) throws android.content.IntentSender.SendIntentException { throw new RuntimeException("skeleton method"); }
    public void startIntentSender(android.content.IntentSender arg0, @Safe android.content.Intent arg1, int arg2, int arg3, int arg4, android.os.Bundle arg5) throws android.content.IntentSender.SendIntentException { throw new RuntimeException("skeleton method"); }
    public void startIntentSenderForResult(android.content.IntentSender arg0, int arg1, @Safe android.content.Intent arg2, int arg3, int arg4, int arg5) throws android.content.IntentSender.SendIntentException { throw new RuntimeException("skeleton method"); }
    public void startIntentSenderForResult(android.content.IntentSender arg0, int arg1, @Safe android.content.Intent arg2, int arg3, int arg4, int arg5, android.os.Bundle arg6) throws android.content.IntentSender.SendIntentException { throw new RuntimeException("skeleton method"); }
    public void startIntentSenderFromChild(android.app.Activity arg0, android.content.IntentSender arg1, int arg2, @Safe android.content.Intent arg3, int arg4, int arg5, int arg6) throws android.content.IntentSender.SendIntentException { throw new RuntimeException("skeleton method"); }
    public void startIntentSenderFromChild(android.app.Activity arg0, android.content.IntentSender arg1, int arg2, @Safe android.content.Intent arg3, int arg4, int arg5, int arg6, android.os.Bundle arg7) throws android.content.IntentSender.SendIntentException { throw new RuntimeException("skeleton method"); }
    public void startManagingCursor(android.database.Cursor arg0) { throw new RuntimeException("skeleton method"); }
    public boolean startNextMatchingActivity(@Safe android.content.Intent arg0) { throw new RuntimeException("skeleton method"); }
    public boolean startNextMatchingActivity(@Safe android.content.Intent arg0, android.os.Bundle arg1) { throw new RuntimeException("skeleton method"); }
    public void startSearch(java.lang.String arg0, boolean arg1, android.os.Bundle arg2, boolean arg3) { throw new RuntimeException("skeleton method"); }
    public void stopManagingCursor(android.database.Cursor arg0) { throw new RuntimeException("skeleton method"); }
    public void takeKeyEvents(boolean arg0) { throw new RuntimeException("skeleton method"); }
    public void triggerSearch(java.lang.String arg0, android.os.Bundle arg1) { throw new RuntimeException("skeleton method"); }
    public void unregisterForContextMenu(android.view.View arg0) { throw new RuntimeException("skeleton method"); }
}
