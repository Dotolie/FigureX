package com.mvtech.figurex;

import java.util.ArrayList;

import com.mvtech.structures.Motion;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CustomArrayAdapter extends ArrayAdapter<Motion>{

	private ViewHolder viewHolder = null;
	private LayoutInflater inflater = null;
	private ArrayList<Motion> infoList = null;
	private Context mContext = null;

	public CustomArrayAdapter(Context c, int textViewResourceId, ArrayList<Motion> arrays) {
		super(c, textViewResourceId, arrays);
		this.inflater = LayoutInflater.from(c);
		this.mContext = c;
		this.infoList = arrays;
	}

	@Override
	public int getCount() {
		return super.getCount();
	}

	@Override
	public Motion getItem(int position) {
		return super.getItem(position);
	}

	@Override
	public long getItemId(int position) {
		return super.getItemId(position);
	}

	@Override
	public View getView(int position, View convertview, ViewGroup parent) {
		
		View v = convertview;
		
		if(v == null){
			viewHolder = new ViewHolder();
			v = inflater.inflate(R.layout.list_row, null);
			viewHolder.tv_no = (TextView)v.findViewById(R.id.tv_no);
			viewHolder.tv_title = (TextView)v.findViewById(R.id.tv_title);
			viewHolder.iv_image = (ImageView)v.findViewById(R.id.iv_image);
			viewHolder.btn_config = (Button)v.findViewById(R.id.btn_config);
			viewHolder.btn_run = (Button)v.findViewById(R.id.btn_run);
			viewHolder.cb_box = (CheckBox)v.findViewById(R.id.cb_box);


			v.setTag(viewHolder);
			
		}else {
			viewHolder = (ViewHolder)v.getTag();
		}

		
		viewHolder.tv_no.setText(String.valueOf(getItem(position).no));
		viewHolder.tv_no.setTag(position);
		
		viewHolder.btn_run.setOnClickListener(buttonClickListener);
		viewHolder.btn_run.setTag(position);

		viewHolder.tv_title.setText(getItem(position).title);
		viewHolder.tv_title.setTag(position);
		
		viewHolder.iv_image.setOnClickListener(buttonClickListener);
		viewHolder.iv_image.setTag(position);
		
		viewHolder.btn_config.setOnClickListener(buttonClickListener);
		viewHolder.btn_config.setTag(position);
		
		viewHolder.cb_box.setTag(position);
		viewHolder.cb_box.setOnClickListener(buttonClickListener);

		if( position == 0)
			viewHolder.iv_image.setImageResource(R.drawable.connect);
		else if( position == 1 )
			viewHolder.iv_image.setImageResource(R.drawable.disconnect);
		else if( position == 2 )
			viewHolder.iv_image.setImageResource(R.drawable.battery_low);
		else if( position == 3 )
			viewHolder.iv_image.setImageResource(R.drawable.vibration);
		else 
			viewHolder.iv_image.setImageResource(R.drawable.action);

		viewHolder.cb_box.setChecked(getItem(position).checked);
		
		return v;
	}
	
	public void setArrayList(ArrayList<Motion> arrays){
		this.infoList  = arrays;
	}
	
	public ArrayList<Motion> getArrayList(){
		return infoList;
	}
	
	private View.OnClickListener buttonClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			MainActivity main = (MainActivity)mContext;
			int position = 0;

			switch (v.getId()) {
			
			// 이미지 클릭
			case R.id.iv_image:
				Toast.makeText(
						mContext, 
						"이미지 Tag = " + v.getTag(),
						Toast.LENGTH_SHORT
						).show();
				break;
			
			// 버튼 클릭
			case R.id.btn_config:
				position = Integer.parseInt( v.getTag().toString() );
				main.editMotion(position);
				break;

			case R.id.btn_run:
				position = Integer.parseInt( v.getTag().toString() );
				main.sendConfig(position);
				break;

				// CheckBox
			case R.id.cb_box:
				position = Integer.parseInt( v.getTag().toString() );
				Motion motion = getItem(position);
				motion.checked = !motion.checked;
				
				Toast.makeText(
						mContext, 
						"체크박스 Tag = " + v.getTag(),
						Toast.LENGTH_SHORT
						).show();
				break;

			default:
				break;
			}
		}
	};
	
	
	/*
	 * ViewHolder
	 */
	class ViewHolder{
		public TextView tv_no = null;
		public TextView tv_title = null;
		public ImageView iv_image = null;
		public Button btn_run = null;
		public Button btn_config = null;
		public CheckBox cb_box = null;
		
	}

	
	@Override
	protected void finalize() throws Throwable {
		free();
		super.finalize();
	}
	
	private void free(){
		inflater = null;
		infoList = null;
		viewHolder = null;
		mContext = null;
	}
}
