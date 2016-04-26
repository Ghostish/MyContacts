package learn.android.kangel.mycontacts.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;

import learn.android.kangel.mycontacts.R;

/**
 * Created by Kangel on 2016/3/30.
 */
public class EditFieldAdapter extends RecyclerView.Adapter<EditFieldAdapter.ViewHolder> {
    private Context context;
    private int defaultSize = 3;
    public final static int TYPE_NAME_FIELD = 23, TYPE_OTHER_FILED = 33;

    public EditFieldAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_NAME_FIELD;
        } else {
            return TYPE_OTHER_FILED;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if (viewType == TYPE_NAME_FIELD) {
            v = LayoutInflater.from(context).inflate(R.layout.item_name_editor, parent, false);
            v.setTag(TYPE_NAME_FIELD);
        } else {
            v = LayoutInflater.from(context).inflate(R.layout.item_edit_file, parent, false);
            v.setTag(TYPE_OTHER_FILED);
        }
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String[] testString = new String[]{"12566", "4545fas", "fafasfs", "afsfa", "asfsfsa"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, testString);
        holder.typeSpinner.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return defaultSize;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView indicateIcon;
        Spinner typeSpinner;
        EditText inputField;
        ImageButton deleteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            Integer type = (Integer) itemView.getTag();
            if (type == TYPE_OTHER_FILED) {
                indicateIcon = (ImageView) itemView.findViewById(R.id.indicate_icon);
                typeSpinner = (Spinner) itemView.findViewById(R.id.type_spinner);
                inputField = (EditText) itemView.findViewById(R.id.input_field);
                deleteButton = (ImageButton) itemView.findViewById(R.id.delete_button);
            }
            if (type == TYPE_NAME_FIELD) {
                typeSpinner = (Spinner) itemView.findViewById(R.id.type_spinner);
                inputField = (EditText) itemView.findViewById(R.id.name_field);
            }
            inputField.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!s.toString().isEmpty()) {
                        defaultSize++;
                        notifyItemInserted(getAdapterPosition() + 1);
                    } else {
                        defaultSize--;
                        notifyItemRemoved(getAdapterPosition() + 1);
                    }
                }
            });
        }
    }
}
