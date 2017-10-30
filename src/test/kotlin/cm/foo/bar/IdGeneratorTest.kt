package cm.foo.bar

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class IdGeneratorTest {

    private val generator = IdGenerator(numberOfIds = 10_000_000, seed = 0)

    @Test
    fun `should create a number of ids`() {
        val size = 10

        val list: Result<Set<String>, Nothing> = generator.generate(size)

        list.map {
            assertThat(it).containsExactlyInAnyOrder(
                    "00000000000",
                    "00000000001",
                    "00000000002",
                    "00000000003",
                    "00000000004",
                    "00000000005",
                    "00000000006",
                    "00000000007",
                    "00000000008",
                    "00000000009"
            )
        }
    }

    @Test
    internal fun `should create two unique lists of ids`() {
        val size = 10
        val generator = IdGenerator(numberOfIds = 15, seed = 0)

        val list1: Result<Set<String>, Nothing> = generator.generate(size)
        val list2: Result<Set<String>, Nothing> = generator.generate(size)

        list1.map {
            assertThat(it).containsExactlyInAnyOrder(
                    "00000000000",
                    "00000000001",
                    "00000000002",
                    "00000000003",
                    "00000000004",
                    "00000000005",
                    "00000000006",
                    "00000000007",
                    "00000000008",
                    "00000000009"
            )
        }

        list2.map {
            assertThat(it).containsExactlyInAnyOrder(
                    "00000000010",
                    "00000000011",
                    "00000000012",
                    "00000000013",
                    "00000000014"
            )
        }
    }

    @Test
    internal fun `should not take more than specified`() {
        val size = 10

        val list1: Result<Set<String>, Nothing> = generator.generate(size)
        val list2: Result<Set<String>, Nothing> = generator.generate(size)

        list1.map {
            assertThat(it).containsExactlyInAnyOrder(
                    "00000000000",
                    "00000000001",
                    "00000000002",
                    "00000000003",
                    "00000000004",
                    "00000000005",
                    "00000000006",
                    "00000000007",
                    "00000000008",
                    "00000000009"
            )
        }

        list2.map {
            assertThat(it).containsExactlyInAnyOrder(
                    "00000000010",
                    "00000000011",
                    "00000000012",
                    "00000000013",
                    "00000000014",
                    "00000000015",
                    "00000000016",
                    "00000000017",
                    "00000000018",
                    "00000000019"
            )
        }
    }

    @Test
    fun `should not create the same id in multiple generators`() {
        runBlocking {

            val size = 10

            val list1 = async(CommonPool) { generator.generate(size) }
            val list2 = async(CommonPool) { generator.generate(size) }
            val result1 = list1.await()
            val result2 = list2.await()

            result1.map { println(it) }
            result2.map { println(it) }

            result1.map { list ->
                result2.map {
                    assertThat(list.none { elem -> it.contains(elem) }).isTrue()
                }
            }
        }
    }
}
