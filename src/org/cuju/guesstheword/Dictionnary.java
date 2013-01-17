package org.cuju.guesstheword;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.Random;

import android.app.Activity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class Dictionnary {
	public String file = null;
	
	public Dictionnary(Activity act) throws IOException {
		String pref = PreferenceManager.getDefaultSharedPreferences(act).getString("dictionnary", "english");
		file = "dict_english";
		if(pref.equals("auto")){
			if(Locale.getDefault().getDisplayName().contains("France")){
				file = "dict_french";
			}else{
				file = "dict_english";
			}
		}else if(pref.equals("french")){
			file = "dict_french";
		}
	}
	
	public String randomWord(Activity act) throws IOException{
		InputStream is = act.getAssets().open(file+".txt");
		InputStreamReader in= new InputStreamReader(is);
		BufferedReader br = new BufferedReader(in); 
		int c = 0;
		int random_index = 1;
		String s = null;
		while(c < random_index) { 
			s = br.readLine();
			if(c==0){
				int lines = Integer.parseInt(s);
				random_index = new Random().nextInt(lines+1)+1;
			}
			c += 1;
		}
		is.close();
		return s;
	}
	
	//TODO: return a upper-case accent-free string for easy comparison 
	static public String normalize(String s){
		return s;
	}
}
