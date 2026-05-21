package co.gorkem.namaz.di

import co.gorkem.namaz.data.remote.api.ImsakiyemApiService
import co.gorkem.namaz.data.remote.api.NominatimApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val IMSAKIYEM_BASE_URL = "https://ezanvakti.imsakiyem.com/"
    private const val NOMINATIM_BASE_URL = "https://nominatim.openstreetmap.org/"

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            )
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(IMSAKIYEM_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideImsakiyemApiService(retrofit: Retrofit): ImsakiyemApiService {
        return retrofit.create(ImsakiyemApiService::class.java)
    }

    @Provides
    @Singleton
    @Named("nominatim")
    fun provideNominatimOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("User-Agent", "NamazVakitleri/1.0 (Android)")
                    .build()
                chain.proceed(request)
            }
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @Named("nominatim")
    fun provideNominatimRetrofit(@Named("nominatim") okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(NOMINATIM_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideNominatimApiService(@Named("nominatim") retrofit: Retrofit): NominatimApiService {
        return retrofit.create(NominatimApiService::class.java)
    }
}
