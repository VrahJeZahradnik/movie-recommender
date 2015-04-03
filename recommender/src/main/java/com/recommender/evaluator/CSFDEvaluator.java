package com.recommender.evaluator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.common.Weighting;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.common.RandomUtils;

import com.google.common.io.ByteStreams;
import com.recommender.mailutil.MailUtil;

public class CSFDEvaluator {
	
	public static final String MOV_PER_USR = "20";
	public static final String FILE_IN = "/data/" + MOV_PER_USR + ".dat";
	public static final String FILE_OUT_SUFFIX = ".txt";
	public static final String LOG_DIR = "res/" + MOV_PER_USR + "/";
	private static final double TEST_DATA = 0.8;
	private static final double TEST_DATA_SET = 0.01;
	private static final double FULL_DATA_SET = 1;
	private static final double THRESH_UP = 1.0;
	private static final double THRESH_DOWN = -1.0;
	private static final double[] THRESH_VALS = {0.9, 0.8, 0.7, 0.6, 0.4, 0.2, 0.0};
	private static final int[] NEIGHBOR_VALS = {2, 4, 8, 16, 32, 64, 128};
	private static final String M_PEARSON= "pearson";
	private static final String M_EUCLIDEAN= "euclidean";
	private static final String M_TANIMOTO= "tanimoto";
	private static final String M_LOGLIKE= "loglikelihood";
	
	private boolean weighted;
	private boolean neighborhood;
	private boolean threshold;
	private boolean random;
	private boolean mail;
	
	private int neighbors;
	private double thresholdSize;
	private double dataSet;
	
	private UserSimilarity similarity;
	private String method;
	private DataModel model;
	private MailUtil mailUtil;
	
