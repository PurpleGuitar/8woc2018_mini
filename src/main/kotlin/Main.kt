fun main(args: Array<String>) {

    // TODO: Don't hard-code these
    val anthologyCode = "bible"
    val languageCode = "en"
    val versionCode = "ulb-en"
    val bookCode = "gen"

    val api = createUnfoldingWordService();
    val catalog = api.catalog()
    catalog.subscribe {
        it.anthologies()
                .filter() { it.slug == anthologyCode }
                .subscribe() {
                    it.languages()
                            .filter() { it.lc == languageCode }
                            .subscribe() {
                                it.versions()
                                        .filter() { it.slug == versionCode }
                                        .subscribe() {
                                            it.books()
                                                    .filter() { it.slug == bookCode }
                                                    .subscribe() {
                                                        println(it)
                                                    }
                                        }
                            }
                }
    }

}

