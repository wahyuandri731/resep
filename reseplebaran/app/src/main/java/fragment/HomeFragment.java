package fragment;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dhiren.reseplebaran.R;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.ump.ConsentForm;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.FormError;
import com.google.android.ump.UserMessagingPlatform;

import java.util.ArrayList;

import activity.MainFragmentActivity;
import adapter.CategoryRvAdapter;
import adapter.PopularRvAdapter;
import helper.GridSpacingItemDecoration;
import helper.SuggestionAdapter;
import iface.IRecipeClick;
import iface.IRvItem;
import model.RecipeModel;

import static activity.UnitId.INTERADS;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private AppCompatActivity appCompatActivity;
    private SearchView mSearchView;
    protected View mView;
    private AdView adView;
    private int loadBannerAgain;
    private ConsentInformation consentInformation;
    private ConsentForm consentForm;


    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    private SuggestionAdapter suggestionAdapter;
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        appCompatActivity = (AppCompatActivity)getActivity();
        appCompatActivity.findViewById(R.id.toolbar).setVisibility(View.GONE);

        getContsenStatus();

        RequestConfiguration configuration = new RequestConfiguration.Builder()
                .build();
        MobileAds.setRequestConfiguration(configuration);
        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        AdView adView = (AdView) getView().findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        adView.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded(){
                Toast.makeText(getContext(), "", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError){
                Toast.makeText(getContext(),"" + adError.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onAdClicked(){
                Toast.makeText(getContext(),"ThankYou",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Cnfigure searchview
        SearchManager searchManager = (SearchManager) appCompatActivity.getApplicationContext().getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) mView.findViewById(R.id.search);
        mSearchView.setQuery("",true);
        mSearchView.setFocusable(true);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(appCompatActivity.getComponentName()));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                suggestionAdapter.changeCursor(MainFragmentActivity.recipeBookXls.searchRecipe(s));
//                new GetSuggestionsAsyncTask(MainActivity.this, s).execute();
                return false;
            }
        });
        mSearchView.setOnTouchListener((v, event) -> {
            mSearchView.setIconified(false);
            return  true;
        });
        suggestionAdapter = new SuggestionAdapter(appCompatActivity.getApplicationContext(), null,
                false, new IRecipeClick() {
            @Override
            public void click(RecipeModel recipeModel) {
                mSearchView.setQuery("",false);
                mSearchView.clearFocus();
                MainFragmentActivity.recipeModel =
                        MainFragmentActivity.recipeBookXls.getRecipe(recipeModel.categoryId, recipeModel.recipeId);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, RecipeFragment.newInstance(), "HOME");
                transaction.addToBackStack("SEARCH_RECIPE");
                transaction.commit();
            }
        });
        mSearchView.setSuggestionsAdapter(suggestionAdapter);

        mSearchView.setQueryHint("Cari resep");
        // TODO: Use the ViewModel
    }

    public void getContsenStatus(){
        // Set tag for underage of consent. false means users are not underage.
        ConsentRequestParameters params = new ConsentRequestParameters
                .Builder()
                .setTagForUnderAgeOfConsent(false)
                .build();

        consentInformation = UserMessagingPlatform.getConsentInformation(getActivity());
        consentInformation.requestConsentInfoUpdate(
                getActivity(),
                params,
                new ConsentInformation.OnConsentInfoUpdateSuccessListener() {
                    @Override
                    public void onConsentInfoUpdateSuccess() {
                        // The consent information state was updated.
                        // You are now ready to check if a form is available.
                        if (consentInformation.isConsentFormAvailable()) {
                            loadForm();
                        }
                    }
                },
                new ConsentInformation.OnConsentInfoUpdateFailureListener() {
                    @Override
                    public void onConsentInfoUpdateFailure(FormError formError) {
                        // Handle the error.
                    }
                });
    }

    public void loadForm() {
        UserMessagingPlatform.loadConsentForm(
                getActivity(),
                new UserMessagingPlatform.OnConsentFormLoadSuccessListener() {
                    @Override
                    public void onConsentFormLoadSuccess(ConsentForm consentForm) {
                        HomeFragment.this.consentForm = consentForm;
                        if(consentInformation.getConsentStatus() == ConsentInformation.ConsentStatus.REQUIRED) {
                            consentForm.show(
                                    getActivity(),
                                    new ConsentForm.OnConsentFormDismissedListener() {
                                        @Override
                                        public void onConsentFormDismissed(@Nullable FormError formError) {
                                            // Handle dismissal by reloading form.
                                            loadForm();
                                        }
                                    });

                        }

                    }
                },
                new UserMessagingPlatform.OnConsentFormLoadFailureListener() {
                    @Override
                    public void onConsentFormLoadFailure(FormError formError) {
                        /// Handle Error.
                    }
                }
        );
    }

    //BANNERADS
    @Override
    public void onDestroy(){
        if (adView !=null){
            adView.destroy();
        }
        super.onDestroy();
    }


    final int MAX_POPULAR_RECIPE_SHOWN = 10;
    final int MAX_HOME_CATEGORY_SHOWN = 3;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mView = view;

        ArrayList<RecipeModel> popRecipe = new ArrayList<>();
        int showPopular = MAX_POPULAR_RECIPE_SHOWN;
        while (showPopular > 0) {
            RecipeModel randomRecipe = MainFragmentActivity.recipeBookXls.getRandomRecipe();
            if (randomRecipe != null && !randomRecipe.recipeName.isEmpty() && !randomRecipe.imgUrl.isEmpty() &&
                    !randomRecipe.ingredients.isEmpty()) {
                popRecipe.add(randomRecipe);
                showPopular--;
            }
        }

        PopularRvAdapter rvAdapterPop = new PopularRvAdapter(popRecipe, getContext());
        rvAdapterPop.setRvItemHandler(new IRvItem() {
            @Override
            public void select(int index) {

            }

            @Override
            public void click(int index) {
                MainFragmentActivity.recipeModel = popRecipe.get(index);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, RecipeFragment.newInstance(), "HOME");
                transaction.addToBackStack("POP_RECIPE");
                transaction.commit();
            }
        });
        RecyclerView rvPop  = (RecyclerView) view.findViewById(R.id.rv_popular);
        rvPop.setLayoutManager(new GridLayoutManager(getContext(),2));
        rvPop.addItemDecoration(new GridSpacingItemDecoration(2, 3, false));
