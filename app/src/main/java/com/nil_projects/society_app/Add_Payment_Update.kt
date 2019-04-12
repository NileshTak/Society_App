package com.nil_projects.society_app

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_add__payment__update.*
import kotlinx.android.synthetic.main.activity_add__worker.*
import kotlinx.android.synthetic.main.fragment_report.*
import java.text.SimpleDateFormat
import java.util.*


class Add_Payment_Update : AppCompatActivity() {

    lateinit var spinner_add_payment_wing: Spinner
    lateinit var spinner_add_payment_flat: Spinner
    lateinit var name_user: TextView
    lateinit var datePickerdialog: DatePickerDialog
    var formate = SimpleDateFormat("MMM, yyyy", Locale.US)
    lateinit var select_month: EditText
    var spin_value_wing: String? = null
    var spin_value_flat: String? = null
    lateinit var dbRef: DocumentReference
    lateinit var nameRef: CollectionReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add__payment__update)


        spinner_add_payment_wing = findViewById<Spinner>(R.id.spinner_add_payment_wing)
        spinner_add_payment_flat = findViewById<Spinner>(R.id.spinner_add_payment_flat)
        name_user = findViewById<TextView>(R.id.name_user_add_payment)
        select_month = findViewById<EditText>(R.id.Select_Month_add_payment_details)


        select_month.setOnClickListener {
            datePicker()
        }

        val optionsWIng = arrayOf("Select Wing", "A", "B", "C", "D")

        spinner_add_payment_wing.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, optionsWIng)
        spinner_add_payment_wing.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(applicationContext, "Please Select Worker Type", Toast.LENGTH_LONG).show()
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (optionsWIng.get(position) != "Select Wing") {
                    Toast.makeText(applicationContext, "Selected Worker Type is : " + optionsWIng.get(position), Toast.LENGTH_LONG).show()
                    spin_value_wing = optionsWIng.get(position)
                }
            }
        }

        val optionsFlat = arrayOf("Select Flat Number", "101", "102", "103", "104")

        spinner_add_payment_flat.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, optionsFlat)
        spinner_add_payment_flat.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(applicationContext, "Please Select Worker Type", Toast.LENGTH_LONG).show()
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                if (optionsFlat.get(position) != "Select Flat Number") {
                    Toast.makeText(applicationContext, "Selected Worker Type is : " + optionsFlat.get(position), Toast.LENGTH_LONG).show()
                    spin_value_flat = optionsFlat.get(position)
                    fetchUserbyFlat(spin_value_flat.toString())
                }
            }
        }
    }

    private fun datePicker() {
        val now = Calendar.getInstance()
        datePickerdialog = DatePickerDialog(this@Add_Payment_Update, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            val selectedMonth = Calendar.getInstance()
            selectedMonth.set(Calendar.YEAR, year)
            selectedMonth.set(Calendar.MONTH, month)
            selectedMonth.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val date = formate.format(selectedMonth.time)
            Toast.makeText(this@Add_Payment_Update, "date : " + date, Toast.LENGTH_SHORT).show()
            select_month.setText(date)
        },
                now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))
        datePickerdialog.show()
    }


//
//    private fun fetchUserbyFlat() {
//
//        val ref = FirebaseDatabase.getInstance().getReference("/Users")
//        ref.addListenerForSingleValueEvent(object : ValueEventListener{
//            override fun onCancelled(p0: DatabaseError) {
//
//            }
//            override fun onDataChange(p0: DataSnapshot) {
//
//                p0.children.forEach {
//                    val user = it.getValue(UserClass :: class.java)
//
//                    if(user!!.wing == spin_value_wing && user.flatno == spin_value_flat)
//                    {
//                        Toast.makeText(this@Add_Payment_Update,user.name,Toast.LENGTH_LONG).show()
//                        name_user.text = user.name
//                    }
//                    else{
//                        name_user.text = ""
//                    }
//                }
//            }
//        })
//    }


    private fun fetchUserbyFlat(flat : String)
    {
        var db = FirebaseFirestore.getInstance()
        db.collection("FlatUsers")
                .whereEqualTo("FlatNo", flat)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val city = documentSnapshot.toObjects(UserClass :: class.java)
                    for (document in city) {
                            name_user.text = document.UserName
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("SocietyFirestore", "Error getting documents.", exception)
                }
        }
}

//    private fun fetchUserbyFlat()
//    {
//        if(!spin_value_wing!!.isEmpty() && !spin_value_flat!!.isEmpty())
//        {
//            simpleQuery(spin_value_wing!!,spin_value_flat!!)
//        }
//    }
//
//
//    private fun simpleQuery(wing: String, flat : String) {
//        var dbRef = FirebaseFirestore.getInstance()
//        val capitalCities = dbRef.collection("FlatUsers").whereEqualTo("Wing", wing)
//                .whereEqualTo("FlatNo", flat)
//                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
//                    name_user.text = querySnapshot!!.size().toString()
//        }
//}

class UserClass(val id : String,val profile_Pic_url : String,val UserName : String,val email : String,val pass : String,
                val city: String,val societyname : String,val wing : String,
                val FlatNo : String,val relation : String,val userAuth : String)
{
    constructor() : this("","","","","","","","","","","")
}
