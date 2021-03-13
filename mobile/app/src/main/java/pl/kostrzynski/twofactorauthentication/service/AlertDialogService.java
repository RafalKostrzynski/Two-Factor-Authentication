package pl.kostrzynski.twofactorauthentication.service;

import android.app.AlertDialog;
import android.content.Context;

import java.util.function.Consumer;

public class AlertDialogService {

    public AlertDialog createBuilder(Context context, String qrMessage,
                                      String title, String positiveButtonString,
                                      Consumer<String> method) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(qrMessage);
        builder.setTitle(title);
        builder.setPositiveButton(positiveButtonString,
                (dialog, which) -> method.accept(qrMessage));
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
