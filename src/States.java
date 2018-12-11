import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;
import java.util.Stack;
import java.util.Vector;

public class States {
	private final int MAX_ITERATION = 100;
	PrintWriter writer;
	private int algo; // the algorithim to be used in this session
	private int n, m; // size of game board
	private Node start; // initial node with the state
	private HashMap<String, Node> open_list; 
	private HashMap<String, Node> closed_list;
	private int calcalatedStates; // amount of states calculated
	private long timer; // time for running of program
	private PairFinish mPairFinish;

	public Tile[] allTiles; // an array for all tiles created in this session
	public Vector<Tile> tiles; // a way to keep the tiles we create first (colored tiles)
	public Tile[] initial; 
	public String[][] checkInit;
	private String goal;

	private final double RED = 2, YELLOW = 0.5, WHITE = 1, FOUND = -1;

	/**
	 * constructor of the states class
	 */
	public States() {
		this.algo = 0;
		this.calcalatedStates = 0;
		this.mPairFinish = new PairFinish();
		this.tiles = new Vector<Tile>();
		this.open_list = new HashMap<String, Node>();
		this.closed_list = new HashMap<String, Node>();
	}

	public void setAlgo(int alg) {
		this.algo = alg;
	}

	public int getAlgo() {
		return this.algo;
	}

	public void setN(int n) {
		this.n = n;
	}

	public void setM(int m) {
		this.m = m;
	}

	public int getN() {
		return this.n;
	}

	public int getM() {
		return this.m;
	}

	public Node getStart() {
		return start;
	}

	public void setStart(Node start) {
		this.start = start;
	}

	public void ReadFile(String dir) throws FileNotFoundException, UnsupportedEncodingException {
		Scanner s = new Scanner(new File(dir)).useDelimiter(",| |\n|\r");
		writer = new PrintWriter("output.txt", "UTF-8");
		String in;
		int helper = 0;
		int co = 0;
		while (s.hasNext()) {
			in = ReadNext(s);
			if (in.length() == 1) {
				setAlgo(Integer.parseInt(in));
				in = ReadNext(s);
			}
			if (in.contains("x")) {
				helper = in.indexOf("x");
				setN(Integer.parseInt(in.substring(0, helper)));
				setM(Integer.parseInt(in.substring(helper + 1)));
				initTiles();
				in = ReadNext(s);
			}
			if (in.equals("Red:")) {
				createTiles(s, RED);
				in = ReadNext(s);
			}
			if (in.equals("Yellow:")) {
				createTiles(s, YELLOW);
				in = ReadNext(s);
			}
			createInitial(s, in);
			setGoal();
		}

//		for (int i = 0; i < tiles.size(); i++) {
//			System.out.println(tiles.elementAt(i).toString() + ", ");
//		}
		// printing out the checker initial matrix puzzle
		System.out.println("printing the initial value");
		System.out.println("");
		for (int i = 0; i < checkInit.length; i++) {
			for (int j = 0; j < checkInit[0].length; j++) {
				System.out.print(checkInit[i][j] + " ");
			}
			System.out.println("");
		}
//		for (int i = 0; i < allTiles.length; i++) {
//			System.out.println(allTiles[i]);
//		}
//		System.out.println(getGoal());
		s.close();
		StartCalc();
		writer.close();
	}

	/**
	 * function that will start the chosen algorithm after creating the starting
	 * node.
	 */
	private void StartCalc() {
		this.timer = System.currentTimeMillis();
		this.start = new Node(getN(), getM(), this.initial);
		System.out.println(this.start.toString());

		startAlgo();
	}

