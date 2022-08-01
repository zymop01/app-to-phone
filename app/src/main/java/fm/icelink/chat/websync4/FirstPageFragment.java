package fm.icelink.chat.websync4;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class FirstPageFragment extends Fragment {

    View view;
    public Button callButton, contactButton, historyButton, zoomButton, idButton, cameraButton;
    public Button settingButton, missingButton;
    public ImageView addButton;
    public boolean zoomButtonClicked = false, contactButtonClicked = false;
    public boolean historyButtonClicked = false, idButtonClicked = false;
    public boolean addButtonClicked = false, callButtonClicked = false;
    public boolean cameraButtonClicked = false, settingButtonClicked = false;
    public boolean missingButtonClicked = false;
    public ListView mContactListView, mRingtoneListView;
    public TextView currentIdTv;
    public boolean firstPageReady = false;
    public String typeOfLast = "contactList";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_first_page, container, false);

        currentIdTv = view.findViewById(R.id.TitleInNewMain);

        idButton = view.findViewById(R.id.SetCurrentIdButtonInNewMain);
        idButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idButtonClicked = true;
            }
        });

        addButton = view.findViewById(R.id.AddButtonInNewMain);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showPopUpAdd();
                addButtonClicked = true;
            }
        });


        callButton = view.findViewById(R.id.CallButtonInNewMain);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showPopUpWindow("YangMing");
                callButtonClicked = true;
            }
        });

        contactButton = view.findViewById(R.id.ContactButtonInNewMain);
        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeOfLast = "contactList";
                //showHistoryOnListView(contactDB);
                contactButtonClicked = true;
            }
        });

        historyButton = view.findViewById(R.id.HistoryButtonInNewMain);
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeOfLast = "history";
                //showHistoryOnListView(contactDB);
                historyButtonClicked = true;
            }
        });

        missingButton = view.findViewById(R.id.MissingCallsButtonInNewMain);
        missingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeOfLast = "missing calls";
                //showHistoryOnListView(contactDB);
                missingButtonClicked = true;
            }
        });

        /*
        zoomButton = view.findViewById(R.id.ZoomButtonInNewMain);
        zoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showHistoryOnListView(contactDB);
                zoomButtonClicked = true;
            }
        });

         */

        cameraButton = view.findViewById(R.id.CameraButtonInNewMain);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showHistoryOnListView(contactDB);
                cameraButtonClicked = true;
            }
        });

        settingButton = view.findViewById(R.id.SettingButtonInNewMain);
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingButtonClicked = true;
            }
        });


        mContactListView =  view.findViewById(R.id.contactListViewInNewMain);

        mContactListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Contacts clickedContact = (Contacts) parent.getItemAtPosition(position);

                /*
                if (DeleteButtonClicked) {
                    if (typeOfLast.equals("contactList"))
                        ((SessionSelectorActivity)getActivity()).deleteOneContact(clickedContact);
                    else if (typeOfLast.equals("history"))
                        ((SessionSelectorActivity)getActivity()).deleteHistory(clickedContact);
                    else if (typeOfLast.equals("id"))
                        ((SessionSelectorActivity)getActivity()).deleteId(clickedContact);
                }
                else {
                    ((SessionSelectorActivity)getActivity()).callContactClicked(clickedContact.getContact());
                }

                 */
                ((SessionSelectorActivity)getActivity()).callContactClicked(clickedContact.getContact());
            }
        });

        mContactListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Contacts clickedContact = (Contacts) parent.getItemAtPosition(position);
                ((SessionSelectorActivity)getActivity()).popUpDialogOfDelete(clickedContact, typeOfLast);
                return true;
            }
        });


        mRingtoneListView = view.findViewById(R.id.ringtoneListViewInNewMain);
        mRingtoneListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Contacts clickedContact = (Contacts) mContactListView.getItemAtPosition(position);
                ((SessionSelectorActivity)getActivity()).setRingtoneClicked(clickedContact);
            }
        });

        firstPageReady = true;
        return view;
    }
}