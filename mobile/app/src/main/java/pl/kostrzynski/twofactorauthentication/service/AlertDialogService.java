package pl.kostrzynski.twofactorauthentication.service;

import android.app.AlertDialog;
import android.content.Context;
import pl.kostrzynski.twofactorauthentication.model.QRPayload;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class AlertDialogService {

    public AlertDialog createBuilder(Context context, String qrMessage, String message,
                                     String title, String positiveButtonString,
                                     Consumer<String> method) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message).setTitle(title)
                .setPositiveButton(positiveButtonString, (dialog, which) -> method.accept(qrMessage))
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        return builder.create();
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
