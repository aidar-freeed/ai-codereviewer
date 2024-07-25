package com.adins.mss.foundation.operators;

import androidx.annotation.Keep;

import com.gadberry.utility.expression.Argument;
import com.gadberry.utility.expression.Expression;
import com.gadberry.utility.expression.Function;
import com.gadberry.utility.expression.InvalidArgumentsException;

import java.util.List;

/**
 * Created by gigin.ginanjar on 14/09/2016.
 */
public class IfElseFunction extends Function {

    @Keep
    public IfElseFunction(Expression expression) {
        super(expression);
    }

    @Override
    protected void checkArgs(List<Argument> args) throws InvalidArgumentsException {
        if (args.size() < 3) {
            throw new InvalidArgumentsException(
                    "IfElseOperator requires a minimum of three arguments."
            );
        }
        for (Argument arg : args) {
            if (arg.isNull()) {
                throw new InvalidArgumentsException(
                        "IfElseOperator cannot accept null arguments.  At least one argument provided was null.");
            }
            if (!arg.isBoolean()) {
                throw new InvalidArgumentsException(
                        "IfElseOperator only accepts boolean.  Wrong type of arguments provided.  Arg: "
                                + arg.toString());
            }
        }

    }

    @Override
    public Argument resolve() {
        Argument condArgument = new Argument(getArgument(0).toBoolean(), getResolver());
        boolean condition = condArgument.toBoolean();
        if (condition) {
            return new Argument(getArgument(1).toBoolean(), getResolver());
        } else {
            return new Argument(getArgument(2).toBoolean(), getResolver());
        }
    }
}
