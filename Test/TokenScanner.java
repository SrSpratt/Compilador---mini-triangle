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
		ERROR, //13
		PROGRAM, //14
		BEGIN, //15	
		TRUE, //18
		FALSE, //19
		DO, //20
		OF, //21
		ELSE, //22
		END, //23
		IF, //24
		OR, //25
		AND, //26
		INTEGER, //26
		REAL, //27
		ARRAY, //28
		BOOLEAN, //29
		THEN, //32
		VAR, //33
		WHILE, //34
		SEMICOLON, //35
		COLON, //36
		BECOMES, //37
		COMMA,  //38
		DOT, //39
		DDOT, //40
		LPAREN, //41
		RPAREN, //42
		EOL, //43
		EOT, //44
		OTHER; //45
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
		//System.out.println(currentChar);
		//System.out.println(column);
		//System.out.println(line);
		actualline.append(currentChar);
		currentChar = (char) fis.read();
		column++;
	}

	public void take(char c) throws IOException{
		//System.out.println(currentChar);
		currentChar = (char) fis.read();
		column++;
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
				column++; //observar a GUI
				c = true;
			} else if (currentChar == ' '){
				//ÊTI RÉS TCHU PASSARDIRETO
				take(currentChar);
				c = true;
			} else if (currentChar == '\n'){
				//CEIME RIRI
				take(currentChar);
				column = 1;
				line++;
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
					column = 1;
					line++;
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

	public Tuple scan(){

		Tuple riposte = new Tuple("0",0);

		try{
			//do {
				this.separator(); 
				int b = this.scanToken();
				if (b != -1){
					//if (b == 0)
					riposte = new Tuple(this.actualline.toString(),b);
				}

		//	} while (fis.available() > 0);//está dentro do CATCH, CARAMBA
		
		} catch (Exception e){
			System.err.printf("Erro na abertura do arquivo: %s. \n", e.getMessage());
		}	

		try{
		if (riposte.getSecond() == 0)
			if (riposte.getFirst().equals("if")){
				String str = tokensenum.IF.name().toLowerCase();
				int i = tokensenum.IF.ordinal();	
				riposte = new Tuple(str, i);
		}
		}catch(Exception e){
                        System.err.printf("Erro na abertura do arquivo: %s. \n", e.getMessage());

		}	
		return riposte;
	}


	
	public static int init (File file){
		
			Tuple currentToken = new Tuple("0",0);
			char currentc, aux;

			int auxI = -1;

			try {
				FileInputStream fis = new FileInputStream(file);
				TokenScanner tokenScanner = new TokenScanner(fis);
				currentToken = tokenScanner.scan();

			} catch (IOException e){
				System.err.printf("Erro na abertura do arquivo: %s. \n", e.getMessage());
			}
			System.out.println(currentToken.getFirst());
			System.out.println(currentToken.getSecond());
			return auxI;
		}


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
			
		init(file);

							

			}
}
