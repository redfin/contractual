[![Build Status](https://travis-ci.org/redfin/contractual.svg?branch=master)](https://travis-ci.org/redfin/contractual)
[![License](http://img.shields.io/:license-apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

# Contractual

## Overview

Contractual is a simple Java library of interfaces with default `@Test` methods for use with JUnit 5.
This allows for generic tests to be written and re-used for every class that makes sense for that contract.
It can also improve the writing of new classes in the first place.
For instance, if you write a new class that overrides the `equals` method but forget to override the `hashCode` method, the `EqualsContract` interface, when applied to the new class's unit test class will fail.

## Installation

```xml
<dependency>
    <groupId>com.redfin</groupId>
    <artifactId>contractual</artifactId>
    <version>2.1.0</version>
    <scope>test</scope>
</dependency>
```

## Example usage

Say you write a new class `Foo` that contains a String value. Two `Foo` instances should
be equal only if they both contain the same String. To properly override the
equals method you have to make sure that the equals method contract (defined in the Object class)
is maintained. You also need to make sure that you override the hashCode method. An example
implementation would be:

```java
public class Foo {
    private final String s;

    public Foo(String s) {
        this.s = Objects.requireNonNull(s);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Foo && s.equals(((Foo)obj).s);
    }

    @Override
    public int hashCode() {
        return s.hashCode();
    }

    // other code specific to Foo
}
```

When it comes time to write the unit tests for `Foo` (say in a test class called `FooTest`) then there
would normally be repeated code for each class that overrides the equals method. Contracts, along with
JUnit 5, can spare you from repeated that test code (and possibly missing cases). The `FooTest` could just
implement the interface, implement any abstract methods, and then it will inherit the test methods defined
in the contract.

```java
public class FooTest implements EqualsContract<Foo> {

    private static final String EQUAL = "hello";
    private static final String NON_EQUAL = "world";

    @Override
    public Foo getInstance() {
        return new Foo(EQUAL);
    }

    @Override
    public Supplier<Foo> getEqualInstanceSupplier() {
        return () -> new Foo(EQUAL);
    }

    @Override
    public Foo getNonEqualInstance() {
        return new Foo(NON_EQUAL);
    }

    // any unit tests specific to Foo
}
```
