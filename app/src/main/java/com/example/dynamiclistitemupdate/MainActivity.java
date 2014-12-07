package com.example.dynamiclistitemupdate;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MyListFragment())
                    .commit();
        }
    }

    public static class MyListFragment extends ListFragment {

        private static final int REQ_DIALOG = 1;

        private int mItemIndex = -1;

        private String[] mItemData = {"-", "-", "-", "-", "-"};

        public MyListFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            setListAdapter(new ArrayAdapter<>(getActivity(),
                    android.R.layout.simple_list_item_1, mItemData));
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            super.onListItemClick(l, v, position, id);

            mItemIndex = position - l.getHeaderViewsCount();

            DialogFragment newFragment = new MyDialogFragment();
            // ダイアログフラグメントに対して、結果の通知先を指定。結果は onActivityResult を呼び出すことで通知される
            newFragment.setTargetFragment(this, REQ_DIALOG);
            newFragment.show(getChildFragmentManager(), "dialog");
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode != REQ_DIALOG || resultCode != RESULT_OK) {
                return;
            }

            if (mItemIndex < 0 || data == null
                    || data.getStringExtra(MyDialogFragment.RESULT_EXTRA_KEY_TEXT) == null) {
                return;
            }

            // ArrayAdapter の中の配列を更新し、notifyDataSetChanged() で更新を通知する
            mItemData[mItemIndex] = data.getStringExtra(MyDialogFragment.RESULT_EXTRA_KEY_TEXT);
            final ArrayAdapter<?> adapter = (ArrayAdapter<?>) getListAdapter();
            adapter.notifyDataSetChanged();
        }
    }

    public static class MyDialogFragment extends DialogFragment {
        public static final String RESULT_EXTRA_KEY_TEXT = "text";

        public MyDialogFragment() {
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("ダイアログ")
                    .setPositiveButton("はい", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            sendResult(Activity.RESULT_OK, "yes");
                        }
                    })
                    .setNegativeButton("いいえ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sendResult(Activity.RESULT_OK, "no");
                        }
                    });
            return builder.create();
        }

        /**
         * 結果をダイアログ表示元へ通知するためのメソッド。
         *
         * targetFragment に通知元がセットされているので、onActivityResult を呼び出すことで通知を行う。
         */
        private void sendResult(int resultCode, String str) {
            final Intent data = new Intent();

            if (str != null) {
                data.putExtra(RESULT_EXTRA_KEY_TEXT, str);
            }

            // 結果を通知する
            getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, data);
        }
    }
}
