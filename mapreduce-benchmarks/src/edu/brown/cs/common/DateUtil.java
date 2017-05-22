/***************************************************************************
*   Copyright (C) 2009 by Andy Pavlo, Brown University                    *
*   http://www.cs.brown.edu/~pavlo/                                       *
*                                                                         *
*   Permission is hereby granted, free of charge, to any person obtaining *
*   a copy of this software and associated documentation files (the       *
*   "Software"), to deal in the Software without restriction, including   *
*   without limitation the rights to use, copy, modify, merge, publish,   *
*   distribute, sublicense, and/or sell copies of the Software, and to    *
*   permit persons to whom the Software is furnished to do so, subject to *
*   the following conditions:                                             *
*                                                                         *
*   The above copyright notice and this permission notice shall be        *
*   included in all copies or substantial portions of the Software.       *
*                                                                         *
*   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,       *
*   EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF    *
*   MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.*
*   IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR     *
*   OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, *
*   ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR *
*   OTHER DEALINGS IN THE SOFTWARE.                                       *
***************************************************************************/
package edu.brown.cs.common;

import java.text.SimpleDateFormat;
import java.util.*;

public abstract class DateUtil {
   
   protected final static SimpleDateFormat secFormatter = new SimpleDateFormat("ss");
   protected final static SimpleDateFormat sqlFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
   protected final static SimpleDateFormat stdFormatter = new SimpleDateFormat("MM-dd-yy HH:mm:ss");
   
   public final static String months[] = { "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                                           "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
   public final static String years[] = { "2007", "2008" };
   public final static String days[] = { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10",
                                         "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
                                         "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31" };
   public final static String hours[] = { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" };
   public final static String minutes[] = { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10",
                                            "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
                                            "21", "22", "23", "24", "25", "26", "27", "28", "29", "30",
                                            "31", "32", "33", "34", "35", "36", "37", "38", "39", "40",
                                            "41", "42", "43", "44", "45", "46", "47", "48", "49", "50",
                                            "51", "52", "53", "54", "55", "56", "57", "58", "59", };
   public final static String ampm[] = { "am", "pm" };
   
   public static int getSeconds(Date time) {
      int ret = 0;
      try { 
         ret = Integer.parseInt(DateUtil.secFormatter.format(time));
      } catch (Exception ex) {
         ex.printStackTrace();
         System.exit(-1);
      }
      return (ret);
   }
   
   public static String convertDateToSQL(Date time) {
      String ret = null;
      try { 
         ret = DateUtil.sqlFormatter.format(time);
      } catch (Exception ex) {
         ex.printStackTrace();
         System.exit(-1);
      }
      return (ret);
   }
   
   public static String format(Date time) {
      if (time == null) {
         return ("-");
      }
      return (DateUtil.stdFormatter.format(time));
   }
   
   public static String format(Long time) {
      if (time == null) {
         return ("-");
      }
      return (DateUtil.stdFormatter.format(new Date(time)));
   }
   
   public static String convertSecondsToString(double seconds) {
      // TODO: Make me nice!
      return (Math.round(seconds) + " seconds");
   }

}
