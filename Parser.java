import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileOutputStream;

public class Parser {

    private TokenScanner tokenScanner;
    private Tuple currentToken;
    

    public Parser(FileInputStream fis, String str) throws IOException{
	    this.tokenScanner = new TokenScanner(fis, str);
	    currentToken = tokenScanner.scan();
    }
		 
    private void acceptIt(){
	currentToken = tokenScanner.scan();
    }

    private void accept(int expectedKind){
	String str = "na";
	for (TokenScanner.tokensenum x: TokenScanner.tokensenum.values())
		if (x.ordinal() == expectedKind)
			str = x.name().toLowerCase();
        if(currentToken.getSecond() == expectedKind)
		currentToken = tokenScanner.scan();
	else
		System.out.println("Na linha: "+tokenScanner.whichLine()+" e coluna: "+tokenScanner.whichColumn()+" era esperado: "+str+", mas foi dado: "+currentToken.getFirst());
    }

    private void parseAtribuicao(){
        parseVariavel();
        accept(TokenScanner.tokensenum.BECOMES.ordinal());
        parseExpressao();
    }

    private void parseBoolLit(){
        if((currentToken.getSecond() == TokenScanner.tokensenum.TRUE.ordinal()) || (currentToken.getSecond() == TokenScanner.tokensenum.FALSE.ordinal()))
		acceptIt();
	else
		System.out.println("Erro sintático boollit\nlinha: "+tokenScanner.whichLine()+"\ncoluna: "+tokenScanner.whichColumn());
    }

    private void parseComando(){
	if(currentToken.getSecond() == TokenScanner.tokensenum.IDENTIFIER.ordinal())
		parseAtribuicao();
	else if(currentToken.getSecond() == TokenScanner.tokensenum.IF.ordinal())
		parseCondicional();
	else if(currentToken.getSecond() == TokenScanner.tokensenum.WHILE.ordinal())
		parseIterativo();
	else if(currentToken.getSecond() == TokenScanner.tokensenum.BEGIN.ordinal())
		parseComandoComposto();
	else
		System.out.println("Erro sintático comando\nlinha: "+tokenScanner.whichLine()+"\ncoluna: "+tokenScanner.whichColumn());
    }

    private void parseComandoComposto(){
	accept(TokenScanner.tokensenum.BEGIN.ordinal());
	parseListaDeComandos();
	accept(TokenScanner.tokensenum.END.ordinal());
    }

    private void parseCondicional(){
	accept(TokenScanner.tokensenum.IF.ordinal());
	parseExpressao();
	accept(TokenScanner.tokensenum.THEN.ordinal());
	parseComando();
	if (currentToken.getSecond() == TokenScanner.tokensenum.ELSE.ordinal()){
		accept(TokenScanner.tokensenum.ELSE.ordinal());
		parseComando();
	}
    }

    private void parseCorpo(){
	parseDeclaracoes();
	parseComandoComposto();
    }

    private void parseDeclaracaoDeVariavel(){
	accept(TokenScanner.tokensenum.VAR.ordinal());
	parseListaDeIds();
	accept(TokenScanner.tokensenum.COLON.ordinal());
	parseTipo();
    }

    private void parseDeclaracoes(){
	while (currentToken.getSecond() == TokenScanner.tokensenum.VAR.ordinal()){
		parseDeclaracaoDeVariavel();
		accept(TokenScanner.tokensenum.SEMICOLON.ordinal());
    	}
    }

    private void parseExpressao(){
	parseExpressaoSimples();
	if ((currentToken.getSecond() == TokenScanner.tokensenum.GREATERTHAN.ordinal()) || (currentToken.getSecond() == TokenScanner.tokensenum.LESSTHAN.ordinal()) || (currentToken.getSecond() == TokenScanner.tokensenum. GREATEREQUAL.ordinal()) || (currentToken.getSecond() == TokenScanner.tokensenum.LESSEQUAL.ordinal()) || (currentToken.getSecond() == TokenScanner.tokensenum.DIFFERENT.ordinal()) || (currentToken.getSecond() == TokenScanner.tokensenum.EQUAL.ordinal())){
		acceptIt();
		parseExpressaoSimples();
	}// else
	//	System.out.println("Erro sintático expressao\nlinha: "+tokenScanner.whichLine()+"\ncoluna: "+tokenScanner.whichColumn());
    }

