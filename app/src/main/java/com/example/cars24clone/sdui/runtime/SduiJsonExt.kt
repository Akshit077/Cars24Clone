package com.example.cars24clone.sdui.runtime

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull

fun JsonObject.string(key: String, default: String = ""): String =
    (this[key] as? JsonPrimitive)?.contentOrNull ?: default

fun JsonObject.int(key: String, default: Int): Int {
    val primitive = this[key] as? JsonPrimitive ?: return default
    return primitive.intOrNull ?: primitive.contentOrNull?.toIntOrNull() ?: default
}

fun JsonObject.element(key: String): JsonElement? = this[key]

/** Number 24 and string "24" compare equal so chip values and lookup keys line up. */
fun jsonLooseEquals(a: JsonElement?, b: JsonElement?): Boolean {
    if (a == null || b == null) return false
    if (a == b) return true
    val left = a as? JsonPrimitive ?: return false
    val right = b as? JsonPrimitive ?: return false
    return left.content == right.content
}
