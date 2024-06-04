package com.tecnocampus.practica3_g103_777;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.ViewHolder> {

    private List<Score> scores;

    public RankingAdapter(List<Score> scores) {
        this.scores = scores;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_score, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Score score = scores.get(position);
        holder.emailTextView.setText(score.getEmail());
        holder.scoreTextView.setText(String.valueOf(score.getCorrectAnswers()));
    }

    @Override
    public int getItemCount() {
        return scores.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView emailTextView;
        public TextView scoreTextView;

        public ViewHolder(View view) {
            super(view);
            emailTextView = view.findViewById(R.id.email_text_view);
            scoreTextView = view.findViewById(R.id.score_text_view);
        }
    }
}