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
import javafx.scene.control.ComboBox
import javafx.scene.control.TextArea
import javafx.scene.layout.Priority
import tornadofx.*
import usfmToMarkdown

class ReaderApp : App(ReaderView::class)

class ReaderView : View() {

    private var catalog: Observable<Catalog> by singleAssign()
    private val catalogLoaded = SimpleBooleanProperty(false)

    private val languages = FXCollections.observableArrayList("Loading...")
    private var languagePicker: ComboBox<String> by singleAssign()
    private val selectedLanguage = SimpleStringProperty()

    private val versions = FXCollections.observableArrayList("Loading...")
    private var versionPicker: ComboBox<String> by singleAssign()
    private val selectedVersion = SimpleStringProperty()

    private val books = FXCollections.observableArrayList("Loading...")
    private var bookPicker: ComboBox<String> by singleAssign()
    private val selectedBook = SimpleStringProperty()

    private var textArea: TextArea by singleAssign()
    override val root = vbox {
        hbox {
            combobox(selectedLanguage, languages) {
                languagePicker = this
                selectedLanguage.set("Loading...")
                setOnAction {
                    catalog
                            .flatMap { it.anthologies() }
                            .filter { it.slug == "bible" }
                            .flatMap { it.languages() }
                            .filter { it.lc == selectedLanguage.value }
                            .flatMap { it.versions() }
                            .collectInto(ArrayList<String>()) { list, item -> list.add(item.slug) }
                            .toObservable()
                            .observeOnFx()
                            .subscribe {
                                versions.clear()
                                versions.addAll(it)
                                versions.sort()
                                versionPicker.selectionModel.selectFirst()
                            }
                }
            }
            combobox(selectedVersion, versions) {
                versionPicker = this
                selectedVersion.set("Loading...")
                setOnAction {
                    catalog
                            .flatMap { it.anthologies() }
                            .filter { it.slug == "bible" }
                            .flatMap { it.languages() }
                            .filter { it.lc == selectedLanguage.value }
                            .flatMap { it.versions() }
                            .filter { it.slug == selectedVersion.value }
                            .flatMap { it.books() }
                            .collectInto(ArrayList<String>()) { list, item -> list.add(item.slug) }
                            .toObservable()
                            .observeOnFx()
                            .subscribe {
                                books.clear()
                                books.addAll(it)
                                books.sort()
                                bookPicker.selectionModel.selectFirst()
                            }
                    catalogLoaded.set(true)
                    textArea.text = "Ready!"
                }
            }
            combobox(selectedBook, books) {
                bookPicker = this
                selectedBook.set("Loading...")
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
                                    selectedLanguage.value,
                                    selectedVersion.value,
                                    selectedBook.value)
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
        textArea.text = "Loading catalog, please wait..."
        runAsync {
            catalog = createUnfoldingWordService().catalog()
            catalog
                    .flatMap { it.anthologies() }
                    .filter { it.slug == "bible" }
                    .flatMap { it.languages() }
                    .collectInto(ArrayList<String>()) { list, language -> list.add(language.lc) }
                    .toObservable()
                    .observeOnFx()
                    .subscribe {
                        languages.clear()
                        languages.addAll(it)
                        languages.sort()
                        if (languages.contains("en")) {
                            selectedLanguage.set("en")
                        } else {
                            languagePicker.selectionModel.selectFirst()
                        }

                    }
        }
    }
}
