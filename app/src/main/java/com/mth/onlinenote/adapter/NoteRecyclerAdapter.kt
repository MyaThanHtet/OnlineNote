package com.mth.onlinenote.adapter

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.mth.onlinenote.R
import com.mth.onlinenote.model.Note
import com.mth.onlinenote.network.RetrofitClient
import com.mth.onlinenote.network.ServiceBuilder
import com.mth.onlinenote.ui.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Mya Than Htet on 24/12/2021
 * Copyright (c) 2021. All rights reserved.
 * Last modified 24/12/2021
 */

class NoteRecyclerAdapter(val c: Context, val note: MutableList<Note>) :
    RecyclerView.Adapter<NoteRecyclerAdapter.ViewHolder>() {

    var isPaid: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            : NoteRecyclerAdapter.ViewHolder {
        val v: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.note_view_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: NoteRecyclerAdapter.ViewHolder, position: Int) {

        holder.nameTv.text = note[position].name
        holder.dateTv.text = note[position].date
        holder.amountTv.text = "${note[position].amount} Ks"
        holder.noteTv.text = note[position].description

        if (note[position].isPaid == 1) {
            holder.isPaidTv.text = "Paid"
            holder.isPaidIv.setImageResource(R.drawable.check_mark)
        } else {
            holder.isPaidTv.text = "Not Paid"
            holder.isPaidIv.setImageResource(R.drawable.exc_mark)
        }

        // to edit and delete data
        holder.showPopupMenuIv.setOnClickListener {
            it
            val popupMenu: PopupMenu = PopupMenu(c, holder.showPopupMenuIv)
            popupMenu.menuInflater.inflate(R.menu.menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.editText -> {

                        val dialog = Dialog(it.context)
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

                        nameEtd.setText(note[position].name)
                        amountEtd.setText(note[position].amount.toString())
                        dateEtd.setText(note[position].date)
                        noteEtd.setText(note[position].description)
                        isPaid = note[position].isPaid

                        val paidRb = dialog.findViewById<RadioButton>(R.id.paid_rb)
                        val notPaidRb = dialog.findViewById<RadioButton>(R.id.not_paid_rb)
                        if (isPaid == 1) {
                            paidRb.isChecked = true
                        } else {
                            notPaidRb.isChecked = true
                        }
                        radioGroup.setOnCheckedChangeListener { _, i ->
                            isPaid = when (i) {
                                R.id.paid_rb -> 1
                                R.id.not_paid_rb -> 0
                                else -> 0
                            }
                        }


                        confirmBnt.setOnClickListener {
                            val notes = Note(
                                note[position].id,
                                nameEtd.text.toString(),
                                amountEtd.text.toString().toInt(),
                                dateEtd.text.toString(),
                                isPaid,
                                noteEtd.text.toString()
                            )

                            val retrofit = ServiceBuilder.buildService(RetrofitClient::class.java)
                            retrofit.updateNote(
                                notes.id,
                                notes.name,
                                notes.amount,
                                notes.date,
                                notes.isPaid,
                                notes.description
                            )
                                .enqueue(
                                    object : Callback<String> {

                                        override fun onFailure(call: Call<String>, t: Throwable) {
                                            Log.i(
                                                MainActivity::class.simpleName,
                                                "on FAILURE!!!!$t"
                                            )
                                        }

                                        override fun onResponse(
                                            call: Call<String>,
                                            response: Response<String>
                                        ) {
                                            val jsonresponse: String = response.body().toString()
                                            Log.i("RETROFITRESULT", jsonresponse)
                                            note[holder.adapterPosition] = notes
                                            Toast.makeText(c, "Updated!", Toast.LENGTH_SHORT).show()
                                            notifyDataSetChanged()
                                        }
                                    }
                                )


                            dialog.dismiss()

                        }

                        dialog.show()

                        true
                    }
                    R.id.deleteText -> {
                        val retrofit = ServiceBuilder.buildService(RetrofitClient::class.java)
                        retrofit.deleteNote(note[holder.adapterPosition].id)
                            .enqueue(
                                object : Callback<String> {

                                    override fun onFailure(call: Call<String>, t: Throwable) {
                                        Log.i(MainActivity::class.simpleName, "on FAILURE!!!!$t")
                                    }

                                    override fun onResponse(
                                        call: Call<String>,
                                        response: Response<String>
                                    ) {
                                        val jsonresponse: String = response.body().toString()
                                        Log.i("RETROFITRESULT", jsonresponse)
                                        note.removeAt(holder.adapterPosition)
                                        Toast.makeText(c, "Deleted!", Toast.LENGTH_SHORT).show()
                                        notifyDataSetChanged()
                                    }
                                }
                            )


                        true
                    }
                    else -> true
                }
            })
            popupMenu.show()
        }

    }

    override fun getItemCount(): Int {
        return note.size
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val nameTv = itemView.findViewById<TextView>(R.id.name)
        val dateTv = itemView.findViewById<TextView>(R.id.date)
        val amountTv = itemView.findViewById<TextView>(R.id.amount_tv)
        val noteTv = itemView.findViewById<TextView>(R.id.noteTv)
        val isPaidTv = itemView.findViewById<TextView>(R.id.isPaid_tv)
        val isPaidIv = itemView.findViewById<ImageView>(R.id.isPaid_iv)
        val showPopupMenuIv = itemView.findViewById<ImageView>(R.id.show_popup_iv)

    }

}