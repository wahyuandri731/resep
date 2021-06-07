package fragment;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dhiren.reseplebaran.R;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.squareup.picasso.Picasso;

import activity.MainFragmentActivity;

import static activity.UnitId.INTERADS;

public class RecipeFragment extends Fragment {
    private AppCompatActivity appCompatActivity;
    private View mView;
    private InterstitialAd mInterstitialAd;

    public static RecipeFragment newInstance() {
        return new RecipeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView =  inflater.inflate(R.layout.recipe_fragment, container, false);

        ((ImageButton) mView.findViewById(R.id.btn_back)).setOnClickListener(v -> {
            appCompatActivity.getSupportFragmentManager().popBackStack();
        });
        if (MainFragmentActivity.recipeModel.imgUrl.length() > 0)
            Picasso.get().load(MainFragmentActivity.recipeModel.imgUrl)
                .into((ImageView) mView.findViewById(R.id.img));
        String ingredientsHtml = MainFragmentActivity.recipeModel.ingredients.replace("\n","<br>");
        String howToHtml = MainFragmentActivity.recipeModel.howto.trim().replace("\n","<br>");
        howToHtml = howToHtml.replaceAll("\\P{Print}", "");
        ingredientsHtml = ingredientsHtml.replaceAll("\\P{Print}", "");
        String allHtml = "<h1>" + MainFragmentActivity.recipeModel.recipeName +  "</h1>" + ingredientsHtml + "<br><br>" + howToHtml;
        allHtml = allHtml.replaceAll("<br><br><br><br>","<br><br>");
        allHtml = allHtml.replaceAll("<br><br><br>","<br><br>");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ((TextView) mView.findViewById(R.id.txt_howto)).setText(Html.fromHtml(allHtml, Html.FROM_HTML_MODE_LEGACY));
        } else
            ((TextView) mView.findViewById(R.id.txt_howto)).setText(Html.fromHtml(allHtml));
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        appCompatActivity = ((AppCompatActivity) getActivity());
        View toolbarView = appCompatActivity.findViewById(R.id.toolbar);
        toolbarView.setVisibility(View.GONE);

        RequestConfiguration configuration = new RequestConfiguration.Builder()
                .build();
        MobileAds.setRequestConfiguration(configuration);
        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        customInterestitial();
    }

    //INTERESTITIALADS
    public void customInterestitial(){
        AdRequest adRequest = new AdRequest.Builder().build();
        iklanInterestitial(adRequest);
    }

    public void iklanInterestitial(AdRequest adRequest){
        InterstitialAd.load(getActivity(),
                INTERADS,
                adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i("TAG", "onAdLoaded");
                        if (mInterstitialAd != null) {
                            mInterstitialAd.show(getActivity());
                        } else {
                            Log.d("TAG", "The interstitial ad wasn't ready yet.");
                        }

                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when fullscreen content is dismissed.
                                Log.d("TAG", "The ad was dismissed.");
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when fullscreen content failed to show.
                                Log.d("TAG", "The ad failed to show.");
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when fullscreen content is shown.
                                // Make sure to set your reference to null so you don't
                                // show it a second time.
                                mInterstitialAd = null;
                                Log.d("TAG", "The ad was shown.");
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i("TAG", loadAdError.getMessage());
                        mInterstitialAd = null;
                    }
                });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}