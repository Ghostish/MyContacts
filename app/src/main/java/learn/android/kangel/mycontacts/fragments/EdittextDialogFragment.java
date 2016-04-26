package learn.android.kangel.mycontacts.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.widget.EditText;

import learn.android.kangel.mycontacts.R;

/**
 * Created by Kangel on 2016/4/8.
 */
public class EditTextDialogFragment extends DialogFragment {

    private String mDisplayText;
    private int mInputType = InputType.TYPE_CLASS_TEXT;

    public interface onEditDialogButtonClickListener {
        void onPositiveButtonClick(String s);
    }

    public static EditTextDialogFragment newInstance(int titleResId, int style) {
        EditTextDialogFragment f = new EditTextDialogFragment();
        Bundle args = new Bundle();
        args.putInt("style", style);
        args.putInt("titleResId", titleResId);
        f.setArguments(args);
        return f;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        int style = args.getInt("style");
        int titleId = args.getInt("titleResId");

        final EditText editText = new EditText(getActivity());
        editText.setInputType(mInputType);
        editText.setSingleLine();
        editText.setText(mDisplayText);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), style);
        builder
                .setTitle(titleId)
                .setNegativeButton(R.string.dialog_cancel, null)
                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (getActivity() instanceof onEditDialogButtonClickListener) {
                            ((onEditDialogButtonClickListener) getActivity()).onPositiveButtonClick(editText.getText().toString());

                        }
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.setView(editText, 50, 50, 50, 50);
        return dialog;
    }

    public void setDisplayTextText(String text) {
        mDisplayText = text;
    }

    public void setInputType(int inputType) {
        this.mInputType = inputType;
    }
}
