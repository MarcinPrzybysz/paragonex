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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.przybysz.paragonex.dto.Receipt;
import pl.przybysz.paragonex.receipt_list.ReceiptListAdapter;

import static android.content.ContentValues.TAG;

public class ReceiptService {
    //na razie stała później w zalezności jaki user
    private final String USER_DB_PATH = "user/test_user/";
    public final String USER_ID = "test_user_id";
    private DatabaseReference mDatabase;
    private StorageReference mStorageRef;


    public ReceiptService() {
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
        this.mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    public void addCollection(Object object, String collectionPath) {

        DocumentReference mDocRef = FirebaseFirestore.getInstance().document(USER_DB_PATH + collectionPath);
        mDocRef.set(object).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error adding document", e);
            }
        });
    }


    public void addReceipt() {

        mDatabase.child("user").child("user_test");
        String key = mDatabase.child("receipts").push().getKey();
        Receipt receipt = new Receipt();
        receipt.setPrice(33.4);
        receipt.setCategory("test");
        receipt.setDescription("opis");
        Map<String, Object> receiptMap = new HashMap<>();
        ;
        try {
//            receiptMap =  pojoToMap(receipt);
            receiptMap.put("price", 2.33);
            receiptMap.put("descriptoin", "opis 123");
            receiptMap.put("category", "Spożywcze");

        } catch (Exception e) {

        }

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/receipts/" + key, receiptMap);

        mDatabase.updateChildren(childUpdates);
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

    public void readOneReceipt(String id) {
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(USER_ID).collection("receipts").document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Receipt receipt = document.toObject(Receipt.class);
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }


    public void readAllReceipt(List<Receipt> receipts, ReceiptListAdapter adapter) {
        FirebaseFirestore.getInstance().collection("users").document(USER_ID).collection("receipts")
                .orderBy("date")
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


    //    metoda nie obsługuje przypadku gdy obiekt ma w sobie inny obiekt/kolekcję
    private Map<String, Object> pojoToMap(Object object) throws IllegalAccessException {
        HashMap<String, Object> map = new HashMap<String, Object>();

        Field[] allFields = object.getClass().getDeclaredFields();
        for (Field field : allFields) {
            field.setAccessible(true);
            Object value = field.get(object);
            map.put(field.getName(), value);
        }
        return map;
    }


}
