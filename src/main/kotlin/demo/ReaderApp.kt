package demo

import com.github.thomasnield.rxkotlinfx.actionEvents
import com.github.thomasnield.rxkotlinfx.observeOnFx
import com.sun.javafx.fxml.builder.JavaFXSceneBuilder
import getUWContentURL
import httpGet
import io.reactivex.Observable
import io.reactivex.functions.BiConsumer
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler
import io.reactivex.schedulers.Schedulers
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import tornadofx.*
import usfmToMarkdown

class ReaderApp : App(ReaderView::class)

class ReaderView : View() {
    var textArea: TextArea by singleAssign()
    override val root = vbox {
        button("Load it!")
                .actionEvents()
                .doOnNext { println("Emitting " + it + " on thread " + Thread.currentThread().name)}
                .subscribe {
                    textArea.text = "Loading, please wait..."
                    getUWContentURL(
                            "bible",
                            "en",
                            "ulb-en",
                            "gen")
                            .observeOn(Schedulers.io())
                            .doOnNext { println("Loading USFM on thread " + Thread.currentThread().name)}
                            .flatMap { httpGet(it) }
                            .flatMap { it.body().use { Observable.fromIterable(it.string().lines()) } }
                            .flatMap { usfmToMarkdown(it) }
                            .collectInto(StringBuilder(), { builder, item -> builder.append(item + "\n") })
                            .toObservable()
                            .observeOnFx()
                            .doOnNext { println("Updating UI on thread " + Thread.currentThread().name)}
                            .subscribe { sb ->
                                textArea.text = sb.toString()
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
