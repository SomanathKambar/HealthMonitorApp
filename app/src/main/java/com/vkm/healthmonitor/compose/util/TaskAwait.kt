package com.vkm.healthmonitor.compose.util

import com.google.android.gms.tasks.Task
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun <T> Task<T>.awaitTask(): T =
    suspendCancellableCoroutine { cont ->
        addOnSuccessListener { r -> cont.resume(r) }
            .addOnFailureListener { e -> cont.resumeWithException(e) }
            .addOnCanceledListener { cont.cancel() }
    }
