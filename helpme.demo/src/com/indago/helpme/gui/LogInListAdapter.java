package com.indago.helpme.gui;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.helpme.demo.interfaces.UserInterface;
import com.indago.helpme.R;
import com.indago.helpme.gui.util.ImageUtility;

public class LogInListAdapter extends ArrayAdapter<UserInterface> {

	private Context mContext;

	public LogInListAdapter(Context context, int resourceId, List<UserInterface> objects) {
		super(context, resourceId, objects);
		mContext = context;
	}

	private class ViewHolder {
		Drawable[] drawables;
		ImageView picture;
		TextView name;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		UserInterface item = getItem(position);

		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

		if(convertView == null) {
			convertView = inflater.inflate(R.layout.list_item_picture_text, null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.tv_list_name);
			holder.picture = (ImageView) convertView.findViewById(R.id.iv_list_picture);

			holder.drawables = new Drawable[4];
			holder.drawables[0] = mContext.getResources().getDrawable(R.drawable.user_picture_background);
			holder.drawables[1] = mContext.getResources().getDrawable(R.drawable.ic_person_dummy);
			holder.drawables[2] = mContext.getResources().getDrawable(R.drawable.user_picture_overlay);
			holder.drawables[3] = mContext.getResources().getDrawable(R.drawable.user_picture_border);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.name.setText(item.getName());

		//		holder.drawables[1] = mContext.getResources().getDrawable(item.getPicture());
		holder.drawables[1] = ImageUtility.retrieveDrawable(getContext(), item.getPicture());
		holder.picture.setImageDrawable(new LayerDrawable(holder.drawables));

		return convertView;
	}
}
