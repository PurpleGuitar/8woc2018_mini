package demo

import tornadofx.*
import javafx.scene.text.FontWeight

class Reader : View() {
    override val root = hbox {
        label("Hi there!")
    }
}

class ReaderApp : App(Reader::class, Styles::class) {
}

class Styles : Stylesheet() {
    init {
        label {
            fontSize = 20.px
            fontWeight = FontWeight.BOLD
            backgroundColor += c("#cecece")
        }
    }
}
