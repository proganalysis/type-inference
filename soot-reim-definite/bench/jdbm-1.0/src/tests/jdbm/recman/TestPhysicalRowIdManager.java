/*

 *  $Id: TestPhysicalRowIdManager.java,v 1.5 2003/09/21 15:49:02 boisvert Exp $

 *

 *  Unit tests for PhysicalRowIdManager class

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


/**

 *  This class contains all Unit tests for {@link PhysicalRowIdManager}.

 */

public class TestPhysicalRowIdManager extends TestCase {



    public TestPhysicalRowIdManager(String name) {

        super(name);

    }



    public void setUp() {

        TestRecordFile.deleteTestFile();

    }



    public void tearDown() {

        TestRecordFile.deleteTestFile();

    }



    /**

     *  Test constructor

     */

    public void testCtor() throws Exception {

        RecordFile f = new RecordFile(TestRecordFile.testFileName);

        PageManager pm = new PageManager(f);

        PhysicalRowIdManager physMgr = new PhysicalRowIdManager(f, pm);



        f.forceClose();

    }



    /**

     *  Test basics

     */

    public void testBasics() throws Exception {



        RecordFile f = new RecordFile(TestRecordFile.testFileName);

        PageManager pm = new PageManager(f);

        PhysicalRowIdManager physMgr = new PhysicalRowIdManager(f, pm);



        // insert a 10,000 byte record.

        byte[] data = TestUtil.makeRecord(10000, (byte) 1);

        Location loc = physMgr.insert( data, 0, data.length );

        assertTrue("check data1",

               TestUtil.checkRecord(physMgr.fetch(loc), 10000, (byte) 1));



        // update it as a 20,000 byte record.

        data = TestUtil.makeRecord(20000, (byte) 2);

        Location loc2 = physMgr.update(loc, data, 0, data.length );

        assertTrue("check data2",

               TestUtil.checkRecord(physMgr.fetch(loc2), 20000, (byte) 2));



        // insert a third record. This'll effectively block the first one

        // from growing

        data = TestUtil.makeRecord(20, (byte) 3);

        Location loc3 = physMgr.insert(data, 0, data.length );

        assertTrue("check data3",

               TestUtil.checkRecord(physMgr.fetch(loc3), 20, (byte) 3));



        // now, grow the first record again

        data = TestUtil.makeRecord(30000, (byte) 4);

        loc2 = physMgr.update(loc2, data, 0, data.length );

        assertTrue("check data4",

               TestUtil.checkRecord(physMgr.fetch(loc2), 30000, (byte) 4));





        // delete the record

        physMgr.delete(loc2);



        f.forceClose();

    }



    /**

     *  Runs all tests in this class

     */

    public static void main(String[] args) {

        junit.textui.TestRunner.run(new TestSuite(TestPhysicalRowIdManager.class));

    }

}

