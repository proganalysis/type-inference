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
package edu.brown.cs.mapreduce.benchmarks;

import org.apache.hadoop.io.*;

/**
 * @author pavlo
 *
 */
public class CompositeKey implements Comparable<Object> {
   public final String key;
   public final Integer type;
   public static final String DELIMITER = "|";
   
   public CompositeKey(String key, Integer type) {
      this.key = key;
      this.type = type;
   }
   public CompositeKey(String str) throws Exception {
      String fields[] = CompositeKey.parse(str);
      this.key = CompositeKey.getKey(fields);
      this.type = CompositeKey.getType(fields);
   }
   
   //
   // Key
   //
   public static String getKey(String fields[]) throws Exception {
      return (fields[0].substring(1, fields[0].length()));
   }
   public static String getKey(String str) throws Exception {
      String fields[] = CompositeKey.parse(str);
      return (CompositeKey.getKey(fields));
   }
   
   //
   // Type
   //
   public static Integer getType(String fields[]) throws Exception {
      return (Integer.parseInt(fields[1].substring(0, fields[1].length() - 1)));
   }
   public static Integer getType(String str) throws Exception {
      String fields[] = CompositeKey.parse(str);
      return (CompositeKey.getType(fields));
   }
   
   public static String[] parse(String str) throws Exception {
      if (str.charAt(0) != '<' && str.charAt(str.length() - 1) != '>') {
         throw new Exception("Invalid CompositeKey string format: " + str);
      }
      String fields[] = str.split("\\" + CompositeKey.DELIMITER);
      if (fields.length == 0) {
         throw new Exception("Invalid Delimiter split for CompositeKey string format: " + str);
      }
      return (fields);
   }
   
   public int compareTo(Object _o) {
      if (_o != null && _o instanceof CompositeKey) {
         CompositeKey o = (CompositeKey)_o; 
         if (o.type != this.type) {
            System.err.println("Comparing two keys [" + this.toString() + "<->" + o.toString() + "] with different types!");
         } else return (this.key.compareTo(o.key));
      }
      return (0);
   }
   public String toString() {
      return ("<" + this.key + CompositeKey.DELIMITER + this.type + ">"); 
   }
   public Text toText() {
      return (new Text(this.toString()));
   }
   
   public static class CompositeKeyComparator extends WritableComparator {
      
      public CompositeKeyComparator() {
         super(Text.class);
      }
      
      /* (non-Javadoc)
       * @see org.apache.hadoop.io.WritableComparator#compare(org.apache.hadoop.io.WritableComparable, org.apache.hadoop.io.WritableComparable)
       */
      @SuppressWarnings("rawtypes")
	@Override
      public int compare(WritableComparable arg0, WritableComparable arg1) {
         //
         // HACK: If they are text, turn them into CompositeKeys and then compare
         //
         if (arg0 instanceof Text && arg1 instanceof Text) {
            try {
               CompositeKey ckey0 = new CompositeKey(((Text)arg0).toString());
               CompositeKey ckey1 = new CompositeKey(((Text)arg1).toString());
               return (ckey0.compareTo(ckey1));
            } catch (Exception ex) {
               ex.printStackTrace();
               System.exit(1);
            }
         }
         return super.compare(arg0, arg1);
      }
      
      
      
   } // END CLASS
   
} // END CLASS