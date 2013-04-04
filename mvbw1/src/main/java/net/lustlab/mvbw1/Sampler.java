package net.lustlab.mvbw1;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Internal class used by Sampler
 * @author Edwin Jakobs
 *
 * @param <T>
 */
class SampleEntry<T> implements Comparable<SampleEntry<T>> {
	private final T value;
	private double weight;
	
	SampleEntry(T value, double weight ) {
		this.value = value;
		this.weight = weight;
	}

	public T getValue() {
		return value;
	}
	
	public double getWeight() {
		return weight;
	}
	
	public void setWeight(double weight) {
		this.weight = weight;
	}
	
	public int compareTo(SampleEntry<T> o) {
		if (weight < o.weight) {
			return 1;
		}
		else if (weight > o.weight){
			return -1;
		}
		else {
			return 0;
		}
	}
}

/**
 * A simple sampler
 * @author Edwin Jakobs
 *
 * @param <T> The type of the items the sampler holds
 */
public class Sampler<T> {

	List<SampleEntry<T>> entries = new ArrayList<SampleEntry<T>>();
	double[] cum = null;
	
	/**
	 * Add an item to the sampler
	 * @param value the value of the item
	 * @param weight the probability this item will be sampled
	 */
	public void add(T value, double weight) {
		entries.add(new SampleEntry<T>(value, weight));
	}

	/**
	 * Seals the sampler, normalises weights
	 */
	public void seal() {
		Collections.sort(entries);
		
		cum = new double[entries.size()];

		double mass = 0; 
		for (int i = 0; i < entries.size(); ++i ) {
			mass += entries.get(i).getWeight();
		}
		
		double sum = 0;
		for (int i = 0 ; i < entries.size(); ++i) {
			sum += entries.get(i).getWeight() / mass;
			cum[i] = sum;
		}
	}
	
	/**
	 * Returns a list of all items in the sampler
	 * @return a list of options
	 */
	public List<T> options() {
		List<T> options = new ArrayList<T>();
		for (SampleEntry<T> entry: entries) {
			options.add(entry.getValue());
		}
		return options;
	}
	
	public void removeOption(T option) {
		SampleEntry<T> toRemove = null;
		for (SampleEntry<T> entry: entries) {
			if (entry.getValue() == option) {
				toRemove = entry;
			}
		}
		if (toRemove != null) {
			entries.remove(toRemove);
		}
		seal();
	}
	
	/**
	 * Returns a random item 
	 * @return A random item
	 */
	public T sample() {
		double r = Math.random();
		
		for (int i = 0 ; i  < cum.length; ++i) {
			if (r < cum[i]) {
				return entries.get(i).getValue();
			}
		}
		
		// should be unreachable.
		return null;
	}
	
}
