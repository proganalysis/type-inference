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

import java.util.*;

/**
 * @author pavlo
 *
 */
public abstract class RandomUtil {
   public static final Random rand = new Random();
   public static final Integer default_stop = Math.round(System.currentTimeMillis() / (long)1000);
   public static final Integer default_start = RandomUtil.default_stop - 153792000;
   
   public static String randomString(int size) {
      String ret = new String();
      
      for (int ctr = 0; ctr < size; ctr++) {
         char data[] = { (char)(RandomUtil.rand.nextInt(93) + 33) };
         //
         // Skip quotation marks
         //
         if (data[0] == '"') {
            ctr--;
         } else {
            ret += new String(data);
         }
      }
      return (ret);
   }
   
   public static String randomIP() {
      String ret = new String();
      ret += RandomUtil.rand.nextInt(225) + ".";
      ret += RandomUtil.rand.nextInt(225) + ".";
      ret += RandomUtil.rand.nextInt(225) + ".";
      ret += RandomUtil.rand.nextInt(254);
      return (ret);
   }
   
   public static Date randomDate() {
      return (RandomUtil.randomDate(RandomUtil.default_start, RandomUtil.default_stop));
   }
   
   public static Date randomDate(Integer start, Integer stop) {
      int timestamp = RandomUtil.rand.nextInt(stop - start) + start;
      return (new Date(timestamp * 1000));
   }

}
