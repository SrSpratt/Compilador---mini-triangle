import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class TokenScanner{
	private int line;
	private int column;
	private FileInputStream fis;
	private FileOutputStream output;
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
		TRUE, //16
		FALSE, //17
		DO, //18
		OF, //19
		ELSE, //20
		END, //21
		IF, //22
		OR, //23
		AND, //24
		INTEGER, //25
		REAL, //26
		ARRAY, //27
		BOOLEAN, //28
		THEN, //29
		VAR, //30
		WHILE, //31
		SEMICOLON, //32
		COLON, //33
		BECOMES, //34
		COMMA,  //35
		DOT, //36
		DDOT, //37
		LPAREN, //38
		RPAREN, //39
		EOL, //40
		EOT, //41
		OTHER; //42
	}

	public TokenScanner(FileInputStream fis, String str) throws IOException{
		line = 1;
		column = 0;
		this.fis = fis;
		this.output = new FileOutputStream(str, true);
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
			while ((currentChar == '!') || (currentChar == '\t') || (currentChar == '\n') || (currentChar == ' ')){
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
					//??TI R??S TCHU PASSARDIRETO
					take(currentChar);
					c = true;
				} else if (currentChar == '\n'){
					//CEIME RIRI
					take(currentChar);
					column = 0;
					line++;
					c = true;
				}
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
					b = tokensenum.COLON.ordinal();
					break;
				case ';':
					takeIt();
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
				/*case '\n':
					takeIt();
					column = 1;
					line++;
					b = tokensenum.EOL.ordinal();
					break;*/
				case (char) -1:
					b = tokensenum.EOT.ordinal();
					break;
				default:
					currentChar = (char) fis.read();
					b = -1;
					System.out.println("OUTRO\n linha: "+this.whichLine()+"\n coluna: "+this.whichColumn());
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
				if (b != -1)
					//if (b == 0)
					riposte = new Tuple(this.actualline.toString().toLowerCase(),b);

		//	} while (fis.available() > 0);//est?? dentro do CATCH, CARAMBA
		
		} catch (Exception e){
			System.err.printf("Erro na abertura do arquivo: %s. \n", e.getMessage());
		}	

		try{
		if (riposte.getSecond() == 0)
			for (tokensenum x : tokensenum.values()){
				if (riposte.getFirst().equals(x.name().toLowerCase())){
					String str = x.name().toLowerCase();
					int i = x.ordinal();	
					riposte = new Tuple(str, i);
				}
			}
		}catch(Exception e){
                        System.err.printf("Erro na abertura do arquivo: %s. \n", e.getMessage());

		}

		try {	
			output.write(Integer.toString(riposte.getSecond()).getBytes());
			output.write('#');
		} catch (IOException e) {
			e.printStackTrace();
		}



		return riposte;
	}
}
