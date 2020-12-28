package pl.przybysz.paragonex.photo_view;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.IOException;

import pl.przybysz.paragonex.R;
import pl.przybysz.paragonex.dto.ParcelableString;

public class PhotoViewFragment extends Fragment {
    final String PHOTO_VIEW = "paragonex.photo_view";
    ImageView imageView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_view, container, false);
        imageView = view.findViewById(R.id.imageView);

        if (getArguments() != null) {
            ParcelableString photoPath = getArguments().getParcelable(PHOTO_VIEW);

            try {
                Bitmap photo = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Uri.parse(photoPath.getValue()));
                imageView.setImageBitmap(photo);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        return view;
    }
}
