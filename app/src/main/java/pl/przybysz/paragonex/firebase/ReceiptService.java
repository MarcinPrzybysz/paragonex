package pl.przybysz.paragonex.firebase;

import android.util.Log;
import android.widget.BaseAdapter;

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

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.przybysz.paragonex.dto.Receipt;

import static android.content.ContentValues.TAG;

public class ReceiptService {
    //na razie stała później w zalezności jaki user
    private final String USER_DB_PATH = "user/test_user/";
    private DatabaseReference mDatabase;


    public ReceiptService() {
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
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


    public void saveUser(Object object) {
        //nadpisuje dane
        mDatabase.child("user").child("test_user_id").setValue(object);
    }


    public void updateUser() {
        //wchodzimy i bierzemy childa
        mDatabase.child("user").child("test_user_id").child("username").setValue("nowe imie");
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


    public void upsertReceipt(Receipt receipt) {
        if (receipt == null) return;

        if (receipt.getId() == null) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users/test_user_id/receipts").push();
            receipt.setId(ref.getKey());
        }

        DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document("test_user_id").collection("receipts").document(receipt.getId());


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

    }


    public void deleteReceipt(String id){
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document("test_user_id").collection("receipts").document(id);
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
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document("test_user_id").collection("receipts").document(id);
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


    public void readAllReceipt(List<Receipt> receipts, BaseAdapter adapter) {
        FirebaseFirestore.getInstance().collection("users").document("test_user_id").collection("receipts")
                .orderBy("date")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
//                            ArrayList<Receipt> receipts = new ArrayList();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                receipts.add(document.toObject(Receipt.class));
                                adapter.notifyDataSetChanged();
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
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
