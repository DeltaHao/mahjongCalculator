package pers.leonzlhao.mahjongcalculator;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.util.List;


public class PlayerListViewAdapter extends BaseAdapter {
    private Context mContext;
    private List<Player> mPlayers;
    private LayoutInflater mInflater;

    public PlayerListViewAdapter(Context context, List<Player> players) {
        mContext = context;
        mPlayers = players;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mPlayers.size();
    }

    @Override
    public Object getItem(int position) {
        return mPlayers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        //1、优化框架搭起
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.activity_player_listview_item, null);
            holder = new ViewHolder();
            holder.nickname = (EditText) convertView.findViewById(R.id.player_nickname);
            holder.points = (TextView) convertView.findViewById(R.id.player_points);
            holder.selfTouchButton = (Button) convertView.findViewById(R.id.player_selfTouch_btn);
            holder.fireOffButton = (Button) convertView.findViewById(R.id.player_fireOff_btn);
            holder.backOnlineButton = (Button) convertView.findViewById(R.id.player_backOnline_btn);
            holder.backOnlineButton.setEnabled(false);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //2、绑定数据到控件
        bindViewData(position, convertView, holder);

        //3、绑定监听事件
        bindViewClickListener(position, convertView, holder);

        //4、根据事件刷新页面
        refreshView(position, convertView, holder);

        return convertView;
    }

    private void refreshView(int position, View convertView, ViewHolder holder) {
        if(!mPlayers.get(position).isOnline) {
            holder.selfTouchButton.setEnabled(false);
            holder.fireOffButton.setEnabled(false);
            holder.backOnlineButton.setEnabled(true);
            holder.nickname.setTextColor(0xff777777);
        } else {
            holder.selfTouchButton.setEnabled(true);
            holder.fireOffButton.setEnabled(true);
            holder.backOnlineButton.setEnabled(false);
            holder.nickname.setTextColor(0xffffffff);
        }
    }

    class ViewHolder {
        private EditText nickname;
        private TextView points;
        private Button selfTouchButton;
        private Button fireOffButton;
        private Button backOnlineButton;
    }

    private void bindViewData(int position, View convertView, ViewHolder holder) {
        holder.nickname.setText(mPlayers.get(position).nickName);
        holder.points.setText("得分：" + Integer.toString(mPlayers.get(position).points));
    }

    private void bindViewClickListener(int position, View convertView, ViewHolder holder) {
        holder.nickname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == false) {
                    mPlayers.get(position).nickName = holder.nickname.getText().toString();
                }
                else {
                    holder.nickname.setSelection(holder.nickname.getText().length());
                }
            }
        });
        holder.selfTouchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelfTouchDialog(position);
            }
        });
        holder.fireOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFireOffDialog(position);
            }
        });
        holder.backOnlineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPlayerBackOnlineDialog(position);
            }
        });
        holder.points.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangePointsDialog(position);
            }
        });
    }

    private void showSelfTouchDialog(int position) {
        final EditText editText = new EditText(mContext);
        editText.setHint("在此输入金额");
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        AlertDialog.Builder selfTouchDialog = new AlertDialog.Builder(mContext);
        selfTouchDialog.setTitle(mPlayers.get(position).nickName + "自摸");
        selfTouchDialog.setView(editText);
        selfTouchDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!editText.getText().toString().isEmpty()) {
                    Toast.makeText(mContext, "恭喜" + mPlayers.get(position).nickName + "!", Toast.LENGTH_SHORT).show();
                    mPlayers.get(position).selfTouch(Integer.parseInt(editText.getText().toString()));
                } else {
                    Toast.makeText(mContext, "请检查输入!", Toast.LENGTH_SHORT).show();
                }
            }
        }).show();

    }

    private void showChangePointsDialog(int position) {
        final EditText editText = new EditText(mContext);
        editText.setHint("在此输入加减金额");
        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
        AlertDialog.Builder changePointsDialog = new AlertDialog.Builder(mContext);
        changePointsDialog.setView(editText);
        changePointsDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!editText.getText().toString().isEmpty()) {
                    mPlayers.get(position).points += Integer.parseInt(editText.getText().toString());
                } else {
                    Toast.makeText(mContext, "请检查输入!", Toast.LENGTH_SHORT).show();
                }
            }
        }).show();

    }

    private void showFireOffDialog(int position) {
        final String[] playerNames = new String[3];

        int i = 0;
        for (Player p: mPlayers) {
            if(p.isOnline && p != mPlayers.get(position)) {
                playerNames[i] = p.nickName;
                i++;
            }
        }

        final EditText editText = new EditText(mContext);
        editText.setHint("在此输入金额");
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        AlertDialog.Builder fireOffDialog = new AlertDialog.Builder(mContext);
        fireOffDialog.setTitle(mPlayers.get(position).nickName + "点炮");
        fireOffDialog.setView(editText);
        final int[] firedPlayerIndex = {-1};
        fireOffDialog.setSingleChoiceItems(playerNames, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (Player p: mPlayers) {
                    if (p.nickName.equals(playerNames[which])){
                        firedPlayerIndex[0] = p.index;
                    }
                }
            }
        });
        fireOffDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!editText.getText().toString().isEmpty() && firedPlayerIndex[0] != -1) {
                    Toast.makeText(mContext, "恭喜" + mPlayers.get(position).nickName + "!", Toast.LENGTH_SHORT).show();
                    mPlayers.get(position).fireOff(Integer.parseInt(editText.getText().toString()), mPlayers.get(firedPlayerIndex[0]));
                } else {
                    Toast.makeText(mContext, "请检查输入!", Toast.LENGTH_SHORT).show();
                }

            }
        }).show();
    }

    private void showPlayerBackOnlineDialog(int position) {
        final String[] playerNames = new String[4];
        int i = 0;

        for (Player p: mPlayers) {
            if(p.isOnline) {
                playerNames[i] = p.nickName;
                i++;
            }
        }

        AlertDialog.Builder playerBackOnlineDialog = new AlertDialog.Builder(mContext);
        playerBackOnlineDialog.setTitle("替换一个在场玩家");
        final int[] backPlayerIndex = {-1};
        playerBackOnlineDialog.setSingleChoiceItems(playerNames, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (Player p: mPlayers) {
                    if (p.nickName.equals(playerNames[which])){
                        backPlayerIndex[0] = p.index;
                    }
                }
            }
        });
        playerBackOnlineDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(backPlayerIndex[0] != -1) {
                    mPlayers.get(position).backOnline(mPlayers.get(backPlayerIndex[0]));
                    notifyDataSetChanged();
                } else {
                    Toast.makeText(mContext, "请检查输入!", Toast.LENGTH_SHORT).show();
                }
            }
        }).show();
    }
}
