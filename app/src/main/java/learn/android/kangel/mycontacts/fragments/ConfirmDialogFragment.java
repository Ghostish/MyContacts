package learn.android.kangel.mycontacts.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import learn.android.kangel.mycontacts.R;

/**
 * Created by Kangel on 2016/3/26.
 */
public class ConfirmDialogFragment extends DialogFragment {
    public interface onConfirmDialogButtonClickListener {
        void onNegativeButtonClick();
        void onPositiveButtonClick();
    }
    public static ConfirmDialogFragment newInstance(int titleResId, int msgResId, int style) {
        ConfirmDialogFragment f = new ConfirmDialogFragment();
        Bundle args = new Bundle();
        args.putInt("titleResId", titleResId);
        args.putInt("msgResId", msgResId);
        args.putInt("style", style);
        f.setArguments(args);
        return f;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        int style = args.getInt("style");
        int titleId = args.getInt("titleResId");
        int msgId = args.getInt("msgResId");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),style);
        builder.setTitle(titleId)
                .setMessage(msgId)
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (getActivity() instanceof onConfirmDialogButtonClickListener) {
                            ((onConfirmDialogButtonClickListener) getActivity()).onNegativeButtonClick();
                        }
                    }
                })
                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (getActivity() instanceof onConfirmDialogButtonClickListener) {
                            ((onConfirmDialogButtonClickListener) getActivity()).onPositiveButtonClick();
                        }
                    }
                });
        return builder.create();
    }
}
