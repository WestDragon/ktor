package io.ktor.client.features.compression

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.response.*
import io.ktor.http.*
import io.ktor.util.*
import kotlinx.coroutines.io.*

class ContentEncoding(
    private val encoders: Map<String, ContentEncoder>,
    private val qualityValues: Map<String, Float>
) {
    private val requestHeader = buildString {
        for (encoder in encoders.values) {
            if (length > 0) append(',')

            append(encoder.name)

            val quality = qualityValues[encoder.name] ?: continue
            check(quality in 0.0..1.0) { "Invalid quality value: $quality for encoder: $encoder" }

            val qualityValue = quality.toString().take(5)
            append(";q=$qualityValue")
        }
    }

    private fun setRequestHeaders(headers: HeadersBuilder) {
        if (headers.contains(HttpHeaders.AcceptEncoding)) return
        headers[HttpHeaders.AcceptEncoding] = requestHeader
    }

    private suspend fun decode(headers: Headers, content: ByteReadChannel): ByteReadChannel {
        val encoding = headers[HttpHeaders.ContentEncoding] ?: return content
        val encoder = encoders[encoding] ?: throw UnsupportedContentEncodingException(encoding)
        return encoder.decode(content)
    }

    class Config {
        internal val encoders: MutableMap<String, ContentEncoder> = mutableMapOf()
        internal val qualityValues: MutableMap<String, Float> = mutableMapOf()

        fun gzip(quality: Float? = null) {
            customEncoder(GZipEncoder, quality)
        }

        fun deflate(quality: Float? = null) {
            customEncoder(DeflateEncoder, quality)
        }

        fun identity(quality: Float? = null) {
            customEncoder(IdentityEncoder, quality)
        }

        fun customEncoder(encoder: ContentEncoder, quality: Float? = null) {
            val name = encoder.name
            encoders[name] = encoder

            if (quality == null) {
                qualityValues.remove(name)
            } else {
                qualityValues[name] = quality
            }
        }
    }

    companion object : HttpClientFeature<Config, ContentEncoding> {
        override val key: AttributeKey<ContentEncoding> = AttributeKey("HttpCompression")

        override fun prepare(block: Config.() -> Unit): ContentEncoding {
            val config = Config().apply(block)


            return with(config) {
                ContentEncoding(encoders, qualityValues)
            }
        }

        override fun install(feature: ContentEncoding, scope: HttpClient) {
            scope.requestPipeline.intercept(HttpRequestPipeline.State) {
                feature.setRequestHeaders(context.headers)
            }

            scope.responsePipeline.intercept(HttpResponsePipeline.Receive) { (type, content) ->
                if (content !is ByteReadChannel) return@intercept

                proceedWith(HttpResponseContainer(type, feature.decode(context.response.headers, content)))
            }
        }
    }
}

fun HttpClientConfig<*>.ContentEncoding(block: ContentEncoding.Config.() -> Unit = {}) {
    install(ContentEncoding, block)
}

@Suppress("KDocMissingDocumentation")
class UnsupportedContentEncodingException(encoding: String) :
    IllegalStateException("Content-Encoding: $encoding unsupported.")
