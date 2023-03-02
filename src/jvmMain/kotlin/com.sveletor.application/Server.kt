package com.sveletor.application

import com.sveletor.application.api.example
import com.sveletor.application.classes.SveletorSession
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.cachingheaders.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.serialization.json.*
import java.time.Duration


/**
 * Application entry point: in conjunction w/ application.conf
 * will set up the server and call the [sveletorMain] module
 */
fun main(args: Array<String>) = EngineMain.main(args)


/**
 * The Sveletor main module. Attaches other necessary plugins.
 */
fun Application.sveletorMain() {
    installPlugins()
    installAPI()

    // Static Content
    routing {
        static("/") {
            // Serves SvelteKit Static Adapter's 'assets' build output
            resources("web/static")
            // Serves resources/static folder content
            resources("static")
        }
    }
}


/** Module that installs ktor plugins */
private fun Application.installPlugins() {
    install(CORS) {
        allowHost("localhost")
        allowHost("localhost:8080")
        allowHost("127.0.0.1")
        allowHost("127.0.0.1:8080")
        allowHeader(HttpHeaders.ContentType)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Patch)
        allowMethod(HttpMethod.Delete)
    }

    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
        })
    }


    install(Sessions) {
        cookie<SveletorSession>("Sveletor_Session_ID", storage = SessionStorageMemory()) {
            cookie.path = "/"
            cookie.maxAgeInSeconds = Duration.ofDays(3).toSeconds()
        }
    }

    install(CachingHeaders) {
        options { _, outgoingContent ->
            when (outgoingContent.contentType?.withoutParameters()) {
                ContentType.Text.JavaScript -> io.ktor.http.content.CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 5 * 60 * 60))
                ContentType.Text.CSS -> io.ktor.http.content.CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 5 * 60 * 60))

                else -> null
            }
        }
    }
}


/** Module that installs API Endpoints */
private fun Application.installAPI() {
    // /api/example.kt
    example()
}