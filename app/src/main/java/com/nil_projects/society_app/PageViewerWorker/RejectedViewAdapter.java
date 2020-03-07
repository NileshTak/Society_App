package com.nil_projects.society_app.PageViewerWorker;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nil_projects.society_app.R;
import com.nil_projects.society_app.fragment.Model;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by florentchampigny on 24/04/15.
 */
public class RejectedViewAdapter extends FirestoreRecyclerAdapter<Model, RejectedViewAdapter.ViewHolder> {

    static final int TYPE_HEADER = 0;
    static final int TYPE_CELL = 1;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("FlatUsers");


    public RejectedViewAdapter(FirestoreRecyclerOptions<Model> options) {
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
                .inflate(R.layout.customrejected_userreq, parent, false);
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
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView wingName;
        public TextView flatNo;
        public TextView relation;
        public TextView userName;
        public TextView contactNo;
        public TextView email;
        public Button reqAcceptBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            wingName = (TextView) itemView.findViewById(R.id.cutom_userreq_wingname);
            flatNo = (TextView) itemView.findViewById(R.id.cutom_userreq_flatno);
            relation = (TextView) itemView.findViewById(R.id.cutom_userreq_relation);
            userName = (TextView) itemView.findViewById(R.id.cutom_userreq_name);
            contactNo = (TextView) itemView.findViewById(R.id.cutom_userreq_number);
            email = (TextView) itemView.findViewById(R.id.cutom_userreq_email);
            reqAcceptBtn = (Button) itemView.findViewById(R.id.custom_userreq_acceptbtn);
        }
    }
}