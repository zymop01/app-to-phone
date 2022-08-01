package fm.icelink.chat.websync4;

import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import fm.icelink.android.LayoutManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class NewZoomOutFragment extends Fragment {

    View view;
    RelativeLayout newZoomOutRelativeLayout;
    public boolean newZoomOutRelativeLayoutClicked = false;
    public LayoutManager localLayoutManager = null;
    public RelativeLayout localContainer = null;
    ImageView MuteAudioButton, newZoomOutExitButton, MuteVideoButton, ChatButton, InviteButton;
    public boolean newZoomOutExitButtonClicked = false, MuteAudioButtonClicked = false;
    public boolean MuteVideoButtonClicked = false, ChatButtonClicked = false;
    public boolean InviteButtonClicked = false, submitButtonClicked = false;

    public boolean textViewClicked = false, editTextClicked = false;

    public boolean audioMuted = false, videoMuted = false, chatOn = false, inviteOn = false;
    public boolean zoomOutLayoutManagerReady = false, isZoomOutLayoutManagerReady = false;
    public LayoutManager remoteLayoutManager = null;
    public RelativeLayout remoteContainer = null;
    public TextView textView;
    private EditText editText;
    private Button submitButton, historyButton;
    public boolean newChattingReady = false, endButtonClicked = false, historyButtonClicked = false;
    public RelativeLayout NewTextContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_new_zoom_out, container, false);

        newZoomOutRelativeLayout = view.findViewById(R.id.NewZoomOutRelativeLayout);
        newZoomOutRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newZoomOutRelativeLayoutClicked = true;
            }
        });

        MuteAudioButton = view.findViewById(R.id.NewZoomOutMuteAudioButton);
        MuteAudioButton.setBackgroundColor(Color.parseColor("#3FE533"));
        MuteAudioButton.setVisibility(View.INVISIBLE);
        MuteAudioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MuteAudioButtonClicked = true;
            }
        });

        MuteVideoButton = view.findViewById(R.id.NewZoomOutMuteVideoButton);
        MuteVideoButton.setBackgroundColor(Color.parseColor("#3FE533"));
        MuteVideoButton.setVisibility(View.INVISIBLE);
        MuteVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MuteVideoButtonClicked = true;
            }
        });

        ChatButton = view.findViewById(R.id.NewZoomOutTextButton);
        ChatButton.setVisibility(View.INVISIBLE);
        ChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatButtonClicked = true;
            }
        });

        InviteButton = view.findViewById(R.id.NewZoomOutInviteButton);
        InviteButton.setVisibility(View.INVISIBLE);
        InviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InviteButtonClicked = true;
            }
        });


        newZoomOutExitButton = view.findViewById(R.id.NewZoomOutExitButton);
        newZoomOutExitButton.setVisibility(View.INVISIBLE);
        newZoomOutExitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newZoomOutExitButtonClicked = true;
            }
        });

        localContainer = (RelativeLayout) view.findViewById(R.id.NewZoomInLocalContainer);
        localLayoutManager = new LayoutManager(localContainer);

        remoteContainer = (RelativeLayout) view.findViewById(R.id.NewZoomInRemoteContainer);
        remoteLayoutManager = new LayoutManager(remoteContainer);
        zoomOutLayoutManagerReady = true;
        isZoomOutLayoutManagerReady = true;


        NewTextContainer = view.findViewById(R.id.NewTextContainer);
        NewTextContainer.setVisibility(View.INVISIBLE);

        textView = (TextView)view.findViewById(R.id.NewChattingLog);
        editText = (EditText)view.findViewById(R.id.NewChattingET);
        submitButton = (Button)view.findViewById(R.id.NewChattingSendButton);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewClicked = true;
            }
        });

        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextClicked = true;
            }
        });


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = editText.getText().toString().trim();
                editText.setText("");
                submitButtonClicked = true;

                if (msg.length() > 0) {
                    ((SessionSelectorActivity)getActivity()).onReceivedText("Me", msg);
                    ((SessionSelectorActivity)getActivity()).writeString(msg);
                    //((SessionSelectorActivity)getActivity()).addOneMsg("Me:" + msg);
                }
            }
        });

        historyButton = view.findViewById(R.id.NewChattingChatHistoryButton);
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                historyButtonClicked = true;
            }
        });

        newChattingReady = true;

        return view;
    }



}