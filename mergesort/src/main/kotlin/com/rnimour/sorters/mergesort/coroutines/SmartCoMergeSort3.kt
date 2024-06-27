package com.rnimour.sorters.mergesort.coroutines

import com.rnimour.sorters.mergesort.*
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
// if the list is small enough, call the normal mergeSort.
private val numberOfCoroutines = AtomicInteger(0)

fun main() {
    println(
        "Hello World! I am a merge sorter using coroutines smartly (way 3). Sorting list ${
            FILENAME.split("/").last()
        }"
    )

    val list = mutableListOf<Int>()
    time("Reading file") {
        File(FILENAME).forEachLine {
            list.add(it.toIntOrNull() ?: -1)
        }
    }

    time("Sorting") {
        runBlocking(Dispatchers.Default) { coMergeSort3(list) }
    }

    println("Number of coroutines started: ${numberOfCoroutines.get()}")

    list.sanityCheckIsSorted()
}

// The merge algorithms, now multithreaded with coroutines.
suspend fun coMergeSort3(list: MutableList<Int>) {
    if (list.size <= MAX_SPLIT_SIZE) {
        mergeSort(list) // no need to create coroutines for small lists
        return
    }

    val middle = list.size / 2
    val left = list.subList(0, middle)
    val right = list.subList(middle, list.size)

    coroutineScope {
        repeat(2) { numberOfCoroutines.incrementAndGet() }
        launch { smartCoMergeSort2(left) }
        launch { smartCoMergeSort2(right) }
    }

    merge(list, left, right)
}
