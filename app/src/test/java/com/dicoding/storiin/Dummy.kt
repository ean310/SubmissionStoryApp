package com.dicoding.storiin

import com.dicoding.storiin.response.Story

object Dummy {

    fun getDummy(): List<Story> {
        val items: MutableList<Story> = arrayListOf()
        for (i in 0..100) {
            val quote = Story(
                i.toString(),
                "ean",
                "from where to",
                "",
                "2024-05-07T22:10:15.213Z",
                -7.833376383439454,
                110.3831320535263
            )
            items.add(quote)
        }
        return items
    }
}