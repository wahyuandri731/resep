package fragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dhiren.reseplebaran.R;

import java.util.ArrayList;

import activity.MainFragmentActivity;
import adapter.RecipeRvAdapter;
import iface.IRvItem;
import model.RecipeModel;

public class RecipeListFragment extends Fragment {

    private AppCompatActivity appCompatActivity;
    RecyclerView recyclerView;
    View mView;

    public static RecipeListFragment newInstance() {
        return new RecipeListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.recipe_list_fragment, container, false);
        ArrayList<RecipeModel> recipes = MainFragmentActivity.recipeBookXls.getRecipes(MainFragmentActivity.recipeModel.categoryId);
        RecipeRvAdapter rvAdapterRecipes = new RecipeRvAdapter(recipes, getContext());
        rvAdapterRecipes.setRvItemHandler(new IRvItem() {
            @Override
            public void select(int index) {

            }

            @Override
            public void click(int index) {
                MainFragmentActivity.recipeModel = recipes.get(index);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, RecipeFragment.newInstance(), "RECIPE_LIST");
                transaction.addToBackStack("RECIPE");
                transaction.commit();
            }
        });


        RecyclerView rvPCat = (RecyclerView) mView.findViewById(R.id.rv);
        rvPCat.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPCat.setAdapter(rvAdapterRecipes);
        rvAdapterRecipes.notifyDataSetChanged();
        recyclerView = (RecyclerView) mView.findViewById(R.id.rv);
        return mView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        appCompatActivity = ((AppCompatActivity)getActivity());
        View toolbarView = appCompatActivity.findViewById(R.id.toolbar);
        toolbarView.setVisibility(View.VISIBLE);
        ((TextView) toolbarView.findViewById(R.id.txt_title)).setText("Masakan " + MainFragmentActivity.recipeModel.categoryName);
        toolbarView.findViewById(R.id.btn_back).setOnClickListener(v -> {
            appCompatActivity.getSupportFragmentManager().popBackStack();
        });
//                View toolbarView = appCompatActivity.findViewById(R.id.toolbar);
//                toolbarView.setVisibility(View.VISIBLE);
//                ((TextView) toolbarView.findViewById(R.id.txt_title)).setText("Masakan " + recipeModel.categoryName);



    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}