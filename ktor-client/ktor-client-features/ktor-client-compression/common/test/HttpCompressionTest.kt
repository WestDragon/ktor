package io.ktor.client.features.compression

import io.ktor.client.request.*
import io.ktor.client.tests.utils.*
import io.ktor.http.*
import kotlin.test.*

class HttpCompressionTest {

    @Test
    fun testIdentity() = clientsTest {
        config {
            install(HttpCompression)
        }

        test { client ->
            val response = client.get<String>("$TEST_SERVER/compression/identity") {
                accept(ContentType.Text.Plain)
            }

            assertEquals("Compressed response!", response)

            client.post<Unit>("$TEST_SERVER/compression/identity") {
                body = "Compressed Request!"
            }
        }
    }

    @Test
    fun testDeflate() = clientsTest {
        config {
            install(HttpCompression)
        }

        test { client ->
            val response = client.get<String>("$TEST_SERVER/compression/deflate") {
                accept(ContentType.Text.Plain)
            }

            assertEquals("Compressed response!", response)

            client.post<Unit>("$TEST_SERVER/compression/deflate") {
                body = "Compressed Request!"
            }
        }
    }

    @Test
    fun testGZip() = clientsTest {
        config {
            install(HttpCompression)
        }

        test { client ->
            val response = client.get<String>("$TEST_SERVER/compression/gzip") {
                accept(ContentType.Text.Plain)
            }

            assertEquals("Compressed response!", response)

            client.post<Unit>("$TEST_SERVER/compression/gzip") {
                body = "Compressed Request!"
            }
        }
    }
}
