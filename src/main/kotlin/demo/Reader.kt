package demo

import com.github.thomasnield.rxkotlinfx.actionEvents
import tornadofx.*

class ReaderApp : App(Reader::class)

class Reader : View() {
    override val root = vbox {
        button("Load it!")
                .actionEvents()
                .subscribe { println(it) }
        textarea {
            isEditable = false
        }
    }

    init {
        title = "Craig's Window"
    }
}
