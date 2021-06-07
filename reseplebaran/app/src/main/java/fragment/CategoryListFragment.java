package fragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.dhiren.reseplebaran.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.ArrayList;

import activity.MainFragmentActivity;
import adapter.CategoryRvAdapter;
import helper.GridSpacingItemDecoration;
import iface.IRvItem;
import model.RecipeModel;

public class CategoryListFragment extends Fragment {

    private RecipeModel mRecipeModel;
    private View mView;
    private AppCompatActivity appCompatActivity;
    private AdView adView;
    private int loadBannerAgain;

    public static CategoryListFragment newInstance() {
        return new CategoryListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.category_list_fragment, container, false);
        appCompatActivity = ((AppCompatActivity)getActivity());
        View toolbarView = appCompatActivity.findViewById(R.id.toolbar);
        toolbarView.setVisibility(View.VISIBLE);

        ((ImageButton)toolbarView.findViewById(R.id.btn_back)).setOnClickListener(v -> {
            appCompatActivity.getSupportFragmentManager().popBackStack();
        });
        ((TextView) toolbarView.findViewById(R.id.txt_title)).setText("Kategori Makanan");

        ArrayList<RecipeModel> catRecipes = MainFragmentActivity.recipeBookXls.getCategories();
        CategoryRvAdapter rvAdapterCat = new CategoryRvAdapter(catRecipes, getContext(), -1);
        rvAdapterCat.setRvItemHandler(new IRvItem() {
            @Override
            public void select(int index) {

            }

            @Override
            public void click(int index) {
                MainFragmentActivity.recipeModel = catRecipes.get(index);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, RecipeListFragment.newInstance(), "CAT_LIST");
                transaction.addToBackStack("REC_LIST");
                transaction.commit();
            }
        });
        RecyclerView rvPCat = (RecyclerView) mView.findViewById(R.id.rv);
        rvPCat.setLayoutManager(new GridLayoutManager(getContext(), 4));
        rvPCat.addItemDecoration(new GridSpacingItemDecoration(4, 30, false));
        rvPCat.setAdapter(rvAdapterCat);
        rvAdapterCat.notifyDataSetChanged();
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        RequestConfiguration configuration = new RequestConfiguration.Builder()
                .build();
        MobileAds.setRequestConfiguration(configuration);
        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        //AdMob
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
    }
    //BANNERADS
    @Override
    public void onDestroy(){
        if (adView !=null){
            adView.destroy();
        }
        super.onDestroy();
    }


}