	private void startAlgo() {
		Stack<Node> stack = new Stack<Node>();
		switch (this.algo) {
		case 1:
			System.out.println("BFS:");
			Queue<Node> q = new LinkedList<Node>();
			q.add(this.start);
			addToList(open_list, this.start);
			startBFS(q);
			break;
		case 2:
			System.out.println("DFID:");
			startDFID();
			break;
		case 3:
			System.out.println("A-STAR:");
			startA_Star();
			break;
		case 4:
			System.out.println("IDA-STAR:");
//			startIDA_StarR();
//			this.calcalatedStates = 0;
			startIDA_Star(stack);
			break;
		case 5:
			System.out.println("DFBnB:");
			this.start.setF_N(this.Heuristic2(this.start));
			this.open_list.put(this.start.getCode(), this.start);
			stack.add(getStart());
			startDFBnB1(stack);
//			startDFBnB();
			break;
		case 6:
			System.out.println("IDA-STAR_REC:");
			startIDA_StarR();
			break;
		}

	}

	private void startBFS(Queue<Node> q) {
		Node cur;

		while (!q.isEmpty()) {
			cur = q.poll();
			RemoveFromList(open_list, cur);
			for (Node child : cur.NextGen()) {
				if (null != child) {
					if (checkGOAL(child)) {
						// found the end node GOAL
						FinishCalc(child);
						return;
					}
					if (!this.closed_list.containsKey(child.getCode())) {
						if (!this.open_list.containsKey(child.getCode())) {
							addToList(open_list, child);
							q.add(child);
						}
					}
				}
			}
			addToList(closed_list, cur);
		}
	}

	private void startDFID() {
		setCalcalatedStates(0);
		int depth = 1;
		Node found;
		while (depth < MAX_ITERATION) {
			found = DLS(this.start, depth);
			if (null != found) {
				FinishCalc(found);
				return;
			}
			depth++;
		}
	}

	private Node DLS(Node cur, int depth) {
		if ((depth == 0) && (checkGOAL(cur))) {
			return cur;
		}
		if (depth > 0) {
			this.calcalatedStates++;
			Node found;
			for (Node child : cur.NextGen()) {
				if (null != child) {
					if (checkGOAL(child)) {
						return child;
					}
					found = DLS(child, depth - 1);
					if (null != found) {
						return found;
					}
				}
			}
		}
		return null;
	}

	private void startA_Star() {
		NodeComparator comparator = new NodeComparator();
		PriorityQueue<Node> PQ = new PriorityQueue<Node>(comparator);
		Node current = null, prevNode = null;
		// The set of nodes already evaluated
		if (!closed_list.isEmpty()) {
			closed_list.clear();
		}
		// The set of currently discovered nodes that are not evaluated yet.
		// Initially, only the start node is known.
		open_list.clear();
		start.setF_N(start.getPrice() + this.Heuristic2(start));
		PQ.add(getStart());
		addToList(open_list, start);
		while (!this.open_list.isEmpty()) { // while open_list is not empty
			current = null;
			// current = open_list element with lowest f cost
			current = PQ.poll();
			if (checkGOAL(current)) {
				FinishCalc(current);
				return;
			}
			RemoveFromList(open_list, current);// remove current from open_list
			addToList(closed_list, current);// add current to closed_list
			for (Node child : current.NextGen()) {
				if (null == child) {
					continue;
				}
				if (checkGOAL(child)) {
					FinishCalc(child);
					return;
				}
				// the created child is'nt in closed list (we haven't taken care of it already)
				if (!this.closed_list.containsKey(child.getCode())) {
					child.setF_N(child.getPrice() + this.Heuristic2(child));
					// if we haven't added this state to the open list
					if (!this.open_list.containsKey(child.getCode())) { 
						// add child to open list
						addToList(this.open_list, child);
						PQ.add(child);
					} else {
						prevNode = this.open_list.get(child.getCode());
						PQ.remove(prevNode);
						if (child.getPrice() < prevNode.getPrice()) { 
							addToList(this.open_list, child);
							PQ.add(child);
						}else{
							addToList(this.open_list, prevNode);
							PQ.add(prevNode);
						}
					}
				} else { // we have taken care of this state.
					if (this.open_list.containsKey(child.getCode())) {
						this.open_list.remove(child.getCode());
					}
				}
			}
//			if (this.calcalatedStates % 2000 == 0) {
//				// System.out.println("1247212 state is: "+cur.getCode());
//				System.out.println("amount of states calculated: " + this.calcalatedStates);
//			}
		}
		NoPath();
		return;
	}

