package io.ktor.client.features.compression

import io.ktor.http.*
import kotlinx.coroutines.io.*

interface ContentEncoder {
    val name: String

    fun isApplicable(message: HttpMessage): Boolean

    suspend fun decode(content: ByteReadChannel): ByteReadChannel
}
