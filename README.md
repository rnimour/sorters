# Sorters

This project is to highlight the simplicity and effectivity of Kotlin's coroutines.

You can run file `RandomGenerator.kt` to generate a list of random numbers. Try 16 million!

Then you can run:
* `MergeSort.kt` to merge it singlethreadedly;  
* `CoMergeSort.kt` to merge it using one coroutine for every item in the list;
* `SmartCoMergeSort.kt` to merge it using "only" about a thousand coroutines (i.e. only the first 10 layers); 
* `SmartCoMergeSort2.kt` to merge it using coroutines until the list is smaller than 1000;
* `SmartCoMergeSort3.kt` same as above, except slightly neater code.