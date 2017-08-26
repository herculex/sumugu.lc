package com.sumugu.liubo.lc.simpleWay;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.sumugu.liubo.lc.R;
import com.sumugu.liubo.lc.contract.ItemContract;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ItemPackageActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;

    private String[] FROM = new String[]{
            ItemContract.Column.ITEM_CONTENT
    };
    private int[] TO = new int[]{
            R.id.text_content
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_package_md);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ItemHistoryFragment frag1 = new ItemHistoryFragment();
        Bundle bundle1 = new Bundle();
        bundle1.putString(ItemHistoryFragment.TITLE, "history");
        bundle1.putInt(ItemHistoryFragment.WHAT_TYPE, ItemHistoryFragment.TYPE_HISTORY);
        frag1.setArguments(bundle1);

        ItemHistoryFragment frag2 = new ItemHistoryFragment();
        Bundle bundle2 = new Bundle();
        bundle2.putString(ItemHistoryFragment.TITLE, "plan");
        bundle2.putInt(ItemHistoryFragment.WHAT_TYPE, ItemHistoryFragment.TYPE_PLAN);
        frag2.setArguments(bundle2);

        ItemHistoryFragment frag3 = new ItemHistoryFragment();
        Bundle bundle3 = new Bundle();
        bundle3.putString(ItemHistoryFragment.TITLE, "reminder");
        bundle3.putInt(ItemHistoryFragment.WHAT_TYPE, ItemHistoryFragment.TYPE_REMINDER);
        frag3.setArguments(bundle3);

        ArrayList<Fragment> list = new ArrayList<>();
        list.add(frag1);
        list.add(frag2);
        list.add(frag3);

        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), list);
        ViewPager viewpager = (ViewPager) findViewById(R.id.viewpager);
        viewpager.setAdapter(adapter);

        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewpager);
        viewpager.setCurrentItem(1);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_lc);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCreateContent();
            }
        });

    }

    void openCreateContent()
    {
        Intent itemContent = new Intent(ItemPackageActivity.this, ItemContentActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("id", "0");
        bundle.putString("content", "create new content");
        itemContent.putExtras(bundle);
        startActivityForResult(itemContent, 2);
        overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
    }

    public class PagerAdapter extends FragmentPagerAdapter {

        ArrayList<Fragment> mFragments;

        public PagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
            super(fm);
            mFragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return ((ItemHistoryFragment) mFragments.get(position)).getTitle();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            Toast.makeText(this, "it's ItemContent'detail come back.", Toast.LENGTH_SHORT).show();
        }
        if (requestCode == 2) {
            Toast.makeText(this, "it's ItemContent'create come back", Toast.LENGTH_SHORT).show();
        }
        if (requestCode == 3) {
            Toast.makeText(this, "it's ItemHistory come back", Toast.LENGTH_SHORT).show();
        }
    }


}
