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
package edu.brown.cs.mapreduce;

import java.io.File;
import java.util.*;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;

/**
 * @author pavlo
 *
 */
public abstract class AbstractHadoopClient {
   public static final String CONFIG_FILES[] = { "hadoop-default.xml", "hadoop-site.xml" };
   public static final String ENV_HADOOP_HOME = "HADOOP_HOME";
   public static final String ENV_HADOOP_CONF = "HADOOP_CONF_DIR";
   public static final Map<String, String> conf = new Hashtable<String, String>();
   
   public static Configuration getConfiguration() {
      String vars[] = { AbstractHadoopClient.ENV_HADOOP_HOME,
                        AbstractHadoopClient.ENV_HADOOP_CONF };
      
      for (String var : vars) {
         String val = System.getenv(var);
         if (val == null || val.length() == 0) {
            System.err.println("ERROR: The environment variable '" + var + "' is not defined!");
            System.exit(1);
         }
         AbstractHadoopClient.conf.put(var, val);
      } // FOR
      //
      // Load in the configuration files that we need
      //
      Configuration conf = new Configuration();
      for (String config_file : AbstractHadoopClient.CONFIG_FILES) {
         File file = new File(AbstractHadoopClient.conf.get(AbstractHadoopClient.ENV_HADOOP_CONF) + "/" + config_file);
         if (!file.exists()) {
            System.err.println("ERROR: The expected config file '" + file.getPath() + "' does not exist!");
            System.exit(1);
         }
         conf.addResource(new Path(file.getAbsolutePath()));
      }
      return (conf);
   }
      

}
