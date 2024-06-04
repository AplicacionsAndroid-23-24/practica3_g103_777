package com.tecnocampus.practica3_g103_777;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class RankingActivity extends AppCompatActivity {

    private RankingAdapter adapter;
    private final List<Score> scoreList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RankingAdapter(scoreList);
        recyclerView.setAdapter(adapter);

        loadScores();
    }

    private void loadScores() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("scores")
                .orderBy("correctAnswers", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        scoreList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Score score = document.toObject(Score.class);
                            scoreList.add(score);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.e("loadScores", "Error al cargar las puntuaciones", task.getException());
                    }
                });
    }
}
