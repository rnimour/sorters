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

// 1   million takes ~0.06s to read, ~0.13s to sort
// 4   million takes ~0.25s to read, ~0.24s to sort
// 16  million takes ~1   s to read, ~0.55s to sort
// 64  million takes ~4   s to read, ~1.9 s to sort

// coroutines implementation which only creates coroutines until the computation is small enough to prevent overhead.
const val MAX_SPLIT_SIZE = 1000
val numberOfCoroutines = AtomicInteger(0)

fun main() {
    println("Hello World! I am a merge sorter using coroutines smartly (way 2). Sorting list ${FILENAME.split("/").last()}")

    val list = mutableListOf<Int>()
    time("Reading file") {
        File(FILENAME).forEachLine {
            list.add(it.toIntOrNull() ?: -1)
        }
    }

    time("Sorting") {
        runBlocking(Dispatchers.Default) { mergeSortRelaxedCoroutines(list) }
    }

    println("Number of coroutines started: ${numberOfCoroutines.get()}")

    list.sanityCheckIsSorted()
}

// The merge algorithms, now multithreaded with coroutines.
suspend fun mergeSortRelaxedCoroutines(list: MutableList<Int>) {
    if (list.size <= 1) {
        return
    }

    val middle = list.size / 2
    val left = list.subList(0, middle)
    val right = list.subList(middle, list.size)

    // only create new coroutines if we have to sort more than 10000 elements
    if (list.size > MAX_SPLIT_SIZE) {
        coroutineScope {
            repeat(2) { numberOfCoroutines.incrementAndGet() }
            launch { mergeSortRelaxedCoroutines(left) }
            launch { mergeSortRelaxedCoroutines(right) }
        }
    } else {
        mergeSortRelaxedCoroutines(left)
        mergeSortRelaxedCoroutines(right)
    }

    merge(list, left, right)
}
