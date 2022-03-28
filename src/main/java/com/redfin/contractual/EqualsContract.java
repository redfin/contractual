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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.redfin.insist.Insist.*;

/**
 * A test contract that should be implemented by all instantiable types
 * that have overridden the {@link Object#equals(Object)} method.
 * This makes a best effort to verify that the type under test has not
 * violated the contract of object equality.
 * <br>
 * Note that the equals method is not friendly with regards to inheritance.
 * This contract makes a best effort to ensure the equality contracts with
 * regards to other instances of the same class but cannot ensure it if
 * superclasses break one or more of the equality contracts.
 *
 * @param <T> the class that is being tested.
 */
public interface EqualsContract<T> extends Testable<T> {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test contract requirements
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * Each call to the {@link Supplier#get} from the returned supplier should
     * return a new, non-null instance of type t that is equal according to the equals method
     * for that type to the value returned from {@link Testable#getInstance()}.
     * <p>
     * Therefore, if you start with:<br>
     * <pre>
     * {@code
     * T tA = getTestableInstance();
     * Supplier<T> supplier = getEqualsContractEqualInstanceSupplier();
     * T tB = supplier.get();
     * T tC = supplier.get();
     * }
     * </pre>
     * Then the following should be true:
     * <pre>
     * {@code
     * (1)
     * tA != tB
     * tA != tC
     * tB != tC
     * (2)
     * tA.equals(tB) == true
     * tA.equals(tC) == true
     * tB.equals(tC) == true
     * }
     * </pre>
     *
     * @return a non-null supplier of non-null type T instances that are
     * separate instances but which are equal.
     */
    Supplier<T> getEqualInstanceSupplier();

    /**
     * @return a new non-null instance of type T that is not equal to the value
     * returned from {@link Testable#getInstance()}.
     */
    T getNonEqualInstance();

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Test
    @DisplayName("the class is final")
    default void testClassIsFinal() {
        // Get test instance
        T a = getInstance();
        // Verify test preconditions
        assumes().withMessage("This test requires that 'a' be non-null")
                 .that(a)
                 .isNotNull();
        // Perform actual test
        asserts().withMessage("An instantiable class that overrides equals should be marked as final")
                 .that(Modifier.isFinal(a.getClass().getModifiers()))
                 .isTrue();
    }

    @Test
    @DisplayName("it is not equal to null")
    default void testAnObjectIsNotEqualToNull() {
        // Get test instances
        T a = getInstance();
        // Verify test preconditions
        assumes().withMessage("This test requires that 'a' be non-null")
                 .that(a)
                 .isNotNull();
        // Perform actual test
        asserts().withMessage("An object should not be equal to null")
                 .that(a)
                 .isNotEqualTo(null);
    }

    @Test
    @DisplayName("it is not equal to a different class")
    default void testAnObjectIsNotEqualToDifferentClass() {
        // Get test instances
        T a = getInstance();
        // Verify test preconditions
        assumes().withMessage("This test requires that 'a' be non-null")
                .that(a)
                .isNotNull();
        // Perform actual test
        Assertions.assertNotEquals(
                new Object(),
                a,
                "An object should not be equal to a different object type"
        );
    }

    @Test
    @DisplayName("it adheres to reflexivity")
    default void testReflexivityOfObject() {
        // Get test instances
        T a = getInstance();
        // Verify test preconditions
        assumes().withMessage("This test requires that 'a' be non-null")
                 .that(a)
                 .isNotNull();
        // Perform actual test
        asserts().withMessage("Object equality should be reflexive such that 'a.equals(a)' should be true")
                 .that(a)
                 .isEqualTo(a);
    }

    @Test
    @DisplayName("it adheres to the symmetry of equal objects")
    default void testSymmetryOfEqualObjects() {
        // Get test instances
        T a = getInstance();
        T b = getEqualInstanceSupplier().get();
        // Verify test preconditions
        assumes().withMessage("This test requires that 'a' be non-null")
                 .that(a)
                 .isNotNull();
        assumes().withMessage("This test requires that 'b' be non-null")
                 .that(b)
                 .isNotNull();
        assumes().withMessage("This test requires that 'a' be equal to 'b'")
                 .that(a)
                 .isEqualTo(b);
        assumes().withMessage("This test requires that 'a' and 'b' be different instances")
                 .that(a == b)
                 .isFalse();
        // Perform actual test
        asserts().withMessage("Object equality should be symmetrical so that if 'a.equals(b)' then it should also be that 'b.equals(a)'")
                 .that(b)
                 .isEqualTo(a);
    }

    @Test
    @DisplayName("it adheres to the symmetry of non-equal objects")
    default void testSymmetryOfNonEqualObjects() {
        // Get test instances
        T a = getInstance();
        T b = getNonEqualInstance();
        // Verify test preconditions
        assumes().withMessage("This test requires that 'a' be non-null")
                 .that(a)
                 .isNotNull();
        assumes().withMessage("This test requires that 'b' be non-null")
                 .that(b)
                 .isNotNull();
        assumes().withMessage("This test requires that 'a' not be equal to 'b'")
                 .that(a)
                 .isNotEqualTo(b);
        assumes().withMessage("This test requires that 'a' and 'b' be different instances")
                 .that(a)
                 .isNot(b);
        // Perform actual test
        asserts().withMessage("Object equality should be symmetrical so that if '!a.equals(b)' then it should also be that '!b.equals(a)'")
                 .that(a)
                 .isNotEqualTo(b);
    }

    @Test
    @DisplayName("it adheres to the transitivity of equal objects")
    default void testTransitivityOfEqualObjects() {
        // Get test instances
        T a = getInstance();
        Supplier<T> supplier = getEqualInstanceSupplier();
        T b = supplier.get();
        T c = supplier.get();
        // Verify test preconditions
        assumes().withMessage("This test requires that 'a' be non-null")
                 .that(a)
                 .isNotNull();
        assumes().withMessage("This test requires that 'b' be non-null")
                 .that(b)
                 .isNotNull();
        assumes().withMessage("This test requires that 'c' be non-null")
                 .that(c)
                 .isNotNull();
        assumes().withMessage("This test requires that 'a' be equal to 'b'")
                 .that(a)
                 .isEqualTo(b);
        assumes().withMessage("This test requires that 'b' be equal to 'c'")
                 .that(b)
                 .isEqualTo(c);
        assumes().withMessage("This test requires that 'a' and 'b' be different instances")
                 .that(a)
                 .isNot(b);
        assumes().withMessage("This test requires that 'a' and 'c' be different instances")
                 .that(a)
                 .isNot(c);
        assumes().withMessage("This test requires that 'b' and 'c' be different instances")
                 .that(b)
                 .isNot(c);
        // Perform actual test
        asserts().withMessage("Object equality should be transitive so that if 'a == b' and 'b == c' then it should also be that 'a == c'")
                 .that(a)
                 .isEqualTo(c);
    }

    @Test
    @DisplayName("it adheres to the transitivity of non-equal objects")
    default void testTransitivityOfNonEqualObjects() {
        // Get test instances
        T a = getInstance();
        T b = getEqualInstanceSupplier().get();
        T c = getNonEqualInstance();
        // Verify test preconditions
        assumes().withMessage("This test requires that 'a' be non-null")
                 .that(a)
                 .isNotNull();
        assumes().withMessage("This test requires that 'b' be non-null")
                 .that(b)
                 .isNotNull();
        assumes().withMessage("This test requires that 'c' be non-null")
                 .that(c)
                 .isNotNull();
        assumes().withMessage("This test requires that 'a' be equal to 'b'")
                 .that(a)
                 .isEqualTo(b);
        assumes().withMessage("This test requires that 'b' not be equal to 'c'")
                 .that(b)
                 .isNotEqualTo(c);
        assumes().withMessage("This test requires that 'a' and 'b' be different instances")
                 .that(a == b)
                 .isFalse();
        assumes().withMessage("This test requires that 'a' and 'c' be different instances")
                 .that(a == c)
                 .isFalse();
        assumes().withMessage("This test requires that 'b' and 'c' be different instances")
                 .that(b == c)
                 .isFalse();
        // Perform actual test
        asserts().withMessage("Object equality should be transitive so that if 'a == b' and 'b != c' then it should also be that 'a != c'")
                 .that(a)
                 .isNotEqualTo(c);
    }

    @Test
    @DisplayName("it adheres to the consistency of equal objects")
    default void testConsistencyOfEqualObjects() {
        // Get test instances
        T a = getInstance();
        T b = getEqualInstanceSupplier().get();
        // Verify test preconditions
        assumes().withMessage("This test requires that 'a' be non-null")
                 .that(a)
                 .isNotNull();
        assumes().withMessage("This test requires that 'b' be non-null")
                 .that(b)
                 .isNotNull();
        assumes().withMessage("This test requires that 'a' be equal to 'b'")
                 .that(a)
                 .isEqualTo(b);
        assumes().withMessage("This test requires that 'a' and 'b' be different instances")
                 .that(a)
                 .isNot(b);
        // Perform actual test
        asserts().withMessage("Object equality should be consistent so that if 'a.equals(b)' multiple calls without having mutated the objects should return true")
                 .that(a)
                 .isEqualTo(b);
    }

    @Test
    @DisplayName("it adheres to the consistency of non-equal objects")
    default void testConsistencyOfNonEqualObjects() {
        // Get test instances
        T a = getInstance();
        T b = getNonEqualInstance();
        // Verify test preconditions
        assumes().withMessage("This test requires that 'a' be non-null")
                 .that(a)
                 .isNotNull();
        assumes().withMessage("This test requires that 'b' be non-null")
                 .that(b)
                 .isNotNull();
        assumes().withMessage("This test requires that 'a' not be equal to 'b'")
                 .that(a)
                 .isNotEqualTo(b);
        assumes().withMessage("This test requires that 'a' and 'b' be different instances")
                 .that(a)
                 .isNot(b);
        // Perform actual test
        asserts().withMessage("Object equality should be consistent so that if '!a.equals(b)' multiple calls without having mutated the objects should return false")
                 .that(a)
                 .isNotEqualTo(b);
    }

    @Test
    @DisplayName("it generates a consistent hash code")
    default void testHashCodeConsistency() {
        // Get test instance
        T a = getInstance();
        // Verify test preconditions
        assumes().withMessage("This test requires that 'a' be non-null")
                 .that(a)
                 .isNotNull();
        // Perform actual test
        int hashCode = a.hashCode();
        asserts().withMessage("An object should return the same hash code through repetitive calls to the hashCode method")
                 .that(hashCode)
                 .isEqualTo(a.hashCode());
    }

    @Test
    @DisplayName("equal objects return equal hash codes")
    default void testEqualObjectsHaveEqualHashCodes() {
        // Get test instances
        T a = getInstance();
        Supplier<T> supplier = getEqualInstanceSupplier();
        T b = supplier.get();
        T c = supplier.get();
        // Verify test preconditions
        assumes().withMessage("This test requires that 'a' be non-null")
                 .that(a)
                 .isNotNull();
        assumes().withMessage("This test requires that 'b' be non-null")
                 .that(b)
                 .isNotNull();
        assumes().withMessage("This test requires that 'c' be non-null")
                 .that(c)
                 .isNotNull();
        assumes().withMessage("This test requires that 'a' be equal to 'b'")
                 .that(a)
                 .isEqualTo(b);
        assumes().withMessage("This test requires that 'b' be equal to 'c'")
                 .that(b)
                 .isEqualTo(c);
        assumes().withMessage("This test requires that 'a' be equal to 'c'")
                 .that(a)
                 .isEqualTo(c);
        assumes().withMessage("This test requires that 'a' and 'b' be different instances")
                 .that(a)
                 .isNot(b);
        assumes().withMessage("This test requires that 'a' and 'c' be different instances")
                 .that(a)
                 .isNot(c);
        assumes().withMessage("This test requires that 'b' and 'c' be different instances")
                 .that(b)
                 .isNot(c);
        // Perform actual test
        int hashCode = a.hashCode();
        Assertions.assertAll("Equal objects should all return the same, consistent hash code.",
                             Stream.of(() -> asserts().that(hashCode).isEqualTo(a.hashCode()),
                                       () -> asserts().that(hashCode).isEqualTo(b.hashCode()),
                                       () -> asserts().that(hashCode).isEqualTo(c.hashCode())));
    }
}
