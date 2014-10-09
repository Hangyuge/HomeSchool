package com.linuxgroup.action;

import com.linuxgroup.model.Message;
import com.linuxgroup.result.Result;
import com.linuxgroup.service.MessageService;
import com.linuxgroup.service.PushService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by tan on 14-9-22.
 */
@Controller
@RequestMapping("/message")
public class MessageRestul {

    @Autowired
    private MessageService messageService;

    @Autowired
    private PushService pushService;

    /**
     * 获取消息
     * @param id 要获取的 id
     * @return 消息实体
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public @ResponseBody
    Message get(HttpServletRequest request, HttpServletResponse response,
                    @PathVariable Integer id) {

        Message message = messageService.get(id);

        return message;
    }

    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody
    Result sendMessage(@RequestBody Message message) {
        Integer msgId = messageService.saveMessage(message);

        Result result = new Result();

        //todo: 修改为对指定用户发送消息
        try {
            // 推送消息的 id
            pushService.pushMessageToAll(""+msgId);
            pushService.pushMessageTo(message.getToAccount(), msgId+"");

            System.out.println("接受到新消息: mesgId:" + msgId + " msgcontent:" + message.getContent());

            result.setStatus("ok");
            result.setRetId(msgId);
        } catch (Exception  e) {
            result.setStatus("error");
        }

        return result;
    }


    // set and get methods


    public MessageService getMessageService() {
        return messageService;
    }

    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    public PushService getPushService() {
        return pushService;
    }

    public void setPushService(PushService pushService) {
        this.pushService = pushService;
    }
}
