package io.ktor.client.features.compression

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.response.*
import io.ktor.util.*

class ContentEncoding {
    class Config {
        fun gzip(quality: Float = 0.5f) {}
        fun deflate(quality: Float = 0.5f) {}
        fun identity(quality: Float = 0.5f) {}
        fun custom(encoder: ContentEncoder, quality: Float = 0.5f) {}
    }

    companion object : HttpClientFeature<Config, ContentEncoding> {
        override val key: AttributeKey<ContentEncoding> = AttributeKey("HttpCompression")

        override fun prepare(block: Config.() -> Unit): ContentEncoding {
            TODO()
        }

        override fun install(feature: ContentEncoding, scope: HttpClient) {
            scope.responsePipeline.intercept(HttpResponsePipeline.Receive) { (type, content) ->
            }
        }
    }
}

fun HttpClientConfig<*>.ContentEncoding(block: ContentEncoding.Config.() -> Unit = {}) {
    install(ContentEncoding, block)
}
