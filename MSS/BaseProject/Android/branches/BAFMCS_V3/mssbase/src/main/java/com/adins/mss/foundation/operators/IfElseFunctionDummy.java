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
public class IfElseFunctionDummy extends Function {

    @Keep
    public IfElseFunctionDummy(Expression expression) {
        super(expression);
    }

    @Override
    protected void checkArgs(List<Argument> args) throws InvalidArgumentsException {
        if (args.size() < 3) {
            throw new InvalidArgumentsException(
                    "IfElseOperator requires a minimum of three arguments."
            );
        }
        if (!args.get(0).isBoolean()) {
            throw new InvalidArgumentsException(
                    "IfElseOperator only accepts boolean.  Wrong type of arguments provided.  Arg: "
                            + args.get(0).toString());
        }
        for (Argument arg : args) {
            if (arg.isNull()) {
                throw new InvalidArgumentsException(
                        "IfElseOperator cannot accept null arguments.  At least one argument provided was null.");
            }
        }
    }

    @Override
    public Argument resolve() {
        Argument condArgument = new Argument(getArgument(0).toBoolean(), getResolver());
        return condArgument;
    }
}
