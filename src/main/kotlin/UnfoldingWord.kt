import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import rx.Observable

data class Catalog(val cat: Array<Anthology>) {
    fun anthologies(): Observable<Anthology> {
        return Observable.from(cat)
    }
}

data class Anthology(val slug: String, val title: String, val langs: Array<Language>) {
    fun languages(): Observable<Language> {
        return Observable.from(langs)
    }
}

data class Language(val lc: String, val vers: Array<Version>) {
    fun versions(): Observable<Version> {
        return Observable.from(vers)
    }
}

data class Version(val slug: String, val name: String, val toc: Array<Book>) {
    fun books(): Observable<Book> {
        return Observable.from(toc)
    }
}

data class Book(val slug: String, val title: String, val src: String)

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
