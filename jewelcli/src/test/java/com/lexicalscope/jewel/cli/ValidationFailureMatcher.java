package com.lexicalscope.jewel.cli;

import static org.hamcrest.Matchers.allOf;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/*
 * Copyright 2012 Tim Wood
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

public class ValidationFailureMatcher {
    public static Matcher<ValidationFailure> validationError(final ValidationFailureType failureType, final String message) {
        return allOf(validationError(failureType), validationError(message));
    }

    public static Matcher<ValidationFailure> validationError(final String message) {
        return new TypeSafeMatcher<ValidationFailure>() {
            @Override public void describeTo(final Description description) {
                description.appendText("validation failure with message ").appendValue(message);
            }

            @Override protected boolean matchesSafely(final ValidationFailure item) {
                return item.getMessage().equals(message);
            }};
    }

    public static Matcher<ValidationFailure> validationError(final ValidationFailureType failureType) {
        return new TypeSafeMatcher<ValidationFailure>() {
            @Override public void describeTo(final Description description) {
                description.appendText("validation failure of type ").appendValue(failureType);
            }

            @Override protected boolean matchesSafely(final ValidationFailure item) {
                return item.getFailureType().equals(failureType);
            }};
    }
}
