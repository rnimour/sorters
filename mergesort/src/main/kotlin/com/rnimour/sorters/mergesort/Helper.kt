package com.rnimour.sorters.mergesort

/**
 * helper function to print time taken like so:
 * ```
 * time("Sorting") { mergeSort(list) }
 * ```
 * Do not use print(ln) inside the action, it will mess up the message
 */
fun time(message: String = "time taken", action: () -> Any): Any {
    print("$message: ")
    val start = System.currentTimeMillis()
    val result = action()
    val end = System.currentTimeMillis()
    println("${end - start} ms")
    return result
}

fun MutableList<Short>.sanityCheckIsSorted() {
    time("Checking") {
        if (isSorted(this)) {
            println("List is sorted")
        } else {
            throw IllegalStateException("List is not sorted")
        }
    }
}

fun isSorted(list: List<Short>): Boolean {
    for (i in 0..<list.size - 1) {
        if (list[i] > list[i + 1]) {
            return false
        }
    }
    return true
}