	private void startIDA_Star(Stack<Node> stack) {
		Node current = null;
		double min = this.Heuristic2(start);
		double threshold = 0;
		while (true) {
			stack.push(start);
			addToList(open_list, start);
			threshold = min;
			min = Double.MAX_VALUE;
			while(!stack.isEmpty()){
				current = stack.peek();
				if(checkGOAL(current)){
					FinishCalc(current);
					return;
				}
				for(Node child : revArray(current.NextGen())){
					if(null != child){
						if(checkGOAL(child)){
							FinishCalc(child);
							return;
						}
						child.setF_N(child.getPrice() + this.Heuristic2(child));
						if(stack.contains(child)){
							continue;
						}
						if(child.getF_N() <= threshold){
				 			stack.push(child);
							addToList(open_list, child);
						} else {
							if(child.getF_N() < min){
								min = child.getF_N();
							}
						}
					}
				}
				stack.remove(current);
				RemoveFromList(open_list, current);
			}
			if(min == Double.MAX_VALUE){
				NoPath();
				return;
			}
//				if (this.calcalatedStates % 1000 == 0) {
//				// System.out.println("1247212 state is: "+cur.getCode());
//				System.out.println("amount of states calculated: " + this.calcalatedStates);
//				System.out.println("the thershold is: " + threshold);
//			}
		}
	}
	
	private Node[] revArray(Node[] arr) {
		int i = arr.length;
		Node[] result = new Node[i];
		i--;
		for (Node node : arr) {
			if(null != node){
				result[i] = node;
			}
			i--;
		}
		return result;
	}

	private void startIDA_StarR(){
		Node Start = this.start;
		double temp = 0;
		double threshold = this.Heuristic(Start);
		while (true) { // run for infinity
			temp = search1(Start, 0, threshold); // function search(node,g
			// score,threshold)
			if (temp == FOUND) { // if goal found
				FinishCalc();
				return;
			}
			if (temp == Double.MAX_VALUE) { // Threshold larger than maximum
				NoPath();							// possible f value
				return; // or set Time limit exceeded
			}
			threshold = temp;
		}
	}
	private double search1(Node node, double g, double threshold) {
		this.calcalatedStates++;
		double min = 0;
		double temp = 0;
		double f = g + this.Heuristic2(node);
		if (f > threshold) { // greater f encountered
			return f;
		}
		if (checkGOAL(node)) { // Goal node found
			this.mPairFinish.setPath(node.getPath()); 
			this.mPairFinish.setPrice(node.getPrice());
			return FOUND;
		}
		min = Double.MAX_VALUE; // min= Minimum integer
		for (Node child : node.NextGen()) {
			if (null == child) {
				continue;
			}
			// recursive call with next node as current node for depth search
			temp = search1(child, child.getPrice(), threshold);
			if (temp == FOUND) { // if goal found
				this.mPairFinish.setPath(node.getPath()); 
				this.mPairFinish.setPrice(node.getPrice());
				return FOUND;
			}
			if (temp < min) { // find the minimum of all ‘f’ greater than threshold encountered
				min = temp;
			}
		}
		return min; // return the minimum ‘f’ encountered greater than threshold
	}

