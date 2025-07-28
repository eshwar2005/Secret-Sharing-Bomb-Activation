import java.math.BigInteger;
import java.util.*;

public class SecretSharingBombWithBaseInput {

    // Class representing each key holder's data
    static class KeyInfo {
        String name;           // Person ID or name
        String operation;      // Always "number" here (direct decimal number)
        BigInteger op1;        // Decimal numeric value (converted from base)
        BigInteger op2;        // null, not used
        BigInteger computedValue;

        KeyInfo(String name, BigInteger val) {
            this.name = name;
            this.operation = "number";
            this.op1 = val;
            this.op2 = null;
            this.computedValue = val;
        }

        public String getLogicDescription() {
            return name + " key in decimal: " + computedValue;
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        int n = 0, k = 0;

        // Input n and k with validation
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

        System.out.println("\nFor each key holder, enter their ID, the base of the key value, and the value string.");

        // Input person ID, base, and value; convert to decimal BigInteger
        for (int i = 0; i < n; i++) {
            System.out.print("Person ID or name #" + (i + 1) + ": ");
            String name = sc.nextLine().trim();

            int base = 10;
            while (true) {
                System.out.print("Enter base of the key value (2 to 36): ");
                String baseStr = sc.nextLine().trim();
                try {
                    base = Integer.parseInt(baseStr);
                    if (base < 2 || base > 36) {
                        System.out.println("Invalid base. Please enter a base between 2 and 36.");
                    } else {
                        break;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter an integer base.");
                }
            }

            BigInteger val;
            while (true) {
                System.out.print("Enter key value for " + name + " in base " + base + ": ");
                String valStr = sc.nextLine().trim();
                try {
                    val = new BigInteger(valStr, base);
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid key value for given base. Please re-enter.");
                }
            }

            keys[i] = new KeyInfo(name, val);
            System.out.println("Stored decimal value: " + val);
            System.out.println();
        }

        // Display summary
        System.out.println("Summary of keys:");
        for (KeyInfo key : keys) {
            System.out.println(key.getLogicDescription());
        }

        // Ask user if they want to enter custom values (optional override)
        System.out.println("\nDo you want to enter custom values for any keys? (yes/no): ");
        String ans = sc.nextLine().trim().toLowerCase();

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

                System.out.print("Enter new decimal numeric value for " + keys[idx].name + ": ");
                String newValStr = sc.nextLine().trim();
                try {
                    BigInteger newVal = new BigInteger(newValStr);

                    if (!newVal.equals(keys[idx].computedValue)) {
                        System.out.println("Warning! Entered value " + newVal +
                                " DOES NOT match the previously stored value " + keys[idx].computedValue +
                                ". This may indicate a wrong/fake key.");
                    } else {
                        System.out.println("Entered value matches previously stored value.");
                    }

                    keys[idx].computedValue = newVal;
                    System.out.println("Updated " + keys[idx].name + "'s key to " + newVal);
                } catch (Exception e) {
                    System.out.println("Invalid number entered. Value not changed.");
                }
            }
        }

        // Prepare arrays and list of names/values for processing all n keys
        List<String> keyNames = new ArrayList<>();
        List<BigInteger> vals = new ArrayList<>();
        for (KeyInfo key : keys) {
            keyNames.add(key.name);
            vals.add(key.computedValue);
        }

        // Generate all combinations of n choose k to reconstruct secret
        List<int[]> combs = combinations(n, k);
        Map<BigInteger, Integer> secretCount = new HashMap<>();
        Map<BigInteger, List<Set<String>>> secretGroups = new HashMap<>();

        for (int[] comb : combs) {
            BigInteger[] xs = new BigInteger[k];
            BigInteger[] ys = new BigInteger[k];
            Set<String> group = new HashSet<>();

            for (int i = 0; i < k; i++) {
                xs[i] = BigInteger.valueOf(comb[i] + 1);   // use 1-based index for x coordinate
                ys[i] = vals.get(comb[i]);
                group.add(keyNames.get(comb[i]));
            }

            try {
                BigInteger secret = lagrangeConstant(xs, ys, BigInteger.ZERO);
                secretCount.put(secret, secretCount.getOrDefault(secret, 0) + 1);
                secretGroups.computeIfAbsent(secret, s -> new ArrayList<>()).add(group);
            } catch (ArithmeticException e) {
                // skip combinations where division fails
            }
        }

        if (secretCount.isEmpty()) {
            System.out.println("\nCould not determine any valid secret! Possibly all keys are invalid or fake.");
            sc.close();
            return;
        }

        // Find the secret that appears most
        BigInteger realSecret = null;
        int maxCount = -1;
        for (Map.Entry<BigInteger, Integer> entry : secretCount.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                realSecret = entry.getKey();
            }
        }

        // Identify honest and fake key holders
        Set<String> honest = new HashSet<>();
        for (Set<String> group : secretGroups.get(realSecret)) {
            honest.addAll(group);
        }
        List<String> fake = new ArrayList<>(keyNames);
        fake.removeAll(honest);

        // Output results and optionally countdown
        System.out.println("\n----- Results -----");
        System.out.println("The secret code to activate the bomb is: " + realSecret);

        if (fake.isEmpty()) {
            System.out.println("\nAll keys are correct. Initiating bomb activation countdown...");
            try {
                for (int i = 10; i >= 1; i--) {
                    System.out.println(i);
                    Thread.sleep(1000); // 1 second pause
                }
            } catch (InterruptedException e) {
                // Ignore interruption
            }
            System.out.println("BOOM! Bomb activated! 💥");
        } else {
            System.out.println("Fake keys held by: " + fake);
        }

        sc.close();
    }

    // Generate all n choose k combinations of indices
    static List<int[]> combinations(int n, int k) {
        List<int[]> out = new ArrayList<>();
        int[] indices = new int[k];
        for (int i = 0; i < k; i++)
            indices[i] = i;

        while (indices[k - 1] < n) {
            out.add(indices.clone());

            int t = k - 1;
            while (t != 0 && indices[t] == n - k + t)
                t--;
            indices[t]++;
            for (int i = t + 1; i < k; i++)
                indices[i] = indices[i - 1] + 1;
        }
        return out;
    }

    // Perform Lagrange interpolation at atX (usually 0) to find constant term (the secret)
    static BigInteger lagrangeConstant(BigInteger[] xs, BigInteger[] ys, BigInteger atX) {
        BigInteger result = BigInteger.ZERO;
        int k = xs.length;

        for (int j = 0; j < k; j++) {
            BigInteger numerator = BigInteger.ONE;
            BigInteger denominator = BigInteger.ONE;

            for (int m = 0; m < k; m++) {
                if (m == j) continue;
                numerator = numerator.multiply(atX.subtract(xs[m]));
                denominator = denominator.multiply(xs[j].subtract(xs[m]));
            }

            BigInteger frac = fracDiv(numerator, denominator);
            result = result.add(ys[j].multiply(frac));
        }

        return result;
    }

    // Exact integer division, throws exception if division is not exact
    static BigInteger fracDiv(BigInteger num, BigInteger denom) {
        if (!num.mod(denom).equals(BigInteger.ZERO))
            throw new ArithmeticException("Non-integer division in interpolation!");
        return num.divide(denom);
    }
}
