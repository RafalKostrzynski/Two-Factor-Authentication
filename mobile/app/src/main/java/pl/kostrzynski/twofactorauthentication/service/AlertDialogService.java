package pl.kostrzynski.twofactorauthentication.service;

import android.app.AlertDialog;
import android.content.Context;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class AlertDialogService {

    public AlertDialog createBuilder(Context context, String qrMessage, String message,
                                     String title, String positiveButtonString,
                                     Consumer<String> method) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setTitle(title);
        builder.setPositiveButton(positiveButtonString,
                (dialog, which) -> method.accept(qrMessage));
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        return builder.create();
    }

    public AlertDialog createBuilder(Context context, String qrMessage, String message,
                                     String title, String positiveButtonString, boolean isPostMethod,
                                     BiConsumer<String, Boolean> method) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setTitle(title);
        builder.setPositiveButton(positiveButtonString,
                (dialog, which) -> method.accept(qrMessage, isPostMethod));
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        return builder.create();
    }

    public AlertDialog createBuilder(Context context, String qrMessage,
                                     String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(qrMessage);
        builder.setTitle(title);
        builder.setNegativeButton("Close", (dialog, which) -> dialog.dismiss());
        return builder.create();
    }
}
