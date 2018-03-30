package neobis.alier.parking

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object Network {
    val REQUEST_TIME = 1000L

    fun initService(): ForumService {
        //http@ //api.jsonbin.io/b/5abdd3d417c4606cad5ef510
        return Retrofit.Builder()
                .baseUrl("http://api.jsonbin.io/")
                .addConverterFactory(initGson())
                .client(getClient())
                .build().create(ForumService::class.java)
    }

    private fun initGson(): GsonConverterFactory {
        return GsonConverterFactory.create(GsonBuilder().create())
    }

    private fun getClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val original = chain.request()

                    val request = original.newBuilder()
                            .method(original.method(), original.body())
                            .build()
                    return@addInterceptor chain.proceed(request)

                }
                .addInterceptor(interceptor)
                .writeTimeout(REQUEST_TIME, TimeUnit.SECONDS)
                .readTimeout(REQUEST_TIME, TimeUnit.SECONDS)
                .connectTimeout(REQUEST_TIME, TimeUnit.SECONDS)
        return client.build()
    }
}