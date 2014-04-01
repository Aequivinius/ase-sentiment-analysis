package ch.uzh.ifi.seal.ase.group3.server.sentiment;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import ch.uzh.ifi.seal.ase.group3.shared.Tweet;
import classifier.ClassifierBuilder;
import classifier.WekaClassifier;

public class Sentiment {
	
	private WekaClassifier wc;
	
	public Sentiment() {
		//costruzione classificatori
		ClassifierBuilder clb = new ClassifierBuilder();
		/*Options opt = new Options();
		clb.setOpt(opt);
		opt.setSelectedFeaturesByFrequency(true);
		opt.setNumFeatures(150);
		opt.setRemoveEmoticons(true);*/
		/* 
		 * try {
			clb.prepareTrain();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			clb.prepareTest();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		NaiveBayes nb = new NaiveBayes();
		WekaClassifier wc = null;
		try {
			wc = clb.constructClassifier(nb);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			wc.classify("i am very sad");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		try {
			wc = clb.retrieveClassifier("weka.classifiers.bayes.NaiveBayes");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}

	public double avg(List<Tweet> tweets){
		
		if (tweets.size() == 0){
			throw new NullPointerException();	//XXX: Wohl nicht das klügste...
		}
		
		double sum = 0;
		
		for (int i = 0; i < tweets.size(); ++i){
			
			Tweet t = tweets.get(i);
			try {
				
				sum += Double.parseDouble(wc.classify(t.getText()));	// XXX: Typecast avoidable?
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		double avg = (sum / tweets.size());
		return avg;
	}
}
