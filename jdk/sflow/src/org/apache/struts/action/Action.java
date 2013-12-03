package org.apache.struts.action;

import java.io.*;
import java.sql.*;
import java.util.*;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.apache.struts.Globals;
import org.apache.struts.config.ModuleConfig;
import org.apache.struts.util.MessageResources;
import org.apache.struts.util.RequestUtils;
import org.apache.struts.util.TokenProcessor;

import checkers.inference.sflow.quals.Tainted;
import checkers.inference.sflow.quals.Poly;
import checkers.inference.reim.quals.*;

public class Action {
    String ACTION_SERVLET_KEY;
    String APPLICATION_KEY;
    String DATA_SOURCE_KEY;
    String ERROR_KEY;
    String EXCEPTION_KEY;
    String FORM_BEANS_KEY;
    String FORWARDS_KEY;
    String LOCALE_KEY;
    String MAPPING_KEY;
    String MAPPINGS_KEY;
    String MESSAGE_KEY;
    String MESSAGES_KEY;
    String MULTIPART_KEY;
    String PLUG_INS_KEY;
    String REQUEST_PROCESSOR_KEY;
    String SERVLET_KEY;
    String TRANSACTION_TOKEN_KEY;
    protected Locale defaultLocale;
    protected ActionServlet servlet;
    public Action() { throw new RuntimeException("skeleton method"); }
    public ActionServlet getServlet() { throw new RuntimeException("skeleton method"); }
    public void setServlet(ActionServlet arg0) { throw new RuntimeException("skeleton method"); }
    public ActionForward execute(ActionMapping arg0, /*-@Tainted*/ ActionForm arg1, ServletRequest arg2, ServletResponse arg3) throws Exception { throw new RuntimeException("skeleton method"); }
    public ActionForward execute(ActionMapping arg0, /*-@Tainted*/ ActionForm arg1, HttpServletRequest arg2, HttpServletResponse arg3) throws Exception { throw new RuntimeException("skeleton method"); }
    public ActionForward perform(ActionMapping arg0, /*-@Tainted*/ ActionForm arg1, ServletRequest arg2, ServletResponse arg3) throws IOException,ServletException { throw new RuntimeException("skeleton method"); }
    public ActionForward perform(ActionMapping arg0, /*-@Tainted*/ ActionForm arg1, HttpServletRequest arg2, HttpServletResponse arg3) throws IOException,ServletException { throw new RuntimeException("skeleton method"); }
    protected String generateToken(/*@Readonly*/ HttpServletRequest arg0) { throw new RuntimeException("skeleton method"); }
    protected DataSource getDataSource(/*@Readonly*/ HttpServletRequest arg0) { throw new RuntimeException("skeleton method"); }
    protected DataSource getDataSource(/*@Readonly*/ HttpServletRequest arg0, String arg1) { throw new RuntimeException("skeleton method"); }
    protected Locale getLocale(/*@Readonly*/ HttpServletRequest arg0) { throw new RuntimeException("skeleton method"); }
    protected MessageResources getResources() { throw new RuntimeException("skeleton method"); }
    protected MessageResources getResources(/*@Readonly*/ HttpServletRequest arg0) { throw new RuntimeException("skeleton method"); }
    protected MessageResources getResources(/*@Readonly*/ HttpServletRequest arg0, String arg1) { throw new RuntimeException("skeleton method"); }
    protected boolean isCancelled(/*@Readonly*/ HttpServletRequest arg0) { throw new RuntimeException("skeleton method"); }
    protected boolean isTokenValid(/*@Readonly*/ HttpServletRequest arg0) { throw new RuntimeException("skeleton method"); }
    protected boolean isTokenValid(/*@Readonly*/ HttpServletRequest arg0, boolean arg1) { throw new RuntimeException("skeleton method"); }
    protected void resetToken(HttpServletRequest arg0) { throw new RuntimeException("skeleton method"); }
    protected void saveErrors(HttpServletRequest arg0, ActionErrors arg1) { throw new RuntimeException("skeleton method"); }
    protected void saveMessages(HttpServletRequest arg0, ActionMessages arg1) { throw new RuntimeException("skeleton method"); }
    protected void saveToken(HttpServletRequest arg0) { throw new RuntimeException("skeleton method"); }
    protected void setLocale(HttpServletRequest arg0, Locale arg1) { throw new RuntimeException("skeleton method"); }
    protected String toHex(byte[] arg0) { throw new RuntimeException("skeleton method"); }
}
