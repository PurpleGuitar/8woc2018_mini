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

fun main(args: Array<String>) {

    val builder = Retrofit.Builder()
        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("https://api.unfoldingword.org")
    val client = OkHttpClient.Builder().build()
    builder.client(client)
    val api = builder.build().create(UnfoldingWordAPI::class.java)
    val catalog = api.catalog()
    catalog.subscribe { println(it)}

}