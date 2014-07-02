package com.eardevelopment;

import java.io.IOException;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;

@SuppressLint("ViewConstructor")
public class PianoView extends ImageView implements OnTouchListener  {
	public boolean stillDrawing;
	float widthReal;
	float numOfOctaves;
	float widthOfNote;
	Bitmap pianoBitmap;
	float YTotal=1074;	
	float YBegin=220;	
	float YEndBlack=715;
	float YEndWhite=970;
	
	
	float YTotalWO=770;
	float YBeginWO=6;
	float YEndBlackWO=490;
	float YEndWhiteWO=755;
	
	
	float XBeginBlack=122;//78;
	float XEndBlack=218;//202;
	//int YDotPLaceWhite=866;
	//int YDotPLaceBlack=610;
	//float[] XErrorsBlack= new float[] {-22,0,22,0,-22,22,0};
	float[] XErrorsBlack= new float[] {-30,0,18,0,-28,22,0};
	Context context;
	boolean withWood;
	boolean doneDraw=false;
	public boolean failedDraw=false;
	Bitmap bm;
	int lastHeight=0;
	public PianoView(Context context, boolean withWood, int numOfNotes) {
		super(context);
		this.context = context;
		this.withWood = withWood;
		if (numOfNotes>0)
			numOfOctaves = ((float)numOfNotes)/7;
		else
			numOfOctaves=0;
		setOnTouchListener(this);
		try
		{
		if (withWood)
			bm = BitmapFactory.decodeStream(context.getAssets().open("PianoOneOctaveF3.png"));
		else
			bm = BitmapFactory.decodeStream(context.getAssets().open("PianoOneOctaveWO.png"));
		}
		catch (IOException e){}
		ViewTreeObserver vto = getViewTreeObserver();		
		final OnGlobalLayoutListener globalListener = new OnGlobalLayoutListener() {

			@Override   
			 public void onGlobalLayout() {
				drawPiano();
			 }
		};
		vto.addOnGlobalLayoutListener(globalListener);
	}
	private void drawPiano() {
	
		int heightPlace = getHeight();
		if (heightPlace==lastHeight)
			return;
		stillDrawing=true;
		lastHeight=heightPlace;
		
		MainActivity.pianoViewRequestedHeight=0;
		if (doneDraw)
		{
			failedDraw=false;
			stillDrawing=false;
			return;
		}
			
		
		int widthPlace = getWidth();
			
		if (numOfOctaves>0)
		{				
			widthReal = widthPlace / numOfOctaves;
			double factor= widthReal / bm.getWidth();					
			if ((int)(bm.getHeight()*factor)>heightPlace)
			{				
				MainActivity.pianoViewRequestedHeight=(int)(bm.getHeight()*factor);
				System.out.println("Height is "+ getHeight()+" Requested Height is "+MainActivity.pianoViewRequestedHeight);
				doneDraw=false;
				failedDraw=true;
				stillDrawing=false;
				return;
			}
			bm=Bitmap.createScaledBitmap(bm, (int)(bm.getWidth()*factor), (int)(bm.getHeight()*factor), true);
		}
		else
		{
			widthReal = bm.getWidth()/(((float)bm.getHeight()/heightPlace));
			numOfOctaves = widthPlace/widthReal;
		}
		widthOfNote = widthReal / 7;
		
		if (!withWood)
		{
			YBegin = YBeginWO;
			YEndBlack=YEndBlackWO;
			YEndWhite=YEndWhiteWO;
			YTotal=YTotalWO;
		}
		YBegin*=heightPlace/YTotal;
		YEndWhite*=heightPlace/YTotal;
		YEndBlack*=heightPlace/YTotal;
		XBeginBlack*=heightPlace/YTotal;
		XEndBlack*=heightPlace/YTotal;
		for (int i=0;i<XErrorsBlack.length;++i)
			XErrorsBlack[i]*=heightPlace/YTotal;
		YTotal*=heightPlace/YTotal;
		
		pianoBitmap = Bitmap.createBitmap((int)Math.ceil(bm.getWidth()*numOfOctaves),bm.getHeight(),bm.getConfig()); 
		int numOfPixels = bm.getHeight()*bm.getWidth();
		int[] pixels = new int[numOfPixels];
		bm.getPixels(pixels, 0, bm.getWidth(), 0, 0, bm.getWidth(), bm.getHeight());
		for (int i=0;i<Math.floor(numOfOctaves);++i)				
			pianoBitmap.setPixels(pixels, 0, bm.getWidth(), i*bm.getWidth(), 0, bm.getWidth(), bm.getHeight());
		pianoBitmap.setPixels(pixels, 0, bm.getWidth(), (int)(Math.floor(numOfOctaves)*bm.getWidth()), 0, (int)(bm.getWidth()*(numOfOctaves-Math.floor(numOfOctaves))), bm.getHeight());
		setImageBitmap(pianoBitmap);
		failedDraw=false;
		doneDraw=true;	
		stillDrawing=false;
	}
	public boolean onTouch(View arg0, MotionEvent arg1) {
		float x = arg1.getX();
		float y = arg1.getY();
		boolean[] hasSharpLydian = new boolean[] {true, true, true, false, true, true, false};		
		int note = (int)(x/widthOfNote);
		int placeInNote=(int)(x-widthOfNote*note);
		if ((y>=YBegin && y<=YEndBlack) && (placeInNote>=XBeginBlack+XErrorsBlack[note%7] && placeInNote <=XEndBlack+XErrorsBlack[note%7]) && hasSharpLydian[note%7])
			//black
			playNote(note,true);
		else if ((y>=YBegin && y<=YEndBlack) && (placeInNote>=0 && placeInNote <=XEndBlack+XErrorsBlack[(note+6)%7]-widthOfNote) && hasSharpLydian[(note+6)%7])
			//black in next note
		{
			playNote(note-1,true);
		}
		else if (y>=YBegin && y<=YEndWhite)
			//white
			playNote(note,false);
		return false;
	}
	private void playNote(int note, boolean halfStep)
	{	
		int[] lydianScale = new int[] {0,2,4,6,7,9,11};
		if (halfStep)
			MainActivity.singeltonPointer.playNotes(new int[] {6+lydianScale[note%7]+12*((int)(note/7))},false);
		else
			MainActivity.singeltonPointer.playNotes(new int[] {5+lydianScale[note%7]+12*((int)(note/7))},false);

		/*		int size = (int)(40.0/1080.0*YTotal);
		int[] pixels = new int[size*size];
		for (int i=0;i<size*size;++i)
			pixels[i]=Color.BLUE;
		float factor = 1074/iv.getHeight();
		if (halfStep)
			pianoBitmap.setPixels(pixels,0,size,(int)(note*widthOfNote*factor+(XBeginBlack*factor+XEndBlack*factor)/2+XErrorsBlack[note%7]*factor),YDotPLaceBlack,size,size);
		else
			pianoBitmap.setPixels(pixels,0,size,(int)(note*widthOfNote*factor+(widthOfNote*factor+size)/2),YDotPLaceWhite,size,size);		
		ImageView iv = (ImageView)findViewById(R.id.image_piano);
		iv.setImageBitmap(pianoBitmap);
		}*/
	}
}
