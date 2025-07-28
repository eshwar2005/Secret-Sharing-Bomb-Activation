import java.math.BigInteger;
import java.util.*;

public class SecretSharingBombWithLogicVerification {

    static class KeyInfo {
        String name;
        String operation;
        BigInteger op1;
        BigInteger op2;     // may be null if operation == "number"
        BigInteger computedValue;

        KeyInfo(String name, String operation, BigInteger op1, BigInteger op2) {
            this.name = name;
            this.operation = operation.toLowerCase();
            this.op1 = op1;
            this.op2 = op2;
            this.computedValue = computeValue();
        }

        // Compute the key value from operation and operands
        private BigInteger computeValue() {
            switch (operation) {
                case "sum":
                    return op1.add(op2);
                case "multiplier":
                    return op1.multiply(op2);
                case "gcd":
                case "hcf":
                    return op1.gcd(op2);
                case "lcm":
                    return op1.multiply(op2).divide(op1.gcd(op2));
                case "number":
                    return op1;
                default:
                    throw new IllegalArgumentException("Unknown operation: " + operation);
            }
        }

        public String getLogicDescription() {
            if (operation.equals("number")) {
                return name + " key is direct number: " + op1;
            } else {
                return name + " key uses operation '" + operation + "' with operands (" + op1 + ", " + op2 + ")";
            }
        }

