package com.example.cbd;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BirraData extends AppCompatActivity implements View.OnClickListener {

    private String bg_uri = "https://firebasestorage.googleapis.com/v0/b/proyecto-cbd.appspot.com/o/birras%2Fbeer_bg.jpg?alt=media&token=fecd000f-56da-48bf-971b-e86d16f5f398";
    private ImageView bg_beer_data;

    private DatabaseManager db = new DatabaseManager();
    private DatabaseReference realTimeRef;
    private StorageReference mstorageRef;
    private Birra birra;
    private EditText nombre;
    private Spinner formato;
    private EditText alcohol;
    private EditText comentarios;
    private ImageView imagen;
    private Button updateAll;
    private Button delete;
    private Button examinarUpdate;
    private Button clearFoto;
    private Button resetFoto;

    private ProgressBar progressBarUpdate;

    private String uri;
    private String nuevaUri;

    private  String birraId;
    private String userInfo;

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_birra_data);

        bg_beer_data = findViewById(R.id.bg_beer_data);
        Glide.with(BirraData.this)
                .load(Uri.parse(bg_uri))
                .into(bg_beer_data);

        progressBarUpdate = findViewById(R.id.progressBarUpdate);

        nombre = findViewById(R.id.nombre);
        formato = findViewById(R.id.formato);
        alcohol = findViewById(R.id.alcohol);
        comentarios = findViewById(R.id.comentarios);

        updateAll = findViewById(R.id.updateAll);
        delete = findViewById(R.id.delete);
        examinarUpdate = findViewById(R.id.examinarUpdate);
        clearFoto = findViewById(R.id.clearUpdate);
        resetFoto = findViewById(R.id.resetUpdate);

        imagen = findViewById(R.id.fotoBirra);

        updateAll.setOnClickListener(this);
        delete.setOnClickListener(this);
        examinarUpdate.setOnClickListener(this);
        imagen.setOnClickListener(this);
        clearFoto.setOnClickListener(this);
        resetFoto.setOnClickListener(this);

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        birraId = bundle.getString("BIRRA");
        userInfo = bundle.getString("USERID");

        assert birraId != null;
        assert userInfo != null;

        realTimeRef = db.realTimeRef().child(userInfo).child(birraId);
        mstorageRef = db.mStorageRef().child(userInfo).child(birraId);

        realTimeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                birra = dataSnapshot.getValue(Birra.class);

                assert birra != null;
                nombre.setText(birra.getNombre());
                formato.setSelection(getPosicion(String.format("%s",birra.getFormato().toString())));
                alcohol.setText(String.format("%s", birra.getAlcohol()));
                comentarios.setText(birra.getComentario());
                resetFoto();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Error al recuperar datos", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void resetFoto(){
        //Picasso.get().load(Uri.parse(birra.getFoto())).fit().centerInside().into(imagen);
        Glide.with(BirraData.this).load(Uri.parse(birra.getFoto())).into(imagen);
        uri = birra.getFoto();
    }

    private void clearFoto(){
        uri = "android.resource://"+getPackageName()+"/"+R.drawable.birra_doe;
        imagen.setImageURI(Uri.parse(uri));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.updateAll:
                progressBarUpdate.setVisibility(View.VISIBLE);
                updateAll.setClickable(false);
                delete.setClickable(false);
                examinarUpdate.setClickable(false);
                imagen.setClickable(false);
                Map<String, Object> updates = checkValues();
                if(!uri.equals(birra.getFoto())){
                    subeFoto(updates);
                }else{
                    actualizaBirra(updates);
                }
                break;
            case R.id.delete:
                realTimeRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mstorageRef.delete();
                        String toast = "Birra eliminada";
                        if(birra.getAlcohol()==0.0){
                            toast = "Zumo eliminado";
                        }
                        Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        resetValues();
                        Toast.makeText(getApplicationContext(), "Error al eliminar", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case R.id.examinarUpdate:
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
            case R.id.fotoBirra:
                Bundle bundle = new Bundle();
                bundle.putString("URI", uri);
                Intent intent = new Intent(BirraData.this, BirraPhoto.class);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.clearUpdate:
                clearFoto();
                break;
            case R.id.resetUpdate:
                resetFoto();
                resetValues();
                break;
            default:
                break;
        }
    }

    private void subeFoto(final Map<String, Object> updates){
        final UploadTask fotoTask = mstorageRef.putFile(Uri.parse(uri));
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
                        return mstorageRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            nuevaUri = Objects.requireNonNull(task.getResult()).toString();
                            updates.put("foto", nuevaUri);
                            actualizaBirra(updates);
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

    private void actualizaBirra(Map<String, Object> updates){
        realTimeRef.updateChildren(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "Datos actualizados", Toast.LENGTH_SHORT).show();
                progressBarUpdate.setVisibility(View.GONE);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                resetValues();
                progressBarUpdate.setVisibility(View.GONE);
                updateAll.setClickable(true);
                delete.setClickable(true);
                examinarUpdate.setClickable(true);
                imagen.setClickable(true);
                Toast.makeText(getApplicationContext(), "Error al actualizar", Toast.LENGTH_SHORT).show();
            }
        });
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
            imagen.setImageURI(data.getData());
            uri = data.getDataString();
        }
    }

    private Map<String, Object> checkValues(){

        Map<String, Object> res = new HashMap<>();
        if(!nombre.getText().toString().isEmpty()){
            res.put("nombre", nombre.getText().toString());
        }
        if(!formato.getSelectedItem().toString().equals("Formato")){
            res.put("formato", getCantidad(formato.getSelectedItem().toString()));
        }
        if(!alcohol.getText().toString().isEmpty() && !alcohol.getText().toString().equals(".")){
            BigDecimal big = new BigDecimal(alcohol.getText().toString());
            int intValue = big.intValue();
            BigDecimal decimal = big.subtract(new BigDecimal(intValue));
            if(decimal.toPlainString().length()<=4){
                res.put("alcohol", Double.parseDouble(alcohol.getText().toString()));
            }
        }
        if(!comentarios.getText().toString().isEmpty()){
            res.put("comentario", comentarios.getText().toString());
        }
        return res;
    }

    private void resetValues(){
        nombre.setText(birra.getNombre());
        formato.setSelection(getPosicion(String.format("%s",birra.getFormato().toString())));
        alcohol.setText(String.format("%s", birra.getAlcohol().toString()));
        comentarios.setText(birra.getComentario());
    }

    private int getIndex(Spinner formatos, String formatoString){
        int res = 0;
        for(int i=0; i<formatos.getCount(); i++){
            if(formatos.getItemAtPosition(i).toString().equals(formatoString)){
                res = i;
                break;
            }
        }
        return res;
    }

    private int getPosicion(String formato){
        int res = 0;
        switch (formato){
            case "0.2":
                res = 1;
                break;
            case "0.25":
                res = 2;
                break;
            case "0.33":
                res = 3;
                break;
            case "0.44":
                res = 4;
                break;
            case "0.5":
                res = 5;
                break;
            case "0.75":
                res = 6;
                break;
            case "1.0":
                res = 7;
                break;
            case "1.1":
                res = 8;
                break;
            default:
                break;
        }
        return res;
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
}
