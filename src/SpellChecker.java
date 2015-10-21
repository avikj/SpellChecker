import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
public class SpellChecker {
	private HashMap<String, Integer> trainedData;
	public static void main(String[] args) throws FileNotFoundException{//main method used for testing
		SpellChecker spellChecker = new SpellChecker();
		Scanner stdin = new Scanner(System.in);
		while(stdin.hasNext()){
			String[] ar = spellChecker.fix(stdin.next(), 2, 20);
			for(int i = 0; i < ar.length; i++)
				System.out.print(ar[i]+" ");
			System.out.println("\n");
		}
			
	}
	public SpellChecker() throws FileNotFoundException{
		trainedData = trainData();
	}
	public String[] fix(String bad, int degrees, int length){
		if(trainedData.containsKey(bad)) return new String[]{bad};
		ArrayList<String> possibilities = getPossibilities(bad.toLowerCase());
		ArrayList<Integer> ratings = getRatings(possibilities, trainedData);
		for(int i = 0; i < ratings.size(); i++){
			ratings.set(i, new Integer(ratings.get(i).intValue()*100));
		}
		while(degrees-->1){
			Iterator<String> it = ((ArrayList<String>)possibilities.clone()).iterator();
			while(it.hasNext()){
				ArrayList<String> lowerPosses = getPossibilities(it.next());
				ArrayList<Integer> lowerRats = getRatings(lowerPosses, trainedData);
				Iterator<String> lpit = lowerPosses.iterator();
				Iterator<Integer> lrit = lowerRats.iterator();
				while(lpit.hasNext()){
					possibilities.add(lpit.next());
					ratings.add(lrit.next());
				}
			}
		}
 		return (getMostProbable(bad, possibilities, ratings, length));
	}
	public int getRating(String s){
		Integer result = trainedData.get(s);
		if(result==null)
			result = new Integer(0);
		return result.intValue();
	}
	public static String[] getMostProbable(String bad, ArrayList<String> possibilities, ArrayList<Integer> prob, int length){
		String[] result = new String[length];
		int[] ratings = new int[length];
		for(int i: ratings){ratings[i]=0;}
		Iterator<String> sit = possibilities.iterator();
		Iterator<Integer> iit = prob.iterator();
		int max = -1;
		//String result = "";
		while(sit.hasNext()){
			int rating = iit.next().intValue();
			String word= sit.next();
			sorter: for(int i = 0; i < ratings.length; i++){
				if(rating>ratings[i]){
					for(int j = ratings.length-1; j > i/*ratings[i]*/; j--){							//TODO
						ratings[j] = ratings[j-1];
						result[j] = result[j-1];
					}
					ratings[i] = rating;
					result[i] = word;
					break sorter;
				}
			}
		}
		String taken = "";
		int valCount = 0;
		for(int i = 0; i < length; i++){		//remove nulls and repeats
			if(result[i]==null) result[i]="";
			if(taken.contains(result[i])){
				result[i] = "";
			}
			else{
				taken+=result[i]+" ";
				valCount++;
			}
		}
		
		String[] trimmedResult = new String[valCount];
		int ti = 0;
		for(int i = 0; i < result.length; i++){
			if(!result[i].equals("")){
				trimmedResult[ti++] = result[i];
			}
		}
		if(max == 0) return (new String[]{("(No suggestions for \""+bad+"\")")});
		return trimmedResult;
	}
	public static ArrayList<Integer> getRatings(ArrayList<String> possibilities, HashMap<String, Integer> trained){
		ArrayList<Integer> result = new ArrayList<Integer>(0);
		Iterator<String> it = possibilities.iterator();
		while(it.hasNext()){
			String temp = it.next();
			Integer res = trained.get(temp);
			if(res==null)
				res = new Integer(0);
			result.add(res);
		}
		return result;
	}
	public static HashMap<String, Integer> trainData() throws FileNotFoundException{
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		Scanner file = new Scanner(new File("big.txt"));
		while(file.hasNext()){
			String key = file.next().toLowerCase();
			Integer result = map.get(key);
			if(result==null)
				result = new Integer(0);
			map.put(key, result.intValue()+1);
		}
		return map;
	}
	public static ArrayList<String> getPossibilities(String bad){
		ArrayList<String> possibilities = new ArrayList<String>(0);
		for(int i = 0; i < bad.length(); i++){						//deletions
			String result = bad.substring(0, i)+bad.substring(i+1);
			possibilities.add(result);
		}

		for(int i = 0; i < bad.length()+1; i++){					//insertions
			for(char c = 97; c <= 122; c++){
				String result = bad.substring(0, i)+c+bad.substring(i);
				possibilities.add(result);
			}
		}
		
		for(int i = 0; i < bad.length(); i++){						//replacements
			for(char c = 97; c <= 122; c++){
				String result = bad.substring(0, i)+c+bad.substring(i+1);
				possibilities.add(result);
			}
		}
		for(int i = 0; i < bad.length()-1; i++){					//letter swaps
			String result = bad.substring(0, i)+bad.charAt(i+1)+bad.charAt(i)+bad.substring(i+2);
			possibilities.add(result);
		}
		return possibilities;
	}
	
	
}