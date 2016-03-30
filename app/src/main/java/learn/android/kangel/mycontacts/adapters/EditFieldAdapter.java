package learn.android.kangel.mycontacts.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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
    private final static int defaultSize = 3;
    public EditFieldAdapter(Context context) {
        this.context = context;
    }

    @Override

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_edit_file, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String[] testString = new String[]{"12566", "4545fas", "fafasfs", "afsfa", "asfsfsa"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, testString);
        holder.typeSpinner.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView indicateIcon;
        Spinner typeSpinner;
        EditText inputField;
        ImageButton deleteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            indicateIcon = (ImageView) itemView.findViewById(R.id.indicate_icon);
            typeSpinner = (Spinner) itemView.findViewById(R.id.type_spinner);
            inputField = (EditText) itemView.findViewById(R.id.input_field);
            deleteButton = (ImageButton) itemView.findViewById(R.id.delete_button);
        }
    }
}
