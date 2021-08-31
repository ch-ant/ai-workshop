import java.util.ArrayList;


/*
	Antoniou Christodoulos 2641
	Efi Karanika 2453
	Gerasimos Pitoulis 2803
	
	compile command: 
	javac Search.java

	run command: 
	java Search <search algorithm> <initial state>

	<search algorithm>: ucs OR alphastar 
	<initial state>: shuffled sequence of ints from 1 to N	split by a comma (N is the total number of ints)	(duplicate values are not allowed)
	
	run examples:
	java Search ucs 4,3,5,2,1
	java Search alphastar 4,3,5,2,1 
*/


class State {


	private ArrayList<Integer> list = new ArrayList<Integer>();
	private State parent = null;
	private String transitionOperator;	// telestis metavasis
	private int cost;	// g(n)

	public ArrayList<Integer> getList() {
		return this.list;
	}

	public State getParent() {
		return this.parent;
	}

	public String getTransitionOperator() {
		return this.transitionOperator;
	}

	public int getCost() {
		return this.cost;
	}

	public void setParent(State Parent) {
		this.parent = Parent;
	}

	public void setTransitionOperator(String Operator) {
		this.transitionOperator = Operator;
	}

	public void setCost(int Cost) {
		this.cost = Cost;
	}

	/* 
		Override necessary for STEP 4 in method search().
		We want contains() to return true as long as two State classes have the same list
		in order to check whether a state is part of the closed queue or not.
	*/
	@Override
	public boolean equals(Object o) {

		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		State state = (State) o;
        
		return this.list.equals(state.getList());
	}
}


class Search {
    

	public static String searchAlgorithm;
	public static State initialState;
	public static int N;
	public static int extendCounter = 0;
	public static int statesCounter = 1; 
	public static String line = "=====================================================\n";


	/**
	 * A method that checks whether the command line arguments are valid
	 * 
	 * @param args: array of command line args
	 * @return true for valid args - otherwise false
	 */
	private static boolean validateArgs(String[] args) {

		if (args.length == 2) {

			if (args[0].equals("ucs") || args[0].equals("alphastar")) {

				searchAlgorithm = args[0];	// save <search algorithm>
				
				initialState = new State();	// new State object for the inital state
				initialState.setCost(0);	// cost g(n) of the initial state is 0
				initialState.setTransitionOperator("Initial State");	// no transition operator for the inital state (telestis metavasis)
				String[] temp = args[1].split(",");	// temporarily split the <initial state> sequence to an array of strings
				N = temp.length;	// the total number of ints N is equal to the length of the array

				// transfer the temp array to the ArrayList<Integer> of the initial state object
				for (int i=0; i<N; i++) {
					initialState.getList().add(Integer.parseInt(temp[i]));
				}
				
				// at this point check whether the state list is valid
				return validateStateList(initialState.getList());
			}
		}
		return false;
	}


	/**
	 * A method that checks whether a state list is valid
	 * 
	 * @param list: ArrayList containing the integers of a state
	 * @return true for a valid list - otherwise false
	 */
	private static boolean validateStateList(ArrayList<Integer> list) {

		int occurances;

		for (int i=0; i<N; i++) {

			// check if each integer is between 1 and N
			if (list.get(i) < 1 || list.get(i) > N) {
				return false;
			}

			occurances = 0;	// occurances of an integer in the list

			// check for duplicates of each integer
			for (int j=0; j<N; j++) {

				if(list.get(i) == list.get(j)) {
					occurances++;
				}
			}
			
			if (occurances != 1) return false;
		}

		return true;
	} 


	/**
	 * Main method implementing the general search algorithm
	 */
	private static void search() {

		State minState = null;

		ArrayList<State> searchQueue = new ArrayList<State>();	// searchQueue = metopo anazitisis
		searchQueue.add(initialState);	// STEP 1. add the initial state to the search queue
		
		ArrayList<State> closedQueue = new ArrayList<State>();	// closed queue = kleisto sinolo


		while (true) {

			// STEP 2. empty search queue scenario
			if (searchQueue.isEmpty()) {	
				System.out.print("No final state has been found for the given initial state:\n" + initialState.getList() + "\n");
				System.exit(-2);
			}

			// STEP 3. apply the given <search algorithm> (ucs OR alphastar)
			if (searchAlgorithm.equals("ucs")) {
				minState = ucs(searchQueue);
			}
			else if (searchAlgorithm.equals("alphastar")) {
				minState = alphastar(searchQueue);
			}
			else {
				System.out.print("An unexpected error has occured");
				System.exit(-3);
			}

			// STEP 4. check if the selected state has already been extended
			if (!closedQueue.contains(minState)) {

				// STEP 5. check if the selected state is a final state
				if (isFinalState(minState)) {
					printSolution(minState);
					break;
				}
				// STEP 6. extend the selected state - add the new states to the search queue - add the selected state to the closed queue
				else {
					extendState(minState, searchQueue);
					closedQueue.add(minState);
					extendCounter++;

					// DEBUG - print a dot once every 1000 states have been extended to indicate that the program is still running
					//if (extendCounter%1000 == 0 ) System.out.print(".");
					// or just constantly print the updated extend counter
					System.out.print("\rSearching . . . " + extendCounter + " extends so far");
				}
			}
		}
	}


	/**
	 * A method responsible for extending the selected state
	 * 
	 * @param state: the selected state to be extended and removed from the search queue
	 * @param searchQueue: the search queue (metopo anazitisis)
	 */
	private static void extendState(State state, ArrayList<State> searchQueue) {

		State transition;

		// apply the transition operators T(k) where 1<k<=N
		for (int i=1; i<N; i++) {

			transition = new State();	// create a new state for each T(k)

			transition.setCost(state.getCost() + 1);
			transition.setParent(state);
			transition.setTransitionOperator("T(" + (i+1) + ")");

			// integers from 1 to k are reversed and added to the list of the new state
			for (int j=0; j<=i; j++) {
				transition.getList().add(state.getList().get(i-j));
			}

			// integers from k+1 to N are unchanged and added to the list of the new state
			for (int j=i+1; j<N; j++) {
				transition.getList().add(state.getList().get(j));
			}

			searchQueue.add(transition);
			statesCounter++;
		}
	}