	private void startDFBnB() {
		NodeComparator comparator = new NodeComparator();
		PriorityQueue<Node> PQ = new PriorityQueue<Node>(comparator);
		PQ.add(getStart());
		double bestVal = this.start.getF_N() * 15;
		Node currentBest = null, node = null;
		while(!PQ.isEmpty()){
			node = PQ.poll();
			RemoveFromList(this.open_list, node);
			if(node.getF_N() < bestVal){
			for (Node child : node.NextGen()) {
				if(null != child){
					child.setF_N(child.getPrice() + this.Heuristic2(child));
					if(PQ.contains(child)){
						Node ne = this.open_list.get(child.getCode());
						if(null != ne){
						PQ.remove(ne);
						if(ne.getF_N() <= child.getF_N()){
							PQ.add(ne);
							addToList(open_list, ne);
						} else {
							PQ.add(child);
							addToList(open_list, child);
						}
						}
					}
					if(child.getF_N() > bestVal){
						continue;
					} else {
						if(checkGOAL(child)){
							if(null == currentBest){
								bestVal = child.getF_N();
								currentBest = child;
							} else {
								if(currentBest.getPrice() > child.getPrice()){
									bestVal = child.getF_N();
									currentBest = child;
								}
							}
						} else { 
							PQ.add(child);
							addToList(this.open_list, child);
						}
					}
				}
			}
			} else {
				if(currentBest != null){
					FinishCalc(currentBest);
					return;
				}				
			}
//			if((this.calcalatedStates % 2000) == 0){
//				System.out.println("amount of calculated states: " + this.calcalatedStates);
//			}
		}
		if(null != currentBest){
			FinishCalc(currentBest);
		} else {
			NoPath();
		}
		return;
	}
	
	private void startDFBnB1(Stack<Node> stack) {
		NodeComparator comparator = new NodeComparator();
		PriorityQueue<Node> PQ = new PriorityQueue<Node>(comparator);
		double bestVal = this.start.getF_N() * this.getM() * this.getN(); // the upper bound which we set in a way which is dependent on the size of the board and the heuristic function
		System.out.println(bestVal);
		Node currentBest = null, node = null;
		while(!stack.isEmpty()){
			node = stack.pop();
			RemoveFromList(this.open_list, node);
			if(node.getF_N() < bestVal){
			for (Node child : node.NextGen()) {
				if(null != child){
					child.setF_N(child.getPrice() + this.Heuristic2(child));
					if(stack.contains(child)){
						Node ne = this.open_list.get(child.getCode());
						if(null != ne){
						if(ne.getF_N() <= child.getF_N()){
							addToList(open_list, ne);
						} else {
							stack.remove(ne);
							PQ.add(child);
							addToList(open_list, child);
						}
						}
					}
					if(child.getF_N() > bestVal){
						continue;
					} else {
						if(checkGOAL(child)){
							if(null == currentBest){
								bestVal = child.getF_N();
								currentBest = child;
							} else {
								if(currentBest.getPrice() > child.getPrice()){
									bestVal = child.getF_N();
									currentBest = child;
								}
							}
						} else { 
							PQ.add(child);
							addToList(this.open_list, child);
						}
					}
				}
			}
			while(!PQ.isEmpty()){
				stack.add(PQ.poll());
			}
			}
//			else {
//				if(currentBest != null){
//					FinishCalc(currentBest);
//					return;
//				}				
//			}
//			if((this.calcalatedStates % 2000) == 0){
//				System.out.println("amount of calculated states: " + this.calcalatedStates);
//			}
		}
		if(null != currentBest){
			FinishCalc(currentBest);
		} else {
			NoPath();
		}
		return;
	}

	private void initTiles() {
		this.allTiles = new Tile[this.n * this.m];
		for (int i = 0; i < allTiles.length; i++) {
			allTiles[i] = new Tile(i, WHITE);
		}
	}

	private void updateTiles(int num, int position) {
		this.initial[position] = allTiles[num];
	}

