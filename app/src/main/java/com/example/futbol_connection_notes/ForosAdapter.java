package com.example.futbol_connection_notes;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ForosAdapter extends RecyclerView.Adapter<ForosAdapter.ViewHolder> {
    private List<Foro> listaForos;
    private Context context;

    public ForosAdapter(Context context, List<Foro> listaForos) {
        this.context = context;
        this.listaForos = listaForos;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombreForo;
        ImageView imagenForo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreForo = itemView.findViewById(R.id.textNombreForo);
            imagenForo = itemView.findViewById(R.id.imageForo);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_foro, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position){
        Foro foro = listaForos.get(position);
        holder.nombreForo.setText(foro.getNombre());
        holder.imagenForo.setImageResource(foro.getImagenResourceId());

        // Al hacer clic en un foro, se abre el chat correspondiente y se pasa el nombre y el logo
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ChatActivity.class);
            intent.putExtra("foroNombre", foro.getNombre());
            intent.putExtra("team_name", foro.getNombre());
            intent.putExtra("team_logo", foro.getImagenResourceId());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listaForos.size();
    }
}
