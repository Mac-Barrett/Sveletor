package com.sveletor.application.api

import com.sveletor.application.classes.SveletorSession
import com.sveletor.application.components.loginPage
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

fun Application.authEndpoints() {
    routing {
        post("/login") {
            val params = call.receiveParameters()
            val username = params["username"] ?: return@post loginPage(call)

            val newSession = SveletorSession(username)
            if (!SveletorSession.validate(newSession)) {
                println("Authentication Failed")
                return@post loginPage(call)
            }
            else {
                println("Session started: $username")
                call.sessions.set(SveletorSession(username))
                call.respondText { "Logged in as: $username" }
            }
        }

        get("/logout") {
            call.sessions.clear("Sveletor_Session_ID")
            call.respondRedirect(call.request.uri)
        }
    }
}