	private void createInitial(Scanner s, String in) {
		int num = 0, place = 0;
		this.checkInit = new String[getN()][getM()];
		this.initial = new Tile[getN() * getM()];
		for (int i = 0; i < checkInit.length; i++) {
			for (int j = 0; j < checkInit[0].length; j++) {
				if (!in.equals("_")) {
					num = Integer.parseInt(in);
				} else {
					num = 0;
				}
				updateTiles(num, place++);

				this.checkInit[i][j] = in;

				if (s.hasNext()) {
					in = ReadNext(s);
				}
			}
		}
	}

	private void createTiles(Scanner s, double color) {
		String in;
		int num;
		while (s.hasNext()) {
			in = s.next();
			if (in.length() < 1) {
				return;
			}
			num = Integer.parseInt(in);
			this.allTiles[num] = new Tile(num, color);
			this.tiles.addElement(new Tile(Integer.parseInt(in), color));
		}

	}

	private static String ReadNext(Scanner s) {
		String in = s.next();
		while (in.length() < 1) {
			if (s.hasNext()) {
				in = s.next();
			} else {
				in = "done";
			}
		}
		return in;
	}

	public String getGoal() {
		return goal;
	}

	private void addToList(HashMap<String, Node> list, Node node) {
		list.put(node.getCode(), node);
	}

	private void RemoveFromList(HashMap<String, Node> list, Node node) {
		Node check = null;
		check = list.remove(node.getCode());
		if (null != check) {
			calcalatedStates++;
		}
	}

	public void setCalcalatedStates(int calcalatedStates) {
		this.calcalatedStates = calcalatedStates;
	}

	private int Heuristic2(Node cur) {
		int count = 0, value = 0;
		double cost = 0;
		Tile[] state = cur.getTiles();
		for (int i = 0; i < state.length; i++) {
			value = state[i].getNum();
			cost = state[i].getColor();
			for (int j = i + 1; j < state.length; j++) {
				if (state[j].getNum() != 0) {
					if (value > state[j].getNum()) {
						count++;
					}
				}
			}
		}
		return count;
	}

	private int Heuristic(Node cur) {
		int count = 0, countI = 0, diff = 0;
		String code = cur.getCode();
		char spot;
		for (int i = 0; i < code.length(); i++) {
			spot = code.charAt(i);
			countI = 0;
			if (spot == 32) {
				return 0;
			}
			diff = Math.abs((spot - 64 - 1) - i);
			while (diff > 0) {
				if (diff > this.getM()) {
					countI++;
					diff -= this.getM();
				} else {
					countI++;
					diff -= 1;
				}
			}
			count += (countI * (i + 1));
		}
		return count;
	}

	private boolean checkGOAL(Node cur) {
		return cur.getCode().equals(this.getGoal());
	}

	private void setGoal() {
		String g = "";
		for (int i = 1; i < allTiles.length; i++) {
			g = g + allTiles[i].getID();
		}
		this.goal = g + " ";
	}

	private void FinishCalc(Node end) {
		this.timer = System.currentTimeMillis() - this.timer;
		double seconds = this.timer / 1000.0;
		writer.println(end.getPath());
		System.out.println(end.getPath());
		writer.println("Num: " + calcalatedStates);
		System.out.println("Num: " + calcalatedStates);
		writer.println("Cost: " + end.getPrice());
		System.out.println("Cost: " + end.getPrice());
		writer.println(seconds + " seconds.");
		System.out.println(seconds + " seconds.");
	}

	private void FinishCalc() {
		this.timer = System.currentTimeMillis() - this.timer;
		double seconds = this.timer / 1000.0;
		writer.println(this.mPairFinish.getPath());
		System.out.println(this.mPairFinish.getPath());
		writer.println("Num: " + calcalatedStates);
		System.out.println("Num: " + calcalatedStates);
		writer.println("Cost: " + this.mPairFinish.getPrice());
		System.out.println("Cost: " + this.mPairFinish.getPrice());
		writer.println(seconds + " seconds.");
		System.out.println(seconds + " seconds.");
	}

	private void NoPath() {
		this.timer = System.currentTimeMillis() - this.timer;
		writer.println("no path");
	}
}
