package ch.uzh.ifi.seal.ase.group3.worker.sentiment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import ch.uzh.ifi.seal.ase.group3.worker.sentiment.classifier.ClassifierBuilder;
import ch.uzh.ifi.seal.ase.group3.worker.sentiment.classifier.WekaClassifier;

public class Sentiment {

	private WekaClassifier wc;

	public Sentiment() {
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
	 * @return avg (between 0 = very bad and 4 = very good).
	 */
	public double avg(File file) {

		if (file.length() == 0) {
			throw new IllegalArgumentException("File is empty!");
		}

		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		}
		String line;
		double sum = 0;
		int counter = 0;

		try {
			while ((line = br.readLine()) != null) {

				sum += Double.parseDouble(wc.classify(line));
				++counter;

			}
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		try {

			br.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		double avg = (sum / counter);

		return avg;

	}
}
