package adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dhiren.reseplebaran.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import iface.IRvItem;
import model.RecipeModel;

public class RecipeRvAdapter extends RecyclerView.Adapter<RecipeRvAdapter.RecipeVH> {
    ArrayList<RecipeModel> mDataset;
    IRvItem itemIface = null;
    Context mContext;

    public RecipeRvAdapter(ArrayList<RecipeModel> mDataset, Context mContext) {
        this.mDataset = mDataset;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public RecipeVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.rv_list_item_recipes, parent, false);
        return new RecipeVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeVH holder, int position) {
        if (mDataset == null || mDataset.isEmpty())
            return;
        if (mDataset.get(position).imgUrl.length() > 0)
            Picasso.get().load(mDataset.get(position).imgUrl)
                .into(holder.imageView);
        holder.textViewRecipe.setText( mDataset.get(position).recipeName);
        if (itemIface != null) {
            holder.layoutView.setOnClickListener(v -> {
                itemIface.click(position);
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public class RecipeVH extends RecyclerView.ViewHolder {
        private CircleImageView imageView;
        private TextView textViewRecipe;
        private View layoutView;

        @SuppressLint("WrongViewCast")
        public RecipeVH(@NonNull View itemView) {
            super(itemView);
            imageView = (CircleImageView) itemView.findViewById(R.id.img_recipe);
            textViewRecipe = (TextView) itemView.findViewById(R.id.txt_recipe_name);
            layoutView = (View) itemView.findViewById(R.id.lay_recipe);
        }
    }

    public void setRvItemHandler(IRvItem iface) {
        itemIface = iface;
    }
}
