import java.util.Scanner;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Main {
    public static void main(String[] args) {
        MultiThreadCalculator calculator = new MultiThreadCalculator();
        calculator.Start();
    }
}

class MultiThreadCalculator{
    public int ArrayElementsSum = 0;
    public int[] calculateArray;

    private int arraySize;
    public int endIndex;


    public void Start(){
        Scanner input = new Scanner(System.in);

        System.out.print("Input a array size: ");
        arraySize = input.nextInt();
        calculateArray = new int[arraySize];
        for (int i = 0; i < arraySize; i++){
            calculateArray[i] = i;
        }

        endIndex = arraySize - 1;
        for (int i = 0; i<arraySize;i++) {
            ArrayElementsSum += calculateArray[i];
        }
        ContinueCycle();
    }

    public void ContinueCycle(){
        int maxThreads = arraySize/2;
        int startIndex = 0;
        CyclicBarrier newCyclicBarrier = new CyclicBarrier(maxThreads, new CalculatorEnd(this));
        while(endIndex>startIndex){
            Thread newThread = new Thread(new CalculatorThread(this, calculateArray, startIndex, endIndex, newCyclicBarrier));
            newThread.start();
            startIndex++;
            endIndex--;
        }
        if(arraySize % 2 > 0)
            arraySize = arraySize/2 + 1;
        else
            arraySize /= 2;
        endIndex = arraySize - 1;
    }

    public synchronized  void SetSumOfElements(int sum, int index){
        calculateArray[index] = sum;
    }
}

class CalculatorEnd extends Thread {
    MultiThreadCalculator calculatorRef;
    public  CalculatorEnd(MultiThreadCalculator calculator){
        calculatorRef = calculator;
    }

    @Override
    public void run() {
        if(calculatorRef.endIndex>0) {
            calculatorRef.ContinueCycle();
            return;
        }

        System.out.println("One thread sum: "+ calculatorRef.ArrayElementsSum);
        System.out.println("Multiply thread sum: " + calculatorRef.calculateArray[0]);
    }
}

class CalculatorThread implements Runnable{

    int[] ThisArray;
    int firstIndex, secondIndex;
    MultiThreadCalculator calculatorRef;
    CyclicBarrier cyclicBarrierRef;

    CalculatorThread(MultiThreadCalculator calculator, int[] array, int firstIndex, int secondIndex, CyclicBarrier cyclicBarrier) {
        this.firstIndex = firstIndex;
        this.secondIndex = secondIndex;
        ThisArray = array;
        calculatorRef = calculator;
        cyclicBarrierRef = cyclicBarrier;
    }

    @Override
    public void run() {
        int sum = ThisArray[firstIndex]+ThisArray[secondIndex];
        calculatorRef.SetSumOfElements(sum, firstIndex);
        try {
            cyclicBarrierRef.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (BrokenBarrierException e) {
            throw new RuntimeException(e);
        }
    }
}