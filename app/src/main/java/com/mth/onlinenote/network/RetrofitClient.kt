package com.mth.onlinenote.network

import com.mth.onlinenote.model.Note
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST


interface RetrofitClient {
    @FormUrlEncoded
    @POST("/api/addnote.php")
    fun addNote(
        @Field("id") id: Int,
        @Field("name") name: String,
        @Field("amount") amount: Int,
        @Field("date") date: String,
        @Field("isPaid") isPaid: Int,
        @Field("description") description: String
    ): Call<String>

    @FormUrlEncoded
    @POST("/api/updatenote.php")
    fun updateNote(
        @Field("id") id: Int,
        @Field("name") name: String,
        @Field("amount") amount: Int,
        @Field("date") date: String,
        @Field("isPaid") isPaid: Int,
        @Field("description") description: String
    ): Call<String>

    @FormUrlEncoded
    @POST("/api/deletenote.php")
    fun deleteNote(
        @Field("id") id: Int
    ): Call<String>

    @GET("/api/note.php")
    fun getAllNotes(
    ): Call<MutableList<Note>>

    @GET("/api/totalamount.php")
    fun getTotalAmount(): Call<String>
}



