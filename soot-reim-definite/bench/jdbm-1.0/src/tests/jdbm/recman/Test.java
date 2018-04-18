/*
 *  $Id: Test.java,v 1.1 2000/05/06 00:00:53 boisvert Exp $
 *
 *  Package test suite
 *
 *  Simple db toolkit
 *  Copyright (C) 1999, 2000 Cees de Groot <cg@cdegroot.com>
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Library General Public License 
 *  as published by the Free Software Foundation; either version 2 
 *  of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Library General Public License for more details.
 *
 *  You should have received a copy of the GNU Library General Public License 
 *  along with this library; if not, write to the Free Software Foundation, 
 *  Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA
 */
package jdbm.recman;

import junit.framework.*;

public class Test extends TestCase {

  public Test(String name) { super(name); }

  public static junit.framework.Test suite() {
    TestSuite retval = new TestSuite();
    retval.addTest(new TestSuite(TestBlockIo.class));
    retval.addTest(new TestSuite(TestLocation.class));
    retval.addTest(new TestSuite(TestFileHeader.class));
    retval.addTest(new TestSuite(TestPhysicalRowId.class));
    retval.addTest(new TestSuite(TestFreePhysicalRowId.class));
    retval.addTest(new TestSuite(TestFreePhysicalRowIdPage.class));
    retval.addTest(new TestSuite(TestFreePhysicalRowIdPageManager.class));
    retval.addTest(new TestSuite(TestFreeLogicalRowIdPage.class));
    retval.addTest(new TestSuite(TestFreeLogicalRowIdPageManager.class));
    retval.addTest(new TestSuite(TestRecordHeader.class));
    retval.addTest(new TestSuite(TestTransactionManager.class));
    retval.addTest(new TestSuite(TestRecordFile.class));
    retval.addTest(new TestSuite(TestPageHeader.class));
    retval.addTest(new TestSuite(TestPageCursor.class));
    retval.addTest(new TestSuite(TestPageManager.class));
    retval.addTest(new TestSuite(TestDataPage.class));
    retval.addTest(new TestSuite(TestPhysicalRowIdManager.class));
    retval.addTest(new TestSuite(TestLogicalRowIdManager.class));
    retval.addTest(new TestSuite(TestRecordManager.class));
    return retval;
  }
  public static void main(String[] args) {
    junit.textui.TestRunner.run(suite());
  }

}
