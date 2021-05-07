package pl.kostrzynski.twofactorauthentication.service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.EditText;
import pl.kostrzynski.twofactorauthentication.R;
import pl.kostrzynski.twofactorauthentication.model.QRPayload;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class AlertDialogService {

    public AlertDialog createBuilder(Context context, Activity activity, QRPayload qrPayload,
                                     String title, String positiveButtonString,
                                     BiConsumer<QRPayload, String> method) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        @SuppressLint("InflateParams")
        View view = activity.getLayoutInflater().inflate(R.layout.layout_dialog, null);
        builder.setTitle(title).setView(view);
        builder.setPositiveButton(positiveButtonString, (dialog, which) ->
                setPassword(context, activity, qrPayload, positiveButtonString, method, view))
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        return builder.create();
    }

    private void setPassword(Context context, Activity activity, QRPayload qrPayload, String positiveButtonString, BiConsumer<QRPayload, String> method, View view) {
        EditText passwordEditText = view.findViewById(R.id.editTextPassword);
        EditText passwordRepeatEditText = view.findViewById(R.id.editTextRepeatedPassword);
        String password = passwordEditText.getText().toString().trim();
        String repeatPassword = passwordRepeatEditText.getText().toString().trim();

        if (password.equals(repeatPassword) && password.length() >= 9 && password.length() <= 60)
            method.accept(qrPayload, password);
        else createBuilder(context, activity, qrPayload,
                "Wrong input try again", positiveButtonString, method).show();
    }

    public AlertDialog createBuilder(Context context, QRPayload qrPayload, String message,
                                     String title, String positiveButtonString,
                                     Consumer<QRPayload> method) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message).setTitle(title)
                .setPositiveButton(positiveButtonString, (dialog, which) -> method.accept(qrPayload));
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        return builder.create();
    }

    public AlertDialog createBuilder(Context context, String qrMessage, String message,
                                     String title, String positiveButtonString, boolean isPostMethod,
                                     BiConsumer<String, Boolean> method) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message).setTitle(title)
                .setPositiveButton(positiveButtonString, (dialog, which) -> method.accept(qrMessage, isPostMethod));
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        return builder.create();
    }

    public AlertDialog createBuilder(Context context, String qrMessage,
                                     String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(qrMessage).setTitle(title)
                .setNegativeButton("Close", (dialog, which) -> dialog.dismiss());
        return builder.create();
    }
}
