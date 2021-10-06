package pers.leonzlhao.mahjongcalculator;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private Button addPlayerButton;
    private GameManager manager;
    private ListView playerListView;
    private PlayerListViewAdapter playerListViewAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_listview);
        manager = GameManager.getInstance();
        playerListView = (ListView) findViewById(R.id.player_listView);
        playerListViewAdapter = new PlayerListViewAdapter(MainActivity.this, manager.players);
        playerListView.setAdapter(playerListViewAdapter);

        addPlayerButton = findViewById(R.id.add_player_btn);
        addPlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReplacePlayerDialog();
            }
        });
    }

    private void showReplacePlayerDialog() {
        final String[] playerNames = new String[4];
        int i = 0;

        for (Player p: manager.players) {
            if(p.isOnline) {
                playerNames[i] = p.nickName;
                i++;
            }
        }

        AlertDialog.Builder ReplacePlayerDialog = new AlertDialog.Builder(MainActivity.this);
        ReplacePlayerDialog.setTitle("替换一个在场玩家");
        final int[] replacedPlayerIndex = {-1};
        ReplacePlayerDialog.setSingleChoiceItems(playerNames, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (Player p: manager.players) {
                    if (p.nickName.equals(playerNames[which])){
                        replacedPlayerIndex[0] = p.index;
                    }
                }
            }
        });
        ReplacePlayerDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(replacedPlayerIndex[0] != -1) {
                    manager.addPlayer(manager.players.get(replacedPlayerIndex[0]));
                    playerListViewAdapter.notifyDataSetChanged();
                }
            }
        }).show();
    }



    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null) {
                    assert v != null;
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    /** here */
                    v.clearFocus();
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        return getWindow().superDispatchTouchEvent(ev) || onTouchEvent(ev);
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            return !(event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom);
        }
        return false;
    }

}