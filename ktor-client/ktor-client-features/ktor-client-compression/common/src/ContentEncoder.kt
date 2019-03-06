package io.ktor.client.features.compression

import io.ktor.util.*
import kotlinx.coroutines.*
import kotlinx.coroutines.io.*

interface ContentEncoder : Encoder {
    val name: String
}

internal object GZipEncoder : ContentEncoder, Encoder by GZip {
    override val name: String = "gzip"
}

internal object DeflateEncoder : ContentEncoder, Encoder by Deflate {
    override val name: String = "deflate"
}

internal object IdentityEncoder : ContentEncoder {
    override fun CoroutineScope.encode(source: ByteReadChannel): ByteReadChannel = source

    override fun CoroutineScope.decode(source: ByteReadChannel): ByteReadChannel = source

    override val name: String = "identity"

}

