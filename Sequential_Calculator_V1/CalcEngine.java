import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Stack;
import javax.swing.JLabel;

/**
 * captures all number inputs and operations through one ActionListener of the Buttons.
 * IImplements the logic of the Calculator.
 */
public class CalcEngine{

    // ActionListener for digits '0-9', '.'  & 'Del' & 'AC' & '+/-' & ....
    // ... '1/X' & 'X²' & '√X' & '%' & 'MS' & 'MR' & 'M+' & 'M-' & 'MC' & '=')
    private final ActionListener allListener;

    // Stack to store operands and the result of intermediate calculations
    private Stack<Double> operands = new Stack<>();
    private Stack<String> operators = new Stack<>();

    // Flag to track if it's the first operand
    private boolean isFirstOperand = true; 

    // decision if the display should be cleared or not
    private boolean clearDisplay = true;

    // Temperary storage for memory (MS, MR, M+, M-) operations
    private String memoryPlus;
    
    /**
     * Engine construction with reference to main window. Über diese Referenz
     * through this, the inputs and results are processed and returned.
     * Defines abstract menthods of the listener objects.
     *
     * @param calcWindow Reference to main window.
     */
    public CalcEngine(CalcWindow calcWindow) {
        
        allListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JLabel display = calcWindow.getDisplayField();
                JLabel displayStack = calcWindow.stackDisplay;
                JLabel displayOperator = calcWindow.operatorDisplay;
                String buttonInput = e.getActionCommand().toString();
                String operator = e.getActionCommand();
                double result = 0;
                switch (buttonInput) {
                    //capturing digits
                    case ".":
                        // check point to avoid multiple decimal points
                        if (!display.getText().contains(".")) {
                            display.setText(display.getText() + buttonInput);
                        }
                        break;
                    case "0":
                    case "1":
                    case "2":
                    case "3":
                    case "4":
                    case "5":
                    case "6":
                    case "7":
                    case "8":
                    case "9":
                        if (clearDisplay) {
                            display.setText("0");
                            clearDisplay = false;
                        }
                        display.setText(display.getText() + buttonInput);
                        break;
                    case "+":
                    case "-":
                    case "*":
                    case "/":
                        // Ensure display is not empty before parsing
                        String currentText = display.getText().trim();
                        double number;
                        // Method to handle the button presses for numbers
                        // Only proceed if the display contains a valid number
                        if (!currentText.isEmpty() && !currentText.equals("-")) {
                            number = Double.parseDouble(display.getText());
                            // Check if it's the first operand
                            if (isFirstOperand) {
                                // Store the first operand
                                operands.push(number);
                                display.setText("0");
                                // Now we have a first operand, subsequent numbers will be the second operand
                                isFirstOperand = false;  
                            } else {
                                // If an operator is pressed, the second operand is entered here
                                calcWindow.stackDisplay.setText(String.valueOf(operands.peek()));
                                operands.push(number);
                                performCalculation();
                                //display.setText(String.valueOf(operands.peek()));
                            }
                        }
                        // Method to handle operator button presses
                        // Store the operator in the operator stack
                        if (operators.isEmpty()) {
                            // If it's the first operator, just push it to the operator stack
                            operators.push(buttonInput);
                        } else {
                            // If there's already an operator, apply the previous operator and then push the new one
                            performCalculation();
                            operators.push(buttonInput);
                            //displayStack.setText(display.getText());
                        }
                        displayStack.setText(String.valueOf(operands.peek()));
                        displayOperator.setText(operators.peek());

                        display.setText("0");
                        System.out.println("--------parameters after calculation is performed------");
                        System.out.println("operands: " + operands);
                        System.out.println("operators: " + operators);
                        break;
                    case "AC":
                        //reset the all display fields and clear the stack
                        display.setText("0");
                        if (!operands.isEmpty()) {
                            operands.clear();
                        }
                        if (!operators.isEmpty()) {
                            operators.clear();
                        }
                        calcWindow.stackDisplay.setText("");
                        calcWindow.msTextDisplay.setText("");
                        calcWindow.msValueDisplay.setText("");
                        calcWindow.operatorDisplay.setText("");
                        isFirstOperand = true;
                        clearDisplay = true;
                        break;
                    case "Del":
                        //remove one right most digit from the display
                        String text = display.getText();
                        System.out.println("length of display text: " + text.length());
                        if (text.length() == 0) {
                            break;
                        }else{
                        if (text.length() == 1) {
                            display.setText("0");
                        }else {
                            display.setText(text.substring(0, text.length() - 1));
                        }
                        break;
                    }
                    case "+/-":
                        //change the sign of the number
                        double temp = Double.parseDouble(display.getText());
                        display.setText(String.valueOf(temp * (-1)));
                        break;
                        case "1/X":
                        result = 1 / Double.parseDouble(display.getText());
                        display.setText(Double.toString(result));
                        break;
                    case "X²":
                        double value = Double.parseDouble(display.getText());
                        result = value * value;
                        System.out.println("result: " + result);
                        display.setText(Double.toString(result));
                        break;
                    case "√X":
                        result = Math.sqrt(Double.parseDouble(display.getText()));
                        display.setText(Double.toString(result));
                        break;
                    case "%":
                        //calculate the percentage of the number
                        result = Double.parseDouble(display.getText()) / 100;
                        display.setText(Double.toString(result));
                        break;
                    case "MS":
                        //store the current display value in the memory 'memoryPlus'
                        calcWindow.msTextDisplay.setText("MS: ");
                        memoryPlus = display.getText();
                        calcWindow.msValueDisplay.setText(memoryPlus);
                        break;
                    case "MR":
                        //recall the value from the memory 'memoryPlus' and display it
                        if (!memoryPlus.isEmpty()) {
                            display.setText(memoryPlus);
                            clearDisplay = true; 
                        }
                        break;
                    case "M+":
                        //add the current display value to the memory 'memoryPlus'
                        double tempValue = Double.parseDouble(memoryPlus) + Double.parseDouble(display.getText());
                        display.setText(String.valueOf(tempValue));
                        break;
                    case "M-":
                        //subtract the current display value from the memory 'memoryPlus'
                        tempValue = Double.parseDouble(memoryPlus) - Double.parseDouble(display.getText());
                        display.setText(String.valueOf(tempValue));
                        break;
                    case "MC":
                        //reset the memory 'memoryPlus'
                        calcWindow.msTextDisplay.setText("");
                        calcWindow.msValueDisplay.setText("");
                        break;
                    
                    case "=":
                        // Method to handle the equals button press
                        if (operators.isEmpty()) {
                            // If there are no operators, just display the number
                            display.setText(display.getText());
                            break;
                        } else if (operands.isEmpty()) {
                            // If there are no operands, just display the number
                            display.setText(display.getText());
                            break;
                        } else{
                            number = Double.parseDouble(display.getText());
                            operands.push(number);
                            performCalculation();
                            display.setText(String.valueOf(operands.peek()));
                            isFirstOperand = true;
                            // If there's only one operand and no operator, just display the number
                            display.setText(String.valueOf(operands.pop()));
                            displayStack.setText("");
                            displayOperator.setText("");
                            break;
                        }

                    default:
                }
            }
        };
    }
    /**
     * Returns the ActionListener object.
     * @return ActionListener
     */
    public ActionListener getAllListener(){
        return this.allListener;
    }

    /**
     * Method to prepare parameters for calculation.
     */
    // Method to perform the calculation based on the operator
    private void performCalculation() {
        if (operands.size() >= 2 && !operators.isEmpty()) {
            // Pop the operands and the operator
            System.out.println("--------parameters before calculation is performed------");
            System.out.println("operands: " + operands);
            System.out.println("operators: " + operators);
            double secondOperand = operands.pop();
            double firstOperand = operands.pop();
            String operator = operators.pop();
            
            // Perform the calculation based on the operator
            double calculationResult = doCalculations(operator, firstOperand, secondOperand);

            // Push the result back onto the operands stack
            operands.push(calculationResult);
        }
    }

    /**
     * Method to preform +, -, * & / operation.
     */

    // Method to apply the operator to two operands
    private double  doCalculations(String operator, double  a, double b) {
        switch (operator) {
            case "+":
                return a + b;
            case "-":
                return a - b;
            case "*":
                return a * b;
            case "/":
                return a / b;
            default:
                throw new IllegalArgumentException("Invalid operator: " + operator);
        }
    }

}


