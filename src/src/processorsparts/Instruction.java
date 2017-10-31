package processorsparts;

public class Instruction {
    private int operationCode;
    private int firsParameter;
    private int secondParameter;
    private int thirdParameter;


    public int getOperationCode() {
        return operationCode;
    }

    public void setOperationCode(int operationCode) {
        this.operationCode = operationCode;
    }

    public int getFirsParameter() {
        return firsParameter;
    }

    public void setFirsParameter(int firsParameter) {
        this.firsParameter = firsParameter;
    }

    public int getSecondParameter() {
        return secondParameter;
    }

    public void setSecondParameter(int secondParameter) {
        this.secondParameter = secondParameter;
    }

    public int getThirdParameter() {
        return thirdParameter;
    }

    public void setThirdParameter(int thirdParameter) {
        this.thirdParameter = thirdParameter;
    }

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
