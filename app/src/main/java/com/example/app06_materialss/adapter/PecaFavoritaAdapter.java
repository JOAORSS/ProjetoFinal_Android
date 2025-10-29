package com.example.app06_materialss.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.app06_materialss.R;
import com.example.app06_materialss.entity.PecaFavorita;
import java.util.List;
import java.util.Locale;

public class PecaFavoritaAdapter extends RecyclerView.Adapter<PecaFavoritaAdapter.PecaViewHolder> {

    private List<PecaFavorita> listaPeca;
    private OnPecaFavoritaClickListener listener;

    public interface OnPecaFavoritaClickListener {
        void onPecaClick(PecaFavorita peca);
    }

    public PecaFavoritaAdapter(List<PecaFavorita> listaPeca, OnPecaFavoritaClickListener listener) {
        this.listaPeca = listaPeca;
        this.listener = listener;
    }

    public static class PecaViewHolder extends RecyclerView.ViewHolder {
        public ImageView imagemImageView;
        public TextView nomeTextView;
        public TextView precoTextView;

        public PecaViewHolder(@NonNull View itemView) {
            super(itemView);
            imagemImageView = itemView.findViewById(R.id.recycler_peca_imagem);
            nomeTextView = itemView.findViewById(R.id.recycler_peca_nome);
            precoTextView = itemView.findViewById(R.id.recycler_peca_preco);
        }

        public void bind(PecaFavorita peca, OnPecaFavoritaClickListener listener) {
            nomeTextView.setText(peca.getNome());
            String precoFormatado = String.format(new Locale("pt", "BR"), "R$ %.2f", peca.getPreco());
            precoTextView.setText(precoFormatado);

            Glide.with(itemView.getContext())
                    .load(peca.getImagem())
                    .into(imagemImageView);

            itemView.setOnClickListener(v -> listener.onPecaClick(peca));
        }
    }

    @NonNull
    @Override
    public PecaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_peca, parent, false);
        return new PecaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PecaViewHolder holder, int position) {
        PecaFavorita peca = listaPeca.get(position);
        holder.bind(peca, listener);
    }

    @Override
    public int getItemCount() {
        return listaPeca.size();
    }

    public void atualizarLista(List<PecaFavorita> novaLista) {
        this.listaPeca.clear();
        this.listaPeca.addAll(novaLista);
        notifyDataSetChanged();
    }
}