//        rvPop.addItemDecoration(new SpacesItemDecoration(10));
        rvPop.setAdapter(rvAdapterPop);
        rvAdapterPop.notifyDataSetChanged();

        ArrayList<RecipeModel> catRecipe = MainFragmentActivity.recipeBookXls.getCategories();
        CategoryRvAdapter rvAdapterCat = new CategoryRvAdapter(catRecipe, getContext(), MAX_HOME_CATEGORY_SHOWN);
        rvAdapterCat.setRvItemHandler(new IRvItem() {
            @Override
            public void select(int index) {

            }

            @Override
            public void click(int index) {
                if (index == MAX_HOME_CATEGORY_SHOWN ) {
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, CategoryListFragment.newInstance(), "HOME");
                    transaction.addToBackStack("CAT_LIST");
                    transaction.commit();
                }
                else {
                    MainFragmentActivity.recipeModel = catRecipe.get(index);
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, RecipeListFragment.newInstance(), "CAT_LIST");
                    transaction.addToBackStack("REC_LIST");
                    transaction.commit();
                }
            }
        });
        RecyclerView rvCat  = (RecyclerView) view.findViewById(R.id.rv_category);
        rvCat.setLayoutManager(new GridLayoutManager(getContext(),4));
        rvCat.addItemDecoration(new GridSpacingItemDecoration(4, 30, false));
//        rvPop.addItemDecoration(new SpacesItemDecoration(10));
        rvCat.setAdapter(rvAdapterCat);
        rvAdapterCat.notifyDataSetChanged();
    }
}