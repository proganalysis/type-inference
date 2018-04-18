/**
 * JDBM LICENSE v1.00
 *
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided
 * that the following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright
 *    statements and notices.  Redistributions must also contain a
 *    copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the
 *    above copyright notice, this list of conditions and the
 *    following disclaimer in the documentation and/or other
 *    materials provided with the distribution.
 *
 * 3. The name "JDBM" must not be used to endorse or promote
 *    products derived from this Software without prior written
 *    permission of Cees de Groot.  For written permission,
 *    please contact cg@cdegroot.com.
 *
 * 4. Products derived from this Software may not be called "JDBM"
 *    nor may "JDBM" appear in their names without prior written
 *    permission of Cees de Groot.
 *
 * 5. Due credit should be given to the JDBM Project
 *    (http://jdbm.sourceforge.net/).
 *
 * THIS SOFTWARE IS PROVIDED BY THE JDBM PROJECT AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL
 * CEES DE GROOT OR ANY CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Copyright 2000 (C) Cees de Groot. All Rights Reserved.
 * Contributions are Copyright (C) 2000 by their associated contributors.
 *
 */

package jdbm.htree;

import jdbm.RecordManager;

import jdbm.recman.BaseRecordManager;
import jdbm.recman.TestRecordFile;

import junit.framework.*;

/**
 *  Test cases for HTree rollback
 */
public class TestRollback
    extends TestCase
{

    public TestRollback( String name )
    {
        super(name);
    }

    public void setUp()
    {
        TestRecordFile.deleteTestFile();
    }

    public void tearDown()
    {
        TestRecordFile.deleteTestFile();
    }

    /**
     * Test case courtesy of Derek Dick (mailto:ddick@users.sourceforge.net)
     */
    public void testRollback1() 
        throws Exception
    {
        RecordManager recman;
        long root;

        // Note: We start out with an empty file
        recman = new BaseRecordManager( TestRecordFile.testFileName );

	root = recman.getNamedObject( "xyz" );
			 			
        HTree tree = null;
        if ( root == 0 ) {
            // create a new one
            tree = HTree.createInstance( recman );
            root = tree.getRecid();
            recman.setNamedObject( "xyz", root );
            recman.commit();
        } else {
            tree = HTree.load( recman, root );
        }

        tree.put( "Foo", "Bar" );
        tree.put( "Fo", "Fum" );

        recman.commit();

        tree.put( "Hello", "World" );

        recman.rollback();

        tree = HTree.load( recman, root );
        assertTrue( tree.get( "Foo" ).equals( "Bar" ) );
        assertTrue( tree.get( "Fo" ).equals( "Fum" ) );
        assertTrue( tree.get( "Hello" ) == null );
    }		

    
    /**
     * Test case courtesy of Derek Dick (mailto:ddick@users.sourceforge.net)
     */
    public void testRollback2() 
        throws Exception
    {
        RecordManager recman;
        long root;

        // Note: We start out with an empty file
        recman = new BaseRecordManager( TestRecordFile.testFileName );

	root = recman.getNamedObject( "xyz" );

        HTree tree = null;
        if ( root == 0 ) {
            // create a new one
            tree = HTree.createInstance( recman );
            root = tree.getRecid();
            recman.setNamedObject( "xyz", root );
            recman.commit();
        } else {
            tree = HTree.load( recman, root );
        }

        tree.put( "hello", "world" );
        tree.put( "goodnight", "gracie" );
        recman.commit();

        tree.put( "derek", "dick" );
        recman.rollback();

        assertTrue( tree.get( "derek" ) == null );
        assertTrue( tree.get( "goodnight" ).equals( "gracie" ) );
        assertTrue( tree.get( "hello" ).equals( "world" ) );
    }
	    
    
    /**
     *  Runs all tests in this class
     */
    public static void main( String[] args )
    {
        junit.textui.TestRunner.run( new TestSuite( TestRollback.class ) );
    }
}
