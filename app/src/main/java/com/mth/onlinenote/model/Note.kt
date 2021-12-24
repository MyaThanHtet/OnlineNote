package com.mth.onlinenote.model

data class Note(
    var id: Int,
    var name: String,
    var amount: Int,
    var date: String,
    var isPaid: Int,
    var description: String
)
