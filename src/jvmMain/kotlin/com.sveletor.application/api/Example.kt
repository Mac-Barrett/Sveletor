package com.sveletor.application.api

import com.sveletor.application.classes.ExampleDataSet
import com.sveletor.application.components.authenticatedEndpoint
import com.sveletor.application.components.authenticatedSveltePage
import com.sveletor.application.components.sveltePage
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.netty.handler.codec.http.HttpMethod
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun Application.example() {
    sveltePage("/")

    sveltePage("/login")

    authenticatedSveltePage("/example")

    routing {
        // Authenticated endpoint in a routing block
        authenticatedEndpoint(HttpMethod.GET, "/login", action = { session ->
            return@authenticatedEndpoint call.respondText(
                text = Json.encodeToString(session),
                status = HttpStatusCode.OK
            )
        })

        route("/data") {
            // Authenticated endpoint in a route block
            authenticatedEndpoint(HttpMethod.GET, "", action = { _ ->
                call.respondText(
                    text = Json.encodeToString(ExampleDataSet.getAll()),
                    status = HttpStatusCode.OK
                )
            })

            // Even deeper nested into the route block
            route("/{id}") {
                authenticatedEndpoint(HttpMethod.GET, "", action = { _ ->
                    val id = call.parameters["id"]?.toIntOrNull()

                    val item = id?.let { ExampleDataSet.get(id) } ?: return@authenticatedEndpoint call.respondText(
                        text = "bad id",
                        status = HttpStatusCode.BadRequest
                    )

                    call.respondText(
                        text = Json.encodeToString(item),
                        status = HttpStatusCode.OK
                    )
                })
            }

            // These equate to the same endpoint as above
            authenticatedEndpoint(HttpMethod.PUT, "/{id}", action = { _ ->
                val id = call.parameters["id"]?.toIntOrNull()

                val itemExists = id?.let { ExampleDataSet.get(id) } != null

                if (id == null) {
                    call.respondText(
                        text = "bad id",
                        status = HttpStatusCode.BadRequest
                    )
                }
                if (itemExists) {
                    call.respondText(
                        text = "ID already exists",
                        status = HttpStatusCode.BadRequest
                    )
                }

                try {
                    ExampleDataSet.put(call.receive())
                    call.respondText(
                        text = "Success!",
                        status = HttpStatusCode.OK
                    )
                }
                catch (ex: Exception) {
                    println(ex.stackTrace)
                    call.respondText(
                        text = "Could not transform data into Data Item Class",
                        status = HttpStatusCode.BadRequest
                    )
                }
            })

            authenticatedEndpoint(HttpMethod.POST, "/{id}", action = { _ ->
                try {
                    ExampleDataSet.post(call.receive())
                    call.respondText(
                        text = "Success!",
                        status = HttpStatusCode.OK
                    )
                }
                catch (ex: Exception) {
                    println(ex.stackTrace)
                    call.respondText(
                        text = "Could not transform data into Data Item Class",
                        status = HttpStatusCode.BadRequest
                    )
                }
            })

            authenticatedEndpoint(HttpMethod.DELETE, "/{id}", action = { _ ->
                val id = call.parameters["id"]?.toIntOrNull()

                id?.let { ExampleDataSet.get(id) } ?: return@authenticatedEndpoint call.respondText(
                    text = "no data exists at this index",
                    status = HttpStatusCode.BadRequest
                )

                ExampleDataSet.delete(id)

                call.respondText(
                    text = "Success!",
                    status = HttpStatusCode.OK
                )
            })
        }
    }
}