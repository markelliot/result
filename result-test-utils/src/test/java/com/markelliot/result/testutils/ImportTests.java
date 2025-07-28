package com.markelliot.result.testutils;

import com.markelliot.result.Result;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static com.markelliot.result.testutils.ResultAssert.assertThat;

/**
 * Just a test to verify the import can be used alongside the standard assertThat().
 */
final class ImportsTests {

    @Test
    public void testImports() {
        // can use regular assertThat
        int number = 5;
        assertThat(number).isEqualTo(5);

        // can use Result's assertThat
        Result<Integer, String> result = Result.ok(6);
        assertThat(result).hasValue(6);

    }

}
