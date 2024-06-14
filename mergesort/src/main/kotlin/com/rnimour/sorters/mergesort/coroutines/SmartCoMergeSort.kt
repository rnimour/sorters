package com.rnimour.sorters.mergesort.coroutines

import com.rnimour.sorters.mergesort.FILENAME
import com.rnimour.sorters.mergesort.merge
import com.rnimour.sorters.mergesort.sanityCheckIsSorted
import com.rnimour.sorters.mergesort.time
import kotlinx.coroutines.*
import java.io.File

// 1   million takes ~0.2s to read, ~ 0.2 s to sort
// 10  million takes ~0.5s to read, ~ 0.45s to sort
// 100 million takes ~5  s to read, ~ 3-4 s to sort

// coroutines implementation which only creates at most 1023 (2^10 - 1) coroutines to prevent overhead.
const val MAX_LEVEL = 10

fun main() {
    println("Hello World! I am a merge sorter using coroutines smartly. Sorting list ${FILENAME.split("/").last()}")

    val list = mutableListOf<Short>()
    time("Reading file") {
        File(FILENAME).forEachLine {
            list.add(it.toShortOrNull() ?: -1)
        }
    }

    time("Sorting") {
        runBlocking(Dispatchers.Default) { mergeSort(list, this, 0) }
    }

    list.sanityCheckIsSorted()
}

// The merge algorithms, now multi-threaded with coroutines.
suspend fun mergeSort(list: MutableList<Short>, coroutineScope: CoroutineScope, level: Int) {
    if (list.size <= 1) {
        return
    }

    val middle = list.size / 2
    val left = list.subList(0, middle)
    val right = list.subList(middle, list.size)

    // only create new coroutines if we are below the max level
    if (level < MAX_LEVEL) {
        // println("level: $level, spawning two new coroutines!")
        val sortLeft = coroutineScope.launch { mergeSort(left, coroutineScope, level + 1) }
        val sortRight = coroutineScope.launch { mergeSort(right, coroutineScope, level + 1) }
        joinAll(sortLeft, sortRight)
    } else {
        mergeSort(left, coroutineScope, level)
        mergeSort(right, coroutineScope, level)
    }

    merge(list, left, right)
}
