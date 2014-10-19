package com.linuxgroup.homeschool.client.ui.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.linuxgroup.homeschool.client.App;
import com.linuxgroup.homeschool.client.R;
import com.linuxgroup.homeschool.client.adapter.RecentChatListAdapter;
import com.linuxgroup.homeschool.client.api.Constants;
import com.linuxgroup.homeschool.client.broadcast.BroadcastRegister;
import com.linuxgroup.homeschool.client.db.dao.RecentChatDao;
import com.linuxgroup.homeschool.client.db.model.RecentChat;
import com.linuxgroup.homeschool.client.db.service.DatabaseManager;
import com.linuxgroup.homeschool.client.manager.UpdateManager;
import com.linuxgroup.homeschool.client.tasks.SimpleBackgroundTask;
import com.linuxgroup.homeschool.client.ui.MainActivity;
import com.linuxgroup.homeschool.client.ui.SearchActivity;

import java.sql.SQLException;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class RecentChatFragment extends Fragment {
    private static final String TAG = "RecentChatFragment";

    @InjectView(R.id.check_update)
    Button bt_check_update;

    @InjectView(R.id.search_friend)
    Button bt_search_friend;

    @InjectView(R.id.listview)
    ListView listView;

    private BroadcastReceiver broadcastReceiver;

    private static final String ownerAccount = (String)App.get(App.ACCOUNT);

    private RecentChatListAdapter recentChatListAdapter;

    public static RecentChatFragment newInstance() {
        RecentChatFragment fragment = new RecentChatFragment();

        return fragment;
    }
    public RecentChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        registerUpdateMessageBroadcast();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recent_chat, container, false);

        ButterKnife.inject(this, view);

        initListView();

        bt_search_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        });

        // 检查更新
        bt_check_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 检查更新
                UpdateManager updateManager = new UpdateManager(getActivity());
                updateManager.checkUpdate();
            }
        });

        return view;
    }

    public void initListView() {
        recentChatListAdapter = new RecentChatListAdapter(getActivity().getLayoutInflater());
        listView.setAdapter(recentChatListAdapter);

        refreshList();
    }

    public void refreshList() {
        // 异步刷新消息，从数据库中读取、更新到 listview
        new SimpleBackgroundTask<List<RecentChat>>(getActivity()) {
            @Override
            protected List<RecentChat> onRun() {
                try {
                    RecentChatDao recentChatDao = DatabaseManager.getRecentChatDao();

                    //todo: userAccount
                    return recentChatDao.queryForAll(ownerAccount);
                } catch (SQLException e) {
                    Log.d(TAG, "从数据库读取recentChat出错");
                    return null;
                }
            }

            @Override
            protected void onSuccess(List<RecentChat> recentChats) {
                //todo: 显示列表
                recentChatListAdapter.replaceLazyList(recentChats);
            }
        }.execute();
    }

    /**
     * 无论是发送消息，还是收到新的消息，都要更新下会话列表
     */
    private void registerUpdateMessageBroadcast() {
        broadcastReceiver = BroadcastRegister.registerUpdateMessageBroadcast(getActivity(), new BroadcastRegister.OnDo() {
            @Override
            public void onDo(Context context, Intent intent) {
                // 数据更新显示
                refreshList();
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        getActivity().unregisterReceiver(broadcastReceiver);
    }
}
