package org.jsalazar.fraud.rules

@FunctionalInterface
interface CustomRule<T> {
    boolean apply(T object)
}