/*
 * Copyright: (c) 2016 Redfin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.redfin.contractual;

import com.redfin.insist.Insist;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * A test contract that should be implemented by the test classes for all
 * instantiable types that have implement the {@link Comparable} interface.
 * This verifies that the type under test has not violated the contract of
 * comparability.
 * <p>
 * Note that the portion of the {@link Comparable} contract regarding exceptions
 * is not tested by this contract and should be asserted by the implementer
 * if necessary. That is, if {@code a.compareTo(b)} throws an exception,
 * then {@code b.compareTo(a)} must also throw an exception.
 *
 * @param <T> the class that is being tested.
 */
public interface ComparableContract<T extends Comparable<T>> extends Testable<T> {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test contract requirements
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * @return an instance of type T that is comparable to the instance
     * returned from {@link Testable#getInstance_Testable()} but not the
     * same instance.
     */
    T getComparableInstance_ComparableContract();

    /**
     * @return an instance of type T that is "smaller", according to
     * the {@link Comparable#compareTo(Object)} method, than the instance
     * returned from {@link Testable#getInstance_Testable()}.
     */
    T getSmallerInstance_ComparableContract();

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Test
    default void testObjectThrowsExceptionWhenComparedToNull_ComparableContract() {
        // Get test instances and validate precondition
        T a = getInstance_Testable();
        Insist.assumption("This test requires that 'a' be non null")
              .that(a)
              .isNotNull();
        // Perform actual test
        Assertions.assertThrows(NullPointerException.class,
                                () -> a.compareTo(null));
    }

    @Test
    default void testObjectReturnsZeroWhenComparedToSelf_ComparableContract() {
        // Get test instances and validate precondition
        T a = getInstance_Testable();
        Insist.assumption("This test requires that 'a' be non null")
              .that(a)
              .isNotNull();
        // Perform actual test
        Insist.assertion("A comparable object should return 0 when compared to itself")
              .that(a.compareTo(a))
              .isZero();
    }

    @Test
    default void testObjectReturnsZeroWhenComparedToComparableObject_ComparableContract() {
        // Get test instances and validate precondition
        T a = getInstance_Testable();
        T b = getComparableInstance_ComparableContract();
        Insist.assumption("This test requires that 'a' be non null")
              .that(a)
              .isNotNull();
        Insist.assumption("This test requires that 'b' be non null")
              .that(b)
              .isNotNull();
        // Perform actual test
        Insist.assertion("A comparable object should return 0 when compared to an object which is comparable")
              .that(a.compareTo(b))
              .isZero();
    }

    @Test
    default void testObjectReturnsNegativeIntWhenComparedToGreaterObject_ComparableContract() {
        // Get test instances and validate precondition
        T a = getInstance_Testable();
        T b = getSmallerInstance_ComparableContract();
        Insist.assumption("This test requires that 'a' be non null")
              .that(a)
              .isNotNull();
        Insist.assumption("This test requires that 'b' be non null")
              .that(b)
              .isNotNull();
        // Perform actual test
        Insist.assertion("A comparable object should return a negative int when compared to an object which is greater")
              .that(b.compareTo(a))
              .isStrictlyNegative();
    }

    @Test
    default void testObjectReturnsPositiveIntWhenComparedToSmallerObject_ComparableContract() {
        // Get test instances and validate precondition
        T a = getInstance_Testable();
        T b = getSmallerInstance_ComparableContract();
        Insist.assumption("This test requires that 'a' be non null")
              .that(a)
              .isNotNull();
        Insist.assumption("This test requires that 'b' be non null")
              .that(b)
              .isNotNull();
        // Perform actual test
        Insist.assertion("A comparable object should return a positive int when compared to an object which is smaller")
              .that(a.compareTo(b))
              .isStrictlyPositive();
    }
}
