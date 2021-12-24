package com.mth.onlinenote.ui

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mth.onlinenote.R
import com.mth.onlinenote.adapter.NoteRecyclerAdapter
import com.mth.onlinenote.model.Note
import com.mth.onlinenote.network.RetrofitClient
import com.mth.onlinenote.network.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Mya Than Htet on 24/12/2021
 * Copyright (c) 2021. All rights reserved.
 * Last modified 24/12/2021
 */

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    var isPaid: Int = 0
    lateinit var calendar: Calendar
    lateinit var simpleDateFormat: SimpleDateFormat
    private var noteList: MutableList<Note> = mutableListOf<Note>()
    private lateinit var noteRecyclerAdapter: NoteRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // change color of ActionBar title
        supportActionBar?.apply {
            val titleText = "Note"
            val titleTextColor = ForegroundColorSpan(Color.BLACK)
            val spannString = SpannableString(titleText)
            spannString.setSpan(
                titleTextColor, 0, titleText.length,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            title = spannString
        }


        // initialize recyclerview and adapter
        recyclerView = findViewById(R.id.recyclerview)
        val linearLayoutManager = LinearLayoutManager(
            this, RecyclerView.VERTICAL, false
        )
        recyclerView.layoutManager = linearLayoutManager
        noteList = mutableListOf()

        noteRecyclerAdapter = NoteRecyclerAdapter(applicationContext, noteList)
        recyclerView.adapter = noteRecyclerAdapter


        val fab: View = findViewById(R.id.add_note_fab)
        fab.setOnClickListener { view ->
            showDialog()
        }

        // fetch all data when activity start
        fetchAllNote()
    }

    // show dialog to Add Data
    private fun showDialog() {
        val dialog = Dialog(this)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.custom_dialog)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val nameEtd = dialog.findViewById<EditText>(R.id.name_edt)
        val amountEtd = dialog.findViewById<EditText>(R.id.amount_edt)
        val dateEtd = dialog.findViewById<EditText>(R.id.date_edt)
        val noteEtd = dialog.findViewById<EditText>(R.id.note_edt)
        val confirmBnt = dialog.findViewById<TextView>(R.id.confirm_btn)
        val radioGroup = dialog.findViewById<RadioGroup>(R.id.radioGroup)
        calendar = Calendar.getInstance()
        simpleDateFormat = SimpleDateFormat("dd/MM/yy")
        val dateTime: String = simpleDateFormat.format(calendar.time).toString()
        dateEtd.setText(dateTime)
        var id: Int = 1
        radioGroup.setOnCheckedChangeListener { _, i ->
            isPaid = when (i) {
                R.id.paid_rb -> 1
                R.id.not_paid_rb -> 0
                else -> 0
            }
        }

        confirmBnt.setOnClickListener {
            val notes = Note(
                id++, nameEtd.text.toString(),
                amountEtd.text.toString().toInt(), dateEtd.text.toString(),
                isPaid,
                noteEtd.text.toString()
            )

            addNote(notes)
            dialog.dismiss()

        }

        dialog.show()

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.actionbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_total_amount) {

            val retrofit = ServiceBuilder.buildService(RetrofitClient::class.java)
            retrofit.getTotalAmount()
                .enqueue(
                    object : Callback<String> {

                        override fun onFailure(call: Call<String>, t: Throwable) {
                            Log.i(MainActivity::class.simpleName, "on FAILURE!!!!$t")
                        }

                        override fun onResponse(call: Call<String>, response: Response<String>) {
                            val jsonresponse: String = response.body().toString()
                            showTotalAmountDialog(jsonresponse)
                        }
                    }
                )



            return true
        }
        return super.onOptionsItemSelected(item)
    }

    // show total amount from server
    private fun showTotalAmountDialog(totalAmount: String) {
        val dialog = Dialog(this)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.total_amount_dialog)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val totalAmountTv = dialog.findViewById<TextView>(R.id.total_amount_tv)
        totalAmountTv.text = "$totalAmount Ks"
        dialog.show()

    }

    // add data to server
    private fun addNote(
        note: Note
    ) {
        val retrofit = ServiceBuilder.buildService(RetrofitClient::class.java)
        retrofit.addNote(note.id, note.name, note.amount, note.date, note.isPaid, note.description)
            .enqueue(
                object : Callback<String> {

                    override fun onFailure(call: Call<String>, t: Throwable) {
                        Log.i(MainActivity::class.simpleName, "on FAILURE!!!!$t")
                    }

                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        val jsonresponse: String = response.body().toString()
                        Log.i("RETROFITRESULT", jsonresponse)
                        noteList.add(note)
                        noteList.reverse()
                        noteRecyclerAdapter.notifyDataSetChanged()
                    }
                }
            )

    }

    // fetch all data from server
    private fun fetchAllNote() {
        val retrofit2 = ServiceBuilder.buildService2(RetrofitClient::class.java)
        retrofit2.getAllNotes().enqueue(object : Callback<MutableList<Note>> {
            override fun onResponse(
                call: Call<MutableList<Note>>,
                response: Response<MutableList<Note>>
            ) {
                val usersResponse = response.body()
                Log.i("USERRESONSE", usersResponse.toString())
                noteList.clear()
                usersResponse?.let {
                    noteList.addAll(it)
                    noteList.reverse()
                }
                noteRecyclerAdapter.notifyDataSetChanged()

            }

            override fun onFailure(call: Call<MutableList<Note>>, t: Throwable) {
                Log.i("USERRESONSE", "fail : $t")

            }

        })
    }
}