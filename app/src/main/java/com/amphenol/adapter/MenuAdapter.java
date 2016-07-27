package com.amphenol.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.amphenol.amphenol.R;
import com.amphenol.entity.MenuItem;

import java.util.List;

/**
 * Created by Carl on 2016/7/11/011.
 */
public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {
    private Context mContext;
    private List<List<MenuItem>> menuDrawer;//菜单集合，二维形式
    private OnMenuItemClickListener onMenuItemClickListener;

    public MenuAdapter(Context context, List<List<MenuItem>> menuItems, OnMenuItemClickListener onMenuItemClickListener) {
        mContext = context;
        this.menuDrawer = menuItems;
        this.onMenuItemClickListener = onMenuItemClickListener;
    }
    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MenuViewHolder menuViewHolder = new MenuViewHolder(LayoutInflater.from(mContext).inflate(R.layout.menu_item_layout, parent, false));
        return menuViewHolder;
    }

    @Override
    public void onBindViewHolder(final MenuViewHolder holder, int position_h) {
        final MenuSecondAdapter menuSecondAdapter = new MenuSecondAdapter(mContext, menuDrawer.get(position_h));
        holder.mGridView.setAdapter(menuSecondAdapter);
        holder.mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position_v, long id) {
                if (onMenuItemClickListener != null) {
                    onMenuItemClickListener.onMenuItemClick(((MenuItem) menuSecondAdapter.getItem(position_v)).getCode());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return menuDrawer == null ? 0 : menuDrawer.size();
    }

    class MenuViewHolder extends RecyclerView.ViewHolder {

        GridView mGridView;

        public MenuViewHolder(View itemView) {
            super(itemView);
            mGridView = (GridView) itemView.findViewById(R.id.meny_item_gv);
        }
    }

    public interface OnMenuItemClickListener {
        void onMenuItemClick(int menuCode);
    }
}
