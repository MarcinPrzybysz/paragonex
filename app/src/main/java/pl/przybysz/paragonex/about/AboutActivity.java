package pl.przybysz.paragonex.about;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

import pl.przybysz.paragonex.R;

public class AboutActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }
}
