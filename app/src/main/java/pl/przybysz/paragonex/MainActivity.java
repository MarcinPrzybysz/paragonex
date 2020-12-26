package pl.przybysz.paragonex;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;

import pl.przybysz.paragonex.dto.Receipt;
import pl.przybysz.paragonex.receipt.ReceiptFragment;
import pl.przybysz.paragonex.receipt_list.ReceiptListFragment;

public class MainActivity extends AppCompatActivity implements ICommunicator {

    ActionBarDrawerToggle toggle;
    final String RECEIPT = "paragonex.receipt";
    final String RECEIPT_LIST = "paragonex.receipt_list";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);

        //wysuwane menu
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //when toggle is opened and we click back toggle menu will close
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
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "PAragonex- super aplikacja do paragonów, polecam.");
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

    //todo: metoda do wymiany danych powmiędzy fragmentami
    @Override
    public void passDataToReceiptList() {
        Bundle bundle = new Bundle();

//        bundle.putString(RECEIPT_LIST, receipt);
//        bundle.putParcelable(RECEIPT_LIST, receipt);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        ReceiptListFragment receiptListFragment = new ReceiptListFragment();

        receiptListFragment.setArguments(bundle);

        transaction.replace(R.id.fragment_container, receiptListFragment).commit();
    }


    @Override
    public void passDataToReceipt(Receipt receipt) {
        Bundle bundle = new Bundle();
//        bundle.putString(RECEIPT, receipt);
        bundle.putParcelable(RECEIPT, receipt);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        ReceiptFragment receiptFragment = new ReceiptFragment();

        receiptFragment.setArguments(bundle);

        transaction.replace(R.id.fragment_container, receiptFragment).commit();
    }

}