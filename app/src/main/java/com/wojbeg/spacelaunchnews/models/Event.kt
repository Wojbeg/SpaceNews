package com.wojbeg.spacelaunchnews.models

import java.io.Serializable

data class Event(
    val id: String,
    val provider: String
): Serializable