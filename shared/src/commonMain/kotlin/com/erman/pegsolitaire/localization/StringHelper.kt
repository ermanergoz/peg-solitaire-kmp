package com.erman.pegsolitaire.localization

import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString

object StringHelper {
    fun get(resource: StringResource): String =
        runBlocking { getString(resource) }

    fun get(resource: StringResource, arg: Int): String =
        runBlocking { getString(resource, arg) }
}
