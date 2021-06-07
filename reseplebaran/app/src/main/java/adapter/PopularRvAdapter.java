package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dhiren.reseplebaran.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import iface.IRvItem;
import model.RecipeModel;

public class PopularRvAdapter extends RecyclerView.Adapter<PopularRvAdapter.PopularVH> {
    ArrayList<RecipeModel> mDataset;
    IRvItem itemIface = null;
    Context mContext;

    public PopularRvAdapter(ArrayList<RecipeModel> mDataset, Context mContext) {
        this.mDataset = mDataset;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public PopularVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.rv_list_item_popular, parent, false);
        return new PopularVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PopularVH holder, int position) {
        if (mDataset == null || mDataset.isEmpty())
            return;
        Picasso.get().load(mDataset.get(position).imgUrl)
                .into(holder.imageView);
        holder.textViewRecipe.setText( mDataset.get(position).recipeName);
        holder.textViewCategory.setText( "Masakan " + mDataset.get(position).categoryName);
        holder.layoutView.setOnClickListener(v -> {
            itemIface.click(position);
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public class PopularVH extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textViewRecipe;
        private TextView textViewCategory;
        private View layoutView;

        public PopularVH(@NonNull View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.img_popular);
            textViewRecipe = (TextView) itemView.findViewById(R.id.txt_recipe_name);
            textViewCategory = (TextView) itemView.findViewById(R.id.txt_category_name);
            layoutView = (View) itemView.findViewById(R.id.lay_popular_item);

        }
    }

    public void setRvItemHandler(IRvItem iface) {
        itemIface = iface;
    }
}
