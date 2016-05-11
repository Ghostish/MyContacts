package learn.android.kangel.mycontacts.fragments;

/**
 * Created by Kangel on 2016/4/29.
 */

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;


/**
 * Created by Kangel on 2016/2/20.
 */
public class ChooseItemDialog extends DialogFragment {
    public interface onDialogItemSelectListener {
        void onItemSelected(int which);
    }

    public static ChooseItemDialog newInstance(int ArrayResId) {
        ChooseItemDialog f = new ChooseItemDialog();
        Bundle args = new Bundle();
        args.putInt("data", ArrayResId);
        f.setArguments(args);
        return f;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        int resId = args.getInt("data");
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(resId, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (getActivity() instanceof onDialogItemSelectListener) {
                    ((onDialogItemSelectListener) getActivity()).onItemSelected(which);
                }
            }
        });
        return builder.create();

    }
}