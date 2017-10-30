package cm.foo.bar

class IdGenerator(val numberOfIds: Int, var seed: Int) {

    @Synchronized
    fun generate(num: Int): Result<Set<String>, Nothing> =
            Result.succeed(ids().take(num).toSet())

    private fun ids(): Sequence<String> =
            generateSequence { (seed++).takeIf { it < numberOfIds } }
                    .map { "$it".padStart(11, '0') }
}
