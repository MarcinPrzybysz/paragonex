package pl.przybysz.paragonex;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;

import pl.przybysz.paragonex.add_new.AddNewFragment;
import pl.przybysz.paragonex.receipt_list.ReceiptListFragment;

public class MainActivity extends AppCompatActivity implements ICommunicator {

    ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                    AddNewFragment addNewFragment = new AddNewFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, addNewFragment).commit();
                    break;
                case R.id.nav_list:
                    ReceiptListFragment receiptListFragment = new ReceiptListFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, receiptListFragment).commit();
                    break;
                case R.id.nav_share:
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
    public void passDataCom(String editTextInput) {
        Bundle bundle = new Bundle();

        bundle.putString("message", editTextInput);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        ReceiptListFragment receiptListFragment = new ReceiptListFragment();

        receiptListFragment.setArguments(bundle);

//        transaction.replace(R.id.fragment_container, receiptListFragment).commit();


    }
}