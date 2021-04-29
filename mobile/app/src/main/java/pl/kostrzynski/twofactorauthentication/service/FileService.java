package pl.kostrzynski.twofactorauthentication.service;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import java.io.*;
import java.security.interfaces.ECPrivateKey;

public class FileService {

    public String findFileNameFromString(String path) {
        try {
            return path.contains("/") ?
                    path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf(".")) : "";
        } catch (Exception e) {
            return "";
        }
    }

    public byte[] getPrivateKeyBytes(String path, Context context) {
        try{
        // TODO provide storage access framework, look into content values
//        File file = new File(path);
//
//        FileInputStream fileInputStream = new FileInputStream(file);
//        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
//        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        StringBuilder myData = new StringBuilder();
            try {
                FileInputStream fis = new FileInputStream(path);
                DataInputStream in = new DataInputStream(fis);
                BufferedReader br =
                        new BufferedReader(new InputStreamReader(in));
                String strLine;
                while ((strLine = br.readLine()) != null) {
                    myData.append(strLine);
                }
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        byte[] privateKeyBytes = null; // TODO handle reading bytes
        ECCService eccService = new ECCService();
        //return eccService.getPrivateKeyFromBytes(privateKeyBytes);
        return null;
        // TODO set key
    } catch (Exception e) {
        Toast.makeText(context, "Something went wrong, Please try again!", Toast.LENGTH_LONG).show();
        return null;
    }
    }

    public File saveKeysToStorage(ECCService eccService, ECPrivateKey privateKey, Context context) throws IOException {
        File path = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        // TODO maybe create random name for the file
        File privateKeyFile = new File(path, "private.key");
        try (FileOutputStream privateKeyOutput = new FileOutputStream(privateKeyFile)) {
            privateKeyOutput.write(eccService.getEncodedPrivateKey(privateKey));
        }
        return privateKeyFile;
    }
}
