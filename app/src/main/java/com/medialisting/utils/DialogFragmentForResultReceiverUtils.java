package com.medialisting.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.DialogFragment;

@SuppressLint("ValidFragment")
public class DialogFragmentForResultReceiverUtils extends DialogFragment {
    private View rootView;
    private Context context;
    private Intent intent;
    private ResultListener listener;

    public final int REQUEST_CODE = 1;

    /**
     * By Pooja<br/>
     * <p/>
     * Use this instead of calling startActivityForResult()
     *
     * ******************
     * HOW TO USE
     * ******************
     * new DialogFragmentForResultReceiverUtils(
     *         context,
     *         new Intent(context, SelectContactForChatActivity.class),
     *         new DialogFragmentForResultReceiverUtils.ResultListener() {
     *             @Override
     *             public void onActivityResult(Intent data) {
     *                 String name = data.getStringExtra("name");
     *                 String number = data.getStringExtra("number");
     *             }
     *         }
     * ).show(getSupportFragmentManager(), "");
     *
     * @param context
     * @param intent    call as startActivityForResult()
     * @param listener  Result receive in this listener onActivityResult()
     */
    public DialogFragmentForResultReceiverUtils(Context context, Intent intent, ResultListener listener) {
        this.context = context;
        this.intent = intent;
        this.listener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = createLayoutWithoutXml();

        startActivityForResult(intent, REQUEST_CODE);

        return rootView;
    }

    /**
     * By Bhavesh<br/>
     * <p/>
     *
     * @return
     */
    private View createLayoutWithoutXml() {
        LinearLayout layout = new LinearLayout(context);
        // Define the LinearLayout's characteristics
        layout.setGravity(Gravity.CENTER);
        layout.setOrientation(LinearLayout.VERTICAL);

        // Set generic layout parameters
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(params);

        return layout;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
          /*  if (data != null) {*/
                listener.onActivityResult(data,requestCode,resultCode);
           // }
            dismissAllowingStateLoss();
        }
    }

    public interface ResultListener {
        /**
         * By Bhavesh<br/>
         * <p/>
         *
         * Call only when data selected by user, otherwise this callback will not call
         *  @param data Never null
         * @param requestCode
         * @param resultCode
         */
        void onActivityResult(Intent data, int requestCode, int resultCode);
    }
}