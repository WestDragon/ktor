package org.jetbrains.ktor.websocket

import org.jetbrains.ktor.application.*
import org.jetbrains.ktor.http.*
import org.jetbrains.ktor.pipeline.*
import org.jetbrains.ktor.routing.*
import java.io.*
import java.util.*

interface WebSocketOutbound {
    fun send(frame: Frame)
}

abstract class WebSocket internal constructor(val call: ApplicationCall, val context: PipelineContext<*>) : Closeable {
    private val handlers = ArrayList<(Frame) -> Unit>()
    private val errorHandlers = ArrayList<(Throwable) -> Unit>()

    abstract val outbound: WebSocketOutbound

    /**
     * Enable or disable masking output messages by a random xor mask
     */
    @Deprecated("Not yet implemented")
    var masking = false

    /**
     * Specifies frame size limit. Connection will be closed if violated
     */
    @Deprecated("Not yet implemented")
    var maxFrameSize = Long.MAX_VALUE

    /**
     * Specifies minimal frame size limit. Connection will be closed if violated
     */
    @Deprecated("Not yet implemented")
    var minFrameSize = 0

    fun handle(handler: (Frame) -> Unit) {
        handlers.add(handler)
    }

    fun handleError(handler: (Throwable) -> Unit) {
        errorHandlers.add(handler)
    }

    override fun close(): Nothing {
        call.close() // TODO move call.close() to some generic point
        context.finishAll()
    }

    protected fun frameHandler(frame: Frame) {
        handlers.forEach { it(frame) }
    }
}

fun RoutingEntry.webSocket(path: String, protocol: String? = null, configure: WebSocket.() -> Unit) {
    route(HttpMethod.Get, path) {
        header(HttpHeaders.Connection, "Upgrade") {
            header(HttpHeaders.Upgrade, "websocket") {
                webSocketProtocol(protocol) {
                    handle {
                        val extensions = call.request.header(HttpHeaders.SecWebSocketExtensions)
                        if (extensions != null) throw IllegalStateException("Extensions are not yet supported")

                        call.respond(WebSocketUpgrade(call, protocol, configure))
                    }
                }
            }
        }
    }
}

private fun RoutingEntry.webSocketProtocol(protocol: String?, block: RoutingEntry.() -> Unit) {
    if (protocol == null) {
        block()
    } else {
        select(WebSocketProtocolsSelector(protocol)).block()
    }
}

private class WebSocketProtocolsSelector(val requiredProtocol: String) : RoutingSelector {
    override fun evaluate(context: RoutingResolveContext, index: Int): RouteSelectorEvaluation {
        val protocols = context.headers[HttpHeaders.SecWebSocketProtocol] ?: return RouteSelectorEvaluation(true, 1.0)
        if (requiredProtocol in parseHeaderValue(protocols).map { it.value }) {
            return RouteSelectorEvaluation(true, 1.0)
        }

        return RouteSelectorEvaluation.Failed
    }
}