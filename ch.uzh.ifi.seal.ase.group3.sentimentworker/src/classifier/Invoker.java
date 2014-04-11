package classifier;

import commands.CalculateWmPrecisionCommand;
import commands.Command;
import commands.ConstructWmCommand;

/**
 * 
 * class that aggregates all commands and delegates them the actions' execution
 * 
 */
public class Invoker {

	private Command _constructWmCommand;
	private Command _calculateWmPrecisionCommand;

	/**
	 * 
	 * @param ptc command that represent the action for preparing train
	 * @param cc command that represent the action for constructing a classifier
	 * @param cwmc command that represent the action for constructing a weighted majority classifier
	 * @param calcPrec command that represent the action for calculating weighted majority's precision
	 */
	public Invoker(ConstructWmCommand cwmc, CalculateWmPrecisionCommand calcPrec) {
		this._constructWmCommand = cwmc;
		this._calculateWmPrecisionCommand = calcPrec;
	}

	/**
	 * invokes command for constructing weighted majority classifier
	 */
	public void constructWm() {
		this._constructWmCommand.execute();
	}

	/**
	 * invokes command for calculating weighted majority classifier's precision
	 */
	public void calculateWmPrecision() {
		this._calculateWmPrecisionCommand.execute();
	}
}
