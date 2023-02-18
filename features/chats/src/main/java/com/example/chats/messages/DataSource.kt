package com.example.chats.messages

import com.example.model.Message

class DataSource {

    fun loadDummyMessage() : List<Message> {
        return listOf(
                Message("Hello","Hi"),
                Message("","Hi"),
                Message("","How are you"),
                Message("Hello","Hi"),
                Message("Hello",""),
                Message("",""),
        )
    }
}