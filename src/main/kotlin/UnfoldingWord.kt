import io.reactivex.Observable
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

data class Catalog(val cat: Array<Anthology>) {
    fun anthologies(): Observable<Anthology> {
        return Observable.fromIterable(cat.toList())
    }
}

data class Anthology(val slug: String, val title: String, val langs: Array<Language>) {
    fun languages(): Observable<Language> {
        return Observable.fromIterable(langs.toList())
    }
}

data class Language(val lc: String, val vers: Array<Version>) {
    fun versions(): Observable<Version> {
        return Observable.fromIterable(vers.toList())
    }
}

data class Version(val slug: String, val name: String, val toc: Array<Book>) {
    fun books(): Observable<Book> {
        return Observable.fromIterable(toc.toList())
    }
}

data class Book(val slug: String, val title: String, val src: String)

interface UnfoldingWordAPI {
    @GET("/uw/txt/2/catalog.json")
    fun catalog(): Observable<Catalog>
}

fun createUnfoldingWordService(): UnfoldingWordAPI {
    val builder = Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create())
            .baseUrl("https://api.unfoldingword.org")
    val client = OkHttpClient.Builder().build()
    builder.client(client)
    return builder.build().create(UnfoldingWordAPI::class.java)
}

fun getUWContentURL(anthologyCode: String, languageCode: String, versionCode: String, bookCode: String): Observable<String> {
    return createUnfoldingWordService()
            .catalog()
            .flatMap { it.anthologies() }
            .filter { it.slug == anthologyCode }
            .flatMap { it.languages() }
            .filter { it.lc == languageCode }
            .flatMap { it.versions() }
            .filter { it.slug == versionCode }
            .flatMap { it.books() }
            .filter { it.slug == bookCode }
            .flatMap { Observable.just(it.src) }
}
