package com.adins.mss.foundation.questiongenerator;

import androidx.annotation.Keep;

import com.gadberry.utility.expression.Argument;
import com.gadberry.utility.expression.Expression;
import com.gadberry.utility.expression.symbol.EqualSymbol;

@Keep
public class NotEqualSymbol extends EqualSymbol {

    @Keep
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
