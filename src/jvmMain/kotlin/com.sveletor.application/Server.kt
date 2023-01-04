package com.sveletor.application

import com.sveletor.application.classes.SveletorSession
import com.sveletor.application.api.authEndpoints
import com.sveletor.application.components.authenticatedSveltePage
import com.sveletor.application.components.sveltePage
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.cachingheaders.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
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
    applicationPlugins()

    applicationPageRouting()

    applicationAPIModules()

    // Static Content
    routing {
        static("/") {
            static("_app") { resources("web/_app") }    // SvelteKit Static Adapter's compiled scripts
            static("image") { resources("web/image") }  // SvelteKit proj's static/image folder
        }
    }
}


/**
 * Svelte Pages should go here.
 */
fun Application.applicationPageRouting() {
    sveltePage("/", "index.html")

    sveltePage("/login", "login.html")

    sveltePage("/welcome", "welcome.html")

    authenticatedSveltePage("/welcome/deeper", "welcome/deeper.html")
}


/**
 * API Modules can go here
 */
private fun Application.applicationAPIModules() {
    authEndpoints()
}


/**
 * Server plugins can go here
 */
private fun Application.applicationPlugins() {
    install(Sessions) {
        cookie<SveletorSession>("Sveletor_Session_ID", storage = SessionStorageMemory()) {
            cookie.path = "/"
            cookie.maxAgeInSeconds = Duration.ofDays(3).toSeconds()
//            cookie.httpOnly = false
//            cookie.encoding = CookieEncoding.URI_ENCODING
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