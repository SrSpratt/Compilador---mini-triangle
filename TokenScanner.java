import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class TokenScanner{
	private int line;
	private int column;
	private FileInputStream fis;
	private StringBuffer commentline;
	private StringBuffer actualline;
	private char currentChar;

	public enum tokensenum{
		IDENTIFIER, //0
		INTLITERAL, //1
		FLOATLITERAL, //2
		OPAD, //3
		OPMUL, //4
		GREATERTHAN, //5
		LESSTHAN, //6
		GREATEREQUAL, //7
		LESSEQUAL, //8
		DIFFERENT, //9
		EQUAL, //10
		BRACKETL, //11
		BRACKETR, //12
		BEGIN, //13
		PROGRAM, //14
		CONST, //15
		TRUE, //16
		FALSE, //17
		ERROR, //18
		DO, //19
		OF, //20
		ELSE, //21
		END, //22
		IF, //23
		IN, //24
		LET, //25
		THEN, //26
		VAR, //27
		WHILE, //28
		SEMICOLON, //29
		COLON, //30
		BECOMES, //31
		COMMA,  //32
		DOT, //33
		DDOT, //34
		ARRAY, //35
		LPAREN, //36
		RPAREN, //37
		EOL, //38
		EOT, //39
		OTHER; //40
	}

	public TokenScanner(FileInputStream fis) throws IOException{
		line = 1;
		column = 1;
		this.fis = fis;
		this.commentline = new StringBuffer();
		this.actualline = new StringBuffer();
		this.currentChar = (char) fis.read();

	}

	public int whichLine (){
		return line;
	}

	public int whichColumn (){
		return column;
	}

	public String currentSpelling(){
		return actualline.toString();
	}

	public boolean isLetter(char c){
		return Character.isLetter(c);
	}

	public boolean isDigit(char c){
		return Character.isDigit(c);
	}

	public void takeIt() throws IOException{
		actualline.append(currentChar);
		currentChar = (char) fis.read();
	}

	public void take(char c) throws IOException{
		currentChar = (char) fis.read();
	}

	public boolean separator(){
		boolean c = false;
		commentline = new StringBuffer();
		try{
			if (currentChar == '!' ){
				while (currentChar != '\n' && currentChar != (char) -1){
					take(currentChar);
					commentline.append(currentChar);
				}
				c = true;
			} else if (currentChar == '\t'){
				//has to PASSARDIRETO
				take(currentChar);
				c = true;
			} else if (currentChar == ' '){
				//ÊTI RÉS TCHU PASSARDIRETO
				take(currentChar);
				c = true;
			} else if (currentChar == '\n'){
				//CEIME RIRI
				take(currentChar);
				c = true;
			}	
		}catch (IOException e){
			System.err.printf("Erro na leitura do caractere: %s. \n", e.getMessage());
		}
		return c;
	}

	public int scanToken (){
		int b = 0;
		currentChar = Character.toLowerCase(currentChar);
		actualline = new StringBuffer();
		try{
			if (isLetter(currentChar)){
				takeIt();
				while (Character.isLetterOrDigit(currentChar))
					takeIt();
				b = tokensenum.IDENTIFIER.ordinal();
				return b;
			}
			if (isDigit(currentChar)){
				while (isDigit(currentChar))
					takeIt();
				b = tokensenum.INTLITERAL.ordinal();
				return b;
			}
			switch (currentChar){
				case '+':
					System.out.println("debug");
				case '-':
					takeIt();
					b = tokensenum.OPAD.ordinal();
					break;
				case '*':
				case '/':
					takeIt();
					b = tokensenum.OPMUL.ordinal();
					break;
				case '>':
					takeIt();
					if (currentChar == '='){
						takeIt();
						b = tokensenum.GREATEREQUAL.ordinal();
						return b;
					}
					b = tokensenum.GREATERTHAN.ordinal();
					break;
				case '<':
					takeIt();
					if (currentChar == '='){
						takeIt();
						b = tokensenum.LESSEQUAL.ordinal();
						return b;
					}
					if (currentChar == '>'){
						takeIt();
						b = tokensenum.DIFFERENT.ordinal();
						return b;
					}
					b = tokensenum.LESSTHAN.ordinal();
					break;
				case '=':
					takeIt();
					b = tokensenum.EQUAL.ordinal();
					break;
				case '[':
					takeIt();
					b = tokensenum.BRACKETL.ordinal();
					break;
				case ']':
					takeIt();
					b = tokensenum.BRACKETR.ordinal();
					break;
				case ':':
					takeIt();
					if (currentChar == '='){
						takeIt();
						b = tokensenum.BECOMES.ordinal();
						return b;	
					}
					b = tokensenum.SEMICOLON.ordinal();
					break;
				case ',':
					takeIt();
					b = tokensenum.COMMA.ordinal();
					break;
				case '.':
					takeIt();
					if (currentChar == '.'){
						takeIt();
						b = tokensenum.DDOT.ordinal();
						return b;
					}
					b = tokensenum.DOT.ordinal();
					break;
				case '(':
					takeIt();
					b = tokensenum.LPAREN.ordinal();
					break;
				case ')':
					takeIt();
					b = tokensenum.RPAREN.ordinal();
					break;
				case '\n':
					takeIt();
					b = tokensenum.EOL.ordinal();
					break;
				case (char) -1:
					b = tokensenum.EOT.ordinal();
					break;
				default:
					currentChar = (char) fis.read();
					b = -1;
					System.out.println("OUTRO");
					//b = tokensenum.OTHER.ordinal();
			}
		}catch (IOException e){
			System.err.printf("Erro na leitura do caractere: %s. \n", e.getMessage());
		}	
		return b;
	}

	public static void main(String []args) {

		File file = new File(args[0]);
		StringBuffer actualstring = new StringBuffer();
		ArrayList<Integer> tokens = new ArrayList<Integer>();
		StringBuilder concat = new StringBuilder();
		StringBuffer commentline = new StringBuffer();

		char currentc, aux;

		actualstring.append("Strings:\n");
		commentline.append("Comments:\n");
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
		TokenScanner tokenScanner = new TokenScanner(fis);
		
		do {
			tokenScanner.separator(); 
			int b = tokenScanner.scanToken();

			if (b != -1){
				concat.append(tokenScanner.actualline.toString()); // concatena as iniciais
				concat.append("\n");
				tokens.add(b);
			}

			commentline.append(tokenScanner.commentline.toString());

		} while (fis.available() > 0);//está dentro do CATCH, CARAMBA

		FileOutputStream output = new FileOutputStream(args[1]);
	
		output.write('\n');
	
		output.write(concat.toString().getBytes());

		output.write('\n');

		for (int count = 0; count < tokens.size(); count ++){

			output.write(("#").getBytes()); 
			output.write(tokens.get(count).toString().getBytes()); //ÉVERI ISTRINGUE RÉS TCHU BI ESCRITA WITHE getBytes()

		}

		} catch (IOException e){
			System.err.printf("Erro na abertura do arquivo: %s. \n", e.getMessage());
		}
						

			}
}
