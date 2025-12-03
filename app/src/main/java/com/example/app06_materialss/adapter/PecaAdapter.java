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

import java.util.List;
import java.util.Locale;

import br.com.autopeca360.dominio.Peca;

public class PecaAdapter extends RecyclerView.Adapter<PecaAdapter.ProdutoViewHolder> {

    private List<Peca> listaPeca;
    private final OnPecaClickListener listener;

    public interface OnPecaClickListener {
        void onPecaClick(Peca peca);
    }

    public PecaAdapter(List<Peca> listaPeca, OnPecaClickListener listener) {
        this.listaPeca = listaPeca;
        this.listener = listener;
    }

    public static class ProdutoViewHolder extends RecyclerView.ViewHolder {

        public ImageView imagemImageView;
        public TextView nomeTextView;
        public TextView precoTextView;

        public ProdutoViewHolder(@NonNull View itemView) {
            super(itemView);
            imagemImageView = itemView.findViewById(R.id.recycler_peca_imagem);
            nomeTextView = itemView.findViewById(R.id.recycler_peca_nome);
            precoTextView = itemView.findViewById(R.id.recycler_peca_preco);
        }
    }

    public void atualizarLista(List<Peca> novaLista) {
        this.listaPeca.clear();
        this.listaPeca.addAll(novaLista);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProdutoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_peca, parent, false);
        return new ProdutoViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ProdutoViewHolder holder, int position) {
        Peca peca = listaPeca.get(position);

        Glide.with(holder.itemView.getContext())
                .load(peca.getImagem())
                .into(holder.imagemImageView);

        holder.nomeTextView.setText(peca.getNome());
        String precoFormatado = String.format(new Locale("pt", "BR"), "R$ %.2f", peca.getPreco());
        holder.precoTextView.setText(precoFormatado);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPecaClick(peca);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (listaPeca == null) {
            return 0;
        }
        return listaPeca.size();
    }
}

