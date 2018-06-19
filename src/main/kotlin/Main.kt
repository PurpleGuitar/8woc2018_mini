import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import rx.Observable

fun main(args: Array<String>) {

    // TODO: Don't hard-code these
    val anthologyCode = "bible"
    val languageCode = "en"
    val versionCode = "ulb-en"
    val bookCode = "gen"

    createUnfoldingWordService()
            .catalog()
            .flatMap { it.anthologies() }
            .filter { it.slug == anthologyCode }
            .flatMap { it.languages() }
            .filter { it.lc == languageCode }
            .flatMap { it.versions() }
            .filter { it.slug == versionCode }
            .flatMap { it.books() }
            .filter { it.slug == bookCode }
            .flatMap { httpGet(it.src) }
            .subscribe { println(it) }
}

fun httpGet(url: String): Observable<Response> {
    return Observable.fromCallable() {
        OkHttpClient.Builder().build().newCall(Request.Builder().url(url).build()).execute()
    }
}
