package com.sveletor.application.api

import com.sveletor.application.classes.ExampleDataSet
import com.sveletor.application.components.authenticatedEndpoint
import com.sveletor.application.components.authenticatedSveltePage
import com.sveletor.application.components.sveltePage
import io.ktor.http.*
import io.ktor.server.application.*
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
            authenticatedEndpoint(HttpMethod.GET, "", action = { session ->
                ExampleDataSet.getAll()
            })

            // Even deeper nested into the route block
            route("/{id}") {
                authenticatedEndpoint(HttpMethod.GET, "", action = { session ->
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
            authenticatedEndpoint(HttpMethod.PUT, "/{id}", action = { session ->
                val id = call.parameters["id"]?.toIntOrNull()


            })

            authenticatedEndpoint(HttpMethod.POST, "/{id}", action = { session ->
                println(session)
            })

            authenticatedEndpoint(HttpMethod.DELETE, "/{id}", action = { session ->
                println(session)
            })
        }
    }
}