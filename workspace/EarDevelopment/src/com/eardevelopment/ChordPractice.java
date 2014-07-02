package com.eardevelopment;

import java.util.Arrays;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
class Chord
{
    int chordInd;
    int note;
};

public class ChordPractice extends ActionBarActivity implements onMyClick{
	int requestedSize;
	boolean allreadyInit=false;
    Button[] NotesButtons;
    TextView RightOrWrong;
    PianoView pianoView;
    Chord[][] chordsAll;
    int[] chordsOptions;
    Button replayButton;
    int numOfTries;
    int numOfSuccesses;
    long cumulativeSuccessTime;
    long start_of_try;
    int noteDiff;
    int lastChordIndPlayed;
    int lastChordInd2Played;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chord_practice);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
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
        noteDiff=0;
        if (MainActivity.Bbinstrument)
            noteDiff=2;
        if (MainActivity.Ebinstrument)
            noteDiff=-3;
        int numOfChords=0;
        int minNote=MainActivity.minNote+noteDiff;
        int maxNote=MainActivity.maxNote+noteDiff;
        if (MainActivity.onlyOneOctave)
        {
            minNote=12+noteDiff;
            maxNote=23+noteDiff;
        }
        numOfChords=0;
        for (int chord=0;chord<MainActivity.chordsToPractice.length;++chord)
            if (MainActivity.chordsToPractice[chord])
                ++numOfChords;
        chordsOptions = new int[numOfChords];
        numOfChords=0;
        for (int chord=0;chord<MainActivity.chordsToPractice.length;++chord)
            if (MainActivity.chordsToPractice[chord])
                chordsOptions[numOfChords++]=chord;
        int[] chordsAllTemp = new int[numOfChords];
        int realNumOfChords=0;
        int[] chordsOptionsReal = new int[chordsOptions.length];
        for (int chordInd=0;chordInd<chordsOptions.length;++chordInd)
        {
            numOfChords=0;
            int chord=chordsOptions[chordInd];
            for (int note=minNote;note<maxNote;++note)
            {
            	if (((MainActivity.chordsUseOnlySingleNotes && MainActivity.notesToPractice[note%12]) || !MainActivity.chordsUseOnlySingleNotes)
            	&& MainActivity.chordsToPractice[chord] && note+MainActivity.ChordsNames[chord].intervals[MainActivity.ChordsNames[chord].intervals.length-1]<=maxNote)
                {
                	boolean chordGood=true;
                	for (int i=0;i<MainActivity.ChordsNames[chord].intervals.length;++i)
                		if (note < MainActivity.lowerIntervalLimits[MainActivity.ChordsNames[chord].intervals[i]])
                			chordGood=false;
                	for (int i=0;i<MainActivity.ChordsNames[chord].intervals.length-1;++i)
                		for (int j=i+1;j<MainActivity.ChordsNames[chord].intervals.length;++j)
                			if (note + MainActivity.ChordsNames[chord].intervals[i] < MainActivity.lowerIntervalLimits[MainActivity.ChordsNames[chord].intervals[j]])
                				chordGood=false;
                	if (chordGood)
                		++numOfChords;
                }
            }
            if (numOfChords>0)
            {
            	chordsAllTemp[realNumOfChords] = numOfChords;
            	chordsOptionsReal[realNumOfChords]=chord;
            	++realNumOfChords;
            }
        }
        chordsOptions=new int[realNumOfChords];        
        chordsAll = new Chord[realNumOfChords][];
        for (int chordInd=0;chordInd<realNumOfChords;++chordInd)
        {
        	chordsOptions[chordInd]=chordsOptionsReal[chordInd];
            int chord=chordsOptionsReal[chordInd];
            numOfChords=0;
            chordsAll[chordInd] = new Chord[chordsAllTemp[chordInd]];
            for (int note=minNote;note<maxNote;++note)
            {
            	if (((MainActivity.chordsUseOnlySingleNotes && MainActivity.notesToPractice[note%12]) || !MainActivity.chordsUseOnlySingleNotes)
            	&& MainActivity.chordsToPractice[chord] && note+MainActivity.ChordsNames[chord].intervals[MainActivity.ChordsNames[chord].intervals.length-1]<=maxNote)
                {            		
                	boolean chordGood=true;
                	for (int i=0;i<MainActivity.ChordsNames[chord].intervals.length;++i)
                		if (note < MainActivity.lowerIntervalLimits[MainActivity.ChordsNames[chord].intervals[i]])
                			chordGood=false;
                	for (int i=0;i<MainActivity.ChordsNames[chord].intervals.length-1;++i)
                		for (int j=i+1;j<MainActivity.ChordsNames[chord].intervals.length;++j)
                			if (note + MainActivity.ChordsNames[chord].intervals[i] < MainActivity.lowerIntervalLimits[MainActivity.ChordsNames[chord].intervals[j]])
                				chordGood=false;
                	if (chordGood)
                	{
                		chordsAll[chordInd][numOfChords]=new Chord();
                		chordsAll[chordInd][numOfChords].chordInd = chord;
                		chordsAll[chordInd][numOfChords].note = note-noteDiff;
                		++numOfChords;
                	}
                }            
            }
        }
    	LinearLayout ll0 = (LinearLayout)findViewById(R.id.chord_practive_layout);
        ll0.setBackgroundColor(MainActivity.BackGroundColor);
        RelativeLayout rl = (RelativeLayout)findViewById(R.id.chord_button_layout_right);
        RelativeLayout ll = (RelativeLayout)findViewById(R.id.chord_button_layout_left);
        
        RightOrWrong = (TextView)findViewById(R.id.text_chord_RightOrWrong);
		((Button)findViewById(R.id.button_chord_replay)).setTextColor(MainActivity.ForGroundColor);		
		
		int maxNumOfIntervals=0;
		for (int i=0;i<chordsOptions.length;++i)
		{
			int curNum = MainActivity.ChordsNames[chordsOptions[i]].intervals.length + 1; 
			if (curNum>maxNumOfIntervals)
				maxNumOfIntervals = curNum;
		}
		String[] buttonsTexts = new String[]{"Bottom\nNote", "2nd", "3rd", "4th", "5th"};
		LinearLayout layout_buttons = ((LinearLayout)findViewById(R.id.button_chord_replay_buttons));
		for (int i=0;i<maxNumOfIntervals;++i)
		{
			Button b =new Button(this);
			b.setTextColor(MainActivity.ForGroundColor);
			b.setTextSize(14);
			b.setOnClickListener(new ButtonListener(this, 1000+i));
			LinearLayout.LayoutParams lp;
			if (i==maxNumOfIntervals-1)
				b.setText("Upper\nNote");
			else
				b.setText(buttonsTexts[i]);
			lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT,1);			
			layout_buttons.addView(b,lp);
		}
		
		((TextView)findViewById(R.id.text_chord_score)).setTextColor(MainActivity.ForGroundColor);        
        ((Button)findViewById(R.id.button_chord_replay)).setTextColor(MainActivity.ForGroundColor);        
        
        NotesButtons = new Button[chordsOptions.length];
        for (int i=0;i<chordsOptions.length;++i)
        {
            NotesButtons[i]=new Button(this);
            NotesButtons[i].setText(MainActivity.ChordsNames[chordsOptions[i]].name);
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
		final LinearLayout main_layout = (LinearLayout)findViewById(R.id.chord_main_layout);
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		final ChordPractice thisP = this;
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
        Timer timer = new Timer();
        timer.schedule(new TimerTask(){
            @Override
            public void run() {
                playNextInterval();
            }
        },1000);
        allreadyInit=true;
    }
	/**
	 * A placeholder fragment containing a simple view.
	 */
    private void playChord(int note, int chordInd)    
    {
    	int[] notes = new int[MainActivity.ChordsNames[chordInd].intervals.length + 1];
    	notes[0]=note;
    	for (int i=0;i<MainActivity.ChordsNames[chordInd].intervals.length;++i)
    		notes[i+1]=note+MainActivity.ChordsNames[chordInd].intervals[i];
    	
    	Arrays.sort(notes);
        if (notes[notes.length-1]>=MainActivity.maxNote)
        {
            for (int i=0;i<notes.length;++i)
               notes[i]-=12;        
        }
        if (notes[0]<=MainActivity.minNote)
        {
            for (int i=0;i<notes.length;++i)
               notes[i]+=12;        
        }
        MainActivity.singeltonPointer.playNotes(notes, false);
    }
    private Random r = new Random(System.currentTimeMillis());
    private void playNextInterval()
    {             
    	lastChordIndPlayed = r.nextInt(chordsAll.length); 
    	lastChordInd2Played = r.nextInt(chordsAll[lastChordIndPlayed].length);
    	playChord(chordsAll[lastChordIndPlayed][lastChordInd2Played].note, chordsAll[lastChordIndPlayed][lastChordInd2Played].chordInd);
        Calendar c = Calendar.getInstance();
        start_of_try = c.getTimeInMillis();
    }
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_chord_practice,
					container, false);
			return rootView;
		}
	}
    public void ButtonPressed(int buttonInd) {
        if (buttonInd>=1000)
        {
        	if (buttonInd==1000)
        		MainActivity.singeltonPointer.playNotes(new int[] {chordsAll[lastChordIndPlayed][lastChordInd2Played].note}, false);
        	else
        	{
        		int buttonPressed = buttonInd-1000-1;
            	if (MainActivity.ChordsNames[chordsAll[lastChordIndPlayed][lastChordInd2Played].chordInd].intervals.length>buttonPressed)
            		MainActivity.singeltonPointer.playNotes(new int[] {chordsAll[lastChordIndPlayed][lastChordInd2Played].note+MainActivity.ChordsNames[chordsAll[lastChordIndPlayed][lastChordInd2Played].chordInd].intervals[buttonPressed]}, false);    	
            	else
            		MainActivity.singeltonPointer.playNotes(new int[] {chordsAll[lastChordIndPlayed][lastChordInd2Played].note+MainActivity.ChordsNames[chordsAll[lastChordIndPlayed][lastChordInd2Played].chordInd].intervals[MainActivity.ChordsNames[chordsAll[lastChordIndPlayed][lastChordInd2Played].chordInd].intervals.length-1]}, false);
        	}
        }
        else
        {
	        ++numOfTries;
	        if ((chordsAll[lastChordIndPlayed][lastChordInd2Played].chordInd)==chordsOptions[buttonInd])
	        {
	            ++numOfSuccesses;
	            Calendar c = Calendar.getInstance();
	            cumulativeSuccessTime += (c.getTimeInMillis()- start_of_try);
	            RightOrWrong.setText("Right!");
	            RightOrWrong.setBackgroundColor(Color.GREEN);
	            playChord(chordsAll[lastChordIndPlayed][lastChordInd2Played].note, chordsAll[lastChordIndPlayed][lastChordInd2Played].chordInd);
	            Timer timer = new Timer();
	            timer.schedule(new TimerTask(){
	                @Override
	                public void run() {
	                    playNextInterval();
	                }
	            },MainActivity.noteTimeInMilliseconds);
	        }
	        else
	        {
	            RightOrWrong.setBackgroundColor(0xFFFF3030);
	            RightOrWrong.setText("Wrong! Try again.");
	            
	            playChord(chordsAll[lastChordIndPlayed][lastChordInd2Played].note, chordsOptions[buttonInd]); 
	        }
	        TextView t = (TextView)findViewById(R.id.text_chord_score);
	        t.setText("Success so far " + numOfSuccesses + "/" + numOfTries + " ("+String.format("%.2f", ((float)numOfSuccesses)/numOfTries)+")\nAverage success time " + String.format("%.2f",((float)cumulativeSuccessTime)/numOfTries/1000) + " seconds");        
        }
    }
    public void replayLastNote(View arg0) {
    	playChord(chordsAll[lastChordIndPlayed][lastChordInd2Played].note, chordsAll[lastChordIndPlayed][lastChordInd2Played].chordInd);        
    }    
}
