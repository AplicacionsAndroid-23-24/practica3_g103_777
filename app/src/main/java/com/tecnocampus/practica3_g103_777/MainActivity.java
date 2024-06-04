package com.tecnocampus.practica3_g103_777;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.*;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Spinner categorySpinner;
    private List<Integer> categoryIds = new ArrayList<>();
    private boolean isGameInProgress = false;  // Variable to track game state
    private Button startGameButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Check if the user is logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // Redirect to the login screen
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            // Continue with the game
            loadCategories();
        }

        // Set up the category spinner
        categorySpinner = findViewById(R.id.categorySpinner);

        // Set up the ranking button
        View rankingButton = findViewById(R.id.ranking_button);
        rankingButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RankingActivity.class);
            startActivity(intent);
        });

        // Set up the start game button
        startGameButton = findViewById(R.id.start_button);
        startGameButton.setOnClickListener(v -> {
            if (isGameInProgress) {
                showNewGameConfirmationDialog();
            } else {
                startNewGame();
            }
        });

        // Update start button text based on game state
        updateStartButtonText();
    }

    private void loadCategories() {
        String url = "https://opentdb.com/api_category.php";
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray categories = response.getJSONArray("trivia_categories");
                        List<String> categoryNames = new ArrayList<>();
                        for (int i = 0; i < categories.length(); i++) {
                            JSONObject category = categories.getJSONObject(i);
                            categoryNames.add(category.getString("name"));
                            categoryIds.add(category.getInt("id"));
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryNames);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        categorySpinner.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
            error.printStackTrace();
        });

        queue.add(jsonObjectRequest);
    }

    private void loadQuestions(int categoryId) {
        String url = "https://opentdb.com/api.php?amount=10&category=" + categoryId;
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray results = response.getJSONArray("results");
                        List<Question> questions = new ArrayList<>();
                        for (int i = 0; i < results.length(); i++) {
                            JSONObject result = results.getJSONObject(i);
                            String questionText = result.getString("question");
                            String correctAnswer = result.getString("correct_answer");
                            JSONArray incorrectAnswersArray = result.getJSONArray("incorrect_answers");
                            List<String> incorrectAnswers = new ArrayList<>();
                            for (int j = 0; j < incorrectAnswersArray.length(); j++) {
                                incorrectAnswers.add(incorrectAnswersArray.getString(j));
                            }
                            questions.add(new Question(questionText, correctAnswer, incorrectAnswers));
                        }
                        showQuestionFragment(questions);
                        isGameInProgress = true;  // Mark game as in progress
                        updateStartButtonText();  // Update button text to "Restart Game"
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> error.printStackTrace());

        queue.add(jsonObjectRequest);
    }

    private void showQuestionFragment(List<Question> questions) {
        QuestionFragment questionFragment = new QuestionFragment(questions);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, questionFragment)
                .commit();
    }

    public void showResults(int correctAnswers) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String userId = user.getUid();
            String userEmail = user.getEmail();

            Map<String, Object> score = new HashMap<>();
            score.put("correctAnswers", correctAnswers);
            score.put("timestamp", FieldValue.serverTimestamp());
            score.put("email", userEmail);

            db.collection("scores").document(userId)
                    .set(score)
                    .addOnSuccessListener(aVoid -> {
                        ResultFragment resultFragment = new ResultFragment(correctAnswers);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragmentContainer, resultFragment)
                                .commit();
                        isGameInProgress = false;  // Mark game as not in progress
                        updateStartButtonText();  // Update button text to "Start Game"
                    })
                    .addOnFailureListener(e -> {
                        e.printStackTrace();
                    });
        }
    }

    private void startNewGame() {
        // Logic to start a new game
        int selectedCategoryPosition = categorySpinner.getSelectedItemPosition();
        if (selectedCategoryPosition != AdapterView.INVALID_POSITION) {
            loadQuestions(categoryIds.get(selectedCategoryPosition));
        }
    }

    private void showNewGameConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Start New Game")
                .setMessage("A game is already in progress. Do you want to start a new game?")
                .setPositiveButton("Yes", (dialog, which) -> startNewGame())
                .setNegativeButton("No", null)
                .show();
    }

    private void updateStartButtonText() {
        if (isGameInProgress) {
            startGameButton.setText("Restart Game");
        } else {
            startGameButton.setText("Start Game");
        }
    }
}
