/**
 *  Instruction definition
 *
 * @author Gómez Brayan, Lara Milton, Quirós Esteban
 * @version 1.0
 * @since 25/11/2017
 */

package physicalcomponentssimulation.processorsparts;

public class Instruction {

    /**
     * Set of values that define an operation
     */
    private int operationCode;
    private int firsParameter;
    private int secondParameter;
    private int thirdParameter;

    /**
     * Initializes every instruction value to zero
     */
    public Instruction(){
        this.operationCode=0;
        this.firsParameter=0;
        this.secondParameter=0;
        this.thirdParameter=0;
    }

    /**
     * Get the operation code(defines which operation to execute)
     * @return
     */
    public int getOperationCode() {
        return operationCode;
    }

    /**
     * Set the operation code
     * @param operationCode new operation code
     */
    public void setOperationCode(int operationCode) {
        this.operationCode = operationCode;
    }

    /**
     * return first instruction parameter(without counting operation code)
     * @return first parameter
     */
    public int getFirsParameter() {
        return firsParameter;
    }

    /**
     * Set first parameter
     * @param firsParameter first parameter
     */
    public void setFirsParameter(int firsParameter) {
        this.firsParameter = firsParameter;
    }

    /**
     * return second instruction parameter(without counting operation code)
     * @return second parameter
     */
    public int getSecondParameter() {
        return secondParameter;
    }

    /**
     * Set first parameter
     * @param secondParameter second parameter
     */
    public void setSecondParameter(int secondParameter) {
        this.secondParameter = secondParameter;
    }

    /**
     * return second instruction parameter(without counting operation code)
     * @return second parameter
     */
    public int getThirdParameter() {
        return thirdParameter;
    }

    /**
     * Set first parameter
     * @param thirdParameter third parameter
     */
    public void setThirdParameter(int thirdParameter) {
        this.thirdParameter = thirdParameter;
    }

    /**
     * Returns the instruction in string form with all its values
     * @return instruction in string form
     */
    @Override
    public String toString() {
        return "Instruction{" +
                "operationCode=" + operationCode +
                ", firsParameter=" + firsParameter +
                ", secondParameter=" + secondParameter +
                ", thirdParameter=" + thirdParameter +
                '}';
    }
}
