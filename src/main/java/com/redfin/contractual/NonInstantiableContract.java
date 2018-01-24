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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.redfin.insist.Insist.*;

/**
 * A test contract that should be implemented by all types that are intended
 * to be static only (i.e. a class that should never be instantiated). This
 * will verify that the class conforms to being a true static class.
 * <p>
 * A non-instantiable class should:<br>
 * <ul>
 * <li>be marked as final</li>
 * <li>have only a single, private, non-argument constructor</li>
 * <li>throw an {@link AssertionError} if the constructor is called via reflection</li>
 * <li>have only static members</li>
 * <li>have only static methods</li>
 * </ul>
 *
 * @param <T> the class that is being tested.
 */
public interface NonInstantiableContract<T> {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test contract requirements
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * @return the class object of the class being tested.
     * Should never return null.
     */
    Class<T> getTestClass();

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Test
    @DisplayName("satisfies the NonInstantiable contract by being a class marked as final")
    default void testClassIsMarkedAsFinal() {
        // Get test class and validate precondition
        Class<T> clazz = getTestClass();
        assumes().withMessage("This test requires that getTestClass() returns a non-null Class")
                 .that(clazz)
                 .isNotNull();
        // Perform actual test
        asserts().withMessage("A non instantiable class should be marked as final")
                 .that(Modifier.isFinal(clazz.getModifiers()))
                 .isTrue();
    }

    @Test
    @DisplayName("satisfies the NonInstantiable contract by being a class with only one constructor")
    default void testClassHasOnlyOneConstructor() {
        // Get test class and validate precondition
        Class<T> clazz = getTestClass();
        assumes().withMessage("This test requires that getTestClass() returns a non-null Class")
                 .that(clazz)
                 .isNotNull();
        // Perform actual test
        asserts().withMessage("A non instantiable class should only have 1 constructor")
                 .that(clazz.getDeclaredConstructors())
                 .hasLengthOf(1);
    }

    @Test
    @DisplayName("satisfies the NonInstantiable contract by being a class that has a declared constructor that takes in zero arguments")
    default void testClassHasTheZeroArgumentConstructor() throws NoSuchMethodException {
        // Get test class and validate precondition
        Class<T> clazz = getTestClass();
        assumes().withMessage("This test requires that getTestClass() returns a non-null Class")
                 .that(clazz)
                 .isNotNull();
        // Perform actual test
        asserts().withMessage("A non instantiable class should have a zero argument constructor")
                 .that(getTestClass().getDeclaredConstructor())
                 .isNotNull();
    }

    @Test
    @DisplayName("satisfies the NonInstantiable contract by being whose no argument constructor is marked as private")
    default void testClassSingleConstructorIsPrivate() throws NoSuchMethodException {
        // Get test class and validate precondition
        Class<T> clazz = getTestClass();
        assumes().withMessage("This test requires that getTestClass() returns a non-null Class")
                 .that(clazz)
                 .isNotNull();
        // Perform actual test
        asserts().withMessage("A non instantiable class should have a private zero argument constructor")
                 .that(Modifier.isPrivate(getTestClass().getDeclaredConstructor().getModifiers()))
                 .isTrue();
    }

    @Test
    @DisplayName("satisfies the NonInstantiable contract by being a class whose no argument constructor throws an AssertionError if called")
    default void testClassThrowsAssertionErrorIfConstructorIsCalled() throws NoSuchMethodException {
        // Get test class and validate precondition
        Class<T> clazz = getTestClass();
        assumes().withMessage("This test requires that getTestClass() returns a non-null Class")
                 .that(clazz)
                 .isNotNull();
        // Perform actual test
        asserts().withMessage("A non instantiable class should throw an AssertionError if the private constructor is called via reflection")
                 .that(clazz.getDeclaredConstructor())
                 .satisfies(constructor -> {
                     Throwable thrown = null;
                     try {
                         constructor.setAccessible(true);
                         constructor.newInstance();
                     } catch (Throwable t) {
                         thrown = t;
                     }
                     return null != thrown &&
                            thrown instanceof InvocationTargetException &&
                            null != thrown.getCause() &&
                            thrown.getCause() instanceof AssertionError;
                 });
    }

    @Test
    @DisplayName("satisfies the NonInstantiable contract by being a class that only has static fields")
    default void testClassOnlyHasStaticFields() {
        // Get test class and validate precondition
        Class<?> clazz = getTestClass();
        assumes().withMessage("This test requires that getTestClass() returns a non-null Class")
                 .that(clazz)
                 .isNotNull();
        // Perform actual test
        List<Field> fields = new ArrayList<>();
        while (clazz != Object.class) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        Assertions.assertAll("All fields of a non-instantiable class should be static",
                             fields.stream()
                                   .map(field -> () -> asserts().withMessage("field [" + field.getName() + "] should be static")
                                                                .that(Modifier.isStatic(field.getModifiers()))
                                                                .isTrue()));
    }

    @Test
    @DisplayName("satisfies the NonInstantiable contract by being a class that only has static methods")
    default void testClassOnlyHasStaticMethods() {
        // Get test class and validate precondition
        Class<?> clazz = getTestClass();
        assumes().withMessage("This test requires that getTestClass() returns a non-null Class")
                 .that(clazz)
                 .isNotNull();
        // Perform actual test
        List<Method> methods = new ArrayList<>();
        while (clazz != Object.class) {
            methods.addAll(Arrays.asList(clazz.getDeclaredMethods()));
            clazz = clazz.getSuperclass();
        }
        Assertions.assertAll("All methods of a non-instantiable class should be static",
                             methods.stream()
                                    .map(method -> () -> asserts().withMessage("method [" + method.getName() + "] should be static")
                                                                  .that(Modifier.isStatic(method.getModifiers()))
                                                                  .isTrue()));
    }
}
