package com.nil_projects.society_app

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.core.view.size
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.tapadoo.alerter.Alerter
import kotlinx.android.synthetic.main.activity_add__payment__update.*
import kotlinx.android.synthetic.main.activity_add__worker.*
import kotlinx.android.synthetic.main.custom_records_layout.*
import kotlinx.android.synthetic.main.fragment_report.*
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


@Suppress("DEPRECATION")
class Add_Payment_Update : AppCompatActivity() {

    lateinit var spinner_add_payment_wing: Spinner
    lateinit var spinner_add_payment_flat: Spinner
    lateinit var name_user: TextView
    lateinit var datePickerdialog: DatePickerDialog
    var formate = SimpleDateFormat("MMM,yyyy", Locale.US)
    lateinit var select_month: TextInputEditText
    var spin_value_wing: String? = null
    var spin_value_flat: String? = null
    lateinit var dbRef: DocumentReference
    lateinit var btn_addtag : Button
    lateinit var nameRef: CollectionReference
    lateinit var btn_update : Button
    lateinit var receiptNo : TextInputEditText
    lateinit var arrOfChips : ArrayList<String>
    var LoggedIn_User_Email: String? = null
    lateinit var listMobileNo : ArrayList<String>
    lateinit var listUserIds : ArrayList<String>
    lateinit var progressDialog: ProgressDialog
    var currentUserId : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add__payment__update)
        supportActionBar!!.title = "Add Payment Update"

        arrOfChips = ArrayList<String>()


        spinner_add_payment_wing = findViewById<Spinner>(R.id.spinner_add_payment_wing)
        btn_addtag = findViewById<View>(R.id.btn_addtag) as Button
        spinner_add_payment_flat = findViewById<Spinner>(R.id.spinner_add_payment_flat)
        name_user = findViewById<TextView>(R.id.name_user_add_payment)
        select_month = findViewById<TextInputEditText>(R.id.Select_Month_add_payment_details)
        btn_update = findViewById<Button>(R.id.btn_update_payment)
        receiptNo = findViewById<TextInputEditText>(R.id.edittext_receiptNo)
        LoggedIn_User_Email = FirebaseAuth.getInstance().currentUser!!.getEmail()
        listMobileNo = ArrayList<String>()
        listUserIds = ArrayList<String>()

        fetchuserMobilefromFirebase()


        select_month.setOnClickListener {
            datePicker()
        }

        btn_addtag.setOnClickListener {
            var tags = select_month.text.toString().split(" ")
            if(select_month.text!!.isEmpty())
            {
                select_month.error = "Please Select Month"
            }
            else{
                var inflator = LayoutInflater.from(this)
                var text : String
                for (text in tags)
                {
                    var chip : Chip = inflator.inflate(R.layout.chips,null,false) as Chip
                    chip.chipText = text
                    chip.setOnCloseIconClickListener {
                        chip_grp.removeView(it)
                    }
                    if(arrOfChips.size < 12)
                    {
                        chip_grp.addView(chip)
                        arrOfChips.add(chip.chipText.toString())
                    }
                    else{
                        select_month.error = "Only 12 Maximam Months can Selected at a Time"
                        Toast.makeText(this,"Only 12 Maximam Months can Selected at a Time",Toast.LENGTH_LONG).show()
                    }
                    select_month.setText("")
                }
        }
    }

        val optionsWIng = arrayOf("Select Wing","Madhumalti Building", "Row House","Aboli Building","Nishigandha Building","Sayali Building","Sonchafa Building")

        spinner_add_payment_wing.adapter = ArrayAdapter<String>(this, R.layout.spinner_textview, optionsWIng)
        spinner_add_payment_wing.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(applicationContext, "Please Select Wing", Toast.LENGTH_LONG).show()
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (optionsWIng.get(position) != "Select Wing") {
                    Toast.makeText(applicationContext, "Selected Wing is : " + optionsWIng.get(position), Toast.LENGTH_LONG).show()
                    spin_value_wing = optionsWIng.get(position)

                    when(optionsWIng.get(position)) {
                        "Select Building" -> {

                        }
                        "Madhumalti Building" -> {

                            val optionsFlat = listOf<String>(
                                    "Select Flat",
                                    "101",
                                    "102",
                                    "103",
                                    "104",
                                    "107",
                                    "108",
                                    "203",
                                    "204",
                                    "205",
                                    "206",
                                    "207",
                                    "208",
                                    "303",
                                    "304",
                                    "306",
                                    "307",
                                    "308",
                                    "401",
                                    "402",
                                    "403",
                                    "404",
                                    "406",
                                    "407",
                                    "408",
                                    "501",
                                    "502",
                                    "503",
                                    "504",
                                    "505",
                                    "506",
                                    "601",
                                    "602",
                                    "603",
                                    "606",
                                    "607",
                                    "608",
                                    "105-06",
                                    "201-02",
                                    "301-02",
                                    "305-405",
                                    "507-08",
                                    "604-05"
                            )
                            flatPassData(optionsFlat)
                        }
                        "Row House" -> {
                            val optionsFlat = listOf<String>("Select Row House", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
                                    "11", "12")

                            flatPassData(optionsFlat)
                        }
                        "Aboli Building" -> {

                            val optionsFlat = listOf<String>("Select Flat", "101", "102", "103", "104",
                                    "201", "202", "203", "204", "301", "302",
                                    "303", "304", "401", "402", "403", "404", "501", "502", "503", "504", "601",
                                    "602", "603", "604", "701", "702", "703", "704")

                            flatPassData(optionsFlat)
                        }
                        "Nishigandha Building" -> {

                            val optionsFlat = listOf<String>("Select Flat", "101", "102", "103", "104",
                                    "201", "202", "203/04", "301", "302", "303", "304", "401", "402", "403", "404",
                                    "501", "502", "503", "504", "601", "602", "603", "604")

                            flatPassData(optionsFlat)
                        }
                        "Sayali Building" -> {

                            val optionsFlat = listOf<String>("Select Flat", "101", "102", "103", "104", "105", "106", "107", "108",
                                    "201", "202", "203", "204", "205", "206", "207", "208", "303", "304", "305", "306", "307", "308", "401", "402", "403", "404",
                                    "405", "406", "407", "408",
                                    "501", "502", "503", "504", "507", "508", "601", "602", "607", "608",
                                    "301/302", "505/508", "603/604", "605/606")

                            flatPassData(optionsFlat)
                        }
                        "Sonchafa Building" -> {

                            val optionsFlat = listOf<String>("Select Flat", "101", "102", "103", "104",
                                    "201", "202", "203", "204", "303", "304", "301", "302", "401", "402", "403", "404",
                                    "501", "502", "503", "504", "601", "602", "603", "604")

                            flatPassData(optionsFlat)
                        }
                    }
                    }
            }
        }

    }

    fun flatPassData(optionsFlat : List<String>)
    {
        spinner_add_payment_flat.adapter = ArrayAdapter<String>(this, R.layout.spinner_textview, optionsFlat)
        spinner_add_payment_flat.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(applicationContext, "Please Select Flat Number", Toast.LENGTH_LONG).show()
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                if (optionsFlat.get(position) != "Select Flat Number") {
                    Toast.makeText(applicationContext, "Selected Flat Number is " + optionsFlat.get(position), Toast.LENGTH_LONG).show()
                    spin_value_flat = optionsFlat.get(position)
                    fetchUserbyFlat(spin_value_flat.toString(),spin_value_wing.toString())

                    btn_update.setOnClickListener {
                        progressDialog = ProgressDialog(this@Add_Payment_Update)
                        progressDialog.setMessage("Wait a Sec....Updating Payment Details")
                        progressDialog.setCancelable(false)
                        progressDialog.show()
                        for(i in 0..arrOfChips.size-1)
                        updateOnFirebase(arrOfChips,spin_value_flat.toString(),spin_value_wing.toString())
                    }
                }
            }
        }
    }


    private fun updateOnFirebase(arr : ArrayList<String>,flat : String,wing : String)
    {
        var db = FirebaseFirestore.getInstance()
        db.collection("FlatUsers")
                .whereEqualTo("FlatNo", flat).whereEqualTo("Wing", wing)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val city = documentSnapshot.toObjects(UserClass :: class.java)
                    for (document in city) {

                        val items = HashMap<String, Any>()
                        items.put("ReceiptNumber", receiptNo.text.toString())
                        for(i in 0..arr.size-1)
                        {
                            items.put("MonthsPaid${i}", arr[i])
                        }
                        db.collection("FlatUsers").document(document.UserID)
                                .collection("PaidMonths").document(receiptNo.text.toString())
                                .set(items).addOnSuccessListener {
                                    showAlert()
                                    progressDialog.dismiss()
                                    chip_grp.removeAllViews()
                                    sendFCMtoUsers()

                                }.addOnFailureListener {
                                    exception: java.lang.Exception -> Toast.makeText(this, exception.toString(), Toast.LENGTH_LONG).show()
                                }
                    }
                }
                .addOnFailureListener { exception ->
                    Alerter.create(this@Add_Payment_Update)
                            .setTitle("Payment Update")
                            .setIcon(R.drawable.alert)
                            .setDuration(4000)
                            .setText("Failed to Update!! Please Try after some time!!")
                            .setBackgroundColorRes(R.color.colorAccent)
                            .show()
                }
    }

    private fun showAlert() {
        Alerter.create(this@Add_Payment_Update)
                .setTitle("Payment Update")
                .setDuration(4000)
                .setIcon(R.drawable.money)
                .setText("Payment Details Updated Successfully!! :)")
                .setBackgroundColorRes(R.color.colorAccent)
                .show()
    }

    private fun sendFCMtoUsers() {

        AsyncTask.execute {
            val SDK_INT = android.os.Build.VERSION.SDK_INT
            if (SDK_INT > 8) {
                val policy = StrictMode.ThreadPolicy.Builder()
                        .permitAll().build()
                StrictMode.setThreadPolicy(policy)
                var sendNotificationID: String

                //This is a Simple Logic to Send Notification different Device Programmatically....
                if (LoggedIn_User_Email.equals("admin@gmail.com") && listMobileNo.isNotEmpty()) {
                    //send_email = "client@gmail.com"

                    for (i in 0..listMobileNo.size-1)
                    {
                        if(listUserIds.get(i) == currentUserId)
                        {
                            sendNotificationID = listMobileNo.get(i)
                            Log.d("OneSignal App",sendNotificationID)

                            try {
                                val jsonResponse: String

                                val url = URL("https://onesignal.com/api/v1/notifications")
                                val con = url.openConnection() as HttpURLConnection
                                con.setUseCaches(false)
                                con.setDoOutput(true)
                                con.setDoInput(true)

                                con.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                                con.setRequestProperty("Authorization", "Basic Y2Q3ODRhYTUtMjA4ZC00NTZjLTg3MDktMzEwNjJkOWMwMTRi")
                                con.setRequestMethod("POST")

                                val strJsonBody = ("{"
                                        + "\"app_id\": \"69734071-08a8-4d63-a7ab-adda8e2197f0\","

                                        + "\"filters\": [{\"field\": \"tag\", \"key\": \"NotificationID\", \"relation\": \"=\", \"value\": \"" + sendNotificationID + "\"}],"

                                        + "\"data\": {\"foo\": \"bar\"},"
                                        + "\"contents\": {\"en\": \"Your Maintainance Payment Details has been Updated\"}"
                                        + "}")


                                println("strJsonBody:\n$strJsonBody")

                                val sendBytes = strJsonBody.toByteArray(charset("UTF-8"))
                                con.setFixedLengthStreamingMode(sendBytes.size)

                                val outputStream = con.getOutputStream()
                                outputStream.write(sendBytes)

                                val httpResponse = con.getResponseCode()
                                println("httpResponse: $httpResponse")

                                if (httpResponse >= HttpURLConnection.HTTP_OK && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                                    val scanner = Scanner(con.getInputStream(), "UTF-8")
                                    jsonResponse = if (scanner.useDelimiter("\\A").hasNext()) scanner.next() else ""
                                    scanner.close()
                                } else {
                                    val scanner = Scanner(con.getErrorStream(), "UTF-8")
                                    jsonResponse = if (scanner.useDelimiter("\\A").hasNext()) scanner.next() else ""
                                    scanner.close()
                                }
                                println("jsonResponse:\n$jsonResponse")

                            } catch (t: Throwable) {
                                t.printStackTrace()
                            }
                        }
                    }

                } else {
                    sendNotificationID = "admin@gmail.com"
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

    private fun fetchuserMobilefromFirebase()
    {
        var db = FirebaseFirestore.getInstance()
        db.collection("FlatUsers")
                .get()
                .addOnSuccessListener { documentSnapshot ->

                    val city = documentSnapshot.toObjects(UserClass::class.java)
                    for (document in city) {
                        if (document != null) {
                            listMobileNo.add(document.MobileNumber)
                            listUserIds.add(document.UserID)
                        }
                    }
                }

                .addOnFailureListener { exception ->
                    Log.w("SocietyFirestore", "Error getting documents.", exception)
                }
    }

    private fun fetchUserbyFlat(flat : String,wing : String)
    {
        var db = FirebaseFirestore.getInstance()
        db.collection("FlatUsers")
                .whereEqualTo("FlatNo", flat).whereEqualTo("Wing", wing)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val city = documentSnapshot.toObjects(UserClass :: class.java)
                    for (document in city) {
                            name_user.text = document.UserName
                            currentUserId = document.UserID
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("SocietyFirestore", "Error getting documents.", exception)
                }
        }
}