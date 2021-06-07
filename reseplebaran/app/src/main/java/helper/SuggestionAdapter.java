package helper;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cursoradapter.widget.CursorAdapter;

import com.dhiren.reseplebaran.R;
import com.squareup.picasso.Picasso;

import iface.IRecipeClick;
import model.RecipeModel;

public class SuggestionAdapter extends CursorAdapter {
    private IRecipeClick mIRecipeClick;

    public SuggestionAdapter(Context context, Cursor c, boolean autoRequery, IRecipeClick rvItemCallback) {
        super(context, c, autoRequery);
        mIRecipeClick = rvItemCallback;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.search_view_suggestion, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        ImageView imgRecipe = view.findViewById(R.id.img_recipe_suggestion);
        TextView textViewRecipe = view.findViewById(R.id.txt_recipe_suggestion);

        if(cursor.getColumnIndex("name") != -1 && cursor.getString(cursor.getColumnIndex("name")) != null) {
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String imgUri = cursor.getString(cursor.getColumnIndex("img"));
            int categoryId = cursor.getInt(cursor.getColumnIndex("cat_id"));
            int recipeId = cursor.getInt(cursor.getColumnIndex("rec_id"));

            if (imgUri.length() > 0) {
                Picasso.get().load(imgUri)
                        .into(imgRecipe);
            }
            textViewRecipe.setText(name);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mIRecipeClick.click(new RecipeModel(imgUri, name, "", categoryId, recipeId));
                }
            });
        }
    }
}