    private void parseExpressaoSimples(){
	parseTermo();
	while(currentToken.getSecond() == TokenScanner.tokensenum.OPAD.ordinal()){
		acceptIt();
		parseTermo();
	}
    }

    private void parseFator(){
	if(currentToken.getSecond() == TokenScanner.tokensenum.IDENTIFIER.ordinal())
			parseVariavel();
	else if(currentToken.getSecond() == TokenScanner.tokensenum.INTLITERAL.ordinal()){
			if (currentToken.getSecond() == TokenScanner.tokensenum.DOT.ordinal())
				parseFloatLit();
			parseLiteral();
	} else if ((currentToken.getSecond() == TokenScanner.tokensenum.TRUE.ordinal()) || (currentToken.getSecond() == TokenScanner.tokensenum.FALSE.ordinal()))
			parseLiteral();
	else if (currentToken.getSecond() == TokenScanner.tokensenum.BRACKETL.ordinal()){
			acceptIt();
			parseExpressao();
			accept(TokenScanner.tokensenum.RPAREN.ordinal());
	} else if (currentToken.getSecond() == TokenScanner.tokensenum.DOT.ordinal()){
			acceptIt();
			if (currentToken.getSecond() == TokenScanner.tokensenum.INTLITERAL.ordinal()){
				acceptIt();
				parseLiteral();
			} else 
				System.out.println("Erro sintático fatorfloat\nlinha: "+tokenScanner.whichLine()+"\ncoluna: "+tokenScanner.whichColumn());
	} else
		System.out.println("Erro sintático fator geral\nlinha: "+tokenScanner.whichLine()+"\ncoluna: "+tokenScanner.whichColumn()+"\nsaída: "+currentToken.getFirst());
    }

    private void parseFloatLit(){
	if(currentToken.getSecond() == TokenScanner.tokensenum.INTLITERAL.ordinal()){
		acceptIt();
		accept(TokenScanner.tokensenum.DOT.ordinal());
		if (currentToken.getSecond() == TokenScanner.tokensenum.INTLITERAL.ordinal())
			acceptIt();
	} else if (currentToken.getSecond() == TokenScanner.tokensenum.DOT.ordinal()){
		acceptIt();
		accept(TokenScanner.tokensenum.INTLITERAL.ordinal());
	} else
		System.out.println("Erro sintático floatlit\nlinha: "+tokenScanner.whichLine()+"\ncoluna: "+tokenScanner.whichColumn());
    }

    private void parseIterativo(){    
	accept(TokenScanner.tokensenum.WHILE.ordinal());
	parseExpressao();
	accept(TokenScanner.tokensenum.DO.ordinal());
	parseComando();
    }

    private void parseListaDeComandos(){
	while((currentToken.getSecond() == TokenScanner.tokensenum.IDENTIFIER.ordinal()) || (currentToken.getSecond() == TokenScanner.tokensenum.IF.ordinal()) || (currentToken.getSecond() == TokenScanner.tokensenum.WHILE.ordinal()) || (currentToken.getSecond() == TokenScanner.tokensenum.BEGIN.ordinal())){
		parseComando();
		accept(TokenScanner.tokensenum.SEMICOLON.ordinal());
	}
    }

    private void parseListaDeIds(){
	accept(TokenScanner.tokensenum.IDENTIFIER.ordinal());
	while(currentToken.getSecond() == TokenScanner.tokensenum.COMMA.ordinal()){
		acceptIt();
		accept(TokenScanner.tokensenum.IDENTIFIER.ordinal());            
	}
    }

