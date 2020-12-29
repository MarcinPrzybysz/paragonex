package pl.przybysz.paragonex.receipt;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import pl.przybysz.paragonex.ICommunicator;
import pl.przybysz.paragonex.R;
import pl.przybysz.paragonex.dto.ParcelableString;
import pl.przybysz.paragonex.dto.Receipt;
import pl.przybysz.paragonex.dto.ReceiptCategory;
import pl.przybysz.paragonex.firebase.ReceiptService;

import static android.app.Activity.RESULT_OK;

public class ReceiptFragment extends Fragment {

    private ICommunicator communicator;

    FloatingActionButton addBtn;
    ImageButton deleteBtn;
    ImageButton addPhotoBtn;
    ImageButton imageButton;
    StorageReference mStorageRef;
    EditText shop;
    EditText price;
    EditText description;
    Spinner category;
    DatePicker datePicker;
    ReceiptService service;
//    ImageView imageView;

    boolean isImageFitToScreen= true;

    Receipt originalModel;
    public static final int REQUEST_IMAGE_CAPTURE = 3;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private Bitmap mImageBitmap;
    private String mCurrentPhotoPath;

    final String RECEIPT = "paragonex.receipt";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        service = new ReceiptService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = setComponents(inflater, container);
        setListeners();
        loadData();
        return view;
    }


    private void loadPhotoFromDB(Receipt receipt) {
        try {
            File photoFile = createImageFile();
            service.readReceiptFile(imageButton, photoFile, service.USER_ID, receipt.getId());
        } catch (IOException ex) {
            ex.printStackTrace();
            Toast.makeText(getActivity(), "Błąd pobierania zdjęcia", Toast.LENGTH_SHORT).show();
        }
    }

    private View setComponents(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_receipt, container, false);
        communicator = (ICommunicator) getActivity();

        shop = view.findViewById(R.id.tv_shop);
        price = view.findViewById(R.id.tv_price);
        description = view.findViewById(R.id.tv_description);
        addBtn = view.findViewById(R.id.floating_button_add);
        deleteBtn = view.findViewById(R.id.delete_button);
        addPhotoBtn = view.findViewById(R.id.add_photo);
        imageButton = view.findViewById(R.id.image_button);
        datePicker = view.findViewById(R.id.datePicker);
        category = view.findViewById(R.id.spinner_category);

        category.setAdapter(new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, ReceiptCategory.values()));
        return view;
    }

    private void setListeners() {
        addBtn.setOnClickListener(view1 -> communicator.passDataToReceiptList());

        addBtn.setOnClickListener(view1 -> {
            if (verifyBeforeSave()) {
                Receipt receipt = service.upsertReceipt(getDtoFromEditors());
                if (mCurrentPhotoPath != null) {
                    service.saveReceiptFile(mCurrentPhotoPath, service.USER_ID, receipt.getId());
                }
                communicator.passDataToReceiptList(); //todo zmienić
            }
        });

        deleteBtn.setOnClickListener(view1 -> {
            service.deleteReceipt(originalModel.getId());
            communicator.passDataToReceiptList(); //todo zmienić
        });

        addPhotoBtn.setOnClickListener(view1 -> {
            if(verifyStoragePermissions(getActivity())){
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                takePicture.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                File photoFile;
                try {
                    photoFile = createImageFile();
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", photoFile);
                        takePicture.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePicture, REQUEST_IMAGE_CAPTURE);

                    }
                } catch (ActivityNotFoundException e) {

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCurrentPhotoPath != null){
                    communicator = (ICommunicator) getActivity();
                    communicator.openPhotoView(new ParcelableString(mCurrentPhotoPath));
                }
            }
        });

    }

    //todo czemu tu w ogole jest pobieranie z bazy całego Receipt jak dostaliśmy je z listy..
    private void loadData() {
        if (getArguments() != null) {
            originalModel = getArguments().getParcelable(RECEIPT);
            if (originalModel != null) {
                loadFromDto(originalModel);
                if(verifyStoragePermissions(getActivity())) {
                    loadPhotoFromDB(originalModel);
                }
                deleteBtn.setVisibility(View.VISIBLE);
            } else {
                originalModel = new Receipt();
            }
        } else {
            originalModel = new Receipt();
        }
    }

    private void loadFromDto(Receipt receipt) {
        shop.setText(receipt.getShop() != null ? receipt.getShop() : "");
        price.setText(receipt.getPrice() != null ? receipt.getPrice().toString() : "0");
        description.setText(receipt.getDescription() != null ? receipt.getDescription() : "");
        if (receipt.getDate() != null) {
            LocalDate date = Instant.ofEpochMilli(receipt.getDate()).atZone(ZoneId.systemDefault()).toLocalDate();
            datePicker.updateDate(date.getYear(), date.getMonth().getValue(), date.getDayOfMonth());
        }

        List categories = Arrays.asList(ReceiptCategory.values());
        try {
            int index = categories.indexOf(ReceiptCategory.getEnumForLabel(receipt.getCategory()));
            category.setSelection(index);
        } catch (UnsupportedOperationException ex) {
            category.setSelection(categories.indexOf(ReceiptCategory.EMPTY));
        }
    }

    private Receipt getDtoFromEditors() {
        Receipt receipt = new Receipt();
        receipt.setId(originalModel.getId());
        receipt.setShop(shop.getText().toString());
        receipt.setDescription(description.getText().toString());
        receipt.setPrice(Double.valueOf(price.getText().toString()));

        LocalDate localDate = LocalDate.of(datePicker.getYear(), datePicker.getMonth() + 1, datePicker.getDayOfMonth());
        Long dateInMillis = localDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
        receipt.setDate(dateInMillis);

        receipt.setCategory(category.getSelectedItem().toString());

        return receipt;
    }

    private boolean verifyBeforeSave() {
        if (TextUtils.isEmpty(shop.getText())) {
            shop.requestFocus();
            Toast.makeText(getActivity(), "należy uzupełnić sklep", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(price.getText())) {
            price.requestFocus();
            Toast.makeText(getActivity(), "należy uzupełnić cenę", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(description.getText())) {
            category.requestFocus();
            Toast.makeText(getActivity(), "należy uzupełnić opis", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE:
                if (resultCode == RESULT_OK) {
                    try {
                        mImageBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Uri.parse(mCurrentPhotoPath));
                        imageButton.setImageBitmap(mImageBitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }


    private File createImageFile() throws IOException {


        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }


    public static boolean verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
            permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return permission == PackageManager.PERMISSION_GRANTED;
        }else{
            return true;
        }
    }


}