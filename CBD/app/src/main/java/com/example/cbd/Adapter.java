package com.example.cbd;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Adapter extends RecyclerView.Adapter<Adapter.Vista> {

    private List<Birra> birras;
    private BirraGallery.BirraListener birraListener;
    private Context context;

    public Adapter(Context context, List<Birra> birras, BirraGallery.BirraListener birraListener){
        this.birras = birras;
        this.birraListener = birraListener;
        this.context = context;
    }

    @Override
    @NonNull
    public Vista onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_birra, parent, false);
        return new Vista(v, birraListener);
    }

    @Override
    public void onBindViewHolder(Vista holder, int position) {
        //Picasso.get().load(Uri.parse(this.birras.get(position).getFoto())).fit().centerCrop().into(holder.imagenBirra);
        Glide.with(this.context).load(Uri.parse(this.birras.get(position).getFoto())).into(holder.imagenBirra);
        holder.titulo.setText(this.birras.get(position).getNombre());
        holder.porcentaje.setText(this.birras.get(position).getAlcohol().toString()+"%");
        holder.fecha.setText(this.birras.get(position).getFecha());
    }

    @Override
    public int getItemCount() {
        return birras.size();
    }


    protected static class Vista extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView imagenBirra;
        private TextView titulo;
        private TextView porcentaje;
        private TextView fecha;
        private BirraGallery.BirraListener birraListener;

        public Vista(View parent, BirraGallery.BirraListener birraListener){
            super(parent);
            this.imagenBirra = itemView.findViewById(R.id.imagenBirra);
            this.titulo = itemView.findViewById(R.id.titulo);
            this.porcentaje = itemView.findViewById(R.id.porcentaje);
            this.fecha = itemView.findViewById(R.id.fecha);
            this.birraListener = birraListener;
            parent.setOnClickListener(this);
            parent.setClickable(true);
        }

        @Override
        public void onClick(View view) {
            birraListener.birraOnClick(getAdapterPosition());
        }
    }
}
