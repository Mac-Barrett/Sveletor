package com.sveletor.application.api

import com.sveletor.application.classes.SveletorSession
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun Application.authEndpoints() {
    routing {
        post("/login") {
            val params = call.receiveParameters()
            val username = params["username"] ?: return@post call.respondRedirect("/login")

            // TODO Implement Session validation
            val newSession = SveletorSession(username)

            if (!SveletorSession.validate(newSession)) {
                println("Authentication Failed")
                return@post call.respondRedirect("/login")
            }
            else {
                println("Session started: $username")
                call.sessions.set(SveletorSession(username))
                call.respondText { "Logged in as: $username" }
            }
        }

        get("/session") {
            call.respondText(Json.encodeToString(call.sessions.get<SveletorSession>()))
        }

        get("/logout") {
            call.sessions.clear("Sveletor_Session_ID")
            call.respondRedirect("/")
        }
    }
}