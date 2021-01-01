package edu.coursera.distributed;

import org.apache.spark.api.java.JavaRDD;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.spark.api.java.JavaPairRDD;
import scala.Tuple2;

/**
 * A wrapper class for the implementation of a single iteration of the iterative
 * PageRank algorithm.
 */
public final class PageRank {
    /**
     * Default constructor.
     */
    private PageRank() {
    }

    /**
     *  
     * @param sites The connectivity of the website graph, keyed on unique
     *              website IDs.
     * @param ranks The current ranks of each website, keyed on unique website
     *              IDs.
     * @return The new ranks of the websites graph, using the PageRank
     *         algorithm to update site ranks.
     */
    public static JavaPairRDD<Integer, Double> sparkPageRank(
            final JavaPairRDD<Integer, Website> sites,
            final JavaPairRDD<Integer, Double> ranks) {
    
    	//Map Reduce - input + group steps
    	JavaPairRDD<Integer, Double> newlyRanked = 
	    	sites.join(ranks).
	    		flatMapToPair( pair -> {
	    			//pair is <Integer, Tuple2<Website, Double>> from join()
	    			//get individual components
	    			Integer websiteId = pair._1();
	    			Tuple2<Website, Double> kv = pair._2();
	    			Website edges = kv._1();
	    			Double rank = kv._2();
	    			
	    			List<Tuple2<Integer, Double>> contributions = new LinkedList<Tuple2<Integer, Double>>();
	    			Iterator<Integer> iter = edges.edgeIterator();
	    			//iterate over list of connected websites
	    			while(iter.hasNext()) {
	    				Integer linkToNextWebsite = iter.next();
	    				//current rank / number of outgoing edges
	    				contributions.add(new Tuple2<Integer, Double>(linkToNextWebsite, rank/(double) edges.getNEdges()));
	    			}
	    			//can store Tuple2 in JavaPairRDD
	    			return contributions;
	    	});
    	
    	//Map Reduce - Reduce step
    	//new_rank(B) = 0.15 + 0.85 * sum(rank(A) / out_count(A)) for all A linking to B
    	return newlyRanked.reduceByKey((Double v1, Double v2) -> v1+v2).mapValues(val ->  0.15 + 0.85 * val);
    	
    }
}
