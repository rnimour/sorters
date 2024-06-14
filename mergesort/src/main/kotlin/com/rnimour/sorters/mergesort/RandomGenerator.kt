package com.rnimour.sorters.mergesort

import java.io.File

// amount of numbers to generate
const val AMOUNT = 10_000_000

// file to write the random numbers to
const val FILE_RANDOM = "random_list${AMOUNT%1_000_000}mil.txt"

// generates a list of random numbers and writes them to a file
fun main() {
    println("generating list with $AMOUNT random numbers in file $FILE_RANDOM")

    time {
        File(FILE_RANDOM).bufferedWriter().use {
            for (i in 0 until AMOUNT) {
                it.write("${(0..Short.MAX_VALUE).random()}\n")
            }
        }
    }
}