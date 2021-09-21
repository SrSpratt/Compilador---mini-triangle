import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Compiler{
	private Parser parser;

	/*public Compiler(File file){
		parser = new Parser(file);
	}*/
	
	public static void main(String []args) {

		File file = new File(args[0]);
		
		if (!(file.exists())) {
			System.out.println(args[0] + " does not exist.");
			return;
			}
		if (!(file.isFile() && file.canRead())){
			System.out.println(file.getName() + " cannot be read from.");
			return;
			}	

			try {
				FileInputStream fis = new FileInputStream(file);
				Parser parser = new Parser(fis, args[1]);
				parser.parse();

			} catch (IOException e){
				System.err.printf("Erro na abertura do arquivo: %s. \n", e.getMessage());
			}
	}
}
