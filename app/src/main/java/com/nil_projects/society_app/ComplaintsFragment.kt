package com.nil_projects.society_app

import android.app.ProgressDialog
import android.content.Intent
import android.opengl.Visibility
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.StrictMode
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_user_profiles_list.*
import kotlinx.android.synthetic.main.custom_complaint.*
import kotlinx.android.synthetic.main.custom_complaint.view.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class ComplaintsFragment : Fragment() {

    lateinit var recycler_complaint : RecyclerView
    val adapter = GroupAdapter<ViewHolder>()
    var db = FirebaseFirestore.getInstance()
    lateinit var progressDialog: ProgressDialog
    var LoggedIn_User_Email: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view= inflater.inflate(R.layout.fragment_complaints, container, false)

        recycler_complaint = view.findViewById<RecyclerView>(R.id.complaints_recycler)
        LoggedIn_User_Email = FirebaseAuth.getInstance().currentUser!!.getEmail()

        fetchData()

        return view
    }

    private fun fetchData() {
        progressDialog = ProgressDialog(activity)
        progressDialog.setMessage("Wait a Sec.......")
        progressDialog.setCancelable(false)
        progressDialog.show()
            db.collection("FlatUsers")
                    .get()
                    .addOnSuccessListener {
                        val city = it.toObjects(UserClass::class.java)
                        for (document in city) {
                            if (document != null) {

                                fetchComplaints(document.UserID)
                            }
                        }

                        recycler_complaint.adapter = adapter
                        progressDialog.dismiss()

                    }.addOnFailureListener {
                        Toast.makeText(context,it.toString(), Toast.LENGTH_LONG).show()
                    }
        }

    private fun fetchComplaints(id : String) {
        db.collection("FlatUsers").document(id)
                .collection("Complaints")
                .get()
                .addOnSuccessListener {

                    it.documents.forEach {
                        val city1 = it.toObject(ComplaintClass::class.java)
                        if(city1 != null)
                        {
                            Log.d("userid",city1.CompheadLine+"kn")
                            adapter.add(customtComplaintclass(city1))
                        }
                    }
                }.addOnFailureListener {
                    Toast.makeText(context,it.toString(), Toast.LENGTH_LONG).show()
                }
    }

    inner class customtComplaintclass(var FinalComplaintList : ComplaintClass) : Item<ViewHolder>()
    {
        override fun getLayout(): Int {
            return R.layout.custom_complaint
        }

        override fun bind(viewHolder: ViewHolder, position: Int) {
          //  viewHolder.itemView.headline_complaint.text = FinalComplaintList.CompheadLine
            viewHolder.itemView.headline_complaint_1.text = FinalComplaintList.CompheadLine
           // viewHolder.itemView.Details_complaint.text = FinalComplaintList.CompDetails
            viewHolder.itemView.process_complaint.text = FinalComplaintList.CompProcess
            viewHolder.itemView.date_complaint.text = FinalComplaintList.CompUpdatedDate
            viewHolder.itemView.flatnum_complaint.text = FinalComplaintList.CompFlatNum
            viewHolder.itemView.wing_complaint.text = FinalComplaintList.CompWingName
            Glide.with(context).load(FinalComplaintList.ComplaintImg).into(viewHolder.itemView.Img_complaint)



            if(FinalComplaintList.CompProcess == "Solved")
            {

                viewHolder.itemView.img_display_success.setAnimation("tick.json")
                viewHolder.itemView.img_display_success.playAnimation()
                viewHolder.itemView.img_display_success.loop(true)


                viewHolder.itemView.btn_solved.visibility = View.INVISIBLE
                viewHolder.itemView.img_display_success.visibility = View.VISIBLE
      //          Glide.with(context).asGif().load(R.drawable.processing).into(viewHolder.itemView.problem_logo)
        //        viewHolder.itemView.problem_logo.setBackgroundResource(R.drawable.processing)
            }
            else{
               // viewHolder.itemView.problem_logo.setBackgroundResource(R.drawable.checkmark)
              //  Glide.with(context).asGif().load(R.drawable.checkmark).into(viewHolder.itemView.problem_logo)




                viewHolder.itemView.btn_solved.visibility = View.VISIBLE
                viewHolder.itemView.img_display_success.visibility = View.INVISIBLE

            }

//            viewHolder.itemView.setOnClickListener {
//                viewHolder.itemView.folding_cell_complaint.toggle(false)
//            }

            viewHolder.itemView.btn_solved.setOnClickListener {

                var db = FirebaseFirestore.getInstance()

                Log.d("id",FinalComplaintList.CompUserID)
                db.document("FlatUsers/" +FinalComplaintList.CompUserID+"/" +"Complaints/" +FinalComplaintList.CompheadLine)
                        .update("CompProcess", "Solved")

                viewHolder.itemView.btn_solved.visibility = View.INVISIBLE
                viewHolder.itemView.img_display_success.visibility = View.VISIBLE
             //   viewHolder.itemView.problem_logo.setBackgroundResource(R.drawable.checkmark)
                viewHolder.itemView.process_complaint.text = "Solved"

                sendFCMtoUsers(FinalComplaintList.CompUserMobileNo)
            }
        }
    }


    private fun sendFCMtoUsers(UserMobile : String) {

        AsyncTask.execute {
            val SDK_INT = android.os.Build.VERSION.SDK_INT
            if (SDK_INT > 8) {
                val policy = StrictMode.ThreadPolicy.Builder()
                        .permitAll().build()
                StrictMode.setThreadPolicy(policy)
                var sendNotificationID: String

                //This is a Simple Logic to Send Notification different Device Programmatically....
                if (LoggedIn_User_Email.equals("admin@gmail.com")) {
                    //send_email = "client@gmail.com"
                    Log.d("OneSignal",UserMobile)
                    sendNotificationID = UserMobile
                    Log.d("OneSignal App", sendNotificationID)

                    try {
                        val jsonResponse: String

                        val url = URL("https://onesignal.com/api/v1/notifications")
                        val con = url.openConnection() as HttpURLConnection
                        con.setUseCaches(false)
                        con.setDoOutput(true)
                        con.setDoInput(true)

                        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                        con.setRequestProperty("Authorization", "Basic NzY1N2E5MGEtM2JjZi00MWU3LTg5ZjYtNjg5Y2Y4Nzg2ZTk0")
                        con.setRequestMethod("POST")

                        val strJsonBody = ("{"
                                + "\"app_id\": \"1a84ca5e-eedd-4f38-9475-8e8c0e78bdfd\","

                                + "\"filters\": [{\"field\": \"tag\", \"key\": \"NotificationID\", \"relation\": \"=\", \"value\": \"" + sendNotificationID + "\"}],"

                                + "\"data\": {\"foo\": \"bar\"},"
                                + "\"contents\": {\"en\": \"Congratualations!! Your Complaint has been Solved :)\"}"
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

                } else {
                    sendNotificationID = "admin@gmail.com"
                }
            }
        }
    }
}