    private void parseLiteral(){
	if ((currentToken.getSecond() == TokenScanner.tokensenum.TRUE.ordinal()) || (currentToken.getSecond() == TokenScanner.tokensenum.FALSE.ordinal()))
		acceptIt();
	else if (currentToken.getSecond() == TokenScanner.tokensenum.INTLITERAL.ordinal()){
		acceptIt();
		if (currentToken.getSecond() == TokenScanner.tokensenum.DOT.ordinal())
			parseFloatLit();
	} else if (currentToken.getSecond() == TokenScanner.tokensenum.DOT.ordinal()){
		acceptIt();
		if (currentToken.getSecond() == TokenScanner.tokensenum.INTLITERAL.ordinal())
			acceptIt();
		else
			System.out.println("Erro sintático literal\nlinha: "+tokenScanner.whichLine()+"\ncoluna: "+tokenScanner.whichColumn());
	} else
		System.out.println("Erro sintático literal\nlinha: "+tokenScanner.whichLine()+"\ncoluna: "+tokenScanner.whichColumn());
    }

    private void parsePrograma(){
	accept(TokenScanner.tokensenum.PROGRAM.ordinal());
	accept(TokenScanner.tokensenum.IDENTIFIER.ordinal());
	accept(TokenScanner.tokensenum.SEMICOLON.ordinal());
	parseCorpo();
    }

    private void parseSeletor(){
	while(currentToken.getSecond() == TokenScanner.tokensenum.BRACKETL.ordinal()){
		acceptIt();
		parseExpressao();
		accept(TokenScanner.tokensenum.BRACKETR.ordinal());
	}	
    }

    private void parseTermo(){
	parseFator();
	while(currentToken.getSecond() == TokenScanner.tokensenum.OPMUL.ordinal()){
		acceptIt();
		parseFator();
	}
    }

    private void parseTipo(){
	if(currentToken.getSecond() == TokenScanner.tokensenum.ARRAY.ordinal())
		parseTipoAgregado();
	else if ((currentToken.getSecond() == TokenScanner.tokensenum.INTEGER.ordinal()) || (currentToken.getSecond() == TokenScanner.tokensenum.REAL.ordinal()) || (currentToken.getSecond() == TokenScanner.tokensenum.BOOLEAN.ordinal()))
		parseTipoSimples();
	else
		System.out.println("Erro sintático tipo\nlinha: "+tokenScanner.whichLine()+"\ncoluna: "+tokenScanner.whichColumn());
	
    }

    private void parseTipoAgregado(){
	accept(TokenScanner.tokensenum.ARRAY.ordinal());
	accept(TokenScanner.tokensenum.BRACKETL.ordinal());
	parseLiteral();
	accept(TokenScanner.tokensenum.DDOT.ordinal());
	parseLiteral();
	accept(TokenScanner.tokensenum.BRACKETR.ordinal());
	accept(TokenScanner.tokensenum.OF.ordinal());
	parseTipo();
    }

    private void parseTipoSimples(){
	if((currentToken.getSecond() == TokenScanner.tokensenum.INTEGER.ordinal()) || (currentToken.getSecond() == TokenScanner.tokensenum.REAL.ordinal()) || (currentToken.getSecond() == TokenScanner.tokensenum.BOOLEAN.ordinal()))
		acceptIt();
	else
		System.out.println("Erro sintático tipo simples\nlinha: "+tokenScanner.whichLine()+"\ncoluna: "+tokenScanner.whichColumn());
    }

    private void parseVariavel(){
	accept(TokenScanner.tokensenum.IDENTIFIER.ordinal());
	parseSeletor();
    }

    public void parse(){
   
	parsePrograma();
	System.out.println("Finalizado\nlinha: "+tokenScanner.whichLine()+"\ncoluna: "+tokenScanner.whichColumn());
	//	if (currentToken.getSecond() != TokenScanner.tokensenum.EOT.ordinal())
	//		System.err.println("Fim de arquivo, inválido:"+"\nlinha: "+tokenScanner.whichLine()+"\ncoluna: "+tokenScanner.whichColumn());
   }
}
