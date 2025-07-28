package com.markelliot.result.testutils;

import com.markelliot.result.Result;
import org.assertj.core.api.AbstractAssert;
import org.junit.jupiter.api.Test;

import static com.markelliot.result.testutils.ResultAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

final class ResultAssertTests {

    @Test
    void testAssertThatFactoryMethod() {
        Result<String, Integer> okResult = Result.ok("success");
        AbstractAssert<?, ?> assertion = assertThat(okResult);
        assertEquals(ResultAssert.class, assertion.getClass());
    }

    @Test
    void testIsOkWithOkResult() {
        Result<String, Integer> okResult = Result.ok("success");
        assertThat(okResult).isOk();
    }

    @Test
    void testIsOkWithErrorResult() {
        Result<String, Integer> errorResult = Result.error(404);
        assertThatThrownBy(() -> assertThat(errorResult).isOk())
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("Expected result to be Ok but was Error: <404>");
    }

    @Test
    void testIsOkWithNullResult() {
        assertThatThrownBy(() -> assertThat((Result<String, Integer>) null).isOk())
                .isInstanceOf(AssertionError.class);
    }

    @Test
    void testIsErrorWithErrorResult() {
        Result<String, Integer> errorResult = Result.error(500);
        assertThat(errorResult).isError();
    }

    @Test
    void testIsErrorWithOkResult() {
        Result<String, Integer> okResult = Result.ok("success");
        assertThatThrownBy(() -> assertThat(okResult).isError())
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("Expected result to be Error but was Ok: <success>");
    }

    @Test
    void testIsErrorWithNullResult() {
        assertThatThrownBy(() -> assertThat((Result<String, Integer>) null).isError())
                .isInstanceOf(AssertionError.class);
    }

    @Test
    void testHasValueWithMatchingValue() {
        Result<String, Integer> okResult = Result.ok("test");
        assertThat(okResult).hasValue("test");
    }

    @Test
    void testHasValueWithNonMatchingValue() {
        Result<String, Integer> okResult = Result.ok("test");
        assertThatThrownBy(() -> assertThat(okResult).hasValue("different"))
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("Expected result value to be <different> but was <test>");
    }

    @Test
    void testHasValueWithErrorResult() {
        Result<String, Integer> errorResult = Result.error(404);
        assertThatThrownBy(() -> assertThat(errorResult).hasValue("test"))
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("Expected result to be Ok but was Error: <404>");
    }

    @Test
    void testHasErrorWithMatchingError() {
        Result<String, Integer> errorResult = Result.error(404);
        assertThat(errorResult).hasError(404);
    }

    @Test
    void testHasErrorWithNonMatchingError() {
        Result<String, Integer> errorResult = Result.error(404);
        assertThatThrownBy(() -> assertThat(errorResult).hasError(500))
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("Expected result error to be <500> but was <404>");
    }

    @Test
    void testHasErrorWithOkResult() {
        Result<String, Integer> okResult = Result.ok("success");
        assertThatThrownBy(() -> assertThat(okResult).hasError(404))
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("Expected result to be Error but was Ok: <success>");
    }

    @Test
    void testHasValueSatisfyingWithValidCondition() {
        Result<String, Integer> okResult = Result.ok("test");
        assertThat(okResult).hasValueSatisfying(value -> {
            assertEquals("test", value);
            assertEquals(4, value.length());
        });
    }

    @Test
    void testHasValueSatisfyingWithFailingCondition() {
        Result<String, Integer> okResult = Result.ok("test");
        assertThatThrownBy(() -> assertThat(okResult).hasValueSatisfying(value -> {
            assertEquals("wrong", value);
        }))
                .isInstanceOf(AssertionError.class);
    }

    @Test
    void testHasValueSatisfyingWithErrorResult() {
        Result<String, Integer> errorResult = Result.error(404);
        assertThatThrownBy(() -> assertThat(errorResult).hasValueSatisfying(value -> {}))
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("Expected result to be Ok but was Error: <404>");
    }

    @Test
    void testHasErrorSatisfyingWithValidCondition() {
        Result<String, Integer> errorResult = Result.error(404);
        assertThat(errorResult).hasErrorSatisfying(error -> {
            assertEquals(404, error);
            assertEquals(Integer.class, error.getClass());
        });
    }

    @Test
    void testHasErrorSatisfyingWithFailingCondition() {
        Result<String, Integer> errorResult = Result.error(404);
        assertThatThrownBy(() -> assertThat(errorResult).hasErrorSatisfying(error -> {
            assertEquals(500, error);
        }))
                .isInstanceOf(AssertionError.class);
    }

    @Test
    void testHasErrorSatisfyingWithOkResult() {
        Result<String, Integer> okResult = Result.ok("success");
        assertThatThrownBy(() -> assertThat(okResult).hasErrorSatisfying(error -> {}))
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("Expected result to be Error but was Ok: <success>");
    }

    @Test
    void testChainingAssertions() {
        Result<String, Integer> okResult = Result.ok("test");
        assertThat(okResult)
                .isOk()
                .hasValue("test")
                .hasValueSatisfying(value -> assertEquals(4, value.length()));
    }

    @Test
    void testChainingErrorAssertions() {
        Result<String, Integer> errorResult = Result.error(404);
        assertThat(errorResult)
                .isError()
                .hasError(404)
                .hasErrorSatisfying(error -> assertEquals(Integer.class, error.getClass()));
    }
}
