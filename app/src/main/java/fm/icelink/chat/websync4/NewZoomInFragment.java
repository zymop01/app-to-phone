package fm.icelink.chat.websync4;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import fm.icelink.android.LayoutManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class NewZoomInFragment extends Fragment {

    View view;
    RelativeLayout newZoomInRelativeLayout;
    public boolean newZoomInRelativeLayoutClicked = false;
    Button newZoomInExitButton;
    public boolean newZoomInExitButtonClicked = false;
    public LayoutManager localLayoutManager = null;
    public RelativeLayout localContainer = null;
    public boolean zoomInLayoutManagerReady = false;
    public LayoutManager remoteLayoutManager = null;
    public RelativeLayout remoteContainer = null;

    public ImageView declineButton;
    public TextView callingId;
    public boolean declineButtonClicked = false;
    public String IdCallTo = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_new_zoom_in, container, false);

        /*
        newZoomInRelativeLayout = view.findViewById(R.id.NewZoomInRelativeLayout);
        newZoomInRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newZoomInRelativeLayoutClicked = true;
            }
        });

         */
        callingId = view.findViewById(R.id.InvitingId);

        callingId.setText("Inviting: " + IdCallTo);

        declineButton = view.findViewById(R.id.InvitingDeclineButton);
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                declineButtonClicked = true;
            }
        });

        return view;
    }
}