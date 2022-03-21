package com.example.cbd;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.math.BigDecimal;
import java.util.Objects;

public class CreateBirra extends AppCompatActivity implements View.OnClickListener {

    private String bg_uri = "https://firebasestorage.googleapis.com/v0/b/proyecto-cbd.appspot.com/o/birras%2Fbeer_bg.jpg?alt=media&token=fecd000f-56da-48bf-971b-e86d16f5f398";
    private ImageView beer_bg;

    private DatabaseManager db = new DatabaseManager();
    private ImageView imagen;
    private Uri imageUri;
    private ProgressBar progressBar;

    private EditText nombreBirra;
    private Spinner formatoBirra;
    private EditText alcoholBirra;
    private EditText comentariosBirra;
    private Button examinar;
    private Button botonGuardar;
    private Button clearFoto;

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_birra);

        beer_bg = findViewById(R.id.bg_beer_create);
        Glide.with(CreateBirra.this)
                .load(Uri.parse(bg_uri))
                .into(beer_bg);

        progressBar = findViewById(R.id.progressBar);

        imagen = findViewById(R.id.imagenBirra);
        nombreBirra = findViewById(R.id.nombreBirra);
        formatoBirra = findViewById(R.id.formatoBirra);
        alcoholBirra = findViewById(R.id.alcoholBirra);
        comentariosBirra = findViewById(R.id.comentariosBirra);
        examinar = findViewById(R.id.examinar);
        botonGuardar = findViewById(R.id.botonGuardar);
        clearFoto = findViewById(R.id.clear);

        examinar.setOnClickListener(this);
        botonGuardar.setOnClickListener(this);
        clearFoto.setOnClickListener(this);

        userId = getIntent().getExtras().getString("ID");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.examinar:
                if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
                    if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permissions, PERMISSION_CODE);
                    }else{
                        seleccionaFoto();
                    }
                }else{
                    seleccionaFoto();
                }
                break;
            case R.id.botonGuardar:
                final String id = db.realTimeRef().push().getKey();
                assert id != null;
                if(imageUri==null){
                    imageUri = Uri.parse("android.resource://"+getPackageName()+"/"+R.drawable.birra_doe);
                }
                String nombre = nombreBirra.getText().toString();
                String formato = formatoBirra.getSelectedItem().toString();
                String alcohol = alcoholBirra.getText().toString();
                String comentarios = comentariosBirra.getText().toString();
                if(checkValues(nombre, formato, alcohol)){
                    Birra birra = new Birra(id, nombre, getCantidad(formato), Double.parseDouble(alcohol), comentarios);
                    subeFotoYBirra(birra);
                }
                break;
            case R.id.clear:
                clearFoto();
                break;
            default:
                break;
        }
    }

    private void subeFotoYBirra(final Birra birra){
        botonGuardar.setClickable(false);
        examinar.setClickable(false);
        progressBar.setVisibility(View.VISIBLE);
        final StorageReference fotoRef = db.mStorageRef().child(userId).child(birra.getId());
        final UploadTask fotoTask = fotoRef.putFile(imageUri);

        fotoTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot > () {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getApplicationContext(), "Foto subida", Toast.LENGTH_LONG).show();
                fotoTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful()){
                            throw Objects.requireNonNull(task.getException());
                        }
                        return fotoRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            birra.setFoto(Objects.requireNonNull(task.getResult()).toString());
                            subeBirra(birra);
                        }else{
                            Toast.makeText(getApplicationContext(), "Error al vincular birra y foto", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                resetValues();
                Toast.makeText(getApplicationContext(), "Error al subir la foto", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void subeBirra(final Birra birra){
        db.realTimeRef()
                .child(userId)
                .child(birra.getId())
                .setValue(birra)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                String toast = "Birra subida";
                if(birra.getAlcohol()==0.0){
                    toast = "Zumo añadido";
                }
                Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                botonGuardar.setClickable(true);
                examinar.setClickable(true);
                Toast.makeText(getApplicationContext(), "Error al subir la birra", Toast.LENGTH_LONG).show();
            }
        });
    }

    private String getExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void seleccionaFoto() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==PERMISSION_CODE){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                seleccionaFoto();
            }else{
                Toast.makeText(getApplicationContext(), "Permiso requerido para fotos", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            assert data != null;
            imageUri = data.getData();
            imagen.setImageURI(imageUri);
        }
    }

    private void clearFoto(){
        imageUri = Uri.parse("android.resource://"+getPackageName()+"/"+R.drawable.birra_doe);
        imagen.setImageURI(null);
    }

    private void resetValues(){
        nombreBirra.setText(null);
        formatoBirra.setSelection(0);
        alcoholBirra.setText(null);
        comentariosBirra.setText(null);
    }

    private double getCantidad(String formato){
        double res = 0.0;
        switch (formato){
            case "20cl":
                res = 0.2;
                break;
            case "25cl":
                res = 0.25;
                break;
            case "330ml":
                res = 0.33;
                break;
            case "440ml":
                res = 0.44;
                break;
            case "500ml":
                res = 0.5;
                break;
            case "75cl":
                res = 0.75;
                break;
            case "1L":
                res = 1.0;
                break;
            case "1,1L":
                res = 1.1;
                break;
            default:
                break;
        }
        return res;
    }

    private Boolean checkValues(String nombre, String formato, String alcohol){
        boolean res = true;
        if(nombre.isEmpty() || formato.equals("Formato") ||
                alcohol.isEmpty() || alcohol.equals(".")){
            res = false;
            Toast.makeText(getApplicationContext(), "Nombre, formato y alcohol requeridos", Toast.LENGTH_LONG).show();
        }else{
            BigDecimal big = new BigDecimal(alcohol);
            int intValue = big.intValue();
            BigDecimal decimal = big.subtract(new BigDecimal(intValue));
            if(decimal.toPlainString().length()>3){
                res = false;
                Toast.makeText(getApplicationContext(), "Alcohol con máximo 1 decimal",Toast.LENGTH_LONG).show();
            }
        }
        return res;
    }
}