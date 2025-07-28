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

import static com.markelliot.result.testutils.ResultAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

import com.markelliot.result.Result;
import org.junit.jupiter.api.Test;

/** Just a test to verify the import can be used alongside the standard assertThat(). */
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
