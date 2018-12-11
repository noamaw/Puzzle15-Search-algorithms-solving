import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

public class Ex1 {

	public static void main(String[] args) {
		States tree = new States();
		try {
			tree.ReadFile("input.txt");
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
