package cm.foo.bar

import cm.foo.bar.Result.Failure
import cm.foo.bar.Result.Success

sealed class Result<out SUCCESS, FAILURE> {

    data class Success<out SUCCESS, FAILURE>(val value: SUCCESS) : Result<SUCCESS, FAILURE>()
    data class Failure<out SUCCESS, FAILURE>(val value: FAILURE) : Result<SUCCESS, FAILURE>()

    infix open fun <NEW_SUCCESS> map(
            transform: (SUCCESS) -> NEW_SUCCESS
    ): Result<NEW_SUCCESS, FAILURE> = when (this) {
        is Success -> Success(transform(value))
        is Failure -> Failure(value)
    }

    infix open fun <NEW_SUCCESS> handle(
            result: Result<(SUCCESS) -> NEW_SUCCESS, FAILURE>
    ): Result<NEW_SUCCESS, FAILURE> = when (this) {
        is Success -> result.map { it(value) }
        is Failure -> Failure(value)
    }

    infix open fun <NEW_SUCCESS> flatMap(
            transform: (SUCCESS) -> Result<NEW_SUCCESS, FAILURE>
    ): Result<NEW_SUCCESS, FAILURE> = when (this) {
        is Success -> transform(value)
        is Failure -> Failure(value)
    }

    companion object {

        infix fun <SUCCESS> succeed(value: SUCCESS): Result<SUCCESS, Nothing> = Success(value)
        infix fun <FAILURE> fail(value: FAILURE): Result<Nothing, FAILURE> = Failure(value)
    }
}

infix fun <SUCCESS, FAILURE, NEW_SUCCESS> Result<(SUCCESS) -> NEW_SUCCESS, FAILURE>.handle(
        result: Result<SUCCESS, FAILURE>
): Result<NEW_SUCCESS, FAILURE> = when (this) {
    is Success -> result.map(value)
    is Failure -> Failure(value)
}