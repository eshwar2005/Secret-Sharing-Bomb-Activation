import java.io.*;
import java.math.BigInteger;
import java.util.*;

public class SecretSharingAuto {

    static class Point {
        BigInteger x, y;
        Point(BigInteger x, BigInteger y) {
            this.x = x; this.y = y;
        }
    }

    // Parses the JSON file and extracts n, k, and roots (x, y) decoded from base.
    // The decoded 'k' is stored in outK[0].
    static List<Point> parseJsonFile(String filename, int[] outK) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line.trim());
        }
        br.close();

        String json = sb.toString().replaceAll("\\s+", "");

        // Parse n and k
        int n = 0, k = 0;
        int keysStart = json.indexOf("\"keys\":{");
        if (keysStart == -1) 
            throw new RuntimeException("JSON format error: missing 'keys'");

        int keysEnd = json.indexOf("}", keysStart);
        String keysBlock = json.substring(keysStart + 8, keysEnd);
        String[] pairs = keysBlock.split(",");
        for (String pair : pairs) {
            if (pair.startsWith("\"n\":")) n = Integer.parseInt(pair.substring(4));
            else if (pair.startsWith("\"k\":")) k = Integer.parseInt(pair.substring(4));
        }
        outK[0] = k;

        List<Point> points = new ArrayList<>();

        // Parse individual roots
        int pos = keysEnd + 1;
        while (pos < json.length()) {
            // Move to start of next key
            while (pos < json.length() && !Character.isDigit(json.charAt(pos))) pos++;
            if (pos >= json.length() || !Character.isDigit(json.charAt(pos))) break;

            int keyEnd = json.indexOf("\":{", pos);
            if (keyEnd == -1) break;

            String xStr = json.substring(pos, keyEnd);
            BigInteger x = new BigInteger(xStr);

            int objStart = keyEnd + 3;
            int objEnd = json.indexOf("}", objStart);
            if (objEnd == -1) break;

            String objBlock = json.substring(objStart, objEnd);

            // Extract base
            int baseIdx = objBlock.indexOf("\"base\":\"");
            int baseStart = baseIdx + 8;
            int baseEnd = objBlock.indexOf("\"", baseStart);
            String baseStr = objBlock.substring(baseStart, baseEnd);

            // Extract value
            int valIdx = objBlock.indexOf("\"value\":\"");
            int valStart = valIdx + 9;
            int valEnd = objBlock.indexOf("\"", valStart);
            String valStr = objBlock.substring(valStart, valEnd);

            // Decode y
            BigInteger y = new BigInteger(valStr, Integer.parseInt(baseStr));

            points.add(new Point(x, y));
            pos = objEnd + 1;
        }
        return points;
    }

    // Computes Lagrange Interpolation at x=0 for first k points
    static BigInteger lagrangeAtZero(List<Point> pts, int k) {
        BigInteger result = BigInteger.ZERO;
        for (int i = 0; i < k; ++i) {
            BigInteger xi = pts.get(i).x;
            BigInteger yi = pts.get(i).y;
            BigInteger numerator = BigInteger.ONE;
            BigInteger denominator = BigInteger.ONE;

            for (int j = 0; j < k; ++j) {
                if (j == i) continue;
                BigInteger xj = pts.get(j).x;
                numerator = numerator.multiply(xj.negate());
                denominator = denominator.multiply(xi.subtract(xj));
            }

            result = result.add(yi.multiply(numerator).divide(denominator));
        }
        return result;
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Usage: java SecretSharingAuto <json_testcase_file>");
            return;
        }

        int[] outK = new int[1];
        List<Point> points = parseJsonFile(args[0], outK);
        int k = outK[0];

        if (points.size() < k) {
            System.out.println("Error: Number of points less than k.");
            return;
        }

        BigInteger secret = lagrangeAtZero(points, k);
        System.out.println(secret);
    }
}
