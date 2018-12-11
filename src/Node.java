import java.util.Comparator;

public class Node {
	private final int RIGHT = 0, DOWN = 1, LEFT = 2, UP =3;
	private int N, M;
	
	private Tile[] tiles; // an Array of the tiles set in the correct order.
	
	private String code;
	private int mHashCode;
	private double f_n;
	private double price; // the price of the path from the initial state up to this state. g(n)
	private String path; // a string of the path from starting position 
	private int reached; // how it reached so we don't create the parent as a child again. if we reached from R dont create L if D not U...
	private int blankSpot; // the spot (of array) in which the blank tile is located.
	
	/**
	 * constructer of node
	 * @param n - number of rows
	 * @param m - number of columns
	 * @param initial - the state of the board
	 */
	public Node(int n, int m, Tile[] initial){
		this.reached = -1;
		this.N = n;
		this.M = m;
		this.path = "";
		this.code = "";
		this.price = 0;
		this.tiles = new Tile[this.N * this.M];
		setTiles(initial);
		setCode();
	}
	
	public Node(Node a){
		this.price = a.price;
	}
	
	/**
	 * when creating child node he inherits a lot of attributs 
	 * @param path of father node to which we will add the step
	 * @param price from start to father we add weight(father, this)
	 * @param reached how we reached this child
	 */
	public void Inherit(String path, double price, int reached){
		this.setReached(reached);
		this.setPath(path);
		this.setPrice(price);
	}
	
	/**
	 * setter for this state
	 * @param initial
	 */
	private void setTiles(Tile[] initial){
		for (int i = 0; i < initial.length; i++) {
			this.tiles[i] = initial[i];
		}
	}
	
	/**
	 * calls to return the child node array
	 * @return children array may contain null
	 */
	public Node[] NextGen(){
		return createChild();
	}
	
	/**
	 * creates the children that are legal moves
	 * @return node array of child nodes
	 */
	private Node[] createChild(){
		Node[] child = new Node[4];
		double stepCost = 0;
		for (int i = 0; i < child.length; i++) {
			switch(i){
			case RIGHT:
				if(this.reached == 2){ break; }
				if((this.blankSpot % this.M) == (this.M - 1)){
					// can't move Right.
					break;
				}
				stepCost = swapTile(RIGHT); // doing the move in order to create the child node.
				child[i] = new Node(this.N, this.M, tiles);
				child[i].Inherit(addStep("R"), this.price + stepCost , i);
				swapTile(RIGHT); // returning the order of the array back to its correct state for this node.
				break;
				
			case DOWN:
				if(this.reached == 3){ break; }
				if(this.blankSpot >= (this.N * this.M) - this.M){
					// can't move Down.
					break;
				}
				stepCost = swapTile(DOWN); // doing the move in order to create the child node.
				child[i] = new Node(this.N, this.M, tiles);
				child[i].Inherit(addStep("D"), this.price + stepCost , i);
				swapTile(DOWN); // returning the order of the array back to its correct state for this node.
				break;
				
			case LEFT:
				if(this.reached == 0){ break; }
				if((this.blankSpot % this.M) == (this.M - this.M)){
					// can't move Left.
					break;
				}
				stepCost = swapTile(LEFT); // doing the move in order to create the child node.
				child[i] = new Node(this.N, this.M, tiles);
				child[i].Inherit(addStep("L"), this.price + stepCost , i);
				swapTile(LEFT); // returning the order of the array back to its correct state for this node.
				break;
				
			case UP:
				if(this.reached == 1){ break; }
				if(this.blankSpot < this.M){
					// can't move Up.
					break;
				}
				stepCost = swapTile(UP); // doing the move in order to create the child node.
				child[i] = new Node(this.N, this.M, tiles);
				child[i].Inherit(addStep("U"), this.price + stepCost , i);
				swapTile(UP); // returning the order of the array back to its correct state for this node.
				break;
			}
		}
		return child;
	}

	/**
	 * swaps to tiles spots in order to create the child node state
	 * @param type - the specified movement type
	 * @return the price of the movement
	 */
	private double swapTile(int type){
		double result = 0;
		Tile t;
		switch(type){
		case RIGHT:
			t = this.tiles[this.blankSpot];
			result = this.tiles[this.blankSpot + 1].getColor();
			this.tiles[this.blankSpot] = this.tiles[this.blankSpot + 1];
			this.tiles[this.blankSpot + 1] = t;
			break;
		case LEFT:
			t = this.tiles[this.blankSpot];
			result = this.tiles[this.blankSpot - 1].getColor();
			this.tiles[this.blankSpot] = this.tiles[this.blankSpot - 1];
			this.tiles[this.blankSpot - 1] = t;
			break;
		case UP:
			t = this.tiles[this.blankSpot];
			result = this.tiles[this.blankSpot - this.M].getColor();
			this.tiles[this.blankSpot] = this.tiles[this.blankSpot - this.M];
			this.tiles[this.blankSpot - this.M] = t;
			break;
		case DOWN:
			t = this.tiles[this.blankSpot];
			result = this.tiles[this.blankSpot + this.M].getColor();
			this.tiles[this.blankSpot] = this.tiles[this.blankSpot + this.M];
			this.tiles[this.blankSpot + this.M] = t;
			break;
		}
		return result;
	}
	
	/**
	 * adds to the path string the addition of the move made
	 * @param step - String of move made
	 * @return new string of path
	 */
	public String addStep(String step){
		if(this.reached < 0){
			return step;
		}
		return getPath() + "-" + step;
	}

	public String getCode(){
		return this.code;
	}
	
	public Tile[] getTiles(){return this.tiles;}
	public int getBlankSpot(){return this.blankSpot;}
	public String getPath(){return this.path;}
	public int getReached(){return this.reached;}
	public double getPrice() {return price;}
	public double getF_N() {return f_n;}
	public void setF_N(double f_n) {this.f_n = f_n;}
	public void setPrice(double price) {this.price = price;}
	public void setPath(String path) {this.path = path;}
	public void setReached(int reached){this.reached = reached;}
	public void setBlankSpot(int blank){this.blankSpot = blank;}
	
	/**
	 * set the code of this state
	 * and setting this nodes hashcode
	 */
	private void setCode(){
		if(this.tiles[0] == null){return;}
		String coder = "";
		int hash = 3;
		for (int i = 0; i < this.tiles.length; i++) {
			coder = coder + this.tiles[i].getID();
			hash = (hash * 7) + (this.tiles[i].getNum() * i);
			if(this.tiles[i].getID() == ' '){
				setBlankSpot(i);
			}
		}
		this.code = coder;
		this.mHashCode = hash;
	}
	
	@Override
	public boolean equals(Object obj) {
		if ((obj == null) ||(!Node.class.isAssignableFrom(obj.getClass()))){
	        return false;
	    }
	    final Node other = (Node) obj;
	    if ((this.getCode() == null) ? (other.getCode() != null) : !this.getCode().equals(other.getCode())) {
	        return false;
	    }
	    return true;
	}

	@Override
	public int hashCode() {
	    return this.mHashCode;
	}
	
	public String toString(){
		return "my CurState is: " + getCode() +".";
	}

}

class NodeComparator implements Comparator<Node>
{
    @Override
    public int compare(Node x, Node y)
    {
    	// Assume neither node is null.
        if (x.getF_N() > y.getF_N())
        {
            return 1;
        }
        if (x.getF_N() < y.getF_N())
        {
            return -1;
        }
        return 0;
    }
}
