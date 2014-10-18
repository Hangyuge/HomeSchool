package com.linuxgroup.homeschool.client.request.job;

import com.linuxgroup.homeschool.client.App;
import com.linuxgroup.homeschool.client.broadcast.BroadcastSender;
import com.linuxgroup.homeschool.client.db.model.ChatMessage;
import com.linuxgroup.homeschool.client.api.MessageApi;
import com.linuxgroup.homeschool.client.db.model.RecentChat;
import com.linuxgroup.homeschool.client.db.service.DatabaseManager;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by tan on 14-9-25.
 */
public class FetchMessageJob extends Job {
    private static String ownerAccount;

    private static final AtomicInteger jobCounter = new AtomicInteger(0);

    private final int id;

    /**
     * 用于请求的 message 的 id
     */
    private Integer requestMsgId;

    public FetchMessageJob(Integer requestMsgId) {
        super(new Params(Priority.LOW).requireNetwork().groupBy("fetch-message"));

        id = jobCounter.incrementAndGet();

        this.requestMsgId = requestMsgId;
    }

    @Override
    public void onAdded() {
    }

    @Override
    public void onRun() throws Throwable {
        System.out.println("run Fetch message " + id);

        // 获取消息
        ChatMessage chatMessage = MessageApi.getMessage(requestMsgId, ChatMessage.class);
        System.out.println(chatMessage.toString());

        // 保存消息到本地数据库
        DatabaseManager.getMessageDao().save(chatMessage);

        
        // 保存会话到本地数据库
        // todo: 检查是否存在

        RecentChat recentChat = new RecentChat();

        recentChat.setUserAccount(getOwnerAccount());
        recentChat.setFriendAccount(chatMessage.getFromAccount());
        recentChat.setIsRead(false);

        DatabaseManager.getRecentChatDao().saveRecentChat(recentChat);


        // 发送收到新消息的广播
        BroadcastSender.sendUpdateMessageBroadcast(App.getContext());
    }

    public static String getOwnerAccount() {
        if (ownerAccount == null) {
            ownerAccount = (String) App.get(App.ACCOUNT);
        }

        return ownerAccount;
    }

    @Override
    protected void onCancel() {
        System.out.println("calcel Fetch message " + id);
    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return false;
    }
}
