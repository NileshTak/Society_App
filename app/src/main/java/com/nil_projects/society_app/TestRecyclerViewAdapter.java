package com.nil_projects.society_app;

import android.app.LauncherActivity;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nil_projects.society_app.fragment.Model;
import com.nil_projects.society_app.fragment.RecyclerViewFragment;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by florentchampigny on 24/04/15.
 */
public class TestRecyclerViewAdapter extends FirestoreRecyclerAdapter<Model, TestRecyclerViewAdapter.ViewHolder> {

    static final int TYPE_HEADER = 0;
    static final int TYPE_CELL = 1;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("FlatUsers");


    public TestRecyclerViewAdapter(FirestoreRecyclerOptions<Model> options) {
        super(options);
    }


    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0:
                return TYPE_HEADER;
            default:
                return TYPE_CELL;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_card_big, parent, false);
        return new ViewHolder(view) {
        };
    }

    @Override
    protected void onBindViewHolder(ViewHolder viewHolder, int i, final Model model) {
        viewHolder.wingName.setText(model.getWing());
        viewHolder.flatNo.setText(model.getFlatNo());
        viewHolder.relation.setText(model.getUserRelation());
        viewHolder.userName.setText(model.getUserName());
        viewHolder.contactNo.setText(model.getMobileNumber());
        viewHolder.email.setText(model.getUserEmail());


        viewHolder.reqAcceptBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                updateReq(model.getMobileNumber());
            }
        });

        viewHolder.reqRejected.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                updateREJReq(model.getMobileNumber());
            }
        });
    }

    private void updateReq(String mob) {

        notebookRef.whereEqualTo("MobileNumber",mob)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                        {
                            Model note = documentSnapshot.toObject(Model.class);
                            Log.d("SocietyTrial",documentSnapshot.getId());

                            DocumentReference bookRef = db.document("FlatUsers/"+documentSnapshot.getId());
                            bookRef.update("userAuth","Accepted");
                        }
                    }
                });
    }

    private void updateREJReq(String mob) {

        notebookRef.whereEqualTo("MobileNumber",mob)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                        {
                            Model note = documentSnapshot.toObject(Model.class);
                            Log.d("SocietyTrial",documentSnapshot.getId());

                            DocumentReference bookRef = db.document("FlatUsers/"+documentSnapshot.getId());
                            bookRef.update("userAuth","Rejected");
                        }
                    }
                });
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView wingName;
        public TextView flatNo;
        public TextView relation;
        public TextView userName;
        public TextView contactNo;
        public TextView email;

        public Button reqAcceptBtn;
        public Button reqRejected;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            wingName = (TextView) itemView.findViewById(R.id.cutom_userreq_wingname);
            flatNo = (TextView) itemView.findViewById(R.id.cutom_userreq_flatno);
            relation = (TextView) itemView.findViewById(R.id.cutom_userreq_relation);
            userName = (TextView) itemView.findViewById(R.id.cutom_userreq_name);
            contactNo = (TextView) itemView.findViewById(R.id.cutom_userreq_number);
            email = (TextView) itemView.findViewById(R.id.cutom_userreq_email);
            reqAcceptBtn = (Button) itemView.findViewById(R.id.custom_userreq_acceptbtn);
            reqRejected = (Button) itemView.findViewById(R.id.custom_userreq_rejectbtn);
        }
    }
}