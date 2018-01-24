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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.redfin.insist.Insist.*;

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
     * returned from {@link Testable#getInstance()} but not the
     * same instance.
     */
    T getComparableInstance();

    /**
     * @return an instance of type T that is "smaller", according to
     * the {@link Comparable#compareTo(Object)} method, than the instance
     * returned from {@link Testable#getInstance()}.
     */
    T getSmallerInstance();

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Test
    @DisplayName("satisfies the ComparableContract by thrown an exception when compared to null")
    default void testObjectThrowsExceptionWhenComparedToNull() {
        // Get test instances and validate precondition
        T a = getInstance();
        assumes().withMessage("This test requires that 'a' be non null")
                 .that(a)
                 .isNotNull();
        // Perform actual test
        //noinspection ConstantConditions
        assumes().withMessage("A comparable object should throw an exception when compared to a null object.")
                 .thatThrows(NullPointerException.class,
                             () -> a.compareTo(null));
    }

    @Test
    @DisplayName("satisfies the ComparableContract by returning zero when compared to itself")
    default void testObjectReturnsZeroWhenComparedToSelf() {
        // Get test instances and validate precondition
        T a = getInstance();
        assumes().withMessage("This test requires that 'a' be non null")
                 .that(a)
                 .isNotNull();
        // Perform actual test
        //noinspection EqualsWithItself
        asserts().withMessage("A comparable object should return 0 when compared to itself")
                 .that(a.compareTo(a))
                 .isZero();
    }

    @Test
    @DisplayName("satisfies the ComparableContract by returning zero when compared to a comparable object")
    default void testObjectReturnsZeroWhenComparedToComparableObject() {
        // Get test instances and validate precondition
        T a = getInstance();
        T b = getComparableInstance();
        assumes().withMessage("This test requires that 'a' be non null")
                 .that(a)
                 .isNotNull();
        assumes().withMessage("This test requires that 'b' be non null")
                 .that(b)
                 .isNotNull();
        // Perform actual test
        asserts().withMessage("A comparable object should return 0 when compared to an object which is comparable")
                 .that(a.compareTo(b))
                 .isZero();
    }

    @Test
    @DisplayName("satisfies the ComparableContract by returning a negative value when compared to a greater object")
    default void testObjectReturnsNegativeIntWhenComparedToGreaterObject() {
        // Get test instances and validate precondition
        T a = getInstance();
        T b = getSmallerInstance();
        assumes().withMessage("This test requires that 'a' be non null")
                 .that(a)
                 .isNotNull();
        assumes().withMessage("This test requires that 'b' be non null")
                 .that(b)
                 .isNotNull();
        // Perform actual test
        asserts().withMessage("A comparable object should return a negative int when compared to an object which is greater")
                 .that(b.compareTo(a))
                 .isStrictlyNegative();
    }

    @Test
    @DisplayName("satisfies the ComparableContract by returning a positive value when compared to a smaller object")
    default void testObjectReturnsPositiveIntWhenComparedToSmallerObject() {
        // Get test instances and validate precondition
        T a = getInstance();
        T b = getSmallerInstance();
        assumes().withMessage("This test requires that 'a' be non null")
                 .that(a)
                 .isNotNull();
        assumes().withMessage("This test requires that 'b' be non null")
                 .that(b)
                 .isNotNull();
        // Perform actual test
        asserts().withMessage("A comparable object should return a positive int when compared to an object which is smaller")
                 .that(a.compareTo(b))
                 .isStrictlyPositive();
    }
}
