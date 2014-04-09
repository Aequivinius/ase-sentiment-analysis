package ch.uzh.ifi.seal.ase.group3.worker.sentiment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import classifier.ClassifierBuilder;
import classifier.WekaClassifier;

public class Sentiment {

	private final File tweetFile;
	private WekaClassifier wc;

	private double result; // the result of the caluculation
	private int tweetsProcessed; // how many tweets have been processed

	public Sentiment(File tweetFile) {
		this.tweetFile = tweetFile;
		if (tweetFile.length() == 0) {
			throw new IllegalArgumentException("File is empty!");
		}

		ClassifierBuilder clb = new ClassifierBuilder();
		try {
			wc = clb.retrieveClassifier("weka.classifiers.bayes.NaiveBayes");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * @param file Reads file line by line
	 * @throws FileNotFoundException
	 */
	public void calculate() throws FileNotFoundException {
		BufferedReader br = null;
		br = new BufferedReader(new FileReader(tweetFile));

		String line;
		double sum = 0;
		int counter = 0;

		try {
			while ((line = br.readLine()) != null) {
				sum += Double.parseDouble(wc.classify(line));
				++counter;
			}
		} catch (NumberFormatException | IOException e1) {
			e1.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				// cannot close the reader
			}
		}

		result = (sum / counter);
		tweetsProcessed = counter;
	}

	/**
	 * Returns the averaged result
	 * 
	 * @return avg (between 0 = very bad and 4 = very good).
	 */
	public double getResult() {
		return result;
	}

	/**
	 * Returns the number of tweets processed
	 * 
	 * @return
	 */
	public int getTweetsProcessed() {
		return tweetsProcessed;
	}
}
