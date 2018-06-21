import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

fun main(args: Array<String>) {

//    // TODO: Don't hard-code these
//    val anthologyCode = "bible"
//    val languageCode = "en"
//    val versionCode = "ulb-en"
//    val bookCode = "gen"
//
//    getUWContentURL(createUnfoldingWordService().catalog(), anthologyCode, languageCode, versionCode, bookCode)
//            .flatMap { httpGet(it) }
//            .flatMap { it.body().use { Observable.fromIterable(it.string().lines()) } }
//            .flatMap { usfmToMarkdown(it) }
//            .subscribe { println(it) }

//        val subject = BehaviorSubject.create<Int>()
//        subject.onNext(5)
//
//        subject.subscribe {
//            println("sub 1 : ${it}")
//        }
//
//        subject.onNext(subject.value + 5)
//
//        subject.subscribe {
//            println("sub 2: ${it}")
//        }
}

fun httpGet(url: String): Observable<Response> {
    return Observable.fromCallable() {
        OkHttpClient.Builder().build().newCall(Request.Builder().url(url).build()).execute()
    }
}

val HEADER = Regex("^\\\\h (.*)$")
val CHAPTER = Regex("^\\\\c (.*)$")
val VERSE = Regex("^\\\\v (\\d+) (.*)$")
fun usfmToMarkdown(line: String): Observable<String> {
    HEADER.matchEntire(line)?.let {
        return Observable.just("# " + it.groupValues[1])
    }
    CHAPTER.matchEntire(line)?.let {
        return Observable.just("\n## Chapter " + it.groupValues[1] + "\n")
    }
    VERSE.matchEntire(line)?.let {
       return Observable.just("(" + it.groupValues[1] +") " + it.groupValues[2])
    }
    return Observable.empty()
}
