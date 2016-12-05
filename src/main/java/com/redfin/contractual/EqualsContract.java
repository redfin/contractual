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

import java.lang.reflect.Modifier;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * A test contract that should be implemented by all instantiable types
 * that have overridden the {@link Object#equals(Object)} method.
 * This makes a best effort to verify that the type under test has not
 * violated the contract of object equality.
 * <p>
 * Note that the equals method is not friendly with regards to inheritance.
 * This contract makes a best effort to ensure the equality contracts with
 * regards to other instances of the same class but cannot ensure it up or
 * down the inheritance hierarchy. An example that breaks the symmetrical
 * portion of the equality contract in a way unable to be tested by this test
 * contract:
 *
 * <pre>
 *     <code>
 * public class A {
 *     private final int i;
 *
 *     public A(int i) {
 *         this.i = i;
 *     }
 *
 *     {@literal @}Override
 *     public boolean equals(Object obj) {
 *         return obj instanceof A {@literal &&} i == ((A) obj).i;
 *     }
 * }
 *
 * public class B extends A {
 *     private final int i;
 *
 *     public B(int i, int j) {
 *         super(i);
 *         this.j = j;
 *     }
 *
 *     {@literal @}Override
 *     public boolean equals(Object obj) {
 *         return obj instanceof B {@literal &&} super.equals(obj) {@literal &&} j == ((B) obj).j;
 *     }
 * }
 *
 * public void testSymmetricEquality() {
 *     A a = new A(1);
 *     B b = new B(1, 2);
 *     a.equals(b); // true
 *     b.equals(a); // false
 * }
 *     </code>
 * </pre>
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
     * for that type to the value returned from {@link Testable#getInstance_Testable()}.
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
    Supplier<T> getEqualInstanceSupplier_EqualsContract();

    /**
     * @return a new non-null instance of type T that is not equal to the value
     * returned from {@link Testable#getInstance_Testable()}.
     */
    T getNonEqualInstance_EqualsContract();

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Test
    default void testClassIsFinal_EqualsContract() {
        // Get test instance
        T a = getInstance_Testable();
        // Verify test preconditions
        Insist.assumption("This test requires that 'a' be non-null")
              .that(a)
              .isNotNull();
        // Perform actual test
        Insist.assertion("An instantiable class that overrides equals should be marked as final")
              .that(Modifier.isFinal(a.getClass().getModifiers()))
              .isTrue();
    }

    @Test
    default void testAnObjectIsNotEqualToNull_EqualsContract() {
        // Get test instances
        T a = getInstance_Testable();
        // Verify test preconditions
        Insist.assumption("This test requires that 'a' be non-null")
              .that(a)
              .isNotNull();
        // Perform actual test
        Insist.assertion("An object should not be equal to null")
              .that(a)
              .isNotEqualTo(null);
    }

    @Test
    default void testReflexivityOfObject_EqualsContract() {
        // Get test instances
        T a = getInstance_Testable();
        // Verify test preconditions
        Insist.assumption("This test requires that 'a' be non-null")
              .that(a)
              .isNotNull();
        // Perform actual test
        Insist.assertion("Object equality should be reflexive such that 'a.equals(a)' should be true")
              .that(a)
              .isEqualTo(a);
    }

    @Test
    default void testSymmetryOfEqualObjects_EqualsContract() {
        // Get test instances
        T a = getInstance_Testable();
        T b = getEqualInstanceSupplier_EqualsContract().get();
        // Verify test preconditions
        Insist.assumption("This test requires that 'a' be non-null")
              .that(a)
              .isNotNull();
        Insist.assumption("This test requires that 'b' be non-null")
              .that(b)
              .isNotNull();
        Insist.assumption("This test requires that 'a' be equal to 'b'")
              .that(a)
              .isNotEqualTo(b);
        Insist.assumption("This test requires that 'a' and 'b' be different instances")
              .that(a == b)
              .isFalse();
        // Perform actual test
        Insist.assertion("Object equality should be symmetrical so that if 'a.equals(b)' then it should also be that 'b.equals(a)'")
              .that(b)
              .isEqualTo(a);
    }

    @Test
    default void testSymmetryOfNonEqualObjects_EqualsContract() {
        // Get test instances
        T a = getInstance_Testable();
        T b = getNonEqualInstance_EqualsContract();
        // Verify test preconditions
        Insist.assumption("This test requires that 'a' be non-null")
              .that(a)
              .isNotNull();
        Insist.assumption("This test requires that 'b' be non-null")
              .that(b)
              .isNotNull();
        Insist.assumption("This test requires that 'a' not be equal to 'b'")
              .that(a)
              .isNotEqualTo(b);
        Insist.assumption("This test requires that 'a' and 'b' be different instances")
              .that(a == b)
              .isFalse();
        // Perform actual test
        Insist.assertion("Object equality should be symmetrical so that if '!a.equals(b)' then it should also be that '!b.equals(a)'")
              .that(a)
              .isNotEqualTo(b);
    }

    @Test
    default void testTransitivityOfEqualObjects_EqualsContract() {
        // Get test instances
        T a = getInstance_Testable();
        Supplier<T> supplier = getEqualInstanceSupplier_EqualsContract();
        T b = supplier.get();
        T c = supplier.get();
        // Verify test preconditions
        Insist.assumption("This test requires that 'a' be non-null")
              .that(a)
              .isNotNull();
        Insist.assumption("This test requires that 'b' be non-null")
              .that(b)
              .isNotNull();
        Insist.assumption("This test requires that 'c' be non-null")
              .that(c)
              .isNotNull();
        Insist.assumption("This test requires that 'a' be equal to 'b'")
              .that(a)
              .isEqualTo(b);
        Insist.assumption("This test requires that 'b' be equal to 'c'")
              .that(b)
              .isEqualTo(c);
        Insist.assumption("This test requires that 'a' and 'b' be different instances")
              .that(a == b)
              .isFalse();
        Insist.assumption("This test requires that 'a' and 'c' be different instances")
              .that(a == c)
              .isFalse();
        Insist.assumption("This test requires that 'b' and 'c' be different instances")
              .that(b == c)
              .isFalse();
        // Perform actual test
        Insist.assertion("Object equality should be transitive so that if 'a == b' and 'b == c' then it should also be that 'a == c'")
              .that(a)
              .isEqualTo(c);
    }

    @Test
    default void testTransitivityOfNonEqualObjects_EqualsContract() {
        // Get test instances
        T a = getInstance_Testable();
        T b = getEqualInstanceSupplier_EqualsContract().get();
        T c = getNonEqualInstance_EqualsContract();
        // Verify test preconditions
        Insist.assumption("This test requires that 'a' be non-null")
              .that(a)
              .isNotNull();
        Insist.assumption("This test requires that 'b' be non-null")
              .that(b)
              .isNotNull();
        Insist.assumption("This test requires that 'c' be non-null")
              .that(c)
              .isNotNull();
        Insist.assumption("This test requires that 'a' be equal to 'b'")
              .that(a)
              .isEqualTo(b);
        Insist.assumption("This test requires that 'b' not be equal to 'c'")
              .that(b)
              .isNotEqualTo(c);
        Insist.assumption("This test requires that 'a' and 'b' be different instances")
              .that(a == b)
              .isFalse();
        Insist.assumption("This test requires that 'a' and 'c' be different instances")
              .that(a == c)
              .isFalse();
        Insist.assumption("This test requires that 'b' and 'c' be different instances")
              .that(b == c)
              .isFalse();
        // Perform actual test
        Insist.assertion("Object equality should be transitive so that if 'a == b' and 'b != c' then it should also be that 'a != c'")
              .that(a)
              .isNotEqualTo(c);
    }

    @Test
    default void testConsistencyOfEqualObjects_EqualsContract() {
        // Get test instances
        T a = getInstance_Testable();
        T b = getEqualInstanceSupplier_EqualsContract().get();
        // Verify test preconditions
        Insist.assumption("This test requires that 'a' be non-null")
              .that(a)
              .isNotNull();
        Insist.assumption("This test requires that 'b' be non-null")
              .that(b)
              .isNotNull();
        Insist.assumption("This test requires that 'a' be equal to 'b'")
              .that(a)
              .isEqualTo(b);
        Insist.assumption("This test requires that 'a' and 'b' be different instances")
              .that(a == b)
              .isFalse();
        // Perform actual test
        Insist.assertion("Object equality should be consistent so that if 'a.equals(b)' multiple calls without having mutated the objects should return true")
              .that(a)
              .isEqualTo(b);
    }

    @Test
    default void testConsistencyOfNonEqualObjects_EqualsContract() {
        // Get test instances
        T a = getInstance_Testable();
        T b = getNonEqualInstance_EqualsContract();
        // Verify test preconditions
        Insist.assumption("This test requires that 'a' be non-null")
              .that(a)
              .isNotNull();
        Insist.assumption("This test requires that 'b' be non-null")
              .that(b)
              .isNotNull();
        Insist.assumption("This test requires that 'a' not be equal to 'b'")
              .that(a)
              .isNotEqualTo(b);
        Insist.assumption("This test requires that 'a' and 'b' be different instances")
              .that(a == b)
              .isFalse();
        // Perform actual test
        Insist.assertion("Object equality should be consistent so that if '!a.equals(b)' multiple calls without having mutated the objects should return false")
              .that(a)
              .isNotEqualTo(b);
    }

    @Test
    default void testHashCodeConsistency_EqualsContract() {
        // Get test instance
        T a = getInstance_Testable();
        // Verify test preconditions
        Insist.assumption("This test requires that 'a' be non-null")
              .that(a)
              .isNotNull();
        // Perform actual test
        int hashCode = a.hashCode();
        Insist.assertion("An object should return the same hash code through repetitive calls to the hashCode method")
              .that(hashCode)
              .isEqualTo(a.hashCode());
    }

    @Test
    default void testEqualObjectsHaveEqualHashCodes_EqualsContract() {
        // Get test instances
        T a = getInstance_Testable();
        Supplier<T> supplier = getEqualInstanceSupplier_EqualsContract();
        T b = supplier.get();
        T c = supplier.get();
        // Verify test preconditions
        Insist.assumption("This test requires that 'a' be non-null")
              .that(a)
              .isNotNull();
        Insist.assumption("This test requires that 'b' be non-null")
              .that(b)
              .isNotNull();
        Insist.assumption("This test requires that 'c' be non-null")
              .that(c)
              .isNotNull();
        Insist.assumption("This test requires that 'a' be equal to 'b'")
              .that(a)
              .isEqualTo(b);
        Insist.assumption("This test requires that 'b' be equal to 'c'")
              .that(b)
              .isEqualTo(c);
        Insist.assumption("This test requires that 'a' be equal to 'c'")
              .that(a)
              .isEqualTo(c);
        Insist.assumption("This test requires that 'a' and 'b' be different instances")
              .that(a == b)
              .isFalse();
        Insist.assumption("This test requires that 'a' and 'c' be different instances")
              .that(a == c)
              .isFalse();
        Insist.assumption("This test requires that 'b' and 'c' be different instances")
              .that(b == c)
              .isFalse();
        // Perform actual test
        int hashCode = a.hashCode();
        Assertions.assertAll("Equal objects should all return the same, consistent hash code.",
                             Stream.of(() -> Insist.assertion().that(hashCode).isEqualTo(a.hashCode()),
                                       () -> Insist.assertion().that(hashCode).isEqualTo(b.hashCode()),
                                       () -> Insist.assertion().that(hashCode).isEqualTo(c.hashCode())));
    }
}
