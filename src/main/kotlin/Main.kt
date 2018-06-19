
fun main(args: Array<String>) {

    val api = createUnfoldingWordService();
    val catalog = api.catalog()
    catalog.subscribe { println(it)}

}