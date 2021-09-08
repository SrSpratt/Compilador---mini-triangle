public class Parser {

    private TokenScanner tokenScanner;
    private int currentToken;
    
    private void acceptIt(){

    }
    private void accept(int expectedKind){
        if(currentToken.ordinal() == expectedKind)
            currentToken = tokenScanner.scanToken();
    }

    private void parseAtribuicao(){
        parseVariavel();
        accept(TokenScanner.tokensenum.BECOMES);
        parseExpressao();
    }
    private void parseBoolLit(){
        switch();
    }
    private void parseComando(){

    }
    private void parseComandoComposto(){

    }
    private void parseCondicional(){

    }
    private void parseCorpo(){

    }
    private void parseDeclaracao(){

    }
    private void parseDeclaracaoDeVariavel(){

    }
    private void parseDeclaracoes(){

    }
    private void parseDigito(){

    }
    private void parseExpressao(){

    }
    private void parseExpressaoSimples(){

    }
    private void parseFator(){

    }
    private void parseFloatLit(){

    }
    private void parseIdentifier(){

    }
    private void parseIntLit(){

    }
    private void parseIterativo(){

    }
    private void parseLetra(){

    }
    private void parseListaDeComandos(){
       
    }
    private void parseListaDeIds(){

    }
    private void parseLiteral(){

    }
    private void parseOpAd(){

    }
    private void parseOpMul(){

    }
    private void parseOpRel(){

    }
    private void parseOutros(){

    }
    private void parsePrograma(){

    }
    private void parseSeletor(){

    }
    private void parseTermo(){

    }
    private void parseTipo(){

    }
    private void parseTipoSimples(){

    }
    private void parseVariavel(){

    }
    private void parseVazio(){

    }

    public void parse(){

    }




}
