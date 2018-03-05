package bwei.com.huanxin05.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.hyphenate.EMContactListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.ui.EaseContactListFragment;
import com.hyphenate.easeui.ui.EaseConversationListFragment;
import com.hyphenate.exceptions.HyphenateException;
import com.socks.library.KLog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;
import bwei.com.huanxin05.R;
import bwei.com.huanxin05.base.BaseActivity;
import bwei.com.huanxin05.base.MyConnectionListener;
import bwei.com.huanxin05.runtimepermissions.PermissionsManager;
import bwei.com.huanxin05.runtimepermissions.PermissionsResultAction;
import bwei.com.huanxin05.ui.fragment.PersonFragment;

public class MainActivity extends BaseActivity {
    EaseConversationListFragment conversationListFragment;
    private static EaseContactListFragment contactListFragment;
    private static EMMessageListener emMessageListener;
    private static PersonFragment personFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        /**
         * 请求所有必要的权限----
         */
        PermissionsManager.getInstance().requestAllManifestPermissionsIfNecessary(this, new PermissionsResultAction() {
            @Override
            public void onGranted() {
//				Toast.makeText(MainActivity.this, "All permissions have been granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDenied(String permission) {
                //Toast.makeText(MainActivity.this, "Permission " + permission + " has been denied", Toast.LENGTH_SHORT).show();
            }
        });
        //注册一个监听连接状态的listener
        EMClient.getInstance().addConnectionListener(new MyConnectionListener(this));
        emMessageListener = new EMMessageListener() {

            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                //收到消息----刷新一下当前页面喽
                conversationListFragment.refresh();
                EMClient.getInstance().chatManager().importMessages(messages);//保存到数据库
            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> messages) {
                //收到透传消息
            }

            @Override
            public void onMessageRead(List<EMMessage> list) {

            }

            @Override
            public void onMessageDelivered(List<EMMessage> list) {

            }

            @Override
            public void onMessageRecalled(List<EMMessage> list) {

            }

            public void onMessageReadAckReceived(List<EMMessage> messages) {
                //收到已读回执
            }

            public void onMessageDeliveryAckReceived(List<EMMessage> message) {
                //收到已送达回执
            }

            @Override
            public void onMessageChanged(EMMessage message, Object change) {
                //消息状态变动
            }
        };
        EMClient.getInstance().chatManager().addMessageListener(emMessageListener);

    }

    private void initView() {
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        EMClient.getInstance().contactManager().setContactListener(new EMContactListener() {

            public void onContactAgreed(String username) {

                //好友请求被同
                //contactListFragment.refresh();
            }

            public void onContactRefused(String username) {
                //好友请求被拒绝
            }

            @Override
            public void onContactInvited(String username, String reason) {
                //收到好友邀请

            }

            @Override
            public void onFriendRequestAccepted(String s) {

            }

            @Override
            public void onFriendRequestDeclined(String s) {

            }

            @Override
            public void onContactDeleted(String username) {
                KLog.e("好友被删除了" + username);
                //被删除时回调此方法

                new Thread() {//需要在子线程中调用
                    @Override
                    public void run() {
                        //需要设置联系人列表才能启动fragment
                        contactListFragment.setContactsMap(getContact());
                        contactListFragment.refresh();
                    }
                }.start();
            }


            @Override
            public void onContactAdded(String username) {
                //增加了联系人时回调此方法
                KLog.e("添加好友了" + username);

                new Thread() {//需要在子线程中调用
                    @Override
                    public void run() {
                        //需要设置联系人列表才能启动fragment
                        contactListFragment.setContactsMap(getContact());
                        contactListFragment.refresh();
                    }
                }.start();

            }
        });


        personFragment = new PersonFragment();
        contactListFragment = new EaseContactListFragment();
        new Thread() {//需要在子线程中调用
            @Override
            public void run() {
                //需要设置联系人列表才能启动fragment
                contactListFragment.setContactsMap(getContact());
            }
        }.start();

        //设置item点击事件
        contactListFragment.setContactListItemClickListener(new EaseContactListFragment.EaseContactListItemClickListener() {

            @Override
            public void onListItemClicked(EaseUser user) {
                startActivity(new Intent(MainActivity.this, ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, user.getUsername()));
            }
        });
        conversationListFragment = new EaseConversationListFragment();
        conversationListFragment.setConversationListItemClickListener(new EaseConversationListFragment.EaseConversationListItemClickListener() {

            @Override
            public void onListItemClicked(EMConversation conversation) {
                //进入聊天页面
                startActivity(new Intent(MainActivity.this, ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, conversation.conversationId()));
            }
        });
        getSupportFragmentManager().beginTransaction().add(R.id.fl_chat, conversationListFragment).commit();
    }

    @OnClick({R.id.tv_chat_list, R.id.tv_contact_list, R.id.tv_persion})
    public void onClick(View view) {
        switch (view.getId()) {
            default:break;
            case R.id.tv_chat_list:
                getSupportFragmentManager().beginTransaction().replace(R.id.fl_chat, conversationListFragment).commit();
                break;
            case R.id.tv_contact_list:
                getSupportFragmentManager().beginTransaction().replace(R.id.fl_chat, contactListFragment).commit();
                break;
            case R.id.tv_persion:
                getSupportFragmentManager().beginTransaction().replace(R.id.fl_chat, personFragment).commit();
                break;
        }
    }

    private Map<String, EaseUser> getContact() {
        Map<String, EaseUser> map = new HashMap<>();
        try {
            List<String> userNames = EMClient.getInstance().contactManager().getAllContactsFromServer();
//            KLog.e("......有几个好友:" + userNames.size());
            for (String userId : userNames) {
//                KLog.e("好友列表中有 : " + userId);
                map.put(userId, new EaseUser(userId));
            }
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
        return map;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EMClient.getInstance().chatManager().removeMessageListener(emMessageListener);
    }
}
