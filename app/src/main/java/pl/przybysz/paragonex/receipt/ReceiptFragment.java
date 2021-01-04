package pl.przybysz.paragonex.receipt;

import android.Manifest;
import android.app.Activity;
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
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

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

    private ReceiptModel model;
    private ReceiptService service;
    private Receipt originalModel;

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
        service = new ReceiptService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        model = new ReceiptModel();
        View view = setComponents(inflater, container);
        setListeners();
        loadData();
        return view;
    }


    private void loadPhotoFromDB(Receipt receipt) {
        try {
            File photoFile = createImageFile();
            service.readReceiptFile(model.imageButton, photoFile, service.USER_ID, receipt.getId());
        } catch (IOException ex) {
            ex.printStackTrace();
            Toast.makeText(getActivity(), "Błąd pobierania zdjęcia", Toast.LENGTH_SHORT).show();
        }
    }

    private View setComponents(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_receipt, container, false);
        communicator = (ICommunicator) getActivity();

        model.shop = view.findViewById(R.id.tv_shop);
        model.price = view.findViewById(R.id.tv_price);
        model.description = view.findViewById(R.id.tv_description);
        model.addBtn = view.findViewById(R.id.floating_button_add);
        model.deleteBtn = view.findViewById(R.id.delete_button);
        model.addPhotoBtn = view.findViewById(R.id.add_photo);
        model.imageButton = view.findViewById(R.id.image_button);
        model.datePicker = view.findViewById(R.id.datePicker);
        model.category = view.findViewById(R.id.spinner_category);

        model.category.setAdapter(new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, ReceiptCategory.values()));
        return view;
    }

    private void setListeners() {
        model.addBtn.setOnClickListener(view1 -> {
            if (verifyBeforeSave()) {
                Receipt receipt = service.upsertReceipt(getDtoFromEditors());
                if (mCurrentPhotoPath != null) {
                    service.saveReceiptFile(mCurrentPhotoPath, service.USER_ID, receipt.getId());
                }
                communicator.openReceiptList();
            }
        });

        model.deleteBtn.setOnClickListener(view1 -> {
            service.deleteReceipt(originalModel.getId());
            communicator.openReceiptList();
        });

        model.addPhotoBtn.setOnClickListener(view1 -> {
            if (verifyStoragePermissions(getActivity())) {
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                takePicture.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                File photoFile;
                try {
                    photoFile = createImageFile();
                    Uri photoURI = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", photoFile);
                    takePicture.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePicture, REQUEST_IMAGE_CAPTURE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        model.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentPhotoPath != null) {
                    communicator = (ICommunicator) getActivity();
                    communicator.openPhotoView(new ParcelableString(mCurrentPhotoPath));
                }
            }
        });

    }

    private void loadData() {
        if (getArguments() != null) {
            loadDataFromParcel();
        } else {
            originalModel = new Receipt();
        }
    }

    private void loadDataFromParcel() {
        assert getArguments() != null;
        originalModel = getArguments().getParcelable(RECEIPT);
        if (originalModel != null) {
            loadFromDto(originalModel);
            if (verifyStoragePermissions(getActivity())) {
                loadPhotoFromDB(originalModel);
            }
            model.deleteBtn.setVisibility(View.VISIBLE);
        } else {
            originalModel = new Receipt();
        }
    }

    private void loadFromDto(Receipt receipt) {
        model.shop.setText(receipt.getShop() != null ? receipt.getShop() : "");
        model.price.setText(receipt.getPrice() != null ? receipt.getPrice().toString() : "0");
        model.description.setText(receipt.getDescription() != null ? receipt.getDescription() : "");
        if (receipt.getDate() != null) {
            LocalDate date = Instant.ofEpochMilli(receipt.getDate()).atZone(ZoneId.systemDefault()).toLocalDate();
            model.datePicker.updateDate(date.getYear(), date.getMonth().getValue(), date.getDayOfMonth());
        }

        List categories = Arrays.asList(ReceiptCategory.values());
        try {
            int index = categories.indexOf(ReceiptCategory.getEnumForLabel(receipt.getCategory()));
            model.category.setSelection(index);
        } catch (UnsupportedOperationException ex) {
            model.category.setSelection(categories.indexOf(ReceiptCategory.EMPTY));
        }
    }

    private Receipt getDtoFromEditors() {
        Receipt receipt = new Receipt();
        receipt.setId(originalModel.getId());
        receipt.setShop(model.shop.getText().toString());
        receipt.setDescription(model.description.getText().toString());
        receipt.setPrice(Double.valueOf(model.price.getText().toString()));

        LocalDate localDate = LocalDate.of(model.datePicker.getYear(), model.datePicker.getMonth() + 1, model.datePicker.getDayOfMonth());
        Long dateInMillis = localDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
        receipt.setDate(dateInMillis);

        receipt.setCategory(model.category.getSelectedItem().toString());

        return receipt;
    }

    private boolean verifyBeforeSave() {
        if (TextUtils.isEmpty(model.shop.getText())) {
            model.shop.requestFocus();
            Toast.makeText(getActivity(), "należy uzupełnić sklep", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(model.price.getText())) {
            model.price.requestFocus();
            Toast.makeText(getActivity(), "należy uzupełnić cenę", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(model.description.getText())) {
            model.category.requestFocus();
            Toast.makeText(getActivity(), "należy uzupełnić opis", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                try {
                    mImageBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Uri.parse(mCurrentPhotoPath));
                    model.imageButton.setImageBitmap(mImageBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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
        } else {
            return true;
        }
    }

}