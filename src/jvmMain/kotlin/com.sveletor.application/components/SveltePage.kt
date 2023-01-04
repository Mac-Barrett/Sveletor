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
 * @param endpoint The route this page will sit on
 * @param filePath The path to the svelte page in the resources directory
 * @param preResponse Suspending function that will run before the server
 * responds w/ the html file. Can be used for additional session vetting.
 *
 * @author Mac Barrett
 */
fun Application.sveltePage(
    endpoint: String,
    filePath: String,
    preResponse: (suspend PipelineContext<Unit, ApplicationCall>.(ApplicationCall) -> Unit)? = null
) {
    routing {
        get(endpoint) {
            if (preResponse != null) preResponse(call)

            call.respondText(
                this::class.java.classLoader.getResource("web/$filePath")!!.readText(),
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
 * @param htmlFileName The file name of the svelte page to respond with
 * @param preResponse Suspending function that will run before the server
 * responds w/ the html file. Can be used for additional session vetting.
 */
fun Application.authenticatedSveltePage(
    endpoint: String,
    htmlFileName: String,
    preResponse: (suspend PipelineContext<Unit, ApplicationCall>.(ApplicationCall) -> Unit)? = null
) {
    sveltePage(endpoint, htmlFileName, preResponse = { call: ApplicationCall ->
        val session = call.sessions.get<SveletorSession>()
        if (session == null || !SveletorSession.validate(session)) {
            loginPage(call)
        }
        else if (preResponse != null) {
            preResponse(call)
        }
    })
}


/**
 * Responds to user with the login.html page. In your svelte app, after logging in,
 * the user will be redirected automatically to the original route they requested.
 */
suspend fun PipelineContext<Unit, ApplicationCall>.loginPage(call: ApplicationCall) {
    call.response.cookies["username"]
    call.respondText(
        this::class.java.classLoader.getResource("web/login.html")!!.readText(),
        ContentType.Text.Html
    )
}