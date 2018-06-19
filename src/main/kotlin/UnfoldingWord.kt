import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import rx.Observable

data class Catalog ( val cat: Array<Anthology>)

data class Anthology (val title: String, val slug: String, val langs: Array<Language>)

data class Language (val lc: String, val vers: Array<Version>)

data class Version (val slug: String, val name: String, val toc: Array<Source>)

data class Source(val slug: String, val title: String, val src: String)

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
