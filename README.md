# Sorters

This project is to highlight the simplicity and effectivity of Kotlin's coroutines.

You can run file `RandomGenerator.kt` to generate a list of random numbers. Try 10 million!

Then you can run:
`MergeSort.kt` to merge it singlethreadedly;  
`CoMergeSort.kt` to merge it using one coroutine for every item in the list;
`SmartCoMergeSort2.kt` to merge it using "only" about a thousand coroutines (i.e. )