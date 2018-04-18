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

package jdbm.helper;

import java.lang.ref.SoftReference;
import junit.framework.AssertionFailedError;

/**
 * Unit test for {@link SoftCache}.
 * 
 * @author <a href="mailto:dranatunga@users.sourceforge.net">Dilum Ranatunga</a>
 * @version $Id: TestSoftCache.java,v 1.1 2003/11/01 13:29:57 dranatunga Exp $
 */
public class TestSoftCache extends TestCachePolicy {

    public TestSoftCache(String name) {
        super(name);
    }

    /**
     * Tests {@link SoftCache#SoftCache()} ,
     * {@link SoftCache#SoftCache(CachePolicy)},
     * {@link SoftCache#SoftCache(float, CachePolicy)}.
     */
    public void testConstructor() {
        { // test that null parameter fails
            try {
                new SoftCache(null);
                fail("constructor will null should have failed.");
            } catch (NullPointerException npe) { }
            try {
                new SoftCache(null);
                fail("constructor will null should have failed.");
                new SoftCache(0.5f, null);
            } catch (NullPointerException npx) { }
        }

        { // test that bad load factor fails.
            try {
                new SoftCache(0.0f, new MRU(5));
                fail("zero load factor should fail");
            } catch (IllegalArgumentException iax) { }
        }

        { // test correct instantiations
            new SoftCache();
            new SoftCache(new MRU(5));
            new SoftCache(0.1f, new MRU(5));
            new SoftCache(10.0f, new MRU(5));
        }
    }

    /**
     * Ensures that {@link SoftCache#put(Object, Object)} operations are
     * delegated to the internal cache.
     */
    public void testPutDelegation() throws CacheEvictionException {
        final int capacity = 5;
        final CachePolicy internal = new MRU(capacity);
        final CachePolicy level2 = new SoftCache(internal);
        final Object key = new Integer(0);
        final Object value = "EXPECTED-VALUE";
        level2.put(key, value);
        assertSame("L2.get() did not return expected.", value, level2.get(key));
        assertSame("L1.get() did not return expected.", value, internal.get(key));
        causeEviction(level2, capacity);
        assertNull(internal.get(key));
    }

    /**
     * Shows that a soft cache can recover an evicted object, and that
     * this recovery is conditioned on soft references not having been
     * cleared.
     */
    public void testL2Recovery() throws CacheEvictionException {
        final int capacity = 5;
        // since soft reference clears are not predictable, we use a indicator indicator
        final SoftReference indicator = new SoftReference(createLargeObject());
        final CachePolicy internal = new MRU(capacity);
        final CachePolicy level2 = new SoftCache(internal);

        for (int attempts = 100; attempts-- > 0;) {
            // keep looping until we get to complete the whole test, or until.
            // we've tried the number of attempts we're willing to make.

            boolean heuristicRedFlag = false;

            for (int i = 0; i <= capacity; ++i) { // note <=
                level2.put("" + i, createLargeObject());
            }
            // look in least recently used: '0'
            assertNull("'0' should have been evicted from internal",
                       internal.get("0"));
            assertNotNull("'0' should still be accessible through L2",
                          level2.get("0"));
            // this will cause '0' to get readded to internal:

            // this construct restarts the test if indicator
            if (null == indicator.get()) {
                heuristicRedFlag = true;
            }
            causeGarbageCollection();
            if (null != indicator.get()) {
                heuristicRedFlag = true;
            }

            try {
                // since '0' was readded, least recently used is '1'
                assertNull("'1' should have been evicted from internal",
                           internal.get("1"));
                assertNull("Soft references should have cleared during gc",
                           level2.get("1"));
                // getting here means test successful. no need to make
                // any further attempts.
                return;
            } catch (AssertionFailedError afe) {
                // The test failed. But if we have a heuristic red flag,
                // we should return. Otherwise, fail the test.
                if (!heuristicRedFlag) {
                    throw afe;
                }
            }
        }

        // getting here means we were unable to test the condition.
        fail("Could not perform soft cache test to completion.");
    }

    /**
     * Tests {@link SoftCache#removeAll()}
     */
    public void testRemoveAll() throws CacheEvictionException {
        final int capacity = 5;
        final CachePolicy internal = new MRU(capacity);
        final CachePolicy level2 = new SoftCache(internal);

        // add a bunch.
        for (int i = 0; i < (capacity + 2); ++i) {
            level2.put("" + i, createLargeObject());
        }
        // show that some are still in internal
        int count = 0;
        for (int i = 0; i < (capacity + 2); ++i) {
            if (null != internal.get("" + i)) {
                count++;
            }
        }
        assertTrue(count > 0);

        // show that all exist in level2.
        for (int i = 0; i < (capacity + 2); ++i) {
            assertNotNull(level2.get("" + i));
        }

        // show that none exist after removeall.
        level2.removeAll();
        for (int i = 0; i < (capacity + 2); ++i) {
            assertNull(internal.get("" + i));
            assertNull(level2.get("" + i));
        }
    }

    /**
     * Shows that {@link SoftCache#remove(Object)} clears objects that
     * are <em>only</em> in L2.
     */
    public void testRemoveClearsL2Objects() throws CacheEvictionException {
        final int capacity = 5;
        final CachePolicy internal = new MRU(capacity);
        final CachePolicy level2 = new SoftCache(internal);

        { // control test: show recovery
            for (int i = 0; i < (capacity + 2); ++i) {
                level2.put("" + i, createLargeObject());
            }
            // "0", "1" evicted.
            assertNull(internal.get("0"));
            assertNull(internal.get("1"));
            assertNotNull(internal.get("2"));
            assertNotNull(level2.get("0"));
            assertNotNull(level2.get("1"));
            assertNotNull(level2.get("2"));
        }

        level2.removeAll(); // this has already been tested

        { // test remove:
            for (int i = 0; i < (capacity + 2); ++i) {
                level2.put("" + i, createLargeObject());
            }
            // "0", "1" evicted, as before.
            // Now, we remove "1" -- L2 only object, and 
            //                "2" -- internal and L2 object 
            level2.remove("1");
            level2.remove("2");
            assertNull(internal.get("0"));
            assertNull(internal.get("1"));
            assertNull(internal.get("2"));
            assertNotNull(level2.get("0"));
            assertNull(level2.get("1"));
            assertNull(level2.get("2"));
        }
    }

    public void testExceptionDuringReinstate() throws CacheEvictionException {
        final int capacity = 5;
        final CachePolicy internal = new MRU(capacity);
        final CachePolicy level2 = new SoftCache(internal);
        final CachePolicyListener listener = new ThrowingListener();

        // Fill internal cache, and overflow by three: "0", "1", "2" evicted from internal
        for (int i = 0; i < (capacity + 3); ++i) {
            level2.put("" + i, new Object());
        }

        { // Null test. Cause "0" to get reinserted.
            assertNull(internal.get("0"));
            assertNotNull(level2.get("0"));
            assertNotNull(internal.get("0"));
            }

        { // Test handling of exception during reinsertion
            level2.addListener(listener);
            assertNull(internal.get("1"));
            assertNull(level2.get("1")); // this would have caused a reinsertion
            assertNull(internal.get("1"));
        }

        { // Test stability: when listener is removed, things are back to normal
            level2.removeListener(listener);
            assertNull(internal.get("2"));
            assertNotNull(level2.get("2"));
            assertNotNull(internal.get("2"));
        }
    }

    protected CachePolicy createInstance(final int capacity) {
        return new SoftCache(new MRU(capacity));
    }
}
