package bwei.com.huanxin05.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import bwei.com.huanxin05.R;
import bwei.com.huanxin05.base.BaseActivity;
import bwei.com.huanxin05.base.GlobalField;
import bwei.com.huanxin05.base.MyConnectionListener;

public class LoginActivity extends BaseActivity {
    private static final int LOGIN_SUCCESS = 1;
    private static final int LOGIN_FAILED = 2;

    @BindView(R.id.et_main_userName)
    EditText etMainUserName;
    @BindView(R.id.et_main_pwd)
    EditText etMainPwd;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOGIN_SUCCESS:
                    Toast.makeText(LoginActivity.this, "登录成功了", Toast.LENGTH_SHORT).show();
                    break;
                case LOGIN_FAILED:
                    int code = msg.arg1;
                    switch (code) {
                        case 202:
                            Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        checkIsLogin();
        //注册一个监听连接状态的listener---------监听网络状态+账户是否在别处登录+请求服务器失败等.
        EMClient.getInstance().addConnectionListener(new MyConnectionListener(this));
    }

    /**
     * 检测是否已经登录过了,登录过了就进入主页面
     */
    private void checkIsLogin() {
        if (EMClient.getInstance().isLoggedInBefore()) {
            /**
             * 不管如何,登录完成之后都要加载所有的组和所有的聊天信息
             */
            EMClient.getInstance().groupManager().loadAllGroups();
            EMClient.getInstance().chatManager().loadAllConversations();
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            Log.e("main", "已经登录过了,进入下一个页面！");
//            Toast.makeText(LoginActivity.this, "已经登录过了,进入下一个页面", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 下线按钮,便于测试
     *
     * @param view
     */
    public void onLogout(View view) {
        EMClient.getInstance().logout(true, new EMCallBack() {
            @Override
            public void onSuccess() {
                Log.e("main", "下线成功了");
            }

            @Override
            public void onError(int i, String s) {
                Log.e("main", "下线失败了！" + s);
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });//下线
    }


    @OnClick(R.id.btn_main_login)
    public void onClick() {
        String userName = "" + etMainUserName.getText().toString().trim();
        String pwd = "" + etMainPwd.getText().toString().trim();
        login(userName, pwd);
    }

    /**
     * 登录
     *
     * @param userName 用户名
     * @param pwd      密码
     */
    private void login(final String userName, String pwd) {
        EMClient.getInstance().login(userName, pwd, new EMCallBack() {
            //回调
            @Override
            public void onSuccess() {
                /**
                 * 登录成功就调用加载数据
                 */
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();
                /**
                 * 保存用户名
                 */
                getSharedPreferences(GlobalField.USERINFO_FILENAME, MODE_PRIVATE).edit().putString("username", userName).commit();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                Log.e("main", "登录聊天服务器成功！");
                mHandler.sendEmptyMessage(LOGIN_SUCCESS);
                finish();
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                Log.e("main", "登录聊天服务器失败！" + message + " code = " + code);
                Message msg = mHandler.obtainMessage();
                msg.obj = message;
                msg.arg1 = code;
                msg.what = LOGIN_FAILED;
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 注册按钮
     *
     * @param view
     */
    public void onRegist(View view) {
        startActivity(new Intent(this, RegistActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(this);
    }
}
