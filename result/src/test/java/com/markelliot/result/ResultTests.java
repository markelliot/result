/*
 * (c) Copyright 2021 Mark Elliot. All rights reserved.
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

package com.markelliot.result;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.function.Consumer;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

final class ResultTests {
    @Test
    public void testOk() throws Exception {
        Result<String, String> result = Result.ok("ok");
        assertThat(result.isError()).isFalse();
        assertThat(result.result()).contains("ok");
        assertThat(result.error()).isEmpty();
        assertThat(result.orElseThrow()).isEqualTo("ok");
        assertThat(result.orElseThrow(IllegalStateException::new)).isEqualTo("ok");
    }

    @Test
    public void testError() {
        Result<String, String> result = Result.error("error");
        assertThat(result.isError()).isTrue();
        assertThat(result.result()).isEmpty();
        assertThat(result.error()).contains("error");
        assertThatThrownBy(result::orElseThrow).isInstanceOf(Exception.class).hasMessage("error");
        assertThatThrownBy(
                        () ->
                                result.orElseThrow(
                                        err -> new IllegalStateException("message: " + err)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("message: error");
    }

    @Test
    public void testMapResult() {
        Result<String, String> ok = Result.ok("ok");
        Result<String, String> error = Result.error("error");
        assertThat(ok.mapResult(String::length)).isEqualTo(Result.ok(2));
        assertThat(error.mapResult(String::length)).isEqualTo(Result.error("error"));
    }

    @Test
    public void testFlatMapResult() {
        Result<String, String> ok = Result.ok("ok");
        Result<String, String> error = Result.error("error");
        assertThat(ok.flatMapResult(r -> Result.ok(2))).isEqualTo(Result.ok(2));
        assertThat(ok.flatMapResult(r -> Result.error("err"))).isEqualTo(Result.error("err"));
        assertThat(error.flatMapResult(r -> Result.ok(2))).isEqualTo(Result.error("error"));
        assertThat(error.flatMapResult(r -> Result.error("error")))
                .isEqualTo(Result.error("error"));
    }

    @Test
    public void testMapError() {
        Result<String, String> ok = Result.ok("ok");
        Result<String, String> error = Result.error("error");
        assertThat(ok.mapError(String::length)).isEqualTo(Result.ok("ok"));
        assertThat(error.mapError(String::length)).isEqualTo(Result.error(5));
    }

    @Test
    public void testFlatMapError() {
        Result<String, String> ok = Result.ok("ok");
        Result<String, String> error = Result.error("error");
        assertThat(ok.flatMapError(e -> Result.ok("o"))).isEqualTo(Result.ok("ok"));
        assertThat(ok.flatMapError(e -> Result.error(5))).isEqualTo(Result.ok("ok"));
        assertThat(error.flatMapError(r -> Result.ok("o"))).isEqualTo(Result.ok("o"));
        assertThat(error.flatMapError(r -> Result.error(5))).isEqualTo(Result.error(5));
    }

    @Test
    public void testMap() {
        Result<String, String> ok = Result.ok("ok");
        Result<String, String> error = Result.error("error");
        assertThat(ok.map(String::length, Function.identity())).isEqualTo(Result.ok(2));
        assertThat(error.map(String::length, Function.identity())).isEqualTo(Result.error("error"));
        assertThat(ok.map(Function.identity(), String::length)).isEqualTo(Result.ok("ok"));
        assertThat(error.map(Function.identity(), String::length)).isEqualTo(Result.error(5));
    }

    @Test
    public void testConsume_Ok() {
        Result<String, String> ok = Result.ok("ok");

        String[] output = new String[1];
        Consumer<String> setOutput =
                x -> {
                    output[0] = x;
                };
        ok.consume(setOutput, setOutput);

        assertThat(output[0]).isEqualTo("ok");
    }

    @Test
    public void testConsume_Error() {
        Result<String, String> error = Result.error("error");

        String[] output = new String[1];
        Consumer<String> setOutput =
                x -> {
                    output[0] = x;
                };
        error.consume(setOutput, setOutput);

        assertThat(output[0]).isEqualTo("error");
    }

    @Test
    public void testFlatMap() {
        Result<String, String> ok = Result.ok("ok");
        Result<String, String> error = Result.error("error");
        assertThat(ok.flatMap(r -> Result.ok(r.length()), Result::error)).isEqualTo(Result.ok(2));
        assertThat(error.flatMap(Result::ok, e -> Result.error(e.length())))
                .isEqualTo(Result.error(5));
        assertThat(ok.flatMap(Result::ok, e -> Result.error(e.length())))
                .isEqualTo(Result.ok("ok"));
        assertThat(error.flatMap(r -> Result.ok(r.length()), Result::error))
                .isEqualTo(Result.error("error"));
        assertThat(ok.flatMap(Result::error, e -> Result.error(e.length())))
                .isEqualTo(Result.error("ok"));
        assertThat(error.flatMap(r -> Result.ok(r.length()), Result::ok))
                .isEqualTo(Result.ok("error"));
    }

    @Test
    void testUnwrap() {
        Result<String, String> ok = Result.ok("ok");
        Result<String, String> error = Result.error("error");

        assertThat(ok.unwrap()).isEqualTo("ok");
        assertThatThrownBy(error::unwrap)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("error");
    }

    @Test
    void testCoerce() {
        Result<String, String> ok = Result.ok("ok");
        Result<String, String> error = Result.error("error");

        assertThatThrownBy(ok::coerce)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot coerce a success-state result");

        Result<Integer, String> coerced = error.coerce();
        assertThat(coerced).isEqualTo(error);
    }

    @Test
    void testMapOrElse() {
        Result<String, String> ok = Result.ok("hello");
        Result<String, String> error = Result.error("error");

        assertThat(ok.map_or_else(String::length, err -> -1)).isEqualTo(5);
        assertThat(error.map_or_else(String::length, err -> -1)).isEqualTo(-1);
    }

    @Test
    void testIsOk() {
        Result<String, String> ok = Result.ok("value");
        Result<String, String> error = Result.error("error");

        assertThat(ok.isOk()).isTrue();
        assertThat(error.isOk()).isFalse();
    }
}
