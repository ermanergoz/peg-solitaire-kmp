package com.erman.pegsolitaire.platform

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class FlowCollector<T>(private val flow: Flow<T>) {
    private var job: Job? = null

    fun collect(onEach: (T) -> Unit) {
        job?.cancel()
        job = flow
            .onEach { onEach(it) }
            .launchIn(CoroutineScope(Dispatchers.Main))
    }

    fun cancel() {
        job?.cancel()
        job = null
    }
}
