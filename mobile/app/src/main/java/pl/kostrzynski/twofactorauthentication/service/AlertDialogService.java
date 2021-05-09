package pl.kostrzynski.twofactorauthentication.service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import com.google.android.material.textfield.TextInputLayout;
import pl.kostrzynski.twofactorauthentication.R;
import pl.kostrzynski.twofactorauthentication.model.QRPayload;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class AlertDialogService {

    public AlertDialog createBuilder(Context context, Activity activity, QRPayload qrPayload,
                                     String title, String positiveButtonString,
                                     BiConsumer<QRPayload, String> method) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        @SuppressLint("InflateParams")
        View view = activity.getLayoutInflater().inflate(R.layout.layout_dialog, null);
        builder.setTitle(title).setView(view);
        builder.setPositiveButton(positiveButtonString, null)
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(dialogInterface -> {
            Button button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view1 -> {
                TextInputLayout passwordInputLayout = view.findViewById(R.id.password_text_input);
                String password = passwordInputLayout.getEditText().getText().toString().trim();

                String errorMessage = getErrorMessage(view, password);
                if (errorMessage.equals("")) {
                    method.accept(qrPayload, password);
                    alertDialog.dismiss();
                } else passwordInputLayout.setError(errorMessage);
            });
        });
        return alertDialog;
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

    private String getErrorMessage(View view, String password) {
        TextInputLayout passwordRepeatInputLayout = view.findViewById(R.id.repeat_password_text_input);
        String repeatPassword = passwordRepeatInputLayout.getEditText().getText().toString().trim();

        String errorMessage = "";
        if (!password.equals(repeatPassword)) {
            passwordRepeatInputLayout.setError("Both input values must be the same");
            errorMessage = "Both input values must be the same";
        } else if (password.length() < 8) errorMessage = "Password must be at least 9 characters long";
        else if (password.length() > 59) errorMessage = "Password can't be longer then 60 characters";
        else if (!Pattern.compile(".*\\d.*").matcher(password).matches())
            errorMessage = "Password must contain at least one digit";
        else if (!Pattern.compile(".*[a-z].*").matcher(password).matches())
            errorMessage = "Password must contain at least 1 lower case letter";
        else if (!Pattern.compile(".*[A-Z].*").matcher(password).matches())
            errorMessage = "Password must contain at least 1 upper case letter";
        else if (!Pattern.compile(".*[@#$%^&+()'=].*").matcher(password).matches())
            errorMessage = "Password must contain at least 1 special character from @#$%^&+()'=";
        else if (!Pattern.compile("\\S+$").matcher(password).matches())
            errorMessage = "Password must not contain whitespaces";
        return errorMessage;
    }
}
