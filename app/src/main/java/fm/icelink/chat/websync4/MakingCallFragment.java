package fm.icelink.chat.websync4;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class MakingCallFragment extends Fragment {

    View view;
    public ImageView declineButton;
    public TextView callingId;
    public boolean declineButtonClicked = false;
    public String IdCallTo = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_making_call, container, false);

        callingId = view.findViewById(R.id.CallingId);

        callingId.setText("Calling: "+ IdCallTo);

        declineButton = view.findViewById(R.id.CallingDeclineButton);
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                declineButtonClicked = true;
            }
        });

        return view;
    }
}