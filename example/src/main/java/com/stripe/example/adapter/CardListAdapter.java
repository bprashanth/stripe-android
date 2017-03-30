package com.stripe.example.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.stripe.android.model.Source;
import com.stripe.android.view.CardDisplayWidget;

import java.util.ArrayList;
import java.util.List;


public class CardListAdapter extends RecyclerView.Adapter<CardListAdapter.ViewHolder> {

    static class ViewHolder extends RecyclerView.ViewHolder {

        private CardDisplayWidget mCardDisplayWidget;

        ViewHolder(CardDisplayWidget cdw) {
            super(cdw);
            mCardDisplayWidget = cdw;
        }

        public void setSource(Source source) {
            mCardDisplayWidget.setCard(source);
        }
    }

    private List<Source> mDataSet = new ArrayList<>();

    public CardListAdapter() { };

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Source source = mDataSet.get(position);
        holder.setSource(source);
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardDisplayWidget cdw = new CardDisplayWidget(parent.getContext());
        return new ViewHolder(cdw);
    }

    public void addItem(@NonNull Source source) {
        if (Source.CARD.equals(source.getType())) {
            mDataSet.add(source);
        }
        notifyDataSetChanged();
    }
}