	public CSFDEvaluator(String[] args) {
		if (args.length < 1) {
			help();
			return;
		}
		
		for (int i = 0; i < args.length; i++) {
			args[i] = args[i].replaceAll("[-_'\"]", "").toLowerCase();
		}

		random = false;
		mail = false;
		dataSet = FULL_DATA_SET;
		neighbors = -1;
		thresholdSize = -2.0;
		
		try {
			InputStream stream = this.getClass().getResourceAsStream(FILE_IN);
			model = new FileDataModel(streamToFile(stream));
//			model = new FileDataModel (new File(FILE_IN));
			parse(args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void parse(String[] args) throws Exception {
		neighborhood = false;
		threshold = false;
		weighted = false;
		getOptions(args);
		if(mail) {
			mailUtil = new MailUtil();
		}
		if(!random) {
			RandomUtils.useTestSeed();
		} else {
			method += "_r";
		}
		
		if (neighbors == -1 && thresholdSize == -2.0) {
			if (threshold) {
				allThresh();
			} else if (neighborhood) {
				allNbhood();
			} else {
				threshold = true;
				allThresh();
				threshold = false;
				neighborhood = true;
				allNbhood();
			}
		} else {
			startEvaluator();
		}	
	}
	
	private void allThresh() throws TasteException {
		for (int i = 0; i < THRESH_VALS.length; i++){
			thresholdSize = THRESH_VALS[i];
			startEvaluator();
		}
	}
	
	private void allNbhood() throws TasteException {
		for (int i = 0; i < NEIGHBOR_VALS.length; i++) {
			neighbors = NEIGHBOR_VALS[i];
			startEvaluator();
		}
	}
	
	private void getOptions(String[] args) throws Exception {
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("n") || args[i].equals("nbhood")) {
				neighborhood = true;
				checkForNumber(args[i + 1]);
			} else if (args[i].equals("t") || args[i].equals("threshold")) {
				threshold = true;
				checkForNumber(args[i + 1]);
			} else if (args[i].equals("w") || args[i].equals("weight")) {
				weighted = true;
			} else if (args[i].equals("r") || args[i].equals("random")) {
				random = true;
			} else if (args[i].equals("m") || args[i].equals("mail")) {
				mail = true;
			} else if (args[i].equals("test")){
				dataSet = TEST_DATA_SET;
			} else if (args[i].equals("p") || args[i].equals(M_PEARSON)) {
				method = M_PEARSON;
				if (weighted) {
					similarity = new PearsonCorrelationSimilarity(model, Weighting.WEIGHTED);
					method += "_w";
				} else {
					similarity = new PearsonCorrelationSimilarity(model);
				}
			} else if (args[i].equals("e") || args[i].equals(M_EUCLIDEAN)) {
				method = M_EUCLIDEAN;
				if (weighted) {
					similarity = new EuclideanDistanceSimilarity(model, Weighting.WEIGHTED);
					method += "_w";
				} else {
					similarity = new EuclideanDistanceSimilarity(model);
				}
			} else if (args[i].equals("tan") || args[i].equals(M_TANIMOTO)) {
				method = M_TANIMOTO;
				similarity = new TanimotoCoefficientSimilarity(model);
			} else if (args[i].equals("l") || args[i].equals(M_LOGLIKE)) {
				method = M_LOGLIKE;
				similarity = new LogLikelihoodSimilarity(model);
			} else if (args[i].equals("a") || args[i].equals("all")) {
//				parse(new String[]{"p"});
				parse(new String[]{"w", "p"});
//				parse(new String[]{"e"});
				parse(new String[]{"w", "e"});
				parse(new String[]{"tan"});
				parse(new String[]{"l"});
			}
		}
	}
	
	private void checkForNumber(String arg) {
		if (arg.matches(".*\\d+.*")) {
			getNumericValue(arg);
		}
	}
	
	private void getNumericValue(String arg) {
		try {
			if (neighborhood) {
				neighbors = Integer.parseInt(arg);
			} else if (threshold) {
				thresholdSize = Double.parseDouble(arg);
				if (thresholdSize > THRESH_UP || thresholdSize < THRESH_DOWN) {
					System.err.println("Threshold out of bounds <-1.0, 1.0>: " + thresholdSize);
					help();
				}
			}
		} catch (NumberFormatException e) {
			System.err.println("Wrong number format!");
			help();
		}
	}
	
	private void help() {
		System.out.println("DESCRIPTION: Application for evaluating a recommender system.\n" +
							"USAGE:\n" +
							"\tCSFDRecommender.jar [-w] [-r] [-m] [--test] [-n INT] [-t FLOAT] model\n" +
							"OPTIONS:\n" +
							"\t-w, --weight\n" +
							"\t\treflect the number of items which are being used for computation\n" +
							"\t-r, --random\n" +
							"\t\tuse random data seed\n" +
							"\t-m, --mail\n" +
							"\t\tsend results via email\n" +
							"\t--test\n" +
							"\t\ttest with a percentage of data being used\n" +
							"\t-n INT, --nbhood INT\n" +
							"\t\tdefine the size of similarity neighbourhood\n" +
							"\t-t FLOAT, --threshold FLOAT\n" +
							"\t\tdefine the size of similarity threshold\n" +
							"MODEL:\n" +
							"\t-p, --pearson\n" +
							"\t\tpearson correlation similarity model\n" +
							"\t-e, --euclidean\n" +
							"\t\teuclidean distance similarity model\n" +
							"\t-tan, --tanimoto\n" +
							"\t\ttanimoto coefficient similarity model\n" +
							"\t-l, --loglikelihood\n" +
							"\t\tloglikelihood similarity model\n" +
							"\t-a, --all\n" +
							"\t\tevaluate all similarity models one by one\n");
	}
	
	private void startEvaluator() throws TasteException {
		RecommenderEvaluator evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator ();
		RecommenderBuilder recommenderBuilder = new RecommenderBuilder() {
			@Override
			public Recommender buildRecommender(DataModel model) throws TasteException {
				UserNeighborhood nbh;
				if (neighborhood) {
					nbh = new NearestNUserNeighborhood(neighbors, similarity, model);
				} else {
					nbh = new ThresholdUserNeighborhood(thresholdSize, similarity, model);
				}
				
				return new GenericUserBasedRecommender(model, nbh, similarity);
			}
		};
		
		long startTime = System.currentTimeMillis();
		double score = evaluator.evaluate(recommenderBuilder, null, model, TEST_DATA, dataSet);
		long endTime = System.currentTimeMillis();
		String time = getTime(startTime, endTime);
		System.out.println(score + " " + time);
		
		printToFile(score, time);
	}
	
	private void printToFile(Double score, String time) {
		try {
			String output;
			String filename = LOG_DIR + method + FILE_OUT_SUFFIX;
			File log = new File(filename);
			File parentDir = log.getParentFile();
			if (!parentDir.exists() && !parentDir.mkdirs()) {
				System.err.println("Failed to create directory");
			} else if (!log.exists()) {
				log.createNewFile();
			} 
	
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(log.getAbsoluteFile(), true)));
			
			if (neighborhood)
				output = "Neighbourhood: " + neighbors + "\t";
			else
				output = "Threshold: " + thresholdSize + "\t";
			
			output += "Score: " + score + "\t\tTime: " + time;

			pw.println(output);
			pw.close();
			
			if (mailUtil != null) {
				mailUtil.sendEmail(method);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private File streamToFile(InputStream in) {
		File tempFile = null;
		try {
			tempFile = File.createTempFile("tempfile", ".tmp");
			tempFile.deleteOnExit();
			
			FileOutputStream out = new FileOutputStream(tempFile);
			ByteStreams.copy(in, out);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return tempFile;
	}
	
	private String getTime(long startTime, long endTime) {
		long totalTime = (long) ((endTime - startTime) / 1000);
		return String.format("%02dh %02dm %02ds", totalTime / 3600, (totalTime / 60) % 60, totalTime % 60);
	}
	
	public static void main(String[] args) {
		new CSFDEvaluator(args);
	}
}

