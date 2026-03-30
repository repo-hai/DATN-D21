package com.example.datn_mobile.data.network.adapter

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter

/**
 * A JsonAdapter that can process Kotlin's Unit type.
 * This is useful for APIs that return a body with no meaningful data (e.g., on successful logout or delete).
 */
class UnitJsonAdapter : JsonAdapter<Unit>() {
    override fun fromJson(reader: JsonReader) {
        // Skips the value, effectively consuming it without parsing into an object.
        reader.skipValue()
    }

    override fun toJson(writer: JsonWriter, value: Unit?) {
        // Writes an empty JSON object {} to represent Unit.
        writer.beginObject().endObject()
    }
}