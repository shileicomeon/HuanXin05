package bwei.com.huanxin05.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.socks.library.KLog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import bwei.com.huanxin05.R;
import bwei.com.huanxin05.base.BaseActivity;
import bwei.com.huanxin05.base.MyConnectionListener;
import bwei.com.huanxin05.utils.ToastUtils;

public class RegistActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_register_phone)
    EditText etRegisterPhone;
    @BindView(R.id.et_register_pwd)
    EditText etRegisterPwd;
    @BindView(R.id.et_register_repwd)
    EditText etRegisterRepwd;
    private static final int REG_SUCCESS = 1;
    private static final int REG_FAILED = 2;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REG_SUCCESS:
                    ToastUtils.showToast(mActivity, "注册成功,请先登录");
                    break;
                case REG_FAILED:
                    if (mActivity == null) KLog.e("是空了");
                    ToastUtils.showToast(mActivity, "该用户已经注册过了,请换一个号码再试");
//                    Toast.makeText(RegistActivity.this, "注册失败了", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        ButterKnife.bind(this);
        tvTitle.setText("用户注册");
        mActivity=this;
        //注册一个监听连接状态的listener
        EMClient.getInstance().addConnectionListener(new MyConnectionListener(this));
    }

    @OnClick(R.id.btn_register)
    public void onClick() {
        String userPhone = etRegisterPhone.getText().toString().trim();
        String pwd = etRegisterPwd.getText().toString().trim();
        String rePwd = etRegisterRepwd.getText().toString().trim();
        if (TextUtils.isEmpty(userPhone)) {
            ToastUtils.showToast(mActivity, "用户名不能为空");
        }
        if (TextUtils.isEmpty(pwd) || TextUtils.isEmpty(rePwd)) {
            ToastUtils.showToast(mActivity, "密码不能为空");
        }
        if (!pwd.equals(rePwd)) {
            ToastUtils.showToast(mActivity, "两次密码输入不一致,请重新输入");
            etRegisterRepwd.setText("");
            etRegisterRepwd.setFocusable(true);
        }
        regist(userPhone, pwd);
    }

    /**
     * 用户注册(这里只是demo,没有自己写服务器,实际开发中是要通过后台服务器来注册,注册成功之后服务器再注册环信账户,为了简化,这里直接注册[官方都不建议这样做哦])
     *
     * @param userPhone
     * @param pwd
     */
    private void regist(final String userPhone, final String pwd) {
        new Thread() {//网络访问需要在子线程中进行
            @Override
            public void run() {
                //注册失败会抛出HyphenateException
                try {
                    EMClient.getInstance().createAccount(userPhone, pwd);//同步方法
                    mHandler.sendEmptyMessage(REG_SUCCESS);
                    startActivity(new Intent(mActivity, LoginActivity.class));
                    finish();
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    KLog.e("错误信息:" + e.getMessage());
                    e.getErrorCode();
                    mHandler.sendEmptyMessage(REG_FAILED);
                }
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
         mHandler.removeCallbacksAndMessages(this);
    }
}
