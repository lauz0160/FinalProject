package com.example.finalproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.widget.Toolbar;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

/**
 after selecting an article, see details and save/delete buttons. get confirmation popups
 */

public class GuardianNewsList extends AppCompatActivity {

    News news;
    CoordinatorLayout coordinatorLayout;
    Guardian_DataBase db;
    Snackbar snackbar;
    Button saveBtn;
    Button deleteBtn;

    /**
     * @param savedInstanceState
     *  is called when the activity first starts up. brings up buttons, toolbar, title, section,
     *  url, textviews, etc
     */
    //https://www.codota.com/code/java/methods/android.content.Intent/getSerializableExtra
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guardian_selected);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.article_details);
        news = (News) getIntent().getSerializableExtra("article");
        String id = news.getId();
        String header = news.getWebTitle();
        String section = news.getSectionName();
        String url = news.getWebUrl();
        coordinatorLayout = findViewById(R.id.coordinatorLayout);

        TextView enterSearch = findViewById(R.id.textView);
        enterSearch.setText(header);
        TextView resultSection = findViewById(R.id.textView2);
        resultSection.setText(section);
        TextView resultUrl = findViewById(R.id.textView12);
        resultUrl.setText(url);

        saveBtn = findViewById(R.id.button);
        deleteBtn = findViewById(R.id.button2);
        deleteBtn.setText(R.string.delete);
        saveBtn.setText(R.string.save);

        db = new Guardian_DataBase(this);

        saveBtn.setOnClickListener(v -> {
                    showAlertDialogSave(id);
                    db.saveDetails(news);
                    snackbar.make(coordinatorLayout, getString(R.string.saved), Snackbar.LENGTH_LONG)
                            .setActionTextColor(Color.RED)
                            .show();
                    saveBtn.setText(R.string.save);
                }

        );
        deleteBtn.setOnClickListener(v -> {
                    showAlertDialogDelete(id);
                    db.delete(id);
                    snackbar.make(coordinatorLayout, getString(R.string.delete), Snackbar.LENGTH_LONG)
                            .setActionTextColor(Color.RED)
                            .show();
                    deleteBtn.setText(R.string.delete);
                }
        );
    }

    /**
     * @param id
     * confirmation dialog alerts
     */
    private void showAlertDialogSave(final String id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton(getString(R.string.article_save_confirm), (dialog, which) -> {
            db.saveDetails(news);
            Snackbar.make(coordinatorLayout, getString(R.string.add), Snackbar.LENGTH_LONG)
                    .show();
            dialog.dismiss();
        });
        builder.setNegativeButton(getString(R.string.article_save_cancel), (dialog, which) -> dialog.dismiss());
        builder.setTitle(getString(R.string.save));
        builder.setMessage(getString(R.string.article_save));
        builder.show();
    }

    private void showAlertDialogDelete(final String id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton(getString(R.string.article_delete_confirm), (dialog, which) -> {
            db.delete(id);
            Snackbar.make(coordinatorLayout, getString(R.string.remove), Snackbar.LENGTH_LONG)
                    .show();
            dialog.dismiss();
        });

        builder.setNegativeButton(getString(R.string.article_delete_cancel), (dialog, which) -> dialog.dismiss());
        builder.setTitle(getString(R.string.delete));
        builder.setMessage(getString(R.string.article_delete));
        builder.show();
    }


}