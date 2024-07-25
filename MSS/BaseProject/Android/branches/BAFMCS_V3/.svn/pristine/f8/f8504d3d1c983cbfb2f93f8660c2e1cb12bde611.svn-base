package com.adins.mss.base.util;

import com.gadberry.utility.expression.Argument;
import com.gadberry.utility.expression.Expression;
import com.gadberry.utility.expression.symbol.EqualSymbol;

public class NotEqualSymbol extends EqualSymbol {

    public NotEqualSymbol(Expression expression) {
        super(expression);
    }

    @Override
    public Argument resolve() {
        boolean result = super.resolve().toBoolean();
        Argument argResult = new Argument(!result, getResolver());
        return argResult;
    }
}
