package com.example.proj.helperClasses;

import com.example.proj.messageHelper;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class firebaseHelperClass {

    DatabaseReference db;
    Boolean saved=null;
    ArrayList<String> reports=new ArrayList<>();


    public firebaseHelperClass(DatabaseReference db) {
        this.db = db;
    }

    //SAVE
    public Boolean save(messageHelper helper)
    {
        if(helper==null)
        {
            saved=false;
        }else {

            try
            {
                db.child("Spacecraft").push().setValue(helper);
                saved=true;
            }catch (DatabaseException e)
            {
                e.printStackTrace();
                saved=false;
            }

        }

        return saved;
    }

    //READ
    public ArrayList<String> retrieve()
    {
        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                fetchData(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                fetchData(dataSnapshot);

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

        return reports;
    }

    private void fetchData(DataSnapshot dataSnapshot)
    {
        reports.clear();
        for (DataSnapshot ds : dataSnapshot.getChildren())
        {
            String msg=ds.getValue(messageHelper.class).getMsg();
            reports.add(msg);
        }
    }
}
