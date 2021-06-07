package activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.snackbar.Snackbar;
import com.dhiren.reseplebaran.R;

import java.util.Arrays;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import fragment.HomeFragment;
import model.RecipeModel;
import storage.RecipeBookXls;

import static android.provider.Settings.Secure.ANDROID_ID;


public class MainFragmentActivity extends AppCompatActivity {
    public static RecipeModel recipeModel;
    public static RecipeBookXls recipeBookXls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
        setContentView(R.layout.main_fragment_activity);
        findViewById(R.id.toolbar).setVisibility(View.GONE);

        RequestConfiguration configuration = new RequestConfiguration.Builder()
                .build();
        MobileAds.setRequestConfiguration(configuration);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });

        recipeBookXls = new RecipeBookXls(this);

        // Memulai transaksi
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, HomeFragment.newInstance());
        ft.commit();

    }



    public Fragment getVisibleFragment() {
        FragmentManager fragmentManager = MainFragmentActivity.this.getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment != null && fragment.isVisible())
                    return fragment;
            }
        }
        return null;
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        Fragment shownFragment = getVisibleFragment();
        if (shownFragment != null) {
            if (shownFragment instanceof HomeFragment) {
                if (doubleBackToExitPressedOnce) {
                    this.finishAffinity();
                }
                this.doubleBackToExitPressedOnce = true;
                Snackbar.make(this, findViewById(R.id.search),
                        "Tekan sekali lagi untuk keluar", Snackbar.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            }
            else {
                getSupportFragmentManager().popBackStack();
            }
        }
    }
}