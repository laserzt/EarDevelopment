package com.eardevelopment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class Settings extends ActionBarActivity implements OnCheckedChangeListener, OnClickListener, OnFocusChangeListener, OnLongClickListener, OnEditorActionListener {

	boolean[] output_notes;
	static final int CHECKBOXES_ID = 5000;
	CheckBox[] checkboxes; 
	int radioButtonChecked;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.interval_practice, menu);
		return true;
	}
    
	@Override
	public void onStart()
	{
		super.onStart();
		RelativeLayout rl = (RelativeLayout)findViewById(R.id.settings_layout);
		rl.setBackgroundColor(MainActivity.BackGroundColor);
		
		
		int maxNumCheckboxes=Math.max(Math.max(MainActivity.IntervalsNames.length,MainActivity.ChordsNames.length), MainActivity.NotesNames.length);
		checkboxes = new CheckBox[maxNumCheckboxes];
		RelativeLayout rl_checkboxes = (RelativeLayout)findViewById(R.id.settings_checkboxes_layout);
		for (int i=0;i<maxNumCheckboxes;++i)
		{
			checkboxes[i]=new CheckBox(this);
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			checkboxes[i].setId(CHECKBOXES_ID+i);
			checkboxes[i].setTextColor(MainActivity.ForGroundColor);
			checkboxes[i].setOnClickListener(this);
			lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			if (i>0)
				lp.addRule(RelativeLayout.BELOW,checkboxes[i-1].getId());
			else
				lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			rl_checkboxes.addView(checkboxes[i],lp);
		}
		((RadioButton)findViewById(R.id.radio_button_Concert)).setTextColor(MainActivity.ForGroundColor);
		((RadioButton)findViewById(R.id.radio_button_Bb)).setTextColor(MainActivity.ForGroundColor);
		((RadioButton)findViewById(R.id.radio_button_Eb)).setTextColor(MainActivity.ForGroundColor);
		((RadioButton)findViewById(R.id.radio_button_Concert)).setOnClickListener(this);
		((RadioButton)findViewById(R.id.radio_button_Bb)).setOnClickListener(this);
		((RadioButton)findViewById(R.id.radio_button_Eb)).setOnClickListener(this);
		((EditText)findViewById(R.id.edittextNotesnum)).setOnFocusChangeListener(this);
		((EditText)findViewById(R.id.edittextNotesnum)).setOnEditorActionListener(this);
		((EditText)findViewById(R.id.edittextNotesnum)).setOnLongClickListener(this);
		((RadioButton)findViewById(R.id.radio_button_single_note_settings)).setTextColor(MainActivity.ForGroundColor);
		((RadioButton)findViewById(R.id.radio_button_interval_settings)).setTextColor(MainActivity.ForGroundColor);
		((RadioButton)findViewById(R.id.radio_button_chord_settings)).setTextColor(MainActivity.ForGroundColor);
		((TextView)findViewById(R.id.textNotesnum)).setTextColor(MainActivity.ForGroundColor);
		((EditText)findViewById(R.id.edittextNotesnum)).setTextColor(MainActivity.ForGroundColor);		
		((RadioButton)findViewById(R.id.radio_button_single_note_settings)).setChecked(true);
		radioButtonChecked = R.id.radio_button_single_note_settings;
		((CheckBox)findViewById(R.id.checkboxEurope)).setTextColor(MainActivity.ForGroundColor);
		((CheckBox)findViewById(R.id.checkboxEurope)).setOnClickListener(this);
		((CheckBox)findViewById(R.id.checkboxEurope)).setOnCheckedChangeListener(this);		
		((CheckBox)findViewById(R.id.checkboxOneOctave)).setTextColor(MainActivity.ForGroundColor);
		((CheckBox)findViewById(R.id.checkboxOneOctave)).setOnClickListener(this);
		((RadioGroup)findViewById(R.id.radio_group_tuning)).setOnClickListener(this);
		((RadioButton)findViewById(R.id.radio_button_Concert)).setChecked(!MainActivity.Bbinstrument && !MainActivity.Ebinstrument);
		((RadioButton)findViewById(R.id.radio_button_Bb)).setChecked(MainActivity.Bbinstrument);
		((RadioButton)findViewById(R.id.radio_button_Eb)).setChecked(MainActivity.Ebinstrument);
		((CheckBox)findViewById(R.id.checkboxEurope)).setChecked(MainActivity.europeanNoteNames);
		((CheckBox)findViewById(R.id.checkboxOneOctave)).setChecked(MainActivity.onlyOneOctave);
		
		((RadioButton)findViewById(R.id.radio_button_single_note_settings)).setOnCheckedChangeListener(this);
		((RadioButton)findViewById(R.id.radio_button_interval_settings)).setOnCheckedChangeListener(this);
		((RadioButton)findViewById(R.id.radio_button_chord_settings)).setOnCheckedChangeListener(this);		
		onCheckedChanged((CheckBox)findViewById(R.id.checkboxEurope), MainActivity.europeanNoteNames);

		((EditText)findViewById(R.id.edittextNotesnum)).setText(String.valueOf(MainActivity.numOfNotesOnPianoInPractises));
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_settings,
					container, false);
			return rootView;
		}
	}
	public boolean Save(int id)
	{		
		if (id == -1)
		{
			RadioGroup r = (RadioGroup)findViewById(R.id.radio_group_settings_type);
			id = r.getCheckedRadioButtonId();
		}		
		boolean allFalse=true;
		switch(id)
		{
			case R.id.radio_button_interval_settings:
			{
				for (int i=0;i<MainActivity.intervalsToPractice.length;++i)
					MainActivity.intervalsToPractice[i]=checkboxes[i].isChecked();					
				for (int i=0;i<MainActivity.intervalsToPractice.length;++i)
					allFalse&=(!MainActivity.intervalsToPractice[i]);
				break;
			}
			case R.id.radio_button_single_note_settings:
			{
				for (int i=0;i<MainActivity.notesToPractice.length;++i)
					MainActivity.notesToPractice[i]=checkboxes[i].isChecked();
				for (int i=0;i<MainActivity.notesToPractice.length;++i)
					allFalse&=(!MainActivity.notesToPractice[i]);
				break;
			}
			case R.id.radio_button_chord_settings:
			{
				for (int i=0;i<MainActivity.chordsToPractice.length;++i)
					MainActivity.chordsToPractice[i]=checkboxes[i].isChecked();
				for (int i=0;i<MainActivity.chordsToPractice.length;++i)
					allFalse&=(!MainActivity.chordsToPractice[i]);
				break;
			}
			default:
				return false;
		}
		MainActivity.Bbinstrument=((RadioButton)findViewById(R.id.radio_button_Bb)).isChecked();
		MainActivity.Ebinstrument=((RadioButton)findViewById(R.id.radio_button_Eb)).isChecked();
		MainActivity.onlyOneOctave=((CheckBox)findViewById(R.id.checkboxOneOctave)).isChecked();
		MainActivity.numOfNotesOnPianoInPractises=Integer.parseInt(((EditText)findViewById(R.id.edittextNotesnum)).getText().toString());
		if (MainActivity.numOfNotesOnPianoInPractises>100)
		{
			MainActivity.numOfNotesOnPianoInPractises=100;
			((EditText)findViewById(R.id.edittextNotesnum)).setText("100");
		    ErrorBox("Maximum 100 notes in the piano!"); 
		}
		if (MainActivity.numOfNotesOnPianoInPractises<5)
		{
			MainActivity.numOfNotesOnPianoInPractises=5;
			((EditText)findViewById(R.id.edittextNotesnum)).setText("5");
			ErrorBox("Minimum 5 notes in the piano!");
		}
		switch(id)
		{
			case R.id.radio_button_interval_settings:
			{				
				MainActivity.intervalsUseOnlySingleNotes=((CheckBox)findViewById(R.id.checkboxEurope)).isChecked();
				break;
			}
			case R.id.radio_button_single_note_settings:
			{
				MainActivity.europeanNoteNames=((CheckBox)findViewById(R.id.checkboxEurope)).isChecked();
				break;
			}
			case R.id.radio_button_chord_settings:
			{
				MainActivity.chordsUseOnlySingleNotes=((CheckBox)findViewById(R.id.checkboxEurope)).isChecked();
				break;
			}
		}						
		if (allFalse)
		{
		    ErrorBox("Must select at least one option"); 
		    return false;
		}
		return true;
	}
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (buttonView.getId()==R.id.checkboxEurope && ((RadioGroup)findViewById(R.id.radio_group_settings_type)).getCheckedRadioButtonId()==R.id.radio_button_single_note_settings)
		{

			if (isChecked)
				MainActivity.NotesNames = MainActivity.NotesNamesEurope;
			else
				MainActivity.NotesNames = MainActivity.NotesNamesReal;
				
				for (int i=0;i<MainActivity.notesToPractice.length;++i)
				{
					checkboxes[i].setText(MainActivity.NotesNames[i]);
					checkboxes[i].setChecked(MainActivity.notesToPractice[i]);
					checkboxes[i].setVisibility(View.VISIBLE);
				}					
				for (int i=MainActivity.notesToPractice.length;i<checkboxes.length;++i)
					checkboxes[i].setVisibility(View.GONE);			
			return;
		}		
		if (!isChecked)
			return;
		
		Save(radioButtonChecked);		
		radioButtonChecked = buttonView.getId();
		
		switch(buttonView.getId())
		{
			case R.id.radio_button_interval_settings:
			{
				((CheckBox)findViewById(R.id.checkboxEurope)).setText("Use only single note practice's notes for bottom note");
				((CheckBox)findViewById(R.id.checkboxEurope)).setChecked(MainActivity.intervalsUseOnlySingleNotes);
				for (int i=0;i<MainActivity.IntervalsNames.length;++i)
				{
					checkboxes[i].setText(MainActivity.IntervalsNames[i]);
					checkboxes[i].setChecked(MainActivity.intervalsToPractice[i]);
					checkboxes[i].setVisibility(View.VISIBLE);
				}
				for (int i=MainActivity.IntervalsNames.length;i<checkboxes.length;++i)
					checkboxes[i].setVisibility(View.GONE);
				break;
			}
			case R.id.radio_button_single_note_settings:
			{
				((CheckBox)findViewById(R.id.checkboxEurope)).setText("European note names");
				((CheckBox)findViewById(R.id.checkboxEurope)).setChecked(MainActivity.europeanNoteNames);
				if (((CheckBox)findViewById(R.id.checkboxEurope)).isChecked())
					MainActivity.NotesNames = MainActivity.NotesNamesEurope;
				else
					MainActivity.NotesNames = MainActivity.NotesNamesReal;
				for (int i=0;i<MainActivity.notesToPractice.length;++i)
				{
					checkboxes[i].setText(MainActivity.NotesNames[i]);
					checkboxes[i].setChecked(MainActivity.notesToPractice[i]);
				}					
				for (int i=MainActivity.notesToPractice.length;i<checkboxes.length;++i)
					checkboxes[i].setVisibility(View.GONE);
				break;
			}
			case R.id.radio_button_chord_settings:
			{
				((CheckBox)findViewById(R.id.checkboxEurope)).setText("Use only single note practice's notes for roots");
				((CheckBox)findViewById(R.id.checkboxEurope)).setChecked(MainActivity.chordsUseOnlySingleNotes);
				for (int i=0;i<MainActivity.ChordsNames.length;++i)
				{
					checkboxes[i].setText(MainActivity.ChordsNames[i].name);
					checkboxes[i].setChecked(MainActivity.chordsToPractice[i]);
					checkboxes[i].setVisibility(View.VISIBLE);
				}
				for (int i=MainActivity.ChordsNames.length;i<checkboxes.length;++i)
					checkboxes[i].setVisibility(View.GONE);
				break;
			}
		}
	}
	private void ErrorBox(String str)
	{
		AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);                      
	    dlgAlert.setTitle("Error"); 
	    dlgAlert.setMessage(str); 
	    dlgAlert.setPositiveButton("OK",new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int whichButton) {
	        }
	   });
	    dlgAlert.setCancelable(true);
	    dlgAlert.create().show();
	}
	@Override
	public void onClick(View arg0) {
		if (!Save(-1))
		{
			((CheckBox)arg0).setChecked(true);
			Save(-1);
		}
	}
	@Override
	public void onFocusChange(View arg0, boolean arg1) {
		Save(-1);
	}
	@Override
	public boolean onLongClick(View arg0) {
		if (arg0.getId()==R.id.edittextNotesnum)
		{
			((EditText)arg0).selectAll();			
		}
		return false;
	}
	@Override
	public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
		Save(-1);
		return false;
	}
}
