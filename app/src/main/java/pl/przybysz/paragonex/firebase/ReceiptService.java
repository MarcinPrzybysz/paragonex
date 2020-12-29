package pl.przybysz.paragonex.firebase;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageButton;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.List;

import pl.przybysz.paragonex.dto.Receipt;
import pl.przybysz.paragonex.receipt_list.ReceiptListAdapter;

import static android.content.ContentValues.TAG;

public class ReceiptService {
    public final String USER_ID = "test_user_id";
    private DatabaseReference mDatabase;
    private StorageReference mStorageRef;


    public ReceiptService() {
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
        this.mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    public Receipt upsertReceipt(Receipt receipt) {
        if (receipt == null) return null;

        if (receipt.getId() == null) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users/" + USER_ID + "/receipts").push();
            receipt.setId(ref.getKey());
        }

        DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(USER_ID).collection("receipts").document(receipt.getId());


        docRef.set(receipt).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "DocumentSnapshot successfully written!");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
        return receipt;
    }


    public void deleteReceipt(String id) {
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(USER_ID).collection("receipts").document(id);
        docRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });

    }


    public void readAllReceipt(List<Receipt> receipts, ReceiptListAdapter adapter) {
        FirebaseFirestore.getInstance().collection("users").document(USER_ID).collection("receipts")
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                receipts.add(document.toObject(Receipt.class));
                                adapter.notifyDataSetChanged();
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        adapter.refreshFilterSourceObjects();
                    }
                });
    }

    public void saveReceiptFile(String fileUri, String userId, String receiptId) {
        StorageReference storageReference = mStorageRef.child("images/users/" + userId + "/" + receiptId);
        storageReference.putFile(Uri.parse(fileUri));
    }

    public void readReceiptFile(ImageButton imageButton, File file, String userId, String receiptId) {
        StorageReference storageReference = mStorageRef.child("images/users/" + userId + "/" + receiptId);
        storageReference.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
                imageButton.setImageBitmap(bitmap);
            }
        });

    }

}
