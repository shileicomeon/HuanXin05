package bwei.com.huanxin05.base;

import android.app.Application;
import android.content.Intent;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.EaseUI;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.EaseNotifier;
import com.hyphenate.easeui.utils.EaseCommonUtils;

/**
 * Created by HDL on 2016/8/11.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        EMOptions options = new EMOptions();
      //  options.setMipushConfig("2882303761517500800", "5371750035800");//小米推送的
        // 默认添加好友时，是不需要验证的，改成需要验证,true:自动验证,false,手动验证
        options.setAcceptInvitationAlways(true);
        //初始化
        EaseUI.getInstance().init(this, options);
        //在做打包混淆时，关闭debug模式，避免消耗不必要的资源
//        EMClient.getInstance().setDebugMode(true);
        EMClient.getInstance().updateCurrentUserNick(getSharedPreferences(GlobalField.USERINFO_FILENAME, MODE_PRIVATE).getString("username", "hdl"));//设置推送的昵称
//        EaseUI easeUI = EaseUI.getInstance();
//        easeUI.getNotifier().setNotificationInfoProvider(new EaseNotifier.EaseNotificationInfoProvider() {
//
//            @Override
//            public String getTitle(EMMessage message) {
//                //修改标题,这里使用默认
//                return null;
//            }
//
//            @Override
//            public int getSmallIcon(EMMessage message) {
//                //设置小图标，这里为默认
//                return 0;
//            }
//
//            @Override
//            public String getDisplayedText(EMMessage message) {
//                // 设置状态栏的消息提示，可以根据message的类型做相应提示
//                String ticker = EaseCommonUtils.getMessageDigest(message, MyApplication.this);
//                if (message.getType() == EMMessage.Type.TXT) {
//                    ticker = ticker.replaceAll("\\[.{2,3}\\]", "[表情]");
//                }
//                EaseUser user = new EaseUser(message.getFrom());
//                if (user != null) {
//                    return user.getNick() + ": " + ticker;
//                } else {
//                    return message.getFrom() + ": " + ticker;
//                }
//            }
//
//            @Override
//            public String getLatestText(EMMessage message, int fromUsersNum, int messageNum) {
//                return null;
//                // return fromUsersNum + "个基友，发来了" + messageNum + "条消息";
//            }
//
//            @Override
//            public Intent getLaunchIntent(EMMessage message) {
//                //设置点击通知栏跳转事件
//                Intent intent = new Intent(MyApplication.this, ChatActivity.class);
//                EMMessage.ChatType chatType = message.getChatType();
//                if (chatType == EMMessage.ChatType.Chat) { // 单聊信息
//                    intent.putExtra("userId", message.getFrom());
//                    intent.putExtra("chatType", EaseConstant.CHATTYPE_SINGLE);
//                } else { // 群聊信息
//                    // message.getTo()为群聊id
//                    intent.putExtra("userId", message.getTo());
//                    if (chatType == EMMessage.ChatType.GroupChat) {
//                        intent.putExtra("chatType", EaseConstant.CHATTYPE_GROUP);
//                    } else {
//                        intent.putExtra("chatType", EaseConstant.CHATTYPE_CHATROOM);
//                    }
//
//                }
//                return intent;
//            }
//        });
    }
}
