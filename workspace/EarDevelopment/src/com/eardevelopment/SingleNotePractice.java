package com.eardevelopment;

import java.util.Calendar;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SingleNotePractice extends ActionBarActivity implements onMyClick{

	boolean allreadyInit=false;
	int[] notesInAllOctaves; 
	int[] notesInOneOctave;
	Button[] NotesButtons;
	TextView RightOrWrong;
	Button replayButton;
	PianoView pianoView;
	int lastNoteIndPlayed;
	int numOfTries;
	int numOfSuccesses;
	long cumulativeSuccessTime;
	long start_of_try;
	int noteDiff;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_single_note_practice);
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
    	if (allreadyInit)
    		return;
		numOfTries = 0;
		numOfSuccesses = 0;
		int numOfNotes=0;		
		noteDiff=0;
		if (MainActivity.Bbinstrument)
			noteDiff=2;
		if (MainActivity.Ebinstrument)
			noteDiff=-3;
		int minNote=MainActivity.minNote+noteDiff;
		int maxNote=MainActivity.maxNote+noteDiff;
		if (MainActivity.onlyOneOctave)
		{
			minNote=12+noteDiff;
			maxNote=23+noteDiff;
		}
		
		for (int note=minNote;note<=maxNote;++note)
			if (MainActivity.notesToPractice[note%12])
				numOfNotes++;
		notesInAllOctaves = new int[numOfNotes];
		numOfNotes=0;
		for (int note=minNote;note<=maxNote;++note)
			if (MainActivity.notesToPractice[note%12])
				notesInAllOctaves[numOfNotes++]=note-noteDiff;
		numOfNotes=0;
		for (int note=0;note<12;++note)
			if (MainActivity.notesToPractice[note])
				numOfNotes++;
		notesInOneOctave = new int[numOfNotes];
		numOfNotes=0;
		for (int note=0;note<12;++note)
			if (MainActivity.notesToPractice[note])
				notesInOneOctave[numOfNotes++]=(note-noteDiff+12)%12;		
		LinearLayout ll0 = (LinearLayout)findViewById(R.id.single_note_practive_layout);
		ll0.setBackgroundColor(MainActivity.BackGroundColor);
		RelativeLayout rl = (RelativeLayout)findViewById(R.id.button_layout_right);
		RelativeLayout ll = (RelativeLayout)findViewById(R.id.button_layout_left);
		
		RightOrWrong = (TextView)findViewById(R.id.text_RightOrWrong);
		
		((Button)findViewById(R.id.button_replay)).setTextColor(MainActivity.ForGroundColor);		
		
		NotesButtons = new Button[notesInOneOctave.length];
		for (int i=0;i<notesInOneOctave.length;++i)
		{
			NotesButtons[i]=new Button(this);
			NotesButtons[i].setText(MainActivity.NotesNames[(notesInOneOctave[i]+noteDiff+12)%12]);
			NotesButtons[i].setOnClickListener(new ButtonListener(this,i));
			NotesButtons[i].setId(i+1);
			NotesButtons[i].setTextColor(MainActivity.ForGroundColor);
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			if (i>=2)
				lp.addRule(RelativeLayout.BELOW, NotesButtons[i-2].getId());
			NotesButtons[i].setLayoutParams(lp);
			if (i%2==0)
				ll.addView(NotesButtons[i]);
			else
				rl.addView(NotesButtons[i]);
		}
		final LinearLayout main_layout = (LinearLayout)findViewById(R.id.single_note_main_layout);
		final SingleNotePractice thisP = this;
		ViewTreeObserver vto = main_layout.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() { 
			@Override
		    public void onGlobalLayout() {
				if (pianoView != null && (pianoView.doneDraw || pianoView.stillDrawing || !pianoView.failedDraw))
					return;
				Display display = getWindowManager().getDefaultDisplay();
				Point size = new Point();
				display.getSize(size);										 	
				LinearLayout.LayoutParams lp;
				if (pianoView != null)
					System.out.println(size.y/4 +","+MainActivity.pianoViewRequestedHeight +"," + pianoView + "," + pianoView.failedDraw);
				else
					System.out.println(size.y/4 +","+MainActivity.pianoViewRequestedHeight + "," + pianoView);
				if (pianoView != null && pianoView.failedDraw && MainActivity.pianoViewRequestedHeight >0) 					
					lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, MainActivity.pianoViewRequestedHeight ,1f);
				else
					lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, size.y/4 ,1f);
				if (pianoView != null)
					main_layout.removeView(pianoView);
				pianoView = new PianoView(thisP, false, MainActivity.numOfNotesOnPianoInPractises);
				
				main_layout.addView(pianoView,lp);
			}
		});

		TextView t = (TextView)findViewById(R.id.text_score);
		t.setTextColor(MainActivity.ForGroundColor);
		t.setText("Success so far " + numOfSuccesses + " / " + numOfTries);
		Timer timer = new Timer();
		timer.schedule(new TimerTask(){
			@Override
			public void run() {
				playNextNote();
			}
		},1000);
		allreadyInit=true;
	}
	private Random r = new Random(System.currentTimeMillis());
	private void playNextNote()
	{		
		lastNoteIndPlayed = r.nextInt(notesInAllOctaves.length); 
		MainActivity.singeltonPointer.playNotes(new int[] {notesInAllOctaves[lastNoteIndPlayed]}, false);
		Calendar c = Calendar.getInstance();
		start_of_try = c.getTimeInMillis();			
	}

	public void ButtonPressed(int buttonInd)
	{
		++numOfTries;
		if ((notesInAllOctaves[lastNoteIndPlayed]%12)==notesInOneOctave[buttonInd])
		{
			++numOfSuccesses;
			Calendar c = Calendar.getInstance();
			cumulativeSuccessTime += (c.getTimeInMillis()- start_of_try);
			RightOrWrong.setText("Right!");
			RightOrWrong.setBackgroundColor(Color.GREEN);
			MainActivity.singeltonPointer.playNotes(new int[] {notesInAllOctaves[lastNoteIndPlayed]}, false);
			Timer timer = new Timer();
			timer.schedule(new TimerTask(){
				@Override
				public void run() {
					playNextNote();
				}
			},MainActivity.noteTimeInMilliseconds);
		}
		else
		{
			RightOrWrong.setBackgroundColor(0xFFFF3030);
			RightOrWrong.setText("Wrong! Try again.");
			
			int note = notesInOneOctave[buttonInd]+(notesInAllOctaves[lastNoteIndPlayed]/12)*12;
			MainActivity.singeltonPointer.playNotes(new int[] {note}, false);
		}
		TextView t = (TextView)findViewById(R.id.text_score);
		t.setText("Success so far " + numOfSuccesses + "/" + numOfTries + " ("+String.format("%.2f", ((float)numOfSuccesses)/numOfTries)+")\nAverage success time " + String.format("%.2f",((float)cumulativeSuccessTime)/numOfTries/1000) + " seconds");
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
	//	int id = item.getItemId();
//		if (id == R.id.action_settings) {
			//return true;
		//}
		return super.onOptionsItemSelected(item);
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
			View rootView = inflater.inflate(
					R.layout.fragment_single_note_practice, container, false);
			return rootView;
		}
	}

	public void replayLastNote(View arg0) {
		MainActivity.singeltonPointer.playNotes(new int[] {notesInAllOctaves[lastNoteIndPlayed]}, false);		
	}

}
