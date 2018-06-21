package demo

import com.github.thomasnield.rxkotlinfx.actionEvents
import com.github.thomasnield.rxkotlinfx.observeOnFx
import getUWContentURL
import httpGet
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javafx.scene.control.TextArea
import tornadofx.*
import usfmToMarkdown

class ReaderApp : App(ReaderView::class)

class ReaderView : View() {
    var textArea: TextArea by singleAssign()
    override val root = vbox {
        button("Read it!") {
            actionEvents().subscribe {
                textArea.text = "Loading catalog, please wait..."
            }
            actionEvents()
                    .observeOn(Schedulers.io())
                    .subscribe {
                        getUWContentURL(
                                "bible",
                                "en",
                                "ulb-en",
                                "gen")
                                .observeOnFx()
                                .doOnNext { textArea.text = "Loading USFM, please wait..." }
                                .observeOn(Schedulers.io())
                                .flatMap { httpGet(it) }
                                .observeOnFx()
                                .doOnNext { textArea.text = "Converting to Markdown, please wait..." }
                                .observeOn(Schedulers.io())
                                .flatMap { it.body().use { Observable.fromIterable(it.string().lines()) } }
                                .flatMap { usfmToMarkdown(it) }
                                .collectInto(StringBuilder()) { builder, item -> builder.append(item + "\n") }
                                .toObservable()
                                .flatMap { Observable.just(it.toString()) }
                                .observeOnFx()
                                .subscribe { textArea.text = it }
                    }
        }
        textarea {
            textArea = this
            isEditable = false
        }
    }

    init {
        title = "Craig's Window"
    }
}
