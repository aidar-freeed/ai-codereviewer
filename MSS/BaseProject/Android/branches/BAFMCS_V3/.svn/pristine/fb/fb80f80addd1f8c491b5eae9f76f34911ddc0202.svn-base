package com.adins.mss.foundation.operators;

import androidx.annotation.Keep;

import com.adins.mss.base.util.NPWPValidation;
import com.gadberry.utility.expression.Argument;
import com.gadberry.utility.expression.Expression;
import com.gadberry.utility.expression.Function;
import com.gadberry.utility.expression.InvalidArgumentsException;

import java.util.List;

/**
 * Created by gigin.ginanjar on 10/10/2016.
 */

public class CheckNPWPFunction extends Function {

    @Keep
    public CheckNPWPFunction(Expression expression) {
        super(expression);
    }

    @Override
    protected void checkArgs(List<Argument> args) throws InvalidArgumentsException {
        if (args.size() > 1) {
            throw new InvalidArgumentsException(
                    "Check NPWP requires a maximum of one argument."
            );
        }
        for (Argument arg : args) {
            if (arg.isNull()) {
                throw new InvalidArgumentsException(
                        "Check NPWP cannot accept null arguments.  At least one argument provided was null.");
            }
        }
    }

    @Override
    public Argument resolve() {
        String condition = getArgument(0).toString();
        NPWPValidation validation = new NPWPValidation(condition);
        boolean isValid = validation.getValidation();
        return new Argument(isValid, getResolver());
    }
}
