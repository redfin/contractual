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

/**
 * A simple interface that defines a method for a class that can
 * create instances of itself to allow for testing. This is intended to
 * be the super type of all interface test contracts for types that
 * can be instantiated.
 *
 * @param <T> the type under test.
 */

public interface Testable<T> {

    /**
     * @return a non-null instance of the class under test. Multiple
     * calls to the method are not required to return the same, or even
     * equal, instances.
     */
    T getInstance();

    @Test
    @DisplayName("can be successfully instantiated")
    default void testCanInstantiate() {
        try {
            Assertions.assertNotNull(getInstance(),
                                     "Should have received a non-null instance from getInstance()");
        } catch (Throwable thrown) {
            Assertions.fail("Should be able to instantiate but caught throwable: " + thrown);
        }
    }
}
