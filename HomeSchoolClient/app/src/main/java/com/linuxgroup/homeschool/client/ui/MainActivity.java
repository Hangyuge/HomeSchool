package com.linuxgroup.homeschool.client.ui;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.linuxgroup.homeschool.client.App;
import com.linuxgroup.homeschool.client.R;
import com.linuxgroup.homeschool.client.db.dao.RecentChatDao;
import com.linuxgroup.homeschool.client.db.model.RecentChat;
import com.linuxgroup.homeschool.client.db.service.DatabaseManager;
import com.linuxgroup.homeschool.client.manager.UpdateManager;
import com.linuxgroup.homeschool.client.ui.fragment.RecentChatFragment;

import java.sql.SQLException;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends BaseActivity {

    @InjectView(R.id.pager)
    ViewPager mViewPager;

/*    @InjectView(R.id.check_update)
    Button bt_check_update;

    @InjectView(R.id.search_friend)
    Button bt_search_friend;*/

    private MyPagerAdapter myPagerAdapter;


    private final String[] tabNames = new String[] {"聊天"/*, "联系人", "公告"*/};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);

        init();

       /* bt_search_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        // 检查更新
        bt_check_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 检查更新
                UpdateManager updateManager = new UpdateManager(MainActivity.this);
                updateManager.checkUpdate();
            }
        });*/

        // todo: test recent dao

        RecentChat recentChat = new RecentChat();
//        recentChat.setId(2);
        //TODO:  需要设置 自动 generateid
        recentChat.setRead(true);
        recentChat.setToAccount("2");

        try {
            RecentChatDao recentChatDao = DatabaseManager.getRecentChatDao();

            recentChatDao.save(recentChat);

            List<RecentChat> recentChats = recentChatDao.queryForAll();

            System.out.println(recentChats.size());
            System.out.println(recentChats.get(0));
            System.out.println(recentChats.get(1));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void init() {
        setListener();

        // 设置 ViewPager
        myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(myPagerAdapter);
    }

    private void setListener() {
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        // 创建一个tab listener，在用户切换tab时调用.
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                // 当tab被选中时, 切换到ViewPager中相应的页面
                mViewPager.setCurrentItem(tab.getPosition());
            }

            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
                // 隐藏指定的tab
            }

            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
                // 可以忽略这个事件
            }
        };

        //定义ActionBar模式为NAVIGATION_MODE_TABS
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // 添加3个tab, 并指定tab的文字和TabListener
        for (String tabName : tabNames) {

            actionBar.addTab(
                    actionBar.newTab()
                            .setText(tabName)
                            .setTabListener(tabListener));
        }

        // 页面变化时当前的tab也相应变化
        mViewPager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        // 当划屏切换页面时，选择相应的tab.
                        actionBar.setSelectedNavigationItem(position);
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //提示如果是服务里调用，必须加入new task标
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    /**
     * ViewPager 的 Adapter
     */
    class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // 根据 postsion 来选择 fragment 的内容

            if (position == 0) { // 聊天记录
                Fragment fragment = RecentChatFragment.newInstance();
                return fragment;
            }

            return null;
        }

        @Override
        public int getCount() {
            return tabNames.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabNames[position];
        }
    }
}
