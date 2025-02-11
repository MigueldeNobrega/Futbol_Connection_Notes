package com.example.futbol_connection_notes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private List<Message> listaMensajes;

    public ChatAdapter(List<Message> listaMensajes) {
        this.listaMensajes = listaMensajes;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView usuario, mensaje;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            usuario = itemView.findViewById(R.id.textUsuario);
            mensaje = itemView.findViewById(R.id.textMensaje);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message msg = listaMensajes.get(position);
        holder.usuario.setText(msg.getUsuario());
        holder.mensaje.setText(msg.getMensaje());
    }

    @Override
    public int getItemCount() {
        return listaMensajes.size();
    }
}
