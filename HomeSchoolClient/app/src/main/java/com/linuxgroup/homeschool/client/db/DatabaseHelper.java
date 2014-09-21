package com.linuxgroup.homeschool.client.db;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.linuxgroup.homeschool.client.db.dao.MessageDao;
import com.linuxgroup.homeschool.client.db.dao.impl.MessageDaoImpl;
import com.linuxgroup.homeschool.client.domain.Message;

import org.w3c.dom.UserDataHandler;

import java.sql.SQLException;

/**
 * Created by tan on 14-9-21.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = "ormlite.db";
    private static final int DATABASE_VERSION = 1;

//    private Dao<Message, Integer> messageDao = null;
    private MessageDao messageDao;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * 创建SQLite数据库
     */
    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            // 建 Message 表, 可以继续添加别的表
            TableUtils.createTable(connectionSource, Message.class);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, Message.class, true);

            onCreate(database, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public MessageDao getMessageDao() throws SQLException {
        if (messageDao == null) {
//            messageDao = getDao(Message.class);
            // 创建自定义的 dao
            messageDao = new MessageDaoImpl(connectionSource);
        }

        return messageDao;
    }

    @Override
    public void close() {
        super.close();
        messageDao = null;
    }
}
