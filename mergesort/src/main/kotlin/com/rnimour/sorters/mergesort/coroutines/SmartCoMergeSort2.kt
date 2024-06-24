package com.rnimour.sorters.mergesort.coroutines

import com.rnimour.sorters.mergesort.FILENAME
import com.rnimour.sorters.mergesort.merge
import com.rnimour.sorters.mergesort.sanityCheckIsSorted
import com.rnimour.sorters.mergesort.time
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import java.util.concurrent.atomic.AtomicInteger

// 1   million takes ~0.2s to read, ~ 0.2 s to sort
// 10  million takes ~0.5s to read, ~ 0.45s to sort
// 100 million takes ~5  s to read, ~ 3-4 s to sort

// coroutines implementation which only creates coroutines until the computation is small enough to prevent overhead.
const val MAX_SPLIT_SIZE = 1000
val numberOfCoroutines = AtomicInteger(0)

fun main() {
    println("Hello World! I am a merge sorter using coroutines smartly (way 2). Sorting list ${FILENAME.split("/").last()}")

    val list = mutableListOf<Short>()
    time("Reading file") {
        File(FILENAME).forEachLine {
            list.add(it.toShortOrNull() ?: -1)
        }
    }

    time("Sorting") {
        runBlocking(Dispatchers.Default) { mergeSortRelaxedCoroutines(list) }
    }

    println("Number of coroutines started: ${numberOfCoroutines.get()}")

    list.sanityCheckIsSorted()
}

// The merge algorithms, now multithreaded with coroutines.
suspend fun mergeSortRelaxedCoroutines(list: MutableList<Short>) {
    if (list.size <= 1) {
        return
    }

    val middle = list.size / 2
    val left = list.subList(0, middle)
    val right = list.subList(middle, list.size)

    // only create new coroutines if we have to sort more than 10000 elements
    if (list.size > MAX_SPLIT_SIZE) {
        coroutineScope {
            launch { mergeSortRelaxedCoroutines(left) }
            launch { mergeSortRelaxedCoroutines(right) }
        }
    } else {
        mergeSortRelaxedCoroutines(left)
        mergeSortRelaxedCoroutines(right)
    }

    merge(list, left, right)
}
