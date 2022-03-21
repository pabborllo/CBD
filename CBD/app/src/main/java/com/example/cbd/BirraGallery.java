package com.example.cbd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class BirraGallery extends AppCompatActivity {

    private String bg_uri = "https://firebasestorage.googleapis.com/v0/b/proyecto-cbd.appspot.com/o/birras%2Fbeer_bg.jpg?alt=media&token=fecd000f-56da-48bf-971b-e86d16f5f398";
    private ImageView beer_bg;

    private DatabaseManager db = new DatabaseManager();
    private List<Birra> birras = new ArrayList<>();
    private String userId;
    private DatabaseReference ref;

    private ConstraintLayout gallery_bg;
    private EditText filtro;
    private Button boton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_birra_gallery);

        gallery_bg = findViewById(R.id.gallery_bg);
        filtro = findViewById(R.id.filtro);
        boton = findViewById(R.id.boton);
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gallery_bg.setVisibility(View.GONE);
                if(!filtro.getText().toString().isEmpty()){
                    ordenPorNombreEqualToStringTodas(filtro.getText().toString());
                    filtro.setText(null);
                    Toast.makeText(getApplicationContext(), "Filtro aplicado", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(), "Ningún filtro añadido", Toast.LENGTH_LONG).show();
                }
            }
        });

        beer_bg = findViewById(R.id.bg_beer_gallery);
        Glide.with(BirraGallery.this)
                .load(Uri.parse(bg_uri))
                .into(beer_bg);
        userId = getIntent().getExtras().getString("ID");
        ref = db.realTimeRef().child(userId);
        ordenPorDefecto();

    }

    private void ordenPorDefecto(){
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                birras.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Birra birra = ds.getValue(Birra.class);
                    birras.add(birra);
                }
                mostrarDatos(birras);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Error al recuperar datos", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void ordenPorChild(String orderChild){
        ref.orderByChild(orderChild).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                birras.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Birra birra = ds.getValue(Birra.class);
                    birras.add(birra);
                }
                mostrarDatos(birras);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Error al recuperar datos", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void ordenPorNombreEqualToString(String equalTo){
        ref.orderByChild("nombre").equalTo(equalTo).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                birras.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Birra birra = ds.getValue(Birra.class);
                    birras.add(birra);
                }
                mostrarDatos(birras);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Error al recuperar datos", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void ordenPorNombreEqualToStringTodas(final String filtro){
        ref.orderByChild("nombre").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                birras.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Birra birra = ds.getValue(Birra.class);
                    if(birra.getNombre().toUpperCase().equals(filtro.toUpperCase())){
                        birras.add(birra);
                    }
                }
                mostrarDatos(birras);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Error al recuperar datos", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void ordenPorChildMayorIgualQue(Double filtro){
        ref.orderByChild("alcohol").startAt(filtro).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                birras.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Birra birra = ds.getValue(Birra.class);
                    birras.add(birra);
                }
                mostrarDatos(birras);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Error al recuperar datos", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void ordenPorChildMenorIgualQue(Double filtro){
        ref.orderByChild("alcohol").endAt(filtro).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                birras.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Birra birra = ds.getValue(Birra.class);
                    birras.add(birra);
                }
                mostrarDatos(birras);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Error al recuperar datos", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void ordenPorChildEqualToDouble(Double equalTo){
        ref.orderByChild("alcohol").equalTo(equalTo).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                birras.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Birra birra = ds.getValue(Birra.class);
                    birras.add(birra);
                }
                mostrarDatos(birras);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Error al recuperar datos", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void mostrarDatos(final List<Birra> birrasOriginal){
        Adapter adaptador = new Adapter(BirraGallery.this, birrasOriginal, new BirraListener() {
            public void birraOnClick(int posicion) {
                String id = birrasOriginal.get(posicion).getId();
                Bundle bundle = new Bundle();
                bundle.putString("BIRRA", id);
                bundle.putString("USERID", userId);

                Intent intent = new Intent(BirraGallery.this, BirraData.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        RecyclerView vista = findViewById(R.id.vista_lista);
        vista.setLayoutManager(new LinearLayoutManager(BirraGallery.this));
        //vista.setLayoutManager(new GridLayoutManager(Galeria.this, 4));
        vista.setAdapter(adaptador);
    }

    private void mostrarFotos(final List<Birra> birrasOriginal){
        AdapterPhotosList adaptador = new AdapterPhotosList(BirraGallery.this, birrasOriginal, new BirraListener() {
            public void birraOnClick(int posicion) {
                String uri = birrasOriginal.get(posicion).getFoto();
                Bundle bundle = new Bundle();
                bundle.putString("URI", uri);
                bundle.putString("USER", userId);
                bundle.putString("BIRRAID",birrasOriginal.get(posicion).getId());

                Intent intent = new Intent(BirraGallery.this, BirraPhoto.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        RecyclerView vista = findViewById(R.id.vista_lista);
        vista.setLayoutManager(new LinearLayoutManager(BirraGallery.this));
        //vista.setLayoutManager(new GridLayoutManager(Galeria.this, 4));
        vista.setAdapter(adaptador);
    }

    public interface BirraListener {
        void birraOnClick(int posicion);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_opciones, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.todasNombre:
                ordenPorChild("nombre");
                return true;
            case R.id.paulaner:
                ordenPorNombreEqualToString("Paulaner");
                return true;
            case R.id.custom_filter:
                gallery_bg.setVisibility(View.VISIBLE);
                return true;
            case R.id.todasAlcohol:
                ordenPorChild("alcohol");
                return true;
            case R.id.menorIgual5:
                ordenPorChildMenorIgualQue(5.0);
                return true;
            case R.id.igual5:
                ordenPorChildEqualToDouble(5.0);
                return true;
            case R.id.mayorIgual5:
                ordenPorChildMayorIgualQue(5.0);
                return true;
            case R.id.fotos:
                mostrarFotos(birras);
                return true;
            case R.id.reset:
                ordenPorDefecto();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
