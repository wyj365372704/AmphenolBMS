package com.amphenol.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amphenol.amphenol.R;
import com.amphenol.entity.MenuItem;
import com.amphenol.ui.MenuImageView;

import java.security.spec.PSSParameterSpec;
import java.util.List;

/**
 * Created by Carl on 2016/7/11/011.
 */
public class MenuSecondAdapter extends BaseAdapter {
    private Context mContext;
    private List<MenuItem> menuItems;

    public MenuSecondAdapter(Context context,List<MenuItem> menuItems){
        mContext = context;
        this.menuItems = menuItems;
    }
    @Override
    public int getCount() {
        return menuItems == null ? 0 : menuItems.size();
    }

    @Override
    public Object getItem(int position) {
        return menuItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MenuItemViewHolder menuItemViewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.menu_second_item_layout,null);
            menuItemViewHolder = new MenuItemViewHolder();
            menuItemViewHolder.mImageView = (ImageView) convertView.findViewById(R.id.menu_second_item_miv);
            menuItemViewHolder.mTextView = (TextView) convertView.findViewById(R.id.menu_second_item_tv);
            convertView.setTag(menuItemViewHolder);
        } else {
            menuItemViewHolder = (MenuItemViewHolder) convertView.getTag();
        }
        menuItemViewHolder.mImageView.setImageResource(menuItems.get(position).getImageRes());
        menuItemViewHolder.mTextView.setText(menuItems.get(position).getTitle());
        return convertView;
    }

    class MenuItemViewHolder {
        ImageView mImageView;//menu图片
        TextView mTextView;//menu文字
    }
}
