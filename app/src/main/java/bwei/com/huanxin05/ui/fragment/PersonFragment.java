package bwei.com.huanxin05.ui.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.socks.library.KLog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import bwei.com.huanxin05.R;
import bwei.com.huanxin05.base.GlobalField;
import bwei.com.huanxin05.base.MyConnectionListener;
import bwei.com.huanxin05.ui.LoginActivity;

public class PersonFragment extends Fragment {


    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_person_username)
    TextView tvPersonUsername;
    private Unbinder bind;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_person, container, false);
        bind = ButterKnife.bind(this, view);
        tvTitle.setText("个人中心");
        String userName = getActivity().getSharedPreferences(GlobalField.USERINFO_FILENAME, Context.MODE_PRIVATE).getString("username", "hdl");
        tvPersonUsername.setText(userName);
        EMClient.getInstance().addConnectionListener(new MyConnectionListener(getActivity()));
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
            bind.unbind();
    }

    @OnClick({R.id.btn_person_add, R.id.btn_exit, R.id.btn_remove_friend})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_person_add:
                addFriend();
                break;
            case R.id.btn_remove_friend:
                removeFriend();
                break;
            case R.id.btn_exit:
                EMClient.getInstance().logout(true, new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        Log.e("main", "下线成功了");
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        getActivity().finish();
                    }

                    @Override
                    public void onError(int i, String s) {
                        Log.e("main", "下线失败了！" + s);
//                        Toast.makeText(MainActivity.this, "下线失败,多点几次吧( " + s + " )", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onProgress(int i, String s) {

                    }
                });//下线
                break;
        }
    }

    /**
     * 移除好友
     */
    private void removeFriend() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("删除好友");
        final EditText newFirendName = new EditText(getActivity());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        newFirendName.setLayoutParams(layoutParams);
        newFirendName.setHint("要删除的好友名");
        builder.setView(newFirendName);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setPositiveButton("移除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Thread() {
                    @Override
                    public void run() {
                        String firendName = newFirendName.getText().toString().trim();
                        try {
                            EMClient.getInstance().contactManager().deleteContact(firendName);
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * 添加好友
     */
    private void addFriend() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("添加好友");
        final EditText newFirendName = new EditText(getActivity());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        newFirendName.setLayoutParams(layoutParams);
        newFirendName.setHint("新好友用户名");
        builder.setView(newFirendName);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setPositiveButton("添加", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Thread() {
                    @Override
                    public void run() {
                        String firendName = newFirendName.getText().toString().trim();
                        try {
                            EMClient.getInstance().contactManager().addContact(firendName, "我是你的朋友");
                            KLog.e("添加好友成功,等待回应:" + firendName);

                        } catch (HyphenateException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
