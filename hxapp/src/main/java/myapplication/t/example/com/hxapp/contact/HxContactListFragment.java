package myapplication.t.example.com.hxapp.contact;

import android.os.Bundle;
import android.util.Log;

import com.hyphenate.EMContactListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMContactManager;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.ui.EaseContactListFragment;
import com.hyphenate.exceptions.HyphenateException;

import java.util.HashMap;
import java.util.List;

/**
 * 联系人列表页面，基于EaseUI实现
 * Created by T on 2016/10/11.
 */


public class HxContactListFragment extends EaseContactListFragment implements EMContactListener {
    private EMContactManager emContactManager;//联系人管理API
    private List<String> contacts;//联系人集合（从环信服务器获取到的）

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //自制UI
        customUI();
        //联系人监听（重要）
        emContactManager= EMClient.getInstance().contactManager();
        emContactManager.setContactListener(this);
        // 异步获取联系人数据
        asyncGetContactsFromServer();

        // 获取一次联系人列表数据，且刷新一次当前List控件
        // 1. EMClient---EMContactManager - getAll联系人(从环信)
        // 1.1 拿到的将是一个 List<String>
        // 1.2 当前EaseUI的EaseContactListFragment中，联系人EaseUser
        //
        // 2. 将联系人EaseUser设置加入到当前列表数据中EaseContactListFragment中的setContactsMap()
        //
        // 3. EaseContactListFragment中的refresh方法
        // 最后，再结合上，联系人的监听,  一切搞定
    }

    private void customUI() {  super.hideTitleBar();
    }
    private void asyncGetContactsFromServer() {
        Runnable runnable = new Runnable() {
            @Override public void run() {
                try {
                    // 从环信服务器获取到所有联系人
                    contacts = emContactManager.getAllContactsFromServer();
                    // 刷新联系人
                    refreshContacts();
                } catch (HyphenateException e) {
                    Log.d("apphx", "asyncGetContactsFromServer! Exception");
                }
            }
        };
        new Thread(runnable).start();
    }

    private void refreshContacts() {
        HashMap<String, EaseUser> hashMap = new HashMap<>();
        for (String hxId : contacts) {
            EaseUser easeUser = new EaseUser(hxId);
            hashMap.put(hxId, easeUser);
        }
        // 设置当前视图上的联系人
        super.setContactsMap(hashMap);
        // 刷新当前视图上的联系人
        super.refresh();
    }

    // 联系人监听 start ----------------------------------------
    // 添加联系人
    @Override public void onContactAdded(String s) {
        contacts.add(s);
        refreshContacts();
    }

    // 删除联系人
    @Override public void onContactDeleted(String s) {
        contacts.remove(s);
        refreshContacts();
    }

    // 收到好友邀请
    @Override public void onContactInvited(String s, String s1) {
        // TODO: 2016/10/11 0011 显示好友邀请信息(同意，拒绝的交互按钮)
    }

    // 好友请求被同意
    @Override public void onContactAgreed(String s) {
        // TODO: 2016/10/11 0011 当对方同意你的好友申请, 显示对方已接受
    }

    // 好友请求被拒绝
    @Override public void onContactRefused(String s) {
        // TODO: 2016/10/11 0011 当对方拒绝你的好友申请，显示对方已拒绝(一般不做处理)
    }
    // 联系人监听 end --------

}
