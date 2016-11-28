import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;



public class Sampling {
	public static int nop = 2;

	public static Hashtable<String, Double> E = new Hashtable<String, Double>();
	public static Hashtable<String, Double> B = new Hashtable<String, Double>();
	public static Hashtable<String, Double> A = new Hashtable<String, Double>();
	public static Hashtable<String, Double> M = new Hashtable<String, Double>();
	public static Hashtable<String, Double> J = new Hashtable<String, Double>();
	
	HashMap<String, ArrayList<String>> parents = new HashMap<String, ArrayList<String>>();
	public static HashMap<String, String> evi = new HashMap<String, String>();
	
	public static String evidence[][] ;
	public static String query[] ;
	public static int values[] = new int[5];
	public static int n,m;
	
	
	public static String[] vars = { "E", "B", "A", "M", "J" };
	public static Stack<String> variable;

	
	public static void createTables() {
		// Create table B
		B.put("t", 0.001);
		B.put("f", 0.999);

		// Create Table E
		E.put("t", 0.002);
		E.put("f", 0.998);

		// Create Table A

		A.put("tt", 0.95);
		A.put("tf", 0.94);
		A.put("ft", 0.29);
		A.put("ff", 0.001);

		// Create Table J
		J.put("t", 0.9);
		J.put("f", 0.05);

		// Create Table M
		M.put("t", 0.70);
		M.put("f", 0.01);

		variable = new Stack<String>(); //Stack of variables
		variable.push("M");
		variable.push("J");
		variable.push("A");
		variable.push("B");
		variable.push("E");
	}
	

	/*
	 * This function is to create a function to maintain the information about
	 * the parents in the Hashmap.
	 */
	public static HashMap<String, ArrayList<String>> createParents() {
		HashMap<String, ArrayList<String>> parents = new HashMap<String, ArrayList<String>>();

		ArrayList<String> a = new ArrayList<String>();
		a.add("B");
		a.add("E");
		parents.put("A", a);

		ArrayList<String> j = new ArrayList<String>();
		j.add("A");
		parents.put("J", j);

		ArrayList<String> m = new ArrayList<String>();
		m.add("A");
		parents.put("M", m);

		ArrayList<String> b = new ArrayList<String>();
		parents.put("B", b);

		ArrayList<String> e = new ArrayList<String>();
		parents.put("E", e);

		return parents;
	}
	/*
	 * The method Prob returns the probability value of each of the element by
	 * considering the dependencies as well. Below is the Program which
	 * implements the enumeration algorithm as specified in Russel and Norvig
	 * Book.
	 * 
	 * element: it is the Element whose probability value is suppose to be found
	 * out value: the value of the element(True or False) evi: HashMap which
	 * contains the given evidences and their values parents: HashMap which
	 * contains the elements and the ArrayList of its Parents
	 * 
	 */

	public static Double Prob(String element, String value, HashMap<String, String> evi,
			HashMap<String, ArrayList<String>> parents) {
		Double finalProb = (double) 0;
		ArrayList<String> p = new ArrayList<String>();

		p = parents.get(element);
		String parentVals = "";
		if (p.size() == 0) { // Size of parent is 0 which means it is E or B as
								// they have no parents
			if (element.equalsIgnoreCase("E")) {
				finalProb = E.get("t");
			} else if (element.equalsIgnoreCase("B"))
				finalProb = B.get("t");
		} else {

			int n = p.size(); // size of parents
			for (int i = 0; i < n; i++) { // Getting the value of Parent
				parentVals = parentVals + evi.get(p.get(i));
			}
			if (element.equalsIgnoreCase("A")) { // If parent is A then get the
													// Probability of A
				finalProb = A.get(parentVals);
			} else if (element.equalsIgnoreCase("J")) { // If parent is J then
														// get the Probability
														// of J
				finalProb = J.get(parentVals);
			} else if (element.equalsIgnoreCase("M")) { // If parent is M then
														// get the Probability
														// of M
				finalProb = M.get(parentVals);
			}
		}
		if (value.equalsIgnoreCase("t")) {
			return finalProb;
		}

		else {
			return (1 - finalProb); // if the value for element is f then return
									// the Probability for False
		}
	}
	// This function is to calculating the probability. It normalizes for the
	// true and false value
	public static HashMap<String, Double> normalize(HashMap<String, Double> QX) {

		Double total = 0.0;

		for (String s : QX.keySet()) {
			total = QX.get(s) + total;
		}

		for (String s : QX.keySet()) {
			QX.put(s, QX.get(s) / total);
		}
		return QX;
	}

	/*
	 * The enumerationAsk function takes the input of following: query: it is
	 * the Query variable var: Stack of all the elements evidence[][]: it is the
	 * list of evidence variables and their values as provided by the user
	 * parents: its the HashMap of variables and their parents. varss: It is the
	 * array which contains list of all variables.
	 */
	public static HashMap<String, Double> enumerationAsk(String query, Stack<String> var, String evidence[][],
			HashMap<String, ArrayList<String>> parents, String[] varss) {

		HashMap<String, Double> QX = new HashMap<String, Double>();

		String output[] = { "f", "t" }; // used to enumerate through the true as
										// well as false values of the query
										// variable
		String e[] = new String[2];

		HashMap<String, String> evi = new HashMap<String, String>();

		for (int k = 0; k < evidence.length; k++) {
			evi.put(evidence[k][0], evidence[k][1]);
		}
		for (int i = 0; i < output.length; i++) {
			evi.put(query, output[i]);
			QX.put(output[i], enumerateAll(var, evi, parents)); // puts the
																// calculated
																// probability
																// for True and
																// False and
																// stores it in
																// HashMap
			evi.remove(query);
		}
		return normalize(QX);

	}
	
	/*
	 * This function is used to enumerate through all the elements of the
	 * list.The following arguments are used in the program:
	 * 
	 * 
	 */

	public static Double enumerateAll(Stack<String> var, HashMap<String, String> evi,
			HashMap<String, ArrayList<String>> parents) {
		if (var.isEmpty())
			return (double) 1.0;
		String s = var.pop();
		if (evi.containsKey(s)) {		//No need to normalise if the element is present in evidence as its value is determined.
			Double valu = Prob(s, evi.get(s), evi, parents) * enumerateAll(var, evi, parents);
			var.push(s);
			return valu;
		} else {
			Double total = 0.0;
			evi.put(s, "t");				//Normalising for the True value of variable
			total = total + Prob(s, "t", evi, parents) * enumerateAll(var, evi, parents);
			evi.put(s, "f");				//Normalising for the False value of variable
			total = total + Prob(s, "f", evi, parents) * enumerateAll(var, evi, parents);
			evi.remove(s);						
			var.push(s);
			return total;
		}

	}
	
	public static double generateRandom() {
		double rand = ThreadLocalRandom.current().nextDouble(0.00, 1.00);
		return rand;
	}

	public static void printArray(int[] input) {
		for (int i = 0 ; i < input.length ; i ++){
			System.out.print (input[i] + "\t");
		}
		System.out.println();
	}
	// Used to compare sample with input recieved 
	public static boolean compareValues(int []samples , int []input){
		boolean flag= false;
		for (int j = 0 ; j < samples.length ; j ++){
			if (input[j] != -1  ){
				if (samples[j] != input[j])
					flag = true;
			}
		}
		
		return flag;
	}
	
	
	public static double likelihoodSampling(int [][]samples, int input[],double[] prob, int query){
		double cntnum = 0.00;
		double cntden = 0.00;
		double result = 0.00;
		int inwquery [] = new int [input.length];
		
		for (int i = 0 ; i  < input.length ; i ++){
			if (i == query){
				inwquery[i] = 1;
			}
			else {
				inwquery[i] = input[i];
			}
		}
		for (int i = 0 ; i < samples.length ; i++){
			boolean denflag = false;
			boolean numflag = false;
			
			denflag = compareValues(samples[i], input);
			if (!denflag){
				cntden += prob[i];
			}
			
			numflag = compareValues(samples[i], inwquery);
			if (!numflag){
				cntnum += prob[i];
			}
		}
		if (cntden !=0){
			result = (cntnum/cntden);
		}else result = 0; 
			
		return result;
	}
	
	public static double priorSampling(int [][]samples, int input[], int query){
		double cntnum = 0.00;
		double cntden = 0.00;
		double result = 0.00;
		int inwquery [] = new int [input.length];
		
		for (int i = 0 ; i  < input.length ; i ++){
			if (i == query){
				inwquery[i] = 1;
			}
			else {
				inwquery[i] = input[i];
			}
		}
		for (int i = 0 ; i < samples.length ; i++){
			boolean denflag = false;
			boolean numflag = false;
			
			denflag = compareValues(samples[i], input);
			if (!denflag){
				cntden ++;
			}
			
			numflag = compareValues(samples[i], inwquery);
			if (!numflag){
				cntnum ++;
			}
		}
		if (cntden != 0.0){
			result = cntnum/cntden;
		}else result = 0.0;
		
		
		return result;
	}

	public static int[][] generateRejectionSamples(int noofsamples, int input[]){
		
		int [][] rejectionSamples = new int[noofsamples][5];
		int counter = 0 ; 
		while (counter < noofsamples){
			int [][] checkInput = generateSamples(1);
			if (compareValues(checkInput[0], input)){
				continue;
			}
			rejectionSamples[counter] = checkInput[0];	
			counter ++;
		}
		return rejectionSamples;
	}
	
	
	public static int[][] generateLikelihoodSamples(int noofsamples, double prob[], int input[] ){
		int[][] samples = new int[noofsamples][5];
		int counter = 0;
		
		double bur;
		double ear;
		double alm;
		double jhn;
		double mry;

		while (counter < noofsamples){
			double weight = 1.000; 
			
			if (input[0] != -1){
				if (input[0] == 1){
					weight *= B.get("t");
					samples[counter][0] = 1;
				}
				else {
					weight *= B.get("f");
					samples[counter][0] = 0;
				}
			}
			else {
				bur = generateRandom();
				if (bur <= B.get("t")) {
					samples[counter][0] = 1;
				} else {
					samples[counter][0] = 0;
				}
			}
			
			if (input[1] != -1){
				if (input[1] == 1){
					weight *= E.get("t");
					samples[counter][1] = 1;
				}else {
					weight *= E.get("f");
					samples[counter][1] = 0;
				}

				
			}else {
				ear = generateRandom();
				if (ear <= E.get("t")) {
					samples[counter][1] = 1;
				}
				else {
					samples[counter][1] = 0;
				}
			}
			String almcond = "" ;
			if (samples[counter][0] == 1){
				almcond += "t";
			}
			else almcond += "f";
			
			if (samples[counter][1] == 1){
				almcond += "t";
			}
			else {
				almcond += "f";
			}
			if (input[2] != -1){
				if (input[2] == 1){
					weight *= A.get(almcond);
					samples[counter][2] = 1;
				}
				else {
					weight *= (1 - A.get(almcond));
					samples[counter][2] = 0;
				}
			}else {
				alm = generateRandom();
				if (alm <= A.get(almcond)){
					samples[counter][2] = 1;
				}
				else {
					samples[counter][2] = 0;
				}
			}
			
			String jhncond = "" ;
			if (samples[counter][2] == 1){
				jhncond += "t";
			}
			else jhncond += "f";
			
			if (input[3] != -1){
				if ( input[3] == 1){
					weight *= J.get(jhncond);
					samples[counter][3] = 1;
				}else {
					weight *= (1 - J.get(jhncond));
					samples[counter][3] = 0;
				}
			}
			else {
				jhn = generateRandom();
				
				if (jhn <= J.get(jhncond)){
					samples[counter][3] = 1;
				}
				else {
					samples[counter][3] = 0;
				}
			}
			
			if (input[4] != -1){
		
				if (input[4] == 1){
					weight *= M.get(jhncond);
					samples[counter][4] = 1;
				}else {
					weight *= (1 - M.get(jhncond));
					samples[counter][4] = 0;
				}
				
			}else {
				mry = generateRandom();
				if (mry <= M.get(jhncond)){
					samples[counter][4] = 1;
				}
				else {
					samples[counter][4] = 0;
				}
			}
			
			prob[counter] = weight;
			counter++;
	}

		return samples;		
	}
	
	public static int[][] generateSamples(int noofsamples) {
			int[][] samples = new int[noofsamples][5];
			int counter = 0;
			double bur;
			double ear;
			double alm;
			double jhn;
			double mry;
	
			while (counter < noofsamples){
				bur = Math.random();

				if (bur <= B.get("t")) {
					samples[counter][0] = 1;

				} else {
					samples[counter][0] = 0;
				}
				ear = Math.random();

				if (ear <= E.get("t")) {
					samples[counter][1] = 1;

				}
				else {
					samples[counter][1] = 0;
				}
				
				alm = Math.random();

				String almcond = "" ;
				if (samples[counter][0] == 1){
					almcond += "t";
				}
				else almcond += "f";
				
				if (samples[counter][1] == 1){
					almcond += "t";
				}
				else {
					almcond += "f";
				}

				if (alm <= A.get(almcond)){
					samples[counter][2] = 1;

				}
				else {
					samples[counter][2] = 0;
				}
				
				jhn = Math.random();

				String jhncond = "" ;
				if (samples[counter][2] == 1){
					jhncond += "t";
				}
				else jhncond += "f";
				if (jhn <= J.get(jhncond)){
					samples[counter][3] = 1;

				}
				else {
					samples[counter][3] = 0;
				}
				
				
				mry = Math.random();

				if (mry <= M.get(jhncond)){
					samples[counter][4] = 1;

				}
				else {
					samples[counter][4] = 0;
				}

				counter++;
		}			
		return samples;

	}
	
	
	public static int getIndex(String query){
		
		switch (query.toLowerCase()) {
		case "b":
			return 0;
		case "e":
			return 1;
		case "a":
			return 2;
		case "j":
			return 3;
		case "m":
			return 4;
		default:	
			break;
		}
		return 0;
	}
	
	public static void readInput(){
		
		Scanner in = new Scanner(System.in);
		String startinput[] = in.nextLine().split(" ");
		if (startinput.length != 2 ){
			System.out.println("Incorrect values for m & n. Exiting ");
			System.exit(1);
		}
		n = Integer.parseInt(startinput[0]);
		m = Integer.parseInt(startinput[1]);
		values = new int[5];
		evidence = new String[n][nop];
		query = new String[m];
		

		for (int i = 0; i < n; i++) {
			evidence[i] = in.nextLine().split(" ");
		}

		for (int i = 0; i < m; i++) {
			query[i] = in.nextLine();
		}

		for (int i = 0; i < values.length; i++) {
			values[i] = -1;
		}
		
		for (int i = 0; i < n; i++) {
			switch (evidence[i][0].toLowerCase()) {
			case "b":
				if (evidence[i][1].toLowerCase().equals("t")){
					values[0] = 1;
				}
				else {
					values[0] = 0;
				}
				break;
			case "e":
				if (evidence[i][1].toLowerCase().equals("t")){
					values[1] = 1;
				}
				else {
					values[1] = 0;
				}
				
				break;
			case "a":
				if (evidence[i][1].toLowerCase().equals("t")){
					values[2] = 1;
				}
				else {
					values[2] = 0;
				}
				
				break;
			case "j":
				if (evidence[i][1].toLowerCase().equals("t")){
					values[3] = 1;
				}
				else {
					values[3] = 0;
				}
				
				break;
			case "m":
				if (evidence[i][1].toLowerCase().equals("t")){
					values[4] = 1;
				}
				else {
					values[4] = 0;
				}
				break;
			default:
				break;
			}
			
		}
	
		
	}
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		createTables();
		
		
		if (args[0].equalsIgnoreCase("p")){
			int samplesize = Integer.parseInt(args[1]);
			if (samplesize >= 10 && samplesize <= 50000){
				readInput();
				int samples[][] = generateSamples(samplesize);
				for (int i =0; i < m ; i++){
					System.out.println(query[i] + " " + priorSampling(samples, values,getIndex(query[i])));
					
				}
			}else {
				System.out.println("Please enter a valid selection of sample size");
			}
		}
		else if(args[0].equalsIgnoreCase("r")){
			int samplesize = Integer.parseInt(args[1]);
			if (samplesize >= 10 && samplesize <= 50000){
				readInput();
				int samples[][] = generateRejectionSamples(Integer.parseInt(args[1]),values);
				for (int i =0; i < m ; i++){
					System.out.println(query[i] + " " + priorSampling(samples, values,getIndex(query[i])));
				
				}
			}else {
				System.out.println("Please enter a valid selection of sample size");
			}
		}else if (args[0].equalsIgnoreCase("l")){
			int samplesize = Integer.parseInt(args[1]);
			if (samplesize >= 10 && samplesize <= 50000){
				readInput();
				double[] prob = new double[Integer.parseInt(args[1])];
				int samples[][] = generateLikelihoodSamples(Integer.parseInt(args[1]), prob ,values);
				
				for (int i =0; i < m ; i++){
					System.out.println(query[i] + " " + likelihoodSampling( samples, values,prob,getIndex(query[i])));
					
				}
			}else {
				System.out.println("Please enter a valid selection of sample size");
			}
			
		}else if (args[0].equalsIgnoreCase("e")){
			if (Integer.parseInt(args[1]) == 0){
				readInput();
				String varss[] = { "E", "B", "A", "M", "J" };
				for (int k = 0; k < evidence.length; k++) {
					evi.put(evidence[k][0], evidence[k][1]);
				}
				
				HashMap<String, ArrayList<String>> parents = createParents();
				
				for (int i = 0; i < query.length; i++) {

					String que = query[i];
					HashMap<String, Double> finalEnum = enumerationAsk(que, variable, evidence, parents, varss);

					System.out.println(query[i] + " " + finalEnum.get("t"));
				}
				
			}else {
				System.out.println("The sample size for enumeration should be 0");
			}
		}
		
		
			
//		System.out.println("Program ends");

	}

}
