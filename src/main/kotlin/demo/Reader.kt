package demo

import javafx.scene.text.FontWeight
import tornadofx.*

class ReaderApp : App(Reader::class)

class Reader : View() {
    override val root = vbox {
        textarea("Loading, please wait...") {
            isEditable = false
        }
    }

    init {
        title = "Craig's Window"
    }
}
