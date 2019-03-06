package io.ktor.util

import io.ktor.util.cio.*
import kotlinx.coroutines.*
import kotlinx.coroutines.io.*
import kotlinx.io.core.*
import java.nio.*
import java.util.zip.*

private const val GZIP_HEADER_SIZE: Int = 10

actual val Deflate: Encoder = object : Encoder {
    override fun CoroutineScope.encode(source: ByteReadChannel): ByteReadChannel =
        source.deflated(gzip = true, coroutineContext = coroutineContext)

    override fun CoroutineScope.decode(source: ByteReadChannel): ByteReadChannel =
        inflate(source, gzip = false)

}

actual val GZip: Encoder = object : Encoder {
    override fun CoroutineScope.encode(source: ByteReadChannel): ByteReadChannel =
        source.deflated(gzip = true, coroutineContext = coroutineContext)

    override fun CoroutineScope.decode(source: ByteReadChannel): ByteReadChannel = inflate(source)
}

private fun CoroutineScope.inflate(
    source: ByteReadChannel,
    gzip: Boolean = true
): ByteReadChannel = writer {
    val readBuffer = KtorDefaultPool.borrow()
    val writeBuffer = KtorDefaultPool.borrow()
    val inflater = Inflater(true)

    if (gzip) {
        val header = source.readPacket(GZIP_HEADER_SIZE)
        val magic = header.readShort()
        val format = header.readByte()
        val padding = header.readBytes()

        // TODO: validate header

        check(padding.all { it == 0.toByte() })
    }

    try {
        while (!source.isClosedForRead) {
            readBuffer.compact()
            if (source.readAvailable(readBuffer) <= 0) continue
            readBuffer.flip()

            inflater.setInput(readBuffer.array(), readBuffer.position(), readBuffer.remaining())

            while (!inflater.needsInput() && !inflater.finished()) {
                inflater.inflateTo(channel, writeBuffer)
                readBuffer.position(readBuffer.limit() - inflater.remaining)
            }
        }

        while (!inflater.finished()) {
            inflater.inflateTo(channel, writeBuffer)
            readBuffer.position(readBuffer.limit() - inflater.remaining)
        }

        if (gzip) {
            check(readBuffer.remaining() == 8) { "Expected 8 bytes in the end. Actual: ${readBuffer.remaining()} $" }
            // TODO: validate checksum
        } else {
            check(!readBuffer.hasRemaining())
        }

    } catch (cause: Throwable) {
        throw cause
    } finally {
        inflater.end()
        KtorDefaultPool.recycle(readBuffer)
        KtorDefaultPool.recycle(writeBuffer)
    }
}.channel

private suspend fun Inflater.inflateTo(channel: ByteWriteChannel, buffer: ByteBuffer) {
    buffer.clear()

    val inflated = inflate(buffer.array(), buffer.position(), buffer.remaining())
    buffer.position(buffer.position() + inflated)
    buffer.flip()
    channel.writeFully(buffer)
}
