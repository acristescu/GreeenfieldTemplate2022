package io.zenandroid.greenfield.data.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.zenandroid.greenfield.data.api.FlickrApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

const val TIMEOUT = 15L

fun buildFlickrApi(client: OkHttpClient, moshi: Moshi) =
    Retrofit.Builder()
        .baseUrl("https://api.flickr.com/")
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(FlickrApi::class.java)

fun buildMoshi() =
    Moshi.Builder()
        .add(Date::class.java, Rfc3339DateJsonAdapter())
        .add(KotlinJsonAdapterFactory())
        .build()

fun buildOKHttpConnection() =
    OkHttpClient.Builder()
        //				.addInterceptor(new Interceptor() {
        //					@Override
        //					public Response intercept(Chain chain) throws IOException {
        //						Request request = chain.request();
        //						if(credentialsManager.getToken() != null) {
        //							request = request
        //									.newBuilder()
        //									.addHeader("Authorization", "Bearer " + credentialsManager.getToken())
        //									.build();
        //						}
        //						return chain.proceed(request);
        //					}
        //				})
        .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(TIMEOUT, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
        .build()