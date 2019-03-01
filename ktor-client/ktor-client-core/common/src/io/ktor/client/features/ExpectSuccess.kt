package io.ktor.client.features

import io.ktor.client.*
import io.ktor.util.*

@Deprecated(
    "[BadResponseStatusException] is deprecated. Use [ResponseException] instead.",
    ReplaceWith("ResponseException"),
    DeprecationLevel.ERROR
)
@Suppress("KDocMissingDocumentation")
typealias BadResponseStatusException = ResponseException

@Deprecated(
    "Use [HttpCallValidator] instead.",
    ReplaceWith("HttpCallValidator"),
    DeprecationLevel.ERROR
)
@Suppress("KDocMissingDocumentation")
class ExpectSuccess {
    @Suppress("DEPRECATION_ERROR")
    companion object : HttpClientFeature<Unit, ExpectSuccess> {

        override val key: AttributeKey<ExpectSuccess>
            get() = error("Deprecated")

        override fun prepare(block: Unit.() -> Unit): ExpectSuccess {
            error("Deprecated")
        }

        override fun install(feature: ExpectSuccess, scope: HttpClient) {
            error("Deprecated")
        }
    }
}
