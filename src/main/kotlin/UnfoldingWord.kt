import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import rx.Observable

data class Catalog ( val mod: String = "")

interface UnfoldingWordAPI {
    @GET("/uw/txt/2/catalog.json")
    fun catalog(): Observable<Catalog>
}

fun createUnfoldingWordService(): UnfoldingWordAPI {
    val builder = Retrofit.Builder()
        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("https://api.unfoldingword.org")
    val client = OkHttpClient.Builder().build()
    builder.client(client)
    return builder.build().create(UnfoldingWordAPI::class.java)
}
