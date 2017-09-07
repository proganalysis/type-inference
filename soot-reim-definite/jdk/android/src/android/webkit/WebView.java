package android.webkit;
import checkers.inference.reim.quals.*;
import checkers.inference.sflow.quals.*;


public class WebView extends android.widget.AbsoluteLayout implements android.view.ViewTreeObserver.OnGlobalFocusChangeListener, android.view.ViewGroup.OnHierarchyChangeListener, android.view.ViewDebug.HierarchyHandler {
    public abstract static interface FindListener {
        public abstract void onFindResultReceived(int arg0, int arg1, boolean arg2);
    }
    public static class HitTestResult {
        public static final int ANCHOR_TYPE = 0;
        public static final int EDIT_TEXT_TYPE = 0;
        public static final int EMAIL_TYPE = 0;
        public static final int GEO_TYPE = 0;
        public static final int IMAGE_ANCHOR_TYPE = 0;
        public static final int IMAGE_TYPE = 0;
        public static final int PHONE_TYPE = 0;
        public static final int SRC_ANCHOR_TYPE = 0;
        public static final int SRC_IMAGE_ANCHOR_TYPE = 0;
        public static final int UNKNOWN_TYPE = 0;
        private java.lang.String mExtra;
        private int mType;
        public HitTestResult() { throw new RuntimeException("skeleton method"); }
        public java.lang.String getExtra() { throw new RuntimeException("skeleton method"); }
        public int getType() { throw new RuntimeException("skeleton method"); }
        public void setExtra(java.lang.String arg0) { throw new RuntimeException("skeleton method"); }
        public void setType(int arg0) { throw new RuntimeException("skeleton method"); }
    }
    public abstract static interface PictureListener {
        public abstract void onNewPicture(android.webkit.WebView arg0, android.graphics.Picture arg1);
    }
    public class PrivateAccess {
        public PrivateAccess() { throw new RuntimeException("skeleton method"); }
        public void awakenScrollBars(int arg0) { throw new RuntimeException("skeleton method"); }
        public void awakenScrollBars(int arg0, boolean arg1) { throw new RuntimeException("skeleton method"); }
        public float getHorizontalScrollFactor() { throw new RuntimeException("skeleton method"); }
        public int getHorizontalScrollbarHeight() { throw new RuntimeException("skeleton method"); }
        public float getVerticalScrollFactor() { throw new RuntimeException("skeleton method"); }
        public void onScrollChanged(int arg0, int arg1, int arg2, int arg3) { throw new RuntimeException("skeleton method"); }
        public void overScrollBy(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5, int arg6, int arg7, boolean arg8) { throw new RuntimeException("skeleton method"); }
        public void setMeasuredDimension(int arg0, int arg1) { throw new RuntimeException("skeleton method"); }
        public void setScrollXRaw(int arg0) { throw new RuntimeException("skeleton method"); }
        public void setScrollYRaw(int arg0) { throw new RuntimeException("skeleton method"); }
        public void super_computeScroll() { throw new RuntimeException("skeleton method"); }
        public boolean super_dispatchKeyEvent(android.view.KeyEvent arg0) { throw new RuntimeException("skeleton method"); }
        public int super_getScrollBarStyle() { throw new RuntimeException("skeleton method"); }
        public boolean super_onGenericMotionEvent(android.view.MotionEvent arg0) { throw new RuntimeException("skeleton method"); }
        public boolean super_onHoverEvent(android.view.MotionEvent arg0) { throw new RuntimeException("skeleton method"); }
        public boolean super_performAccessibilityAction(int arg0, android.os.Bundle arg1) { throw new RuntimeException("skeleton method"); }
        public boolean super_performLongClick() { throw new RuntimeException("skeleton method"); }
        public boolean super_requestFocus(int arg0, android.graphics.Rect arg1) { throw new RuntimeException("skeleton method"); }
        public void super_scrollTo(int arg0, int arg1) { throw new RuntimeException("skeleton method"); }
        public boolean super_setFrame(int arg0, int arg1, int arg2, int arg3) { throw new RuntimeException("skeleton method"); }
        public void super_setLayoutParams(android.view.ViewGroup.LayoutParams arg0) { throw new RuntimeException("skeleton method"); }
    }
    public class WebViewTransport {
        private android.webkit.WebView mWebview;
        public WebViewTransport() { throw new RuntimeException("skeleton method"); }
        public android.webkit.WebView getWebView() { throw new RuntimeException("skeleton method"); }
        public void setWebView(android.webkit.WebView arg0) { throw new RuntimeException("skeleton method"); }
    }
    private static final java.lang.String LOGTAG = null;
    public static final java.lang.String SCHEME_GEO = null;
    public static final java.lang.String SCHEME_MAILTO = null;
    public static final java.lang.String SCHEME_TEL = null;
    private android.webkit.WebViewProvider mProvider;
    public WebView(android.content.Context arg0) { 
		super(arg0);
		throw new RuntimeException("skeleton method"); }
    public WebView(android.content.Context arg0, android.util.AttributeSet arg1) { 
		super(arg0,arg1);
		throw new RuntimeException("skeleton method"); }
    public WebView(android.content.Context arg0, android.util.AttributeSet arg1, int arg2) { 
		super(arg0,arg1,arg2);
		throw new RuntimeException("skeleton method"); }
    protected WebView(android.content.Context arg0, android.util.AttributeSet arg1, int arg2, java.util.Map<java.lang.String,java.lang.Object> arg3, boolean arg4) { 
		super(arg0,arg1,arg2);
		throw new RuntimeException("skeleton method"); }
    public WebView(android.content.Context arg0, android.util.AttributeSet arg1, int arg2, boolean arg3) { 
		super(arg0,arg1,arg2);
		throw new RuntimeException("skeleton method"); }
    private static void checkThread() { throw new RuntimeException("skeleton method"); }
    public static void disablePlatformNotifications() { throw new RuntimeException("skeleton method"); }
    public static void enablePlatformNotifications() { throw new RuntimeException("skeleton method"); }
    private void ensureProviderCreated() { throw new RuntimeException("skeleton method"); }
    public static java.lang.String findAddress(java.lang.String arg0) { throw new RuntimeException("skeleton method"); }
    private static android.webkit.WebViewFactoryProvider getFactory() { throw new RuntimeException("skeleton method"); }
    public static android.webkit.PluginList getPluginList() { throw new RuntimeException("skeleton method"); }
    public void addJavascriptInterface(java.lang.Object arg0, java.lang.String arg1) { throw new RuntimeException("skeleton method"); }
    public boolean canGoBack() { throw new RuntimeException("skeleton method"); }
    public boolean canGoBackOrForward(int arg0) { throw new RuntimeException("skeleton method"); }
    public boolean canGoForward() { throw new RuntimeException("skeleton method"); }
    public boolean canZoomIn() { throw new RuntimeException("skeleton method"); }
    public boolean canZoomOut() { throw new RuntimeException("skeleton method"); }
    public android.graphics.Picture capturePicture() { throw new RuntimeException("skeleton method"); }
    public void clearCache(boolean arg0) { throw new RuntimeException("skeleton method"); }
    public void clearFormData() { throw new RuntimeException("skeleton method"); }
    public void clearHistory() { throw new RuntimeException("skeleton method"); }
    public void clearMatches() { throw new RuntimeException("skeleton method"); }
    public void clearSslPreferences() { throw new RuntimeException("skeleton method"); }
    public void clearView() { throw new RuntimeException("skeleton method"); }
    protected int computeHorizontalScrollOffset() { throw new RuntimeException("skeleton method"); }
    protected int computeHorizontalScrollRange() { throw new RuntimeException("skeleton method"); }
    public void computeScroll() { throw new RuntimeException("skeleton method"); }
    protected int computeVerticalScrollExtent() { throw new RuntimeException("skeleton method"); }
    protected int computeVerticalScrollOffset() { throw new RuntimeException("skeleton method"); }
    protected int computeVerticalScrollRange() { throw new RuntimeException("skeleton method"); }
    public android.webkit.WebBackForwardList copyBackForwardList() { throw new RuntimeException("skeleton method"); }
    public void debugDump() { throw new RuntimeException("skeleton method"); }
    public void destroy() { throw new RuntimeException("skeleton method"); }
    public boolean dispatchKeyEvent(android.view.KeyEvent arg0) { throw new RuntimeException("skeleton method"); }
    public void documentHasImages(android.os.Message arg0) { throw new RuntimeException("skeleton method"); }
    public void dumpViewHierarchyWithProperties(java.io.BufferedWriter arg0, int arg1) { throw new RuntimeException("skeleton method"); }
    public void emulateShiftHeld() { throw new RuntimeException("skeleton method"); }
    public int findAll(java.lang.String arg0) { throw new RuntimeException("skeleton method"); }
    public void findAllAsync(java.lang.String arg0) { throw new RuntimeException("skeleton method"); }
    public android.view.View findHierarchyView(java.lang.String arg0, int arg1) { throw new RuntimeException("skeleton method"); }
    public void findNext(boolean arg0) { throw new RuntimeException("skeleton method"); }
    public void flingScroll(int arg0, int arg1) { throw new RuntimeException("skeleton method"); }
    public void freeMemory() { throw new RuntimeException("skeleton method"); }
    public android.net.http.SslCertificate getCertificate() { throw new RuntimeException("skeleton method"); }
    public int getContentHeight() { throw new RuntimeException("skeleton method"); }
    public int getContentWidth() { throw new RuntimeException("skeleton method"); }
    public android.graphics.Bitmap getFavicon() { throw new RuntimeException("skeleton method"); }
    public android.webkit.WebView.HitTestResult getHitTestResult() { throw new RuntimeException("skeleton method"); }
    public java.lang.String[] getHttpAuthUsernamePassword(java.lang.String arg0, java.lang.String arg1) { throw new RuntimeException("skeleton method"); }
    public java.lang.String getOriginalUrl() { throw new RuntimeException("skeleton method"); }
    public int getProgress() { throw new RuntimeException("skeleton method"); }
    public float getScale() { throw new RuntimeException("skeleton method"); }
    public android.webkit.WebSettings getSettings() { throw new RuntimeException("skeleton method"); }
    public java.lang.String getTitle() { throw new RuntimeException("skeleton method"); }
    public java.lang.String getTouchIconUrl() { throw new RuntimeException("skeleton method"); }
    public java.lang.String getUrl() { throw new RuntimeException("skeleton method"); }
    public int getVisibleTitleHeight() { throw new RuntimeException("skeleton method"); }
    public android.webkit.WebViewProvider getWebViewProvider() { throw new RuntimeException("skeleton method"); }
    public android.view.View getZoomControls() { throw new RuntimeException("skeleton method"); }
    public void goBack() { throw new RuntimeException("skeleton method"); }
    public void goBackOrForward(int arg0) { throw new RuntimeException("skeleton method"); }
    public void goForward() { throw new RuntimeException("skeleton method"); }
    public void invokeZoomPicker() { throw new RuntimeException("skeleton method"); }
    public boolean isPaused() { throw new RuntimeException("skeleton method"); }
    public boolean isPrivateBrowsingEnabled() { throw new RuntimeException("skeleton method"); }
    public void loadData(java.lang.String arg0, java.lang.String arg1, java.lang.String arg2) { throw new RuntimeException("skeleton method"); }
    public void loadDataWithBaseURL(java.lang.String arg0, java.lang.String arg1, java.lang.String arg2, java.lang.String arg3, java.lang.String arg4) { throw new RuntimeException("skeleton method"); }
    public void loadUrl(@Safe java.lang.String arg0) { throw new RuntimeException("skeleton method"); }
    public void loadUrl(@Safe java.lang.String arg0, java.util.Map<java.lang.String,java.lang.String> arg1) { throw new RuntimeException("skeleton method"); }
    protected void onAttachedToWindow() { throw new RuntimeException("skeleton method"); }
    public void onChildViewAdded(android.view.View arg0, android.view.View arg1) { throw new RuntimeException("skeleton method"); }
    public void onChildViewRemoved(android.view.View arg0, android.view.View arg1) { throw new RuntimeException("skeleton method"); }
    protected void onConfigurationChanged(android.content.res.Configuration arg0) { throw new RuntimeException("skeleton method"); }
    public android.view.inputmethod.InputConnection onCreateInputConnection(android.view.inputmethod.EditorInfo arg0) { throw new RuntimeException("skeleton method"); }
    protected void onDetachedFromWindow() { throw new RuntimeException("skeleton method"); }
    protected void onDraw(android.graphics.Canvas arg0) { throw new RuntimeException("skeleton method"); }
    protected void onDrawVerticalScrollBar(android.graphics.Canvas arg0, android.graphics.drawable.Drawable arg1, int arg2, int arg3, int arg4, int arg5) { throw new RuntimeException("skeleton method"); }
    protected void onFocusChanged(boolean arg0, int arg1, android.graphics.Rect arg2) { throw new RuntimeException("skeleton method"); }
    public boolean onGenericMotionEvent(android.view.MotionEvent arg0) { throw new RuntimeException("skeleton method"); }
    public void onGlobalFocusChanged(android.view.View arg0, android.view.View arg1) { throw new RuntimeException("skeleton method"); }
    public boolean onHoverEvent(android.view.MotionEvent arg0) { throw new RuntimeException("skeleton method"); }
    public void onInitializeAccessibilityEvent(android.view.accessibility.AccessibilityEvent arg0) { throw new RuntimeException("skeleton method"); }
    public void onInitializeAccessibilityNodeInfo(android.view.accessibility.AccessibilityNodeInfo arg0) { throw new RuntimeException("skeleton method"); }
    public boolean onKeyDown(int arg0, android.view.KeyEvent arg1) { throw new RuntimeException("skeleton method"); }
    public boolean onKeyMultiple(int arg0, int arg1, android.view.KeyEvent arg2) { throw new RuntimeException("skeleton method"); }
    public boolean onKeyUp(int arg0, android.view.KeyEvent arg1) { throw new RuntimeException("skeleton method"); }
    protected void onMeasure(int arg0, int arg1) { throw new RuntimeException("skeleton method"); }
    protected void onOverScrolled(int arg0, int arg1, boolean arg2, boolean arg3) { throw new RuntimeException("skeleton method"); }
    public void onPause() { throw new RuntimeException("skeleton method"); }
    public void onResume() { throw new RuntimeException("skeleton method"); }
    protected void onScrollChanged(int arg0, int arg1, int arg2, int arg3) { throw new RuntimeException("skeleton method"); }
    protected void onSizeChanged(int arg0, int arg1, int arg2, int arg3) { throw new RuntimeException("skeleton method"); }
    public boolean onTouchEvent(android.view.MotionEvent arg0) { throw new RuntimeException("skeleton method"); }
    public boolean onTrackballEvent(android.view.MotionEvent arg0) { throw new RuntimeException("skeleton method"); }
    protected void onVisibilityChanged(android.view.View arg0, int arg1) { throw new RuntimeException("skeleton method"); }
    public void onWindowFocusChanged(boolean arg0) { throw new RuntimeException("skeleton method"); }
    protected void onWindowVisibilityChanged(int arg0) { throw new RuntimeException("skeleton method"); }
    public boolean overlayHorizontalScrollbar() { throw new RuntimeException("skeleton method"); }
    public boolean overlayVerticalScrollbar() { throw new RuntimeException("skeleton method"); }
    public boolean pageDown(boolean arg0) { throw new RuntimeException("skeleton method"); }
    public boolean pageUp(boolean arg0) { throw new RuntimeException("skeleton method"); }
    public void pauseTimers() { throw new RuntimeException("skeleton method"); }
    public boolean performAccessibilityAction(int arg0, android.os.Bundle arg1) { throw new RuntimeException("skeleton method"); }
    public boolean performLongClick() { throw new RuntimeException("skeleton method"); }
    public void postUrl(@Safe java.lang.String arg0, byte[] arg1) { throw new RuntimeException("skeleton method"); }
    public void refreshPlugins(boolean arg0) { throw new RuntimeException("skeleton method"); }
    public void reload() { throw new RuntimeException("skeleton method"); }
    public void removeJavascriptInterface(java.lang.String arg0) { throw new RuntimeException("skeleton method"); }
    public boolean requestChildRectangleOnScreen(android.view.View arg0, android.graphics.Rect arg1, boolean arg2) { throw new RuntimeException("skeleton method"); }
    public boolean requestFocus(int arg0, android.graphics.Rect arg1) { throw new RuntimeException("skeleton method"); }
    public void requestFocusNodeHref(android.os.Message arg0) { throw new RuntimeException("skeleton method"); }
    public void requestImageRef(android.os.Message arg0) { throw new RuntimeException("skeleton method"); }
    public boolean restorePicture(android.os.Bundle arg0, java.io.File arg1) { throw new RuntimeException("skeleton method"); }
    public android.webkit.WebBackForwardList restoreState(android.os.Bundle arg0) { throw new RuntimeException("skeleton method"); }
    public void resumeTimers() { throw new RuntimeException("skeleton method"); }
    public void savePassword(java.lang.String arg0, java.lang.String arg1, java.lang.String arg2) { throw new RuntimeException("skeleton method"); }
    public boolean savePicture(android.os.Bundle arg0, java.io.File arg1) { throw new RuntimeException("skeleton method"); }
    public android.webkit.WebBackForwardList saveState(android.os.Bundle arg0) { throw new RuntimeException("skeleton method"); }
    public void saveWebArchive(java.lang.String arg0) { throw new RuntimeException("skeleton method"); }
    public void saveWebArchive(java.lang.String arg0, boolean arg1, android.webkit.ValueCallback<java.lang.String> arg2) { throw new RuntimeException("skeleton method"); }
    public void setBackgroundColor(int arg0) { throw new RuntimeException("skeleton method"); }
    public void setCertificate(android.net.http.SslCertificate arg0) { throw new RuntimeException("skeleton method"); }
    public void setDownloadListener(android.webkit.DownloadListener arg0) { throw new RuntimeException("skeleton method"); }
    public void setFindListener(android.webkit.WebView.FindListener arg0) { throw new RuntimeException("skeleton method"); }
    protected boolean setFrame(int arg0, int arg1, int arg2, int arg3) { throw new RuntimeException("skeleton method"); }
    public void setHorizontalScrollbarOverlay(boolean arg0) { throw new RuntimeException("skeleton method"); }
    public void setHttpAuthUsernamePassword(java.lang.String arg0, java.lang.String arg1, java.lang.String arg2, java.lang.String arg3) { throw new RuntimeException("skeleton method"); }
    public void setInitialScale(int arg0) { throw new RuntimeException("skeleton method"); }
    public void setLayerType(int arg0, android.graphics.Paint arg1) { throw new RuntimeException("skeleton method"); }
    public void setLayoutParams(android.view.ViewGroup.LayoutParams arg0) { throw new RuntimeException("skeleton method"); }
    public void setMapTrackballToArrowKeys(boolean arg0) { throw new RuntimeException("skeleton method"); }
    public void setNetworkAvailable(boolean arg0) { throw new RuntimeException("skeleton method"); }
    public void setOverScrollMode(int arg0) { throw new RuntimeException("skeleton method"); }
    public void setPictureListener(android.webkit.WebView.PictureListener arg0) { throw new RuntimeException("skeleton method"); }
    public void setScrollBarStyle(int arg0) { throw new RuntimeException("skeleton method"); }
    public void setVerticalScrollbarOverlay(boolean arg0) { throw new RuntimeException("skeleton method"); }
    public void setWebChromeClient(android.webkit.WebChromeClient arg0) { throw new RuntimeException("skeleton method"); }
    public void setWebViewClient(android.webkit.WebViewClient arg0) { throw new RuntimeException("skeleton method"); }
    public boolean shouldDelayChildPressedState() { throw new RuntimeException("skeleton method"); }
    public boolean showFindDialog(java.lang.String arg0, boolean arg1) { throw new RuntimeException("skeleton method"); }
    public void stopLoading() { throw new RuntimeException("skeleton method"); }
    public boolean zoomIn() { throw new RuntimeException("skeleton method"); }
    public boolean zoomOut() { throw new RuntimeException("skeleton method"); }
}
