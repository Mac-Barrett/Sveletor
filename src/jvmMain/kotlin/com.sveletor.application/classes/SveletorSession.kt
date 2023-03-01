package com.sveletor.application.classes

import kotlinx.serialization.Serializable

@Serializable
class SveletorSession(val username: String) {
    // TODO Customize your session variables :)
    fun validate(): Boolean {
        return true
    }
}