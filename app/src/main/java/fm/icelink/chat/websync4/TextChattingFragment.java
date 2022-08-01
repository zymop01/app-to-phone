package fm.icelink.chat.websync4;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class TextChattingFragment extends androidx.fragment.app.Fragment {

    public View view;
    private App app;
    public TextView textView;
    private EditText editText;
    private Button submitButton, endButton;
    public boolean endButtonClicked = false;
    //private TextChatFragment.OnTextReadyListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = App.getInstance(null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_text_chatting, container, false);
        textView = (TextView)view.findViewById(R.id.log2);
        editText = (EditText)view.findViewById(R.id.text2);
        submitButton = (Button)view.findViewById(R.id.send2);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = editText.getText().toString().trim();
                editText.setText("");

                if (msg.length() > 0) {
                    ((SessionSelectorActivity)getActivity()).onReceivedText("Me", msg);
                    ((SessionSelectorActivity)getActivity()).writeString(msg);
                }
            }
        });


        return view;
    }
}