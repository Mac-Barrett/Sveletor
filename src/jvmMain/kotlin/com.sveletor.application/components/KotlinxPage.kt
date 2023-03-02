package com.sveletor.application.components

import com.sveletor.application.classes.SveletorSession
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.html.*


/**
 * An authenticated Variant of kotlinxPage. Validates the session in the
 * preResponse of [kotlinxPage]. If valid, invokes [preResponse] and responds
 * with the html built by [headWidgets] & [bodyWidgets]
 *
 * @param path GET request binding path.
 * @param title Title for the page
 * @param preResponse code to run prior to responding with HTML.
 * @param headWidgets Lambda with HEAD receiver to define <head> contents.
 * @param bodyWidgets Lambda with BODY receiver defining <body> contents.
 */
fun Application.authenticatedKotlinxPage(
    path: String,
    title: String = "EasyButton",
    preResponse: (suspend (ApplicationCall, SveletorSession?) -> Unit)? = null,
    headWidgets: (HEAD.(call: ApplicationCall, session: SveletorSession) -> Unit)?,
    bodyWidgets: BODY.(call: ApplicationCall, session: SveletorSession) -> Unit
) {
    kotlinxPage(
        path = path,
        preResponse = { call: ApplicationCall, session: SveletorSession? ->
            if (session == null || !session.validate()) {
                return@kotlinxPage call.respondText(
                    this::class.java.classLoader.getResource("web${path}.html")!!.readText(),
                    ContentType.Text.Html
                )
            }
            preResponse?.invoke(call, session)
        },
        title = title,
        headWidgets = { call, session -> headWidgets?.let { it(call, session!!) } },
        bodyWidgets = { call, session -> bodyWidgets(call, session!!) }
    )
}


/**
 * An endpoint that returns an SSR page defined by the [headWidgets] & [bodyWidgets]
 *
 * @param path GET request binding path.
 * @param title Title for the page
 * @param preResponse code to run prior to responding with HTML.
 * @param headWidgets Lambda with HEAD receiver to define <head> contents.
 * @param bodyWidgets Lambda with BODY receiver defining <body> contents.
 */
fun Application.kotlinxPage(
    path: String,
    title: String = "EasyButton",
    preResponse: suspend (ApplicationCall, SveletorSession?) -> Unit,
    headWidgets: (HEAD.(call: ApplicationCall, session: SveletorSession?) -> Unit)? = null,
    bodyWidgets: BODY.(call: ApplicationCall, session: SveletorSession?) -> Unit
) {
    routing {
        get(path) {
            val session = call.sessions.get<SveletorSession>()
            preResponse(call, session)

            call.respondHtml{
                page(
                    title = title,
                    headWidgets = {
                        if (headWidgets != null) {
                            headWidgets(call, session)
                        }
                    },
                    bodyWidgets = {
                        bodyWidgets(call, session)
                    }
                )
            }
        }
    }
}


/**
 * A simple page template for creating an SSR page. Useful for
 * sending a simple form that doesn't require a svelte page.
 */
fun HTML.page(
    title: String = "EasyButton",
    headWidgets: HEAD.() -> Unit,
    bodyWidgets: BODY.() -> Unit
) {
    head {
        title(title)
        headWidgets()
    }
    body {
        bodyWidgets()
    }
}