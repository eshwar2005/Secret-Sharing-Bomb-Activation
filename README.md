Secret-Sharing-Bomb-Activation
A Java console application simulating secret sharing and bomb activation using cryptographic principles inspired by Shamir's Secret Sharing scheme. Participants (key holders) possess secret keys generated from mathematical operations. The bomb activates only when a specified minimum number of keys are combined correctly.

Features
Interactive input mode allowing any number of key holders n and minimum keys k required to activate the bomb.

Supports multiple operations for key generation: sum, multiplier, gcd, hcf, lcm, and direct number input.

Validates user-entered custom key values with warnings if mismatches occur.

Enables user selection of a subset of keys for secret reconstruction.

Displays the equation representing the selected keys (e.g., y = P1 + P4 + ...).

Simulates bomb activation with a dramatic countdown on successful validation.

Detects fake or incorrect keys and reports failures clearly.

Robust input validation for user interactions.

[NEW] Automatic JSON input parsing for polynomial roots encoded with varying bases.

[NEW] Polynomial interpolation (Lagrange method) implementation to reconstruct the secret constant term from provided roots.

[NEW] Non-interactive mode for processing test cases in JSON files, designed for assignment/sample test cases.

Handles large secret values using Java's BigInteger.

How to Run
Interactive Mode (Original User Interaction)
Compile the program:

bash
javac SecretSharingBombWithLogicVerification.java
Run:

bash
java SecretSharingBombWithLogicVerification
Follow the prompts to:

Enter total number of key holders (n) and the minimum keys needed to activate (k).

For each key holder, enter their name, select an operation (sum, multiplier, gcd, hcf, lcm, or number) and provide operands.

Optionally override keys with custom values.

Select keys to reconstruct the secret and enter values to verify and activate the bomb.

Automated Mode for JSON Test Cases (New Feature)
Place test case JSON files (e.g., testcase1.json, testcase2.json) in the project directory.

Use the new SecretSharingAuto program to parse and compute the secret constant c from the files.

Compile:

bash
javac SecretSharingAuto.java
Run for each test case:

bash
java SecretSharingAuto testcase1.json
java SecretSharingAuto testcase2.json
The output will be the secret constant term c (a big integer), reconstructed from the given polynomial roots.

Example Interaction (Interactive Mode)
text
Enter total number of key holders (n): 3
Enter number of keys needed to activate bomb (k): 2

Name of key holder #1: Alice
Operation for Alice: sum
Enter first operand: 10
Enter second operand: 20
Computed key value: 30

Name of key holder #2: Bob
Operation for Bob: number
Enter first operand: 42
Computed key value: 42

Name of key holder #3: Carol
Operation for Carol: multiplier
Enter first operand: 2
Enter second operand: 7
Computed key value: 14

Summary of keys and logic:
Alice key = 30, Bob key = 42, Carol key = 14

Select keys to reconstruct secret (space separated): Alice Bob
Enter value for Alice: 30
Enter value for Bob: 42

Correct! Bomb activated countdown starting...
10
9
...
1
BOOM! Bomb activated! 💥
How It Works
Each key's secret value is generated with the chosen mathematical operation.

To activate the bomb, at least k participants combine their keys.

The program validates entered values against computed keys.

In automated mode, the program reconstructs the secret constant term using polynomial interpolation (Lagrange method) based on input roots provided in JSON.

Successful reconstruction triggers the bomb countdown simulation.

Incorrect or fake keys cause activation failure messages.

JSON Input Format (for Automated Mode)
Input JSON files must specify:

"keys": { "n": <number of roots>, "k": <minimum number of roots required> }

Each root as an object with its key as the x-coordinate, and a "base" and "value" to decode the y-coordinate.

Example:

json
{
  "keys": {
    "n": 4,
    "k": 3
  },
  "1": {"base": "10", "value": "4"},
  "2": {"base": "2", "value": "111"},
  "3": {"base": "10", "value": "12"},
  "6": {"base": "4", "value": "213"}
}
Disclaimer
This project is purely educational and simulates secret sharing and activation logic for learning purposes. It should never be used for any real-world sensitive or dangerous applications.

Contribution & Customization
Feel free to customize or expand the repository.
If you want me to add badges, installation instructions, contribution guidelines, or automated test suites, please ask.

Links to the new automated mode code for your repository:
SecretSharingAuto.java (Add this to your repo)

You can include or link it in your repo as an additional program to run the assignment test cases efficiently.
