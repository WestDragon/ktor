package io.ktor.client.features.compression

import io.ktor.client.request.*
import io.ktor.client.tests.utils.*
import io.ktor.http.*
import kotlin.test.*

private const val TEST_URL = "$TEST_SERVER/compression"

class ContentEncodingTest {
    @Test
    fun testIdentity() {
        clientsTest {
            config {
                ContentEncoding()
            }

            test { client ->
                val response = client.get<String>("$TEST_URL/identity") {
                    accept(ContentType.Text.Plain)
                }

                assertEquals("Compressed response!", response)
            }
        }
    }

    @Test
    fun testDeflate() = clientsTest {
        config {
            ContentEncoding()
        }

        test { client ->
            val response = client.get<String>("$TEST_URL/deflate") {
                accept(ContentType.Text.Plain)
            }

            assertEquals("Compressed response!", response)
        }
    }

    @Test
    fun testGZip() = clientsTest {
        config {
            ContentEncoding()
        }

        test { client ->
            val response = client.get<String>("$TEST_URL/gzip") {
                accept(ContentType.Text.Plain)
            }

            assertEquals("Compressed response!", response)
        }
    }
}
