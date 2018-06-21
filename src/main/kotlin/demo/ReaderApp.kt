package demo

import Catalog
import com.github.thomasnield.rxkotlinfx.actionEvents
import com.github.thomasnield.rxkotlinfx.observeOnFx
import createUnfoldingWordService
import getUWContentURL
import httpGet
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.TextArea
import javafx.scene.layout.Priority
import tornadofx.*
import usfmToMarkdown

class ReaderApp : App(ReaderView::class)

class ReaderView : View() {
    private var textArea: TextArea by singleAssign()
    private var languages: ComboBox<String> by singleAssign()
    private var books: ComboBox<String> by singleAssign()
    private var catalog: Observable<Catalog> by singleAssign()
    private val catalogLoaded = SimpleBooleanProperty(false)
    override val root = vbox {
        hbox {
            combobox(SimpleStringProperty(), FXCollections.observableArrayList<String>("Loading...")) {
                languages = this
                selectionModel.selectFirst()
            }
            combobox(SimpleStringProperty(), FXCollections.observableArrayList<String>("Loading...")) {
                books = this
                selectionModel.selectFirst()
            }
            button("Read it!") {
                enableWhen(catalogLoaded)
                action {
                    textArea.text = "Searching catalog, please wait..."
                }
                actionEvents()
                        .observeOn(Schedulers.io())
                        .subscribe {
                            getUWContentURL(
                                    catalog,
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
        }
        textarea {
            textArea = this
            isEditable = false
            vboxConstraints { vGrow = Priority.ALWAYS }
        }
    }

    init {
        title = "Bible Reader"
        runAsync {
            println("Loading catalog on thread " + Thread.currentThread().name)
            catalog = createUnfoldingWordService().catalog()
        } ui {
            catalogLoaded.set(true)
            textArea.text = "Ready!"
        }
    }
}
