package com.deepak.besaat.utils

import kotlinx.coroutines.*

object Coroutines {
    lateinit var scope: Job
    fun backThread(work:suspend (()->Unit)){
       scope =CoroutineScope(Dispatchers.IO).launch {
            work()
        }
    }

    fun mainThread(work:(()->Unit))=
        CoroutineScope(Dispatchers.Main).launch {
            work()
        }
}