package classifier;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.util.LinkedList;
import java.util.List;

import util.Options;
import ch.uzh.ifi.seal.ase.group3.worker.sentimentworker.ServletFileUtil;
import documents.DocumentsSet;

/**
 * The receiver class
 */
public class ClassifierBuilder {

	private final DocumentsSet _ds;
	private Options opt;

	public ClassifierBuilder() {
		_ds = new DocumentsSet();
	}

	/**
	 * gets the options of classifier builder
	 * 
	 * @return the options of classifier builder
	 */
	public Options getOpt() {
		return opt;
	}

	/**
	 * sets given options of classifier builder
	 * 
	 * @param opt options of classifier builder
	 */
	public void setOpt(Options opt) {
		this.opt = opt;
	}

	/**
	 * deserializes a classifier whose name is given
	 * 
	 * @param classifierName the classifier's name
	 * @return the constructed classifier
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public WekaClassifier retrieveClassifier(String classifierName) throws FileNotFoundException,
			IOException, ClassNotFoundException {
		ObjectInputStream ois = new ObjectInputStream(ServletFileUtil.getInstance().getFileStream(
				classifierName + ".model"));
		WekaClassifier wc = (WekaClassifier) ois.readObject();
		ois.close();
		return wc;
	}

	/**
	 * constructs a weighted majority classifier
	 * 
	 * @throws Exception
	 */
	public void constructWm() throws Exception {
		List<IClassifier> wc = new LinkedList<IClassifier>();
		for (String str : this.opt.getWmClassifiersName()) {
			wc.add(this.retrieveClassifier(str));
		}
		WeightedMajority wm = new WeightedMajority(wc);
		while (true) {
			InputStreamReader reader = new InputStreamReader(System.in);
			BufferedReader myInput = new BufferedReader(reader);
			String str = new String();
			System.out.println("Inserisci una stringa da classificare: ");
			str = myInput.readLine();
			Item ist = wm.weightedClassify(str);
			System.out.println("Classificazione: " + ist.getPolarity());
			System.out.println("Inserisci la corretta polarizzazione: ");
			str = myInput.readLine();
			ist.setTarget(str);
		}
	}

	/**
	 * calculates weighted majority classifier's precision
	 * 
	 * @throws Exception
	 */
	public void calculateWmPrecision() throws Exception {
		List<IClassifier> wc = new LinkedList<IClassifier>();
		for (String str : this.opt.getWmClassifiersName()) {
			wc.add(this.retrieveClassifier(str));
		}
		WeightedMajority wm = new WeightedMajority(wc);
		int i = 1;
		float correct = 0;
		float[] fun;
		fun = new float[183];
		Preprocesser pr = new Preprocesser();
		Item temp;
		InputStream fstream = ServletFileUtil.getInstance().getFileStream("test_base.txt");
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine;
		String str, pol;
		while ((strLine = br.readLine()) != null) {
			String[] items = strLine.split(";;");
			str = items[5].toLowerCase();
			pol = items[0];
			temp = wm.weightedClassify(pr.preprocessDocument(str));
			temp.setTarget(pol);
			wm.setTarget(temp);
			if (temp.getPolarity().equals(temp.getTarget()))
				correct++;
			System.out.println(correct / i);
			System.out.print(wm.get_cl2weight().get(1) + " ");
			System.out.print(wm.get_cl2weight().get(2) + " ");
			System.out.println(wm.get_cl2weight().get(3));
			fun[i - 1] = correct / i;
			i++;
		}

		br.close();
	}
}
