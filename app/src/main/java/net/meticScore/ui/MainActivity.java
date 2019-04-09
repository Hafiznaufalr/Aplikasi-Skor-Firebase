package net.meticScore.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;

import net.meticScore.R;
import net.meticScore.adapter.ListViewAdapter;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    public int flag=0;
    FloatingActionButton fab_add;
    ListView lv;
   public ArrayList<String> names=new ArrayList<>();
    public ArrayList<String> batterys=new ArrayList<>();
    public ListViewAdapter lviewAdapter;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference myRef = database.getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        lv=(ListView) findViewById(R.id.lv);
        flag=0;
        retreiveData();
        fab_add = (FloatingActionButton)findViewById(R.id.fab_add);

        fab_add.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addMembers();
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override

            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showChangeLangDialog(names.get(i), batterys.get(i));
//                try {
//                    Object o = lv.getItemAtPosition(i);
//                    String str = (String) o;
//                    showChangeLangDialog(name, battery);
//                    Toast.makeText(getApplicationContext(), name + "," + battery, Toast.LENGTH_SHORT).show();
//                }catch (Exception ae){Toast.makeText(getApplicationContext(), ae.toString(), Toast.LENGTH_SHORT).show();}
            }
        });
    }

    public void showChangeLangDialog(final String name, final String battery) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this,R.style.MyDialogTheme);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);
        final TextView edt = (TextView) dialogView.findViewById(R.id.text1);
        edt.setText(battery);
        final FloatingActionButton addt=(FloatingActionButton)dialogView.findViewById(R.id.fab_add);
        final FloatingActionButton subt=(FloatingActionButton)dialogView.findViewById(R.id.fab_sub);
        dialogBuilder.setTitle("Update Skor");
        dialogBuilder.setMessage(name);
        addt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String a = edt.getText().toString();
                int bat_temp=Integer.parseInt(a);
                bat_temp+=20;
                edt.setText(Integer.toString(bat_temp));
            }
        });
        subt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String a = edt.getText().toString();
                int bat_temp=Integer.parseInt(a);
                bat_temp-=20;
                edt.setText(Integer.toString(bat_temp));
            }
        });
        dialogBuilder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
                String a = edt.getText().toString();
                updateBattery(name,a);
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }


    //updating battery
    public void updateBattery(final String name, final String battery){
        final DatabaseReference myMem = database.getReference("members");
        myMem.orderByChild("name").equalTo(name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    snapshot.getRef().child("battery").setValue(battery);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //floating action button onClick calls this function
    public void addMembers(){
        final DatabaseReference childRef = myRef.child("members");
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this,R.style.MyDialogTheme);
        alertDialog.setTitle("Add Member");
        alertDialog.setMessage("Enter Name");
        final EditText et_members = new EditText(MainActivity.this);
        et_members.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        alertDialog.setView(et_members);
        alertDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String new_member = et_members.getText().toString();
                Member member= new Member(new_member, "0");
                try {

                    childRef.push().setValue(member);
                }
                catch(Exception e){
                    Toast.makeText(getApplicationContext(), e.toString() , Toast.LENGTH_SHORT).show();
                }
            }
        });
        alertDialog.setNegativeButton("Cancel", null);
        AlertDialog dialog = alertDialog.create();
        dialog.show();
    }

    public void retreiveData(){
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                showList(dataSnapshot);
//                getUpdates(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                getUpdates(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void getUpdates(DataSnapshot ds){
        names.clear();
        batterys.clear();
        for (DataSnapshot data : ds.getChildren()) {
            Member m = new Member(data.getValue(Member.class).name, data.getValue(Member.class).battery);
            names.add(m.name);
            batterys.add(m.battery);
        }
        lviewAdapter = new ListViewAdapter(MainActivity.this, names, batterys);
        lv.setAdapter(lviewAdapter);
    }
    public void showList(DataSnapshot ds){
        for (DataSnapshot data : ds.getChildren()) {
            Member m = new Member(data.getValue(Member.class).name, data.getValue(Member.class).battery);
            names.add(m.name);
            batterys.add(m.battery);
        }
        lviewAdapter = new ListViewAdapter(MainActivity.this, names, batterys);
        lv.setAdapter(lviewAdapter);
    }

    public static class Member{
        public String name;
        public String battery;
        public Member(){

        }
        public Member(String name, String battery){
            this.name=name;
            this.battery=battery;
        }
    }
}
