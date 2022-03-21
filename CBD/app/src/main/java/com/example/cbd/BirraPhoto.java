package com.example.cbd;

import androidx.appcompat.app.AppCompatActivity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import com.bumptech.glide.Glide;

public class BirraPhoto extends AppCompatActivity {

    ImageView imagen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_birra_photo);

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        String uri = bundle.getString("URI");
        assert uri != null;

        imagen = findViewById(R.id.foto);
        Glide.with(BirraPhoto.this).load(Uri.parse(uri)).into(imagen);
        //Picasso.get().load(Uri.parse(uri)).fit().centerCrop().into(imagen);

        /*
        String userId = bundle.getString("USER");
        assert userId != null;
        String birraId = bundle.getString("BIRRAID");
        assert birraId != null;

        StorageReference storageRef = new DatabaseManager().mStorageRef().child(userId).child(birraId);
        try {
            final File localFile = File.createTempFile("images", "jpg");;

            storageRef.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            imagen.setImageURI(Uri.parse(localFile.getAbsolutePath()));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(getApplicationContext(), "Error al cargar la foto", Toast.LENGTH_LONG).show();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
         */
    }
}
