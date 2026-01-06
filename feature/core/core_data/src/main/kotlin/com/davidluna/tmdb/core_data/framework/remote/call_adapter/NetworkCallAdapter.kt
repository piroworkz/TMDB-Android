package com.davidluna.tmdb.core_data.framework.remote.call_adapter

import arrow.core.Either
import com.davidluna.tmdb.core_data.framework.remote.model.RemoteError
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Converter
import java.lang.reflect.Type

class NetworkCallAdapter<R : Any>(
    private val successType: Type,
    private val errorConverter: Converter<ResponseBody, RemoteError>,
) : CallAdapter<R, Call<Either<RemoteError, R>>> {
    override fun responseType(): Type = successType

    override fun adapt(call: Call<R>): Call<Either<RemoteError, R>> =
        NetworkCall(call, errorConverter)
}
