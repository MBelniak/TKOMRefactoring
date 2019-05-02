package Parser;

import java.util.List;

public class Production {
    private NonTerminalSymbol leftSymbol;
    private List<Symbol> rightSymbols;
    private int productionNumber;

    NonTerminalSymbol getLeftSymbol() {
        return leftSymbol;
    }

    List<Symbol> getRightSymbols() {
        return rightSymbols;
    }
}
