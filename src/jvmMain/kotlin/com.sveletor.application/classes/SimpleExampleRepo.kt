package com.sveletor.application.classes

import kotlinx.serialization.Serializable

object ExampleDataSet {
    private val data: MutableMap<Int, Data> = mutableMapOf(
        1 to Data(1, "Mac", 24, 52000)
    )
    fun getAll(): Map<Int, Data> {
        return data
    }

    fun get(id: Int): Data? {
        return data[id]
    }

    fun post(item: Data) {
        data[item.id] = item
    }

    fun put(item: Data): Boolean {
        if (data[item.id] == null) {
            data[item.id] = item
            return true
        }
        return false
    }

    fun delete(id: Int) {
        data.remove(id)
    }
}

@Serializable
data class Data(
    val id: Int,
    val name: String,
    val age: Int,
    val salary: Int
)