        public String getComputationStep() {
            if (operation.equals("number")) {
                return name + ": Direct value = " + computedValue;
            } else {
                String opSymbol = "";
                switch (operation) {
                    case "sum": opSymbol = "+"; break;
                    case "multiplier": opSymbol = "*"; break;
                    case "gcd":
                    case "hcf": opSymbol = "gcd"; break;
                    case "lcm": opSymbol = "lcm"; break;
                }
                if (operation.equals("gcd") || operation.equals("hcf") || operation.equals("lcm")) {
                    return name + ": " + operation.toUpperCase() + "(" + op1 + ", " + op2 + ") = " + computedValue;
                } else {
                    return name + ": " + op1 + " " + opSymbol + " " + op2 + " = " + computedValue;
                }
            }
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        int n = 0, k = 0;

        // Input total keys n and required keys k, with validation
        while (true) {
            System.out.print("Enter total number of key holders (n): ");
            if (sc.hasNextInt()) {
                n = sc.nextInt();
            } else {
                System.out.println("Invalid input, please enter an integer.");
                sc.next();
                continue;
            }

            System.out.print("Enter number of keys needed to activate bomb (k): ");
            if (sc.hasNextInt()) {
                k = sc.nextInt();
            } else {
                System.out.println("Invalid input, please enter an integer.");
                sc.next();
                continue;
            }
            sc.nextLine();

            if (k > n) {
                System.out.println("Error: Number of keys needed (k) cannot be greater than total keys (n). Please re-enter.");
            } else if (k <= 0 || n <= 0) {
                System.out.println("Error: Both n and k must be positive integers. Please re-enter.");
            } else {
                break;
            }
        }

        KeyInfo[] keys = new KeyInfo[n];

        System.out.println("\nFor each key holder, enter logic/type and operands to compute the key value.");
        System.out.println("Supported logic: sum, multiplier, gcd, hcf, lcm, number");
        System.out.println("If logic is 'number', enter only one operand.\n");

        // Input for each key holder
        for (int i = 0; i < n; i++) {
            System.out.print("Name of key holder #" + (i + 1) + ": ");
            String name = sc.nextLine().trim();

            String operation;
            BigInteger op1 = null, op2 = null;

            while (true) {
                System.out.print("Operation for " + name + " (sum, multiplier, gcd, hcf, lcm, number): ");
                operation = sc.nextLine().trim().toLowerCase();
                List<String> allowedOps = Arrays.asList("sum", "multiplier", "gcd", "hcf", "lcm", "number");
                if (!allowedOps.contains(operation)) {
                    System.out.println("Invalid operation. Try again.");
                    continue;
                }

                try {
                    System.out.print("Enter first operand (integer): ");
                    op1 = new BigInteger(sc.nextLine().trim());

                    if (!operation.equals("number")) {
                        System.out.print("Enter second operand (integer): ");
                        op2 = new BigInteger(sc.nextLine().trim());
                    } else {
                        op2 = null;
                    }
                    break;
                } catch (Exception e) {
                    System.out.println("Invalid integer input. Try again.");
                }
            }

            keys[i] = new KeyInfo(name, operation, op1, op2);
            System.out.println("Computed key value: " + keys[i].computedValue);
            System.out.println(keys[i].getComputationStep());
            System.out.println();
        }

        // Summary display
        System.out.println("Summary of key holders and their logic/keys:");
        for (KeyInfo key : keys) {
            System.out.println(key.getLogicDescription() + " ; Computed Key = " + key.computedValue);
        }

        System.out.println("\nDo you want to enter custom values for any keys? (yes/no): ");
        String ans = sc.nextLine().trim().toLowerCase();

        // Override keys if user chooses yes
        if (ans.equals("yes") || ans.equals("y")) {
            while (true) {
                System.out.print("Enter the key holder's name to change value (or type 'done' to finish): ");
                String nameToChange = sc.nextLine().trim();
                if (nameToChange.equalsIgnoreCase("done")) break;

                int idx = -1;
                for (int i = 0; i < n; i++) {
                    if (keys[i].name.equalsIgnoreCase(nameToChange)) {
                        idx = i;
                        break;
                    }
                }
                if (idx == -1) {
                    System.out.println("Name not found. Please try again.");
                    continue;
                }

                System.out.print("Enter new numeric value for " + keys[idx].name + ": ");
                String newValStr = sc.nextLine().trim();
                try {
                    BigInteger newVal = new BigInteger(newValStr);

                    if (!newVal.equals(keys[idx].computedValue)) {
                        System.out.println("Warning! Entered value " + newVal +
                                " DOES NOT match the computed key " + keys[idx].computedValue +
                                " from the declared logic. This may indicate a wrong/fake key.");
                    } else {
                        System.out.println("Entered value matches the computed key. Good.");
                    }

                    keys[idx].computedValue = newVal;
                    System.out.println("Updated " + keys[idx].name + "'s key to " + newVal);
                } catch (Exception e) {
                    System.out.println("Invalid number entered. Value not changed.");
                }
            }
        }

        // Now, prompt user to enter person IDs to use in the verification equation:
        List<String> selectedKeysForEquation = new ArrayList<>();
        System.out.println("\nEnter the person IDs of keys you want to use to create the equation (space separated):");
        while (true) {
            String line = sc.nextLine().trim();
            if (line.isEmpty()) {
                System.out.println("Please enter at least " + k + " keys.");
                continue;
            }
            String[] tokens = line.split("\\s+");
            if (tokens.length < k) {
                System.out.println("Error: Minimum keys needed are " + k + ". You entered " + tokens.length + ". Please enter again.");
                continue;
            }
            // Verify if all keys exist
            boolean allValid = true;
            for (String tk : tokens) {
                boolean found = false;
                for (KeyInfo key : keys) {
                    if (key.name.equalsIgnoreCase(tk)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    System.out.println("Key holder '" + tk + "' not found. Enter keys again:");
                    allValid = false;
                    break;
                }
            }
            if (!allValid) continue;

            selectedKeysForEquation = Arrays.asList(tokens);
            break;
        }

        // Show equation like y = P1 + P4 + ...
        StringBuilder equationStr = new StringBuilder("y = ");
        for (int i = 0; i < selectedKeysForEquation.size(); i++) {
            equationStr.append(selectedKeysForEquation.get(i));
            if (i != selectedKeysForEquation.size() - 1)
                equationStr.append(" + ");
        }
        System.out.println("\nCompute this equation:");
        System.out.println(equationStr);

        // Prompt user to input values for each key in the equation
        BigInteger sumInput = BigInteger.ZERO;
        Map<String, BigInteger> userProvidedValues = new HashMap<>();
        for (String keyName : selectedKeysForEquation) {
            while (true) {
                System.out.print("Enter value for " + keyName + ": ");
                String valIn = sc.nextLine().trim();
                try {
                    BigInteger val = new BigInteger(valIn);
                    userProvidedValues.put(keyName, val);
                    sumInput = sumInput.add(val);
                    break;
                } catch (Exception e) {
                    System.out.println("Invalid number. Please enter an integer.");
                }
            }
        }

        // Compute correct sum for selected keys using stored computed values
        BigInteger correctSum = BigInteger.ZERO;
        for (String keyName : selectedKeysForEquation) {
            for (KeyInfo key : keys) {
                if (key.name.equalsIgnoreCase(keyName)) {
                    correctSum = correctSum.add(key.computedValue);
                    break;
                }
            }
        }

        System.out.println("\nSum of entered values: " + sumInput);
        System.out.println("Expected sum based on stored keys: " + correctSum);

        // Compare sums and decide
        if (sumInput.equals(correctSum)) {
            System.out.println("\nCorrect! Bomb activated countdown starting...");
            try {
                for (int i = 10; i >= 1; i--) {
                    System.out.println(i);
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                // ignore interruptions
            }
            System.out.println("BOOM! Bomb activated! 💥");
        } else {
            System.out.println("\nIncorrect total value entered!");
            System.out.println("Bomb activation failed due to wrong key values.");
        }

        sc.close();
    }
}
