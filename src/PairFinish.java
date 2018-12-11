/**
 * object for returning the finishing information for recursive functions
 * @author Noam
 *
 */
public class PairFinish {
	private final String INITs = "starting";
	private final double INITd = -1;
	private String path;
	private double price;
	
	public PairFinish(){
		this.path = INITs;
		this.price = INITd;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		if(getPath().equals(INITs)){
			this.path = path;			
		}
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		if(getPrice() == INITd){
			this.price = price;
		}
	}
	

}
