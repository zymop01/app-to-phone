package fm.icelink.chat.websync4;


import android.content.Context;


import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import android.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;




public class PagerAdapter extends FragmentPagerAdapter {
    Context context;

    static final int VideoTabIndex = 0;
    static final int TextTabIndex = 1;

    public PagerAdapter(
            androidx.fragment.app.FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    
    @Override
    public androidx.fragment.app.Fragment getItem(int position) {
        switch (position) {
            case VideoTabIndex:
                return new VideoChatFragment();
            //case TextTabIndex:
                //return new TextChatFragment();

            default:
                return null;
        }
    }
    
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case VideoTabIndex:
                return "Video";
            case TextTabIndex:
                return "Text";
            default:
                return "Unknown";
        }
    }
    
    @Override
    public int getCount() {
        return 2;
    }
    
    public View getTabView(int position) {
        View v = LayoutInflater.from(context).inflate(R.layout.tab, null);
        TextView header = (TextView)v.findViewById(R.id.header);
        header.setText(getPageTitle(position));
        
        TextView badge = (TextView)v.findViewById(R.id.badge);
        badge.setVisibility(View.INVISIBLE);
        
        return v;
    }
}
