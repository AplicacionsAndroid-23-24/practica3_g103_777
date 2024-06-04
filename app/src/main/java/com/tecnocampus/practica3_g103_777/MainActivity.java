package com.tecnocampus.practica3_g103_777;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
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
import com.google.firebase.inappmessaging.model.Button;

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
        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Verificar si el usuario está logueado
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // Redirigir a la pantalla de inicio de sesión
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            // Continuar con el juego
            loadCategories();
        }

        //Seleccionar preguntas de categoria seleccionada
        categorySpinner = findViewById(R.id.categorySpinner);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                loadQuestions(categoryIds.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // No se ha seleccionado nada
            }
        });

        View rankingButton = findViewById(R.id.ranking_button);
        rankingButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RankingActivity.class);
            startActivity(intent);
        });
    }
    private void loadCategories() {
        String url = "https://opentdb.com/api_category.php";
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    // Procesar la respuesta y llenar el Spinner
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
            // Manejar error
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
            Map<String, Object> score = new HashMap<>();
            score.put("correctAnswers", correctAnswers);
            score.put("timestamp", FieldValue.serverTimestamp());

            db.collection("scores").document(userId)
                    .set(score)
                    .addOnSuccessListener(aVoid -> {
                        ResultFragment resultFragment = new ResultFragment(correctAnswers);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragmentContainer, resultFragment)
                                .commit();
                    })
                    .addOnFailureListener(e -> {
                        // Manejar error
                        e.printStackTrace();
                    });
        }
    }
}