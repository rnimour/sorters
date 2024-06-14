package com.rnimour.sorters.mergesort.coroutines

import com.rnimour.sorters.mergesort.FILENAME
import com.rnimour.sorters.mergesort.merge
import com.rnimour.sorters.mergesort.sanityCheckIsSorted
import com.rnimour.sorters.mergesort.time
import kotlinx.coroutines.*
import java.io.File

// 1   million takes ~0.10s to read, ~1  s to sort
// 10  million takes ~0.5 s to read, ~9  s to sort
// 100 million takes ~5   s to read, ~???s to sort (longer than 600s). java.lang.OutOfMemoryError: Java heap space

// naive coroutines implementation: create new coroutine for each mergeSort call.
// Massive overhead: 10 million coroutines started
fun main() = runBlocking {
    println("Hello World! I am a merge sorter using coroutines. Sorting list ${FILENAME.split("/").last()}")

    val list = mutableListOf<Short>()
    time("Reading file") {
        File(FILENAME).forEachLine {
            list.add(it.toShortOrNull() ?: -1)
        }
    }

    time("Sorting") {
        runBlocking(Dispatchers.Default) { mergeSort(list, this) }
    }

    list.sanityCheckIsSorted()
}

// The merge algorithms, now multi-threaded with coroutines.
suspend fun mergeSort(list: MutableList<Short>, coroutineScope: CoroutineScope) {
    if (list.size <= 1) {
        return
    }

    val middle = list.size / 2
    val left = list.subList(0, middle)
    val right = list.subList(middle, list.size)

    val sortLeft = coroutineScope.async { mergeSort(left, coroutineScope) }
    val sortRight = coroutineScope.async { mergeSort(right, coroutineScope) }

    awaitAll(sortLeft, sortRight)

    merge(list, left, right)
}
