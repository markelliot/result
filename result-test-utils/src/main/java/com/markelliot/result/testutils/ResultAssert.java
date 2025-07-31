/*
 * (c) Copyright 2025 Mark Elliot. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.markelliot.result.testutils;

import com.markelliot.result.Result;
import java.util.function.Consumer;
import org.assertj.core.api.AbstractAssert;

/** Custom AssertJ assertions for {@code Result<T, E>} class */
public final class ResultAssert<T, E> extends AbstractAssert<ResultAssert<T, E>, Result<T, E>> {

    public ResultAssert(Result<T, E> actual) {
        super(actual, ResultAssert.class);
    }

    // Static factory method - this is the key to enabling assertThat(result) syntax
    public static <T, E> ResultAssert<T, E> assertThat(Result<T, E> actual) {
        return new ResultAssert<>(actual);
    }

    // Assert that the result is successful/ok
    public ResultAssert<T, E> isOk() {
        isNotNull();
        actual.error()
                .ifPresent(
                        error ->
                                failWithMessage(
                                        "Expected result to be Ok but was Error: <%s>",
                                        actual.error().get()));
        return this;
    }

    // Assert that the result is an error
    public ResultAssert<T, E> isError() {
        isNotNull();
        if (actual.isOk()) {
            failWithMessage("Expected result to be Error but was Ok: <%s>", actual.result().get());
        }
        return this;
    }

    // Assert that the ok value matches expected
    public ResultAssert<T, E> hasValue(T expectedValue) {
        isOk();

        T result = actual.result().get();

        if (!result.equals(expectedValue)) {
            failWithMessage("Expected result value to be <%s> but was <%s>", expectedValue, result);
        }
        return this;
    }

    // Assert that the error matches expected
    public ResultAssert<T, E> hasError(E expectedError) {
        isError();

        E error = actual.error().get();

        if (!error.equals(expectedError)) {
            failWithMessage("Expected result error to be <%s> but was <%s>", expectedError, error);
        }
        return this;
    }

    // Assert that the ok value satisfies a condition
    public ResultAssert<T, E> hasValueSatisfying(Consumer<T> valueConsumer) {
        isOk();
        T result = actual.result().get();
        valueConsumer.accept(result);
        return this;
    }

    // Assert that the error satisfies a condition
    public ResultAssert<T, E> hasErrorSatisfying(Consumer<E> errorConsumer) {
        isError();

        E error = actual.error().get();

        errorConsumer.accept(error);
        return this;
    }
}
