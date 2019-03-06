package io.ktor.util

import kotlinx.coroutines.*
import kotlinx.coroutines.io.*

expect val Deflate: Encoder

expect val GZip: Encoder

interface Encoder {
    fun CoroutineScope.encode(source: ByteReadChannel): ByteReadChannel

    fun CoroutineScope.decode(source: ByteReadChannel): ByteReadChannel
}
