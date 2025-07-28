import java.math.BigInteger;
import java.util.*;

public class SecretSharingBombWithLogicVerification {

    static class KeyInfo {
        String name;
        String operation;
        BigInteger op1;
        BigInteger op2;     // null if operation = number
        BigInteger computedValue;

        KeyInfo(String name, String operation, BigInteger op1, BigInteger op2) {
            this.name = name;
            this.operation = operation.toLowerCase();
            this.op1 = op1;
            this.op2 = op2;
            this.computedValue = computeValue();
        }

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

        // Input total number of key holders (n) and min keys (k)
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

        // Input keys data
        for (int i = 0; i < n; i++) {
            System.out.print("Name of key holder #" + (i + 1) + ": ");
            String name = sc.nextLine().trim();

            String operation;
            BigInteger op1 = null, op2 = null;

            while (true) {
                System.out.print("Operation for " + name + ": ");
                operation = sc.nextLine().trim().toLowerCase();
                List<String> allowedOps = Arrays.asList("sum", "multiplier", "gcd", "hcf", "lcm", "number");
                if (!allowedOps.contains(operation)) {
                    System.out.println("Invalid operation. Try again.");
                    continue;
                }

                try {
                    System.out.print("Enter first operand: ");
                    op1 = new BigInteger(sc.nextLine().trim());

                    if (!operation.equals("number")) {
                        System.out.print("Enter second operand: ");
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

        System.out.println("Summary of key holders and their logic/keys:");
        for (KeyInfo key : keys) {
            System.out.println(key.getLogicDescription() + " ; Computed Key = " + key.computedValue);
        }

        System.out.print("\nDo you want to enter custom values for any keys? (yes/no): ");
        String ans = sc.nextLine().trim().toLowerCase();

        if (ans.equals("yes") || ans.equals("y")) {
            // Existing code for custom value override (not shown here to keep example concise)
            // You can keep your previous override logic here if needed.
            System.out.println("Custom values override currently not implemented in this snippet.");
            System.exit(0);
        }

        // User chooses keys to include in equation
        System.out.print("\nEnter the person IDs of keys you want to use to create the equation (space separated): ");
        List<String> selectedKeys = new ArrayList<>();

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

            boolean allExist = true;
            for (String t : tokens) {
                boolean found = false;
                for (KeyInfo key : keys) {
                    if (key.name.equalsIgnoreCase(t)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    System.out.println("Person ID " + t + " not found. Please enter valid keys.");
                    allExist = false;
                    break;
                }
            }

            if (allExist) {
                selectedKeys.addAll(Arrays.asList(tokens));
                break;
            }
        }

        // Print equation string
        System.out.println("\nCompute this equation:");
        System.out.print("y = ");
        for (int i = 0; i < selectedKeys.size(); i++) {
            System.out.print(selectedKeys.get(i));
            if (i != selectedKeys.size() - 1) System.out.print(" + ");
        }
        System.out.println();

        // Prompt user to enter values for each key in the equation
        BigInteger sumEntered = BigInteger.ZERO;
        Map<String, BigInteger> enteredValues = new HashMap<>();

        for (String keyName : selectedKeys) {
            while (true) {
                System.out.print("Enter value for " + keyName + ": ");
                String valStr = sc.nextLine().trim();
                try {
                    BigInteger val = new BigInteger(valStr);
                    sumEntered = sumEntered.add(val);
                    enteredValues.put(keyName, val);
                    break;
                } catch (Exception e) {
                    System.out.println("Invalid number. Try again.");
                }
            }
        }

        // Calculate expected sum according to stored keys
        BigInteger expectedSum = BigInteger.ZERO;
        for (String keyName : selectedKeys) {
            for (KeyInfo key : keys) {
                if (key.name.equalsIgnoreCase(keyName)) {
                    expectedSum = expectedSum.add(key.computedValue);
                    break;
                }
            }
        }

        System.out.println("\nSum of entered values: " + sumEntered);
        System.out.println("Expected sum based on stored keys: " + expectedSum);

        if (sumEntered.equals(expectedSum)) {
            System.out.println("\nCorrect! Bomb activated countdown starting...");
            try {
                for (int i = 10; i >= 1; i--) {
                    System.out.println(i);
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                // ignored
            }
            System.out.println("BOOM! Bomb activated! 💥");
        } else {
            System.out.println("\nIncorrect total value entered!");
            System.out.println("Bomb activation failed due to wrong key values.");
        }

        sc.close();
    }
}
