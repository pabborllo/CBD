package com.example.cbd;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DatabaseManager {

    private DatabaseReference realTimeRef;
    private DatabaseReference usersRef;
    private StorageReference mStorageRef;

    public DatabaseManager()
    {
        this.realTimeRef = FirebaseDatabase.getInstance().getReference("birras");
        this.usersRef = FirebaseDatabase.getInstance().getReference("users");
        this.mStorageRef = FirebaseStorage.getInstance().getReference("birras");
    }

    public DatabaseReference getUsersRef(){
        return usersRef;
    }

    public DatabaseReference realTimeRef(){
        return realTimeRef;
    }

    public StorageReference mStorageRef(){
        return mStorageRef;
    }
}
