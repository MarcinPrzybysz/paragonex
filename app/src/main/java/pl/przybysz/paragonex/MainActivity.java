package pl.przybysz.paragonex;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;

import pl.przybysz.paragonex.dto.ParcelableString;
import pl.przybysz.paragonex.dto.Receipt;
import pl.przybysz.paragonex.photo_view.PhotoViewFragment;
import pl.przybysz.paragonex.receipt.ReceiptFragment;
import pl.przybysz.paragonex.receipt_list.ReceiptListFragment;

public class MainActivity extends AppCompatActivity implements ICommunicator {

    ActionBarDrawerToggle toggle;
    final String RECEIPT = "paragonex.receipt";
    final String PHOTO_VIEW = "paragonex.photo_view";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        NavigationView navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(menuItem -> {

            switch (menuItem.getItemId()) {
                case R.id.nav_add_new:
                    ReceiptFragment addNewFragment = new ReceiptFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, addNewFragment).commit();
                    break;
                case R.id.nav_list:
                    ReceiptListFragment receiptListFragment = new ReceiptListFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, receiptListFragment).commit();
                    break;
                case R.id.nav_share:
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "Paragonex- super aplikacja do paragonów, polecam.");
                    sendIntent.setType("text/plain");

                    Intent shareIntent = Intent.createChooser(sendIntent, null);
                    startActivity(shareIntent);
                    break;
            }
            menuItem.setChecked(false);
            drawer.closeDrawers();
            return true;
        });

        ReceiptListFragment receiptListFragment = new ReceiptListFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, receiptListFragment).commit();

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void passDataToReceiptList() {
        Bundle bundle = new Bundle();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        ReceiptListFragment receiptListFragment = new ReceiptListFragment();

        receiptListFragment.setArguments(bundle);

        transaction.replace(R.id.fragment_container, receiptListFragment).commit();
    }


    @Override
    public void passDataToReceipt(Receipt receipt) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(RECEIPT, receipt);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        ReceiptFragment receiptFragment = new ReceiptFragment();

        receiptFragment.setArguments(bundle);

        transaction.replace(R.id.fragment_container, receiptFragment).addToBackStack(RECEIPT).commit();
    }

    @Override
    public void openPhotoView(ParcelableString photoPath) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(PHOTO_VIEW, photoPath);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        PhotoViewFragment photoViewFragment = new PhotoViewFragment();

        photoViewFragment.setArguments(bundle);

        transaction.replace(R.id.fragment_container, photoViewFragment).addToBackStack(PHOTO_VIEW).commit();
    }

}