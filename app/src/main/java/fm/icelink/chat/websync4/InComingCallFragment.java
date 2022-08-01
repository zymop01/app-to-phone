package fm.icelink.chat.websync4;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class InComingCallFragment extends Fragment {

    View view;
    public ImageView acceptButton, declineButton;
    public TextView inComingCallId;
    public boolean acceptButtonClicked = false, declineButtonClicked = false;
    public String inComingId = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_in_coming_call, container, false);

        inComingCallId = view.findViewById(R.id.InComingCallId);

        acceptButton = view.findViewById(R.id.InComingAcceptButton);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptButtonClicked = true;
            }
        });

        declineButton = view.findViewById(R.id.InComingDeclineButton);
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                declineButtonClicked = true;
            }
        });

        inComingCallId.setText("In Coming From: "+ inComingId);

        return view;
    }
}