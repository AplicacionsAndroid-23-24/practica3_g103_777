package com.tecnocampus.practica3_g103_777;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.color.utilities.Score;

import java.util.List;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.ViewHolder> {

    /*TODO los Score "Petan".
    @SuppressLint("RestrictedApi") lo soluciona pero no se si se deberia usar
    DESCRIPCION DEL ERROR:
    Score can only be accessed from within the same library group (referenced groupId=`com.google.android.material` from groupId=`practica3_g103_777`)*/
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
        //TODO No pilla .getUsername y . getCorrectAnswer.
        // @SuppressLint("RestrictedApi") tampoco lo soluciona
        holder.usernameTextView.setText(score.getUsername());
        holder.scoreTextView.setText(String.valueOf(score.getCorrectAnswers()));
    }

    @Override
    public int getItemCount() {
        return scores.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView usernameTextView;
        public TextView scoreTextView;

        public ViewHolder(View view) {
            super(view);
            usernameTextView = view.findViewById(R.id.username_text_view);
            scoreTextView = view.findViewById(R.id.score_text_view);
        }
    }
}