	/**
	 * A method that finds and prints the solution path when a final state has been found
	 * 
	 * @param state: the selected state that was determined as final state
	 */
	private static void printSolution(State state) {

		ArrayList<State> solutionPath = new ArrayList<State>();

		// find the solution path 
		while (state.getParent() != null) {

			solutionPath.add(state);	// add the current state to the solution path
			state = state.getParent();	// move to the parent
		}

		// finally add the inital state to the solution path
		solutionPath.add(initialState);

		System.out.print("\n" + line);

		// run the solution path (backwards) and print it
		for (int i = solutionPath.size()-1; i>=0; i--) {

			// print state number + state cost + state list + transition operator
			String message = "State: " + (solutionPath.size()-i) + 
											"\tCost: " + String.valueOf(solutionPath.get(i).getCost()) + "\t\t" + 
											solutionPath.get(i).getList() + "\t" + 
											solutionPath.get(i).getTransitionOperator();

			System.out.println(message);
		}

		System.out.print("\nTotal states: " + statesCounter + "\tExtends: " + extendCounter + "\n" + line);
	}


	/**
	 * A method responsible for determining whether a state is a final state or not
	 * 
	 * @param state: the selected state
	 * @return true if the state parameter is a final state - otherwise false
	 */
	private static boolean isFinalState(State state) {

		for (int i=0; i<N; i++) {

			if (state.getList().get(i) != (i+1)) {
				return false;
			}
		}
		return true;
	}


	/**
	 * A method that implements the ucs algorithm
	 * determines the state with the lowest cost g(n) in the search queue
	 * 
	 * @param searchQueue: the search queue (metopo anazitisis)
	 * @return a State object which is the selected state by the algorithm
	 */
	private static State ucs(ArrayList<State> searchQueue) {
		
		State minState;
		int minCost = searchQueue.get(0).getCost();	// start from the leftmost state in the search queue
		int statePosition = 0;

		// run the search queue and check if there is a state with a lower cost
		for (int i=0; i<searchQueue.size(); i++) {

			if (searchQueue.get(i).getCost() < minCost) {
				minCost = searchQueue.get(i).getCost();
				statePosition = i;
			}
		}

		// the selected state is removed from the search queue and then returned
		minState = searchQueue.get(statePosition);
		searchQueue.remove(statePosition);
		
		return minState;
	}


	/**
	 * A method that implements the alphastar algorithm
	 * determines the state with the min(g(n)+h(n)) in the search queue
	 * 
	 * @param searchQueue: the search queue (metopo anazitisis)
	 * @return a State object which is the selected state by the algorithm
	 */
	private static State alphastar(ArrayList<State> searchQueue) {

		
		State minState;
		// start from the leftmost state in the search queue 
		int min = searchQueue.get(0).getCost() + heuristic(searchQueue.get(0).getList());
		int statePosition = 0;
		int heuristic;
		String message;

		System.out.println();	// comment this for faster execution

		// run the search queue and find the state with the min(g(n)+h(n))
		for (int i=0; i<searchQueue.size(); i++) {

			heuristic = heuristic(searchQueue.get(i).getList());
			
			if ((searchQueue.get(i).getCost() + heuristic) < min) {
				min = searchQueue.get(i).getCost() + heuristic;
				statePosition = i;
			}
			
			message = "DEBUG: list: " + searchQueue.get(i).getList()  + 
								"\th(n)=" + heuristic +
								"  g(n)=" + searchQueue.get(i).getCost() +
								"\t\tg(n)+h(n)=" + (searchQueue.get(i).getCost() + heuristic);

			System.out.println(message);	// comment this for faster execution
		}
		
		// the selected state is removed from the search queue and then returned
		minState = searchQueue.get(statePosition);
		searchQueue.remove(statePosition);

		// comment this for faster execution
		System.out.println("> selected state:" + minState.getList() + "\tmin(g(n)+h(n)):" + min + "\n");

		return minState;
	}


	/**
	 * A method that implements the heuristic function for a given state list
	 * 
	 * @param list: ArrayList containing the integers of a state
	 * @return h(n)
	 */
	private static int heuristic(ArrayList<Integer> list) {

		int heuristic = 0;

		// run the list excluding the first item
		for (int i=1; i<N; i++) {

			// if any given list item is not at the expected position for a final state
			if (list.get(i) != (i+1)) {
				
				if (heuristic == 0) heuristic++;	// at least one transition operation is required
				
				// check descending order with previous list item
				if (list.get(i-1) > list.get(i) && list.get(i-1) != (list.get(i)+1)) heuristic++;
			}
		}
		return heuristic;
	}



    public static void main(String[] args) {
		

		// invalid command line args handling
		if (!validateArgs(args)) {
			String message = "Invalid args!\n\n" +
											"run command:\n" +
											"java ask1 <search algorithm> <initial state>\n\n" +
											"<search algorithm>: ucs OR alphastar\n" +
											"<initial state>: shuffled sequence of ints from 1 to N split by a comma (N is the total number of ints) (duplicate values are not allowed)\n\n" +
											"run examples:\n" +
											"java ask1 ucs 4,3,5,2,1\n" +
											"java ask1 alphastar 4,3,5,2,1\n";
			
			System.out.print(line+message+line);
			System.exit(-1);
		}
		
		
		search();


	}
}
