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

public class CategoryRvAdapter extends RecyclerView.Adapter<CategoryRvAdapter.CategoryVH> {
    ArrayList<RecipeModel> mDataset;
    IRvItem itemIface;
    int mMaxShownCount = -1;

    public CategoryRvAdapter(ArrayList<RecipeModel> mDataset, Context mContext, int maxShown) {
        this.mDataset = mDataset;
        this.mContext = mContext;
        this.mMaxShownCount =  maxShown;
    }

    Context mContext;

    @NonNull
    @Override
    public CategoryVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.rv_list_item_category, parent, false);
        return new CategoryVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryVH holder, int position) {
        if (mDataset == null || mDataset.isEmpty())
            return;
        if (mDataset.get(position) != null) {
            if (mMaxShownCount != -1 && position == mMaxShownCount) {
                holder.imageView.setImageResource(R.drawable.ic_baseline_more_horiz_24);
                holder.textView.setText("Lainnya");
                holder.textView.setTag("OTHER");
            }
            else {
                if (mDataset.get(position).imgUrl.length() > 0) {
                    Picasso.get().load(mDataset.get(position).imgUrl)
                            .into(holder.imageView);
                }
                holder.textView.setText("" + mDataset.get(position).categoryName);
                holder.textView.setTag("");
            }
            if (itemIface != null) {
                holder.layoutView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemIface.click(position);
                    }
                });
            }
        }

    }

    @Override
    public int getItemCount() {
        if (mMaxShownCount == -1)
            return mDataset.size();
        else
            return mMaxShownCount + 1;
    }

    public class CategoryVH extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textView;
        private View layoutView;

        public CategoryVH(@NonNull View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.img_category);
            textView = (TextView) itemView.findViewById(R.id.txt_category);
            layoutView = (View) itemView.findViewById(R.id.lay_category_item);
        }
    }

    public void setRvItemHandler(IRvItem iface) {
        itemIface = iface;
    }
}
