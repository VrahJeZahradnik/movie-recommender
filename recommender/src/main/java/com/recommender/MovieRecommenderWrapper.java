package com.recommender;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletContext;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.cf.taste.web.RecommenderWrapper;

import com.google.common.io.ByteStreams;

public class MovieRecommenderWrapper extends RecommenderWrapper {

	private ServletContext context;
	
	protected MovieRecommenderWrapper() throws TasteException, IOException {
		super();
	}

	public MovieRecommenderWrapper(ServletContext context) throws TasteException, IOException {
		super();
		this.context = context;
	}

	@Override
	protected Recommender buildRecommender() throws IOException, TasteException {
		
		DataModel model = new FileDataModel(loadResourcesToFile("/WEB-INF/classes/data/20.dat"));
//		DataModel model = new FileDataModel(new File("src/main/resources/data/20.dat"));
		UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
		UserNeighborhood nbh = new NearestNUserNeighborhood(10, similarity, model);
		Recommender recommender = new GenericUserBasedRecommender(model, nbh, similarity);
		return recommender;
	}
	
	private File loadResourcesToFile(String resource) {
		InputStream stream = context.getResourceAsStream(resource);
		File tempFile = null;
		try {
			tempFile = File.createTempFile("tempfile", ".tmp");
			tempFile.deleteOnExit();
			
			FileOutputStream out = new FileOutputStream(tempFile);
			ByteStreams.copy(stream, out);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return tempFile;
	}
}
