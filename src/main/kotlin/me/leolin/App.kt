package me.leolin

import io.vertx.core.AbstractVerticle
import io.vertx.core.Vertx
import io.vertx.core.http.ServerWebSocket
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.stereotype.Component
import java.util.Collections
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author leolin
 */

fun main(args: Array<String>) {
    val context = SpringApplication.run(javaClass<App>(), *args)
    val vertx = Vertx.vertx()
    vertx.deployVerticle(context.getBean(javaClass<WebsocketVerticle>()))
}

@SpringBootApplication open class App

@Component class WebsocketVerticle : AbstractVerticle() {
    override fun start() {
        val id = AtomicInteger(0)

        val map = Collections.synchronizedMap(hashMapOf<Int, ServerWebSocket>())

        fun broadcast(msg: String) {
            map.forEach {
                it.value.writeFinalTextFrame(msg)
            }
        }

        vertx.createHttpServer().websocketHandler { webSocket ->
            val userId = id.incrementAndGet()

            broadcast("$userId join to chat!")

            map.put(userId, webSocket)

            webSocket.writeFinalTextFrame("hi, your id is $userId")
            webSocket.handler { buffer ->
                val message = buffer.getString(0, buffer.length())
                broadcast("$userId : $message")
            }

            webSocket.closeHandler {
                map.remove(userId)
                broadcast("$userId left!")
            }

        }.listen(8000)
    }
}
