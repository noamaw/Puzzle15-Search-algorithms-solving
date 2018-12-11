/**
 * Tile representation for each tile in the current states
 * @author Noam
 *
 */
public class Tile {
	private static int number = 0;
	private int num;
	private double color;
	private char id;
	
	public Tile(int num, double color){
		Tile.setNumber(Tile.getNumber() + 1);
		this.num = num;
		this.color = color;
		setID(num);
	}
	
	public int getNum(){return this.num;}
	public double getColor(){return this.color;}
	public char getID(){return this.id;}
	
	private void setID(int num){
		if(num == 0){
			this.id = 32;
		} else {
			this.id = (char)(num + 64);
		}
	}
	public String toString(){
		if(getNum() == 0){
			return "Hi, i am tile: "+getID() + ". tile's value is: "+"_"+
					", and its color is: "+getColor();
		}
		return "Hi, i am tile: "+getID() + ". tile's value is: "+getNum()+
				", and its color is: "+getColor();
	}

	public static int getNumber() {
		return number;
	}

	public static void setNumber(int number) {
		Tile.number = number;
	}
}
