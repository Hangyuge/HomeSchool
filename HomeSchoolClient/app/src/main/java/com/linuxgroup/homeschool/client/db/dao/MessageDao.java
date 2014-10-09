package com.linuxgroup.homeschool.client.db.dao;

import com.j256.ormlite.dao.Dao;
import com.linuxgroup.homeschool.client.model.Message;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by tan on 14-9-21.
 */
public interface MessageDao extends Dao<Message, Integer> {
    public void save(Message message) throws SQLException;
    public Message get(Integer id) throws SQLException;


    /**
     * 获取 account1 与 account2 的所有聊天记录
     */
    public List<Message> queryFor(String account1, String account2) throws SQLException;

}
