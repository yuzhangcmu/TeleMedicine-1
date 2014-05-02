package edu.cmu.smartphone.telemedicine;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MessageRecordsActivity extends Activity {

	ArrayList<HashMap<String, Object>> chatList = null;
	String[] from = { "image", "text" };
	int[] to = { R.id.chatlist_image_me, R.id.chatlist_text_me, R.id.chatlist_image_other, R.id.chatlist_text_other };
	int[] layout = { R.layout.chat_listitem_me, R.layout.chat_listitem_other };
	String userQQ = null;

	public final static int OTHER = 1;
	public final static int ME = 0;

	protected ListView chatListView = null;
	protected TextView chat_contact_name = null;

	protected MyChatAdapter adapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.message_records_view);
		chatList = new ArrayList<HashMap<String, Object>>();
		chatListView = (ListView) findViewById(R.id.chat_list);
		chat_contact_name = (TextView) findViewById(R.id.chat_contact_name);

		loadMessageRecordsFromLocalDB();
		
		// chat_contact_name.setText();

		adapter = new MyChatAdapter(this, chatList, layout, from, to);
		chatListView.setAdapter(adapter);
	}
	
	protected void loadMessageRecordsFromLocalDB() {
		addTextToList("不管你是谁", ME);
		addTextToList("群发的我不回\n  ^_^", OTHER);
		addTextToList("哈哈哈哈", ME);
		addTextToList("新年快乐！", OTHER);
	}

	protected void addTextToList(String text, int who) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("person", who);
		map.put("image", who == ME ? R.drawable.contact_0 : R.drawable.contact_1);
		map.put("text", text);
		chatList.add(map);
	}

	private class MyChatAdapter extends BaseAdapter {

		Context context = null;
		ArrayList<HashMap<String, Object>> chatList = null;
		int[] layout;
		String[] from;
		int[] to;

		public MyChatAdapter(Context context,
				ArrayList<HashMap<String, Object>> chatList, int[] layout,
				String[] from, int[] to) {
			super();
			this.context = context;
			this.chatList = chatList;
			this.layout = layout;
			this.from = from;
			this.to = to;
		}

		@Override
		public int getCount() {
			return chatList.size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		class ViewHolder {
			public ImageView imageView = null;
			public TextView textView = null;

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			int who = (Integer) chatList.get(position).get("person");

			convertView = LayoutInflater.from(context).inflate(layout[who == ME ? 0 : 1], null);
			holder = new ViewHolder();
			holder.imageView = (ImageView) convertView.findViewById(to[who * 2 + 0]);
			holder.textView = (TextView) convertView.findViewById(to[who * 2 + 1]);

			System.out.println(holder);
			System.out.println("WHYWHYWHYWHYW");
			System.out.println(holder.imageView);
			holder.imageView.setBackgroundResource((Integer) chatList.get(position).get(from[0]));
			holder.textView.setText(chatList.get(position).get(from[1]).toString());
			return convertView;
		}
	}
}
