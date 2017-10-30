package cm.foo.bar

import cm.foo.bar.Result.Companion.fail
import cm.foo.bar.Result.Companion.succeed
import cm.foo.bar.Result.Success
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ResultTest {

    @Nested
    inner class Functor {

        @Test
        fun `should transform the contents of a success`() {
            val content = "YAY"
            val name = "Bob"
            val result = succeed(content)

            assertThat(result map { it + " $name" })
                    .isEqualTo(Result succeed "$content $name")
        }

        @Test
        fun `should not transform the contents if a failure`() {
            val content = "Boo"
            val result = Result fail content

            assertThat(result map { "Bob" })
                    .isEqualTo(Result fail content)
        }

    }

    @Nested
    inner class Applicative {

        @Test
        fun `should apply function to value`() {
            val success1: Result<(Int) -> List<Int>, Int> = Success({ it: Int -> listOf(it) })
            val success2: Result<Int, Int> = Success(value = 3)
            assertThat((success1 handle success2)).isEqualTo(succeed(listOf(3)))
        }

        @Test
        fun `should not apply function to value for failure`() {
            val success1: Result<(Int) -> List<Int>, String> = fail("Fail")
            val success2: Result<Int, String> = Success(3)
            assertThat(success1 handle success2).isEqualTo(fail("Fail"))
            assertThat(success2 handle success1).isEqualTo(fail("Fail"))
        }

        @Test
        fun `should apply function to value regardless of order`() {
            val success1: Result<(Int) -> List<Int>, Int> = Success({ it: Int -> listOf(it) })
            val success2: Result<Int, Int> = Success(3)
            assertThat(success2 handle success1).isEqualTo(Result succeed listOf(3))
        }

        @Test
        fun `should not apply function to value for failure regardless of order`() {
            val success1: Result<(Int) -> List<Int>, String> = Success({ it: Int -> listOf(it) })
            val success2: Result<Int, String> = Result.fail("Fail")
            assertThat(success2 handle success1).isEqualTo(Result fail "Fail")
            assertThat(success1 handle success2).isEqualTo(Result fail "Fail")
        }
    }

    @Nested
    inner class Monad {

        @Test
        fun `should transform the contents of a success`() {
            val content = "yay"
            val result = Result succeed content

            assertThat(result flatMap { (Result succeed it) map String::toUpperCase })
                    .isEqualTo(Result succeed "YAY")
        }

        @Test
        fun `should not transform the contents if a failure`() {
            val content = "yay"
            val result: Result<String, String> = Result.fail(content)
            val result1: (String) -> Result<String, String> =
                    {it -> succeed(it) as Result<String, String>}

            assertThat(result flatMap { result1(it) map String::toUpperCase })
                    .isEqualTo(Result fail "yay")
        }
    }

}