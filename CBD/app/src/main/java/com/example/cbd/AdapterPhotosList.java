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

public class AdapterPhotosList extends RecyclerView.Adapter<AdapterPhotosList.Vista> {

    private List<Birra> birras;
    private BirraGallery.BirraListener birraListener;
    private Context context;

    public AdapterPhotosList(Context context, List<Birra> birras, BirraGallery.BirraListener birraListener){
        this.birras = birras;
        this.birraListener = birraListener;
        this.context = context;
    }

    @Override
    @NonNull
    public Vista onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);
        return new Vista(v, birraListener);
    }

    @Override
    public void onBindViewHolder(Vista holder, int position) {
        //Picasso.get().load(Uri.parse(this.birras.get(position).getFoto())).fit().centerCrop().into(holder.imagenBirra);
        Glide.with(this.context).load(Uri.parse(this.birras.get(position).getFoto())).into(holder.fotoLista);
        holder.pieFoto.setText(this.birras.get(position).getNombre());
    }

    @Override
    public int getItemCount() {
        return birras.size();
    }


    protected static class Vista extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView fotoLista;
        private TextView pieFoto;
        private BirraGallery.BirraListener birraListener;

        public Vista(View parent, BirraGallery.BirraListener birraListener){
            super(parent);
            this.fotoLista = itemView.findViewById(R.id.fotoLista);
            this.pieFoto = itemView.findViewById(R.id.pieFoto);
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
