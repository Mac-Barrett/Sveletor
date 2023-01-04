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
 * to these api should be GET
 *
 * @param endpoint The route this page will sit on. This should match the
 * path to the svelte page in the resources directory e.g.:
 *
 * - /foo will serve the file at resources/web/foo.html
 * - /foo/bar will serve the file at resources/web/foo/bar.html
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
 * @param endpoint The route this page will sit on
 * @param preResponse Suspending function that will run before the server
 * responds w/ the html file. Can be used for additional session vetting.
 */
fun Application.authenticatedSveltePage(
    endpoint: String,
    preResponse: (suspend PipelineContext<Unit, ApplicationCall>.(ApplicationCall) -> Unit)? = null
) {
    sveltePage(endpoint, preResponse = { call: ApplicationCall ->
        val session = call.sessions.get<SveletorSession>()
        if (session == null || !SveletorSession.validate(session)) {
            call.respondRedirect("/login")
        }
        else if (preResponse != null) {
            preResponse(call)
        }
    })
}