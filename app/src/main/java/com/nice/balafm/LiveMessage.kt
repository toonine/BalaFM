package com.nice.balafm


enum class Type {
    SENT, RECEIVED
}

data class LiveMessage(val type: Type, val content: String, val heedPic : String, val nickName: String)