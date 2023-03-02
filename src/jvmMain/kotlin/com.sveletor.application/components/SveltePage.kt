package com.sveletor.application.components

import com.sveletor.application.classes.SveletorSession
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import io.ktor.server.sessions.*


/**
 * Svelte Page - Will respond with contentType text/html to client. All requests
 * to these endpoints should be GET
 *
 * @param endpoint The route this page will sit on. This should match the
 * path to the svelte page in the resources directory e.g.:
 *
 * - "/" will serve the file at /resources/web/index.html
 * - "/foo" will serve the file at /resources/web/foo.html
 * - "/foo/bar" will serve the file at /resources/web/foo/bar.html
 *
 * This is necessary because Svelte's Static adapter forces the server to
 * mimic its file based routing system to a certain extent.
 *
 * @param preResponse Suspending function that will run before the server
 * responds w/ the html file. Can be used for additional session vetting.
 *
 * @author Mac Barrett
 */
fun Application.sveltePage(
    endpoint: String,
    preResponse: (suspend PipelineContext<Unit, ApplicationCall>.(ApplicationCall) -> Unit)? = null
) {
    routing {
        get(endpoint) {
            if (preResponse != null) preResponse(call)

            val path = if (endpoint == "/") "/index" else endpoint
            call.respondText(
                this::class.java.classLoader.getResource("web${path}.html")!!.readText(),
                ContentType.Text.Html
            )
        }
    }
}


/**
 * Authenticated Svelte Page - Will validate user's session, ensuring they're logged in.
 * Utilizes [sveltePage]
 * - If they aren't logged in, the server will respond w/ the static login page
 * - If they are, it will run the [preResponse] it received and then respond normally.
 *
 * @param endpoint The route this page will sit on. This should match the
 * path to the svelte page in the resources directory e.g.:
 *
 * - "/" will serve the file at /resources/web/index.html
 * - "/foo" will serve the file at /resources/web/foo.html
 * - "/foo/bar" will serve the file at /resources/web/foo/bar.html
 *
 * This is necessary because Svelte's Static adapter forces the server to
 * mimic its file based routing system to a certain extent.
 *
 * @param preResponse Suspending function that will run before the server
 * responds w/ the html file. Can be used for additional session vetting.
 */
fun Application.authenticatedSveltePage(
    endpoint: String,
    preResponse: (suspend PipelineContext<Unit, ApplicationCall>.(ApplicationCall, SveletorSession) -> Unit)? = null
) {
    val deployment = environment.config.propertyOrNull("ktor.ENVIRONMENT")?.getString()

    sveltePage(endpoint, preResponse = { call: ApplicationCall ->
        var session = call.sessions.get<SveletorSession>()

        if (session == null || !session.validate()) {
            when (deployment) {
//                 Start new session so that dev's don't have to login all the time
                "dev" -> {
                    session = SveletorSession(
                        username = "MBARRETT"
                    )
                    call.sessions.set(session)

                    if (preResponse != null) {
                        preResponse(call, session)
                    }
                }

                // Force login
                else -> {
                    return@sveltePage call.respondText(
                        text = this::class.java.classLoader.getResource("web/login.html")!!.readText(),
                        ContentType.Text.Html
                    )
                }
            }
        }
        else if (preResponse != null) {
            preResponse(call, session)
        }
    })
}