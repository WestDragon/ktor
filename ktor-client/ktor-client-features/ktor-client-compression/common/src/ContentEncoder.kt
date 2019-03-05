package io.ktor.client.features.compression

import kotlinx.coroutines.io.*

interface ContentEncoder {
    val name: String

    suspend fun decode(content: ByteReadChannel): ByteReadChannel
}

internal object GZipEncoder : ContentEncoder {
    override val name: String = "gzip"

    override suspend fun decode(content: ByteReadChannel): ByteReadChannel {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

internal object DeflateEncoder : ContentEncoder {
    override suspend fun decode(content: ByteReadChannel): ByteReadChannel {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override val name: String
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

}

internal object IdentityEncoder : ContentEncoder {
    override val name: String = "identity"

    override suspend fun decode(content: ByteReadChannel): ByteReadChannel = content
}

