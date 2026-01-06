package com.davidluna.tmdb.core_data.framework.remote.call_adapter

import arrow.core.Either
import com.davidluna.tmdb.core_data.framework.remote.model.RemoteError
import okhttp3.Request
import okhttp3.ResponseBody
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Converter
import retrofit2.Response
import java.net.UnknownHostException

class NetworkCall<R : Any>(
    private val call: Call<R>,
    private val errorConverter: Converter<ResponseBody, RemoteError>,
) : Call<Either<RemoteError, R>> {

    override fun clone(): Call<Either<RemoteError, R>> =
        NetworkCall(call.clone(), errorConverter)

    override fun execute(): Response<Either<RemoteError, R>> {
        throw UnsupportedOperationException("Execute is not supported in NetworkCall")
    }

    override fun enqueue(callback: Callback<Either<RemoteError, R>>) {
        return call.enqueue(
            object : Callback<R> {
                override fun onResponse(call: Call<R>, response: Response<R>) {
                    callback.toAppResponse(response)
                }

                override fun onFailure(call: Call<R>, throwable: Throwable) {
                    val message: String = when {
                        throwable is UnknownHostException -> "Please check your internet connection"
                        throwable.message.isNullOrEmpty() -> "Unknown error"
                        else -> throwable.message.orEmpty()
                    }
                    println("<-- NetworkCall failed: $message")
                    callback.onResponse(
                        this@NetworkCall,
                        Response.success(
                            Either.Left(
                                RemoteError(
                                    statusCode = -1,
                                    statusMessage = message,
                                    success = false
                                )
                            )
                        )
                    )
                }

            }
        )
    }

    override fun isExecuted(): Boolean = call.isExecuted
    override fun cancel() = call.cancel()
    override fun isCanceled(): Boolean = call.isCanceled
    override fun request(): Request = call.request()
    override fun timeout(): Timeout = call.timeout()

    private fun Callback<Either<RemoteError, R>>.toAppResponse(response: Response<R>) {
        val body = response.body()
        val error = response.errorBody()
        if (response.isSuccessful && body != null) {
            onResponse(this@NetworkCall, Response.success(Either.Right(body)))
        } else {
            val remoteError = try {
                when {
                    error == null || error.contentLength() == 0L -> null
                    else -> errorConverter.convert(error)
                }
            } catch (_: Exception) {
                null
            }

            onResponse(
                this@NetworkCall,
                Response.success(
                    Either.Left(
                        remoteError ?: RemoteError(
                            statusCode = response.code(),
                            statusMessage = response.message(),
                            success = false
                        )
                    )
                )
            )
        }
    }
}
