package com.example.app06_materialss.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.app06_materialss.R;
import com.example.app06_materialss.entity.PecaCarrinho;
import java.util.List;
import java.util.Locale;

public class CarrinhoAdapter extends RecyclerView.Adapter<CarrinhoAdapter.CarrinhoViewHolder> {

    private List<PecaCarrinho> listaItens;
    private OnItemInteractionListener listener;

    public interface OnItemInteractionListener {
        void onItemDelete(PecaCarrinho item);
    }

    public CarrinhoAdapter(List<PecaCarrinho> listaItens, OnItemInteractionListener listener) {
        this.listaItens = listaItens;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CarrinhoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_peca_carrinho, parent, false);
        return new CarrinhoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarrinhoViewHolder holder, int position) {
        PecaCarrinho item = listaItens.get(position);
        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return listaItens.size();
    }

    public void atualizarLista(List<PecaCarrinho> novaLista) {
        this.listaItens.clear();
        this.listaItens.addAll(novaLista);
        notifyDataSetChanged();
    }

    public static class CarrinhoViewHolder extends RecyclerView.ViewHolder {
        ImageView imagemPeca;
        TextView nomePeca;
        TextView quantidadePeca;
        TextView precoPeca;

        public CarrinhoViewHolder(@NonNull View itemView) {
            super(itemView);
            imagemPeca = itemView.findViewById(R.id.carrinho_peca_imagem);
            nomePeca = itemView.findViewById(R.id.carrinho_peca_nome);
            quantidadePeca = itemView.findViewById(R.id.carrinho_peca_quantidade);
            precoPeca = itemView.findViewById(R.id.carrinho_peca_preco);
        }

        public void bind(final PecaCarrinho item, final OnItemInteractionListener listener) {
            nomePeca.setText(item.getNome());
            quantidadePeca.setText(String.valueOf(item.getQuantidade()));
            String precoFormatado = String.format(new Locale("pt", "BR"), "R$ %.2f", item.getPreco());
            precoPeca.setText(precoFormatado);

            Glide.with(itemView.getContext())
                    .load(item.getImagem())
                    .into(imagemPeca);

            itemView.setOnLongClickListener(v -> {
                PopupMenu popup = new PopupMenu(itemView.getContext(), itemView);
                popup.getMenuInflater().inflate(R.menu.carrinho_contexto, popup.getMenu());
                popup.setOnMenuItemClickListener(menuItem -> {
                    if (menuItem.getItemId() == R.id.menu_excluir_item) {
                        if (listener != null) {
                            listener.onItemDelete(item);
                        }
                        return true;
                    }
                    return false;
                });
                popup.show();
                return true;
            });
        }
    }
}

