package io.ktor.client.features.compression

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.util.*

class HttpCompression {

    class Config

    companion object : HttpClientFeature<Config, HttpCompression> {
        override val key: AttributeKey<HttpCompression> = AttributeKey("HttpCompression")

        override fun prepare(block: Config.() -> Unit): HttpCompression {
            TODO()
        }

        override fun install(feature: HttpCompression, scope: HttpClient) {
            TODO()
        }

    }
}
