package edu.cmu.smartphone.telemedicine;


import android.app.TabActivity;
import edu.cmu.smartphone.telemedicine.R;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
 
public class TabWidget extends TabActivity {
 
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab);//这里使用了上面创建的xml文件（Tab页面的布局）
        Resources res = getResources(); // Resource object to get Drawables
        TabHost tabHost = getTabHost();  // The activity TabHost
        TabSpec spec;
        Intent intent;  // Reusable Intent for each tab
        
        Drawable image1 = this.getResources().getDrawable(R.drawable.sym_action_chat);
 
        //  first tab, display the history chatting.
        intent = new Intent(this,ChatHistoryActivity.class);//新建一个Intent用作Tab1显示的内容
        spec = tabHost.newTabSpec("tab1")//新建一个 Tab
        .setIndicator("Message", image1)//设置名称以及图标
        .setContent(intent);//设置显示的intent，这里的参数也可以是R.id.xxx
        tabHost.addTab(spec);//添加进tabHost
        
        Drawable image2 = this.getResources().getDrawable(R.drawable.ic_menu_friendslist);
 
        // second tab, display the contact
        intent = new Intent(this,ContactActivity.class);//第二个Intent用作Tab1显示的内容
        spec = tabHost.newTabSpec("tab2")//新建一个 Tab
        .setIndicator("Contacts", image2)//设置名称以及图标
        .setContent(intent);//设置显示的intent，这里的参数也可以是R.id.xxx
        tabHost.addTab(spec);//添加进tabHost
        
        Drawable image3 = this.getResources().getDrawable(R.drawable.ic_action_settings);
        
        // the third tab.
        intent = new Intent(this,SettingActivity.class); 
        spec = tabHost.newTabSpec("tab2")//新建一个 Tab
        .setIndicator("Setting", image3)//设置名称以及图标
        .setContent(intent);//设置显示的intent，这里的参数也可以是R.id.xxx
        tabHost.addTab(spec);//添加进tabHost
 
        // hide the keyboard.
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        tabHost.setCurrentTab(1);
    }
 
}