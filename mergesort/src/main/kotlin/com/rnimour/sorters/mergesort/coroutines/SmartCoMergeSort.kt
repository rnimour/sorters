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

// 1   million takes ~0.06s to read, ~0.13s to sort
// 4   million takes ~0.25s to read, ~0.21s to sort
// 16  million takes ~1   s to read, ~0.5 s to sort
// 64  million takes ~4   s to read, ~1.7 s to sort

// coroutines implementation which only creates at most 1023 (2^10 - 1) coroutines to prevent overhead.
private const val MAX_LEVEL = 10

fun main() {
    println("Hello World! I am a merge sorter using coroutines smartly. Sorting list ${FILENAME.split("/").last()}")

    val list = mutableListOf<Int>()
    time("Reading file") {
        File(FILENAME).forEachLine {
            list.add(it.toIntOrNull() ?: -1)
        }
    }

    time("Sorting") {
        runBlocking(Dispatchers.Default) { smartCoMergeSort(list, 0) }
    }

    list.sanityCheckIsSorted()
}

// The merge algorithms, now multithreaded with coroutines.
suspend fun smartCoMergeSort(list: MutableList<Int>, level: Int) {
    if (list.size <= 1) {
        return
    }

    val middle = list.size / 2
    val left = list.subList(0, middle)
    val right = list.subList(middle, list.size)

    // only create new coroutines if we are below the max level
    if (level < MAX_LEVEL) {
        coroutineScope {
            launch { smartCoMergeSort(left, level + 1) }
            launch { smartCoMergeSort(right, level + 1) }
        }
    } else {
        smartCoMergeSort(left, level + 1)
        smartCoMergeSort(right, level + 1)
    }

    merge(list, left, right)
}
