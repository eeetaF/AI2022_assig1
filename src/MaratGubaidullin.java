import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class MaratGubaidullin {
    static final int n = 9;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Initial map generation
        ArrayList<ArrayList<Cell>> map = Algorithms.generateInitialMap();

        // current map generation
        System.out.print("Choose map generation (1 - input.txt, 2 - console, 3 - test generation): ");
        char Choice = sc.next().charAt(0);
        while (Choice != '1' && Choice != '2' && Choice != '3') {
            System.out.print("Wrong choice. Choose map generation (1 - input.txt, 2 - console, 3 - test generation): ");
            Choice = sc.next().charAt(0);
        }

        int generations = 1;
        switch (Choice) {
            case '1' ->
                    // Read map from file
                    Algorithms.readMapFromFile(map);
            case '2' ->
                    // Read map from console
                    Algorithms.readMapFromConsole(map);
            case '3' -> {
                // Generate map
                System.out.print("Enter number of generations: ");
                generations = sc.nextInt();
                Algorithms.generateMap(map);
            }
        }

        int algorithm, winsA = 0, winsB = 0, losesA = 0, losesB = 0;
        double[] timesA = new double[1000], timesB = new double[1000];
        if (Choice != '3') {
            // set algorithm
            System.out.print("Choose algorithm (1 - A*, 2 - Backtracking): ");
            char charAlgorithm = sc.next().charAt(0);
            while (charAlgorithm != '1' && charAlgorithm != '2') {
                System.out.print("Wrong choice. Choose algorithm (1 - A*, 2 - Backtracking): ");
                charAlgorithm = sc.next().charAt(0);
            }
            algorithm = charAlgorithm - '0';

            int result;
            Algorithms.printMap(map);

            long start = System.nanoTime();
            if (algorithm == 1) {
                result = A_star.start(map);
                long finish = System.nanoTime();
                long timeElapsed = finish - start;
                if (result != -1) {
                    try {
                        FileWriter myWriter = new FileWriter("outputAStar.txt", true);
                        myWriter.write(timeElapsed / 1000000 + " ms\n");
                        myWriter.close();
                    } catch (Exception ignored) {}
                }
                System.out.println("Time elapsed: " + timeElapsed / 1000000 + " ms");
            } else {
                result = Backtracking.start(map);
                long finish = System.nanoTime();
                long timeElapsed = finish - start;
                if (result != -1) {
                    try {
                        FileWriter myWriter = new FileWriter("outputBacktracking.txt", true);
                        myWriter.write(timeElapsed / 1000000 + " ms\n");
                        myWriter.close();
                    } catch (Exception ignored) {}
                }
                System.out.println("Time elapsed: " + timeElapsed / 1000000 + " ms");
            }
            System.out.println(result);
            Algorithms.clearMap(map);
            Algorithms.generateMap(map);
        } else {
            int resultA, resultB;
            long begin = System.nanoTime();
            for (int i = 0; i < generations; i++) {
                Algorithms.printMap(map);

                System.out.println("Generation #" + (i + 1));
                System.out.println("Time from beginning: " + (System.nanoTime() - begin) / 1000000000 + " sec");

                System.out.println("A*");
                double start = System.nanoTime();
                resultA = A_star.start(map);
                double finish = System.nanoTime();
                double timeElapsed = finish - start;
                timesA[i] = timeElapsed;
                if (resultA != -1) winsA++;
                else losesA++;
                System.out.println("Time elapsed: " + timeElapsed / 1000000 + " ms");
                System.out.println("Result: " + resultA);
                System.out.println();

                System.out.println("Backtracking");
                start = System.nanoTime();
                resultB = Backtracking.start(map);
                finish = System.nanoTime();
                timeElapsed = finish - start;
                timesB[i] = timeElapsed;
                if (resultB != -1) winsB++;
                else losesB++;
                System.out.println("Time elapsed: " + timeElapsed / 1000000 + " ms");
                System.out.println("Result: " + resultB);
                System.out.println();
                if (resultA != resultB) {
                    break;
                }
                Algorithms.clearMap(map);
                Algorithms.generateMap(map);
            }

            double meanA = 0, meanB = 0, modeMedianA, modeMedianB, standardDeviationA = 0, standardDeviationB = 0;
            for (int i = 0; i < 1000; i++) {
                meanA += timesA[i];
                meanB += timesB[i];
            }
            for (int i = 0; i < 1000; i++) {
                for (int j = 0; j < 1000 - i - 1; j++) {
                    if (timesA[j] > timesA[j + 1]) {
                        double temp = timesA[j];
                        timesA[j] = timesA[j + 1];
                        timesA[j + 1] = temp;
                    }
                    if (timesB[j] > timesB[j + 1]) {
                        double temp = timesB[j];
                        timesB[j] = timesB[j + 1];
                        timesB[j + 1] = temp;
                    }
                }
            }
            meanA /= 1000;
            meanB /= 1000;
            meanA /= 1000000; // ms
            meanB /= 1000000; // ms
            modeMedianA = (timesA[499] + timesA[500]) / 2;
            modeMedianB = (timesB[499] + timesB[500]) / 2;
            modeMedianA /= 1000000; // ms
            modeMedianB /= 1000000; // ms
            for (int i = 0; i < 1000; i++) {
                standardDeviationA += Math.pow(timesA[i] / 1000000 - meanA, 2);
                standardDeviationB += Math.pow(timesB[i] / 1000000 - meanB, 2);
            }
            standardDeviationA /= 999;
            standardDeviationB /= 999;
            standardDeviationA = Math.sqrt(standardDeviationA);
            standardDeviationB = Math.sqrt(standardDeviationB);


            try {
                FileWriter myWriter = new FileWriter("Statistical_Analysis.txt");
                myWriter.write("A*:\n");
                myWriter.write("Wins: " + winsA + "\n");
                myWriter.write("Loses: " + losesA + "\n");
                myWriter.write("Win rate: " + (double) winsA / generations * 100 + "%\n");
                myWriter.write("Mean: " + meanA + " ms\n");
                myWriter.write("Mode-Median: " + modeMedianA + " ms\n");
                myWriter.write("Standard deviation: " + standardDeviationA + " ms\n");
                myWriter.write("\nBacktracking:\n");
                myWriter.write("Wins: " + winsB + "\n");
                myWriter.write("Loses: " + losesB + "\n");
                myWriter.write("Win rate: " + (double) winsB / generations * 100 + "%\n");
                myWriter.write("Mean: " + meanB + " ms\n");
                myWriter.write("Mode-Median: " + modeMedianB + " ms\n");
                myWriter.write("Standard deviation: " + standardDeviationB + " ms\n");
                myWriter.close();
            } catch (Exception ignored) {}
        }


        sc.close();
    }

    /**
     * Cell enum for map
     */
    public enum Cell {
        // Empty, Jack, Davy, Kraken, Rock, Tortuga, Chest, dangerZone
        E, J, D, K, R, T, C, Z, JZ, KR
        // JZ - Special case, Jack and Danger cell
        // KR - Special case, Kraken and Rock cell
    }

    /**
     * Class that contains general methods for the algorithms and maps
     */
    public static class Algorithms {
        /**
         * @return an empty map nxn
         */
        public static ArrayList<ArrayList<Cell>> generateInitialMap() {
            ArrayList<ArrayList<Cell>> map = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                map.add(new ArrayList<>());
                for (int j = 0; j < n; j++)
                    map.get(i).add(Cell.E);

            }
            return map;
        }

        /**
         * @param map the map to be cleared
         */
        public static void clearMap(ArrayList<ArrayList<Cell>> map) {
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++)
                    map.get(i).set(j, Cell.E);
        }

        /**
         * @param map the map to be printed
         */
        public static void printMap(ArrayList<ArrayList<Cell>> map) {
            for (int i = 0; i < n * 2 + 1; i++)
                System.out.print("-");
            System.out.print("\n ");
            for (int i = 0; i < n; i++) {
                System.out.print(" " + i);
            }
            System.out.println();

            for (int i = 0; i < n; i++) {
                System.out.print(i);
                for (int j = 0; j < n; j++) {
                    switch (map.get(i).get(j)) {
                        case E -> System.out.print(" -");
                        case J -> System.out.print(" *");
                        case D -> System.out.print(" D");
                        case K -> System.out.print(" K");
                        case R -> System.out.print(" R");
                        case T -> System.out.print(" T");
                        case C -> System.out.print(" C");
                        case Z -> System.out.print(" Z");
                        case JZ -> System.out.print("JZ");
                        case KR -> System.out.print("KR");
                    }
                }
                System.out.println();
            }
            for (int i = 0; i < n * 2 + 1; i++)
                System.out.print("-");
            System.out.println();
        }

        /**
         * @param map the map is to be filled in from a file
         */
        public static void readMapFromFile(ArrayList<ArrayList<Cell>> map) {
            try {
                Scanner sc = new Scanner(new File("input.txt"));
                readMap(map, sc);
            } catch (Exception e) {
                System.out.println("Input file is invalid or does not exist!");}


        }

        /**
         * @param map the map is to be filled in from the console
         */
        public static void readMapFromConsole(ArrayList<ArrayList<Cell>> map) {
            try {
                Scanner sc = new Scanner(System.in);
                readMap(map, sc);
            } catch (Exception e) {
                System.out.println("Input is invalid, try again!");
                Algorithms.clearMap(map);
                Algorithms.readMapFromConsole(map);
            }
        }

        /**
         * @param map the map is to be filled in
         * @param sc the scanner (file/console etc.)
         */
        public static void readMap(ArrayList<ArrayList<Cell>> map, Scanner sc) throws Exception {
            for (int i = 0; i < 6; i++) {
                String coordinates = sc.next();
                int x = coordinates.charAt(1) - '0';
                int y = coordinates.charAt(3) - '0';
                switch (i) {
                    case 0 -> {
                        if (x != 0 || y != 0) throw new Exception();
                        map.get(x).set(y, Cell.J);
                    }
                    case 1 -> {
                        if (x == 0 && y == 0) throw new Exception();
                        map.get(x).set(y, Cell.D);
                        Algorithms.FillDangerDavy(map, x, y);
                    }
                    case 2 -> {
                        if ((x == 0 && y == 0) || (map.get(x).get(y) == Cell.D)) throw new Exception();
                        map.get(x).set(y, Cell.K);
                        Algorithms.FillDangerKraken(map, x, y);
                    }
                    case 3 -> {
                        if (map.get(x).get(y) == Cell.E || map.get(x).get(y) == Cell.Z)
                            map.get(x).set(y, Cell.R);
                        else if (map.get(x).get(y) == Cell.K)
                            map.get(x).set(y, Cell.KR);
                        else
                            throw new Exception();
                    }
                    case 4 -> {
                        if ((x == 0 && y == 0) || (map.get(x).get(y) == Cell.D) || (map.get(x).get(y) == Cell.K)
                                || (map.get(x).get(y) == Cell.Z) || (map.get(x).get(y) == Cell.R) || (map.get(x).get(y) == Cell.KR))
                            throw new Exception();
                        map.get(x).set(y, Cell.C);
                    }
                    case 5 -> {
                        if ((x == 0 && y == 0) || (map.get(x).get(y) == Cell.D) || (map.get(x).get(y) == Cell.K)
                                || (map.get(x).get(y) == Cell.Z) || (map.get(x).get(y) == Cell.R) || (map.get(x).get(y) == Cell.KR)
                                || (map.get(x).get(y) == Cell.C))
                            throw new Exception();
                        map.get(x).set(y, Cell.T);
                    }
                }
            }
            sc.close();
        }

        /**
         * @param map the map is to be filled in by random generation
         */
        public static void generateMap(ArrayList<ArrayList<Cell>> map) {
            // Jack
            map.get(0).set(0, Cell.J);
            Random random = new Random();

            // Davy
            int xD, yD;
            do {
                xD = random.nextInt(n);
                yD = random.nextInt(n);
            } while (xD == 0 && yD == 0);
            map.get(xD).set(yD, Cell.D);
            Algorithms.FillDangerDavy(map, xD, yD);

            // Kraken
            int xK, yK;
            do {
                xK = random.nextInt(n);
                yK = random.nextInt(n);
            } while ((xD == xK && yD == yK) || (xK == 0 && yK == 0));
            map.get(xK).set(yK, Cell.K);
            Algorithms.FillDangerKraken(map, xK, yK);

            // Chest
            int xC, yC;
            do {
                xC = random.nextInt(n);
                yC = random.nextInt(n);
            } while ((xD == xC && yD == yC) || (xK == xC && yK == yC) || (xC == 0 && yC == 0) ||
                    (map.get(xC).get(yC) == Cell.Z));
            map.get(xC).set(yC, Cell.C);

            // Tortuga
            int xT, yT;
            do {
                xT = random.nextInt(n);
                yT = random.nextInt(n);
            } while ((xD == xT && yD == yT) || (xK == xT && yK == yT) || (xC == xT && yC == yT) || (xT == 0 && yT == 0)
                    || (map.get(xT).get(yT) == Cell.Z));
            map.get(xT).set(yT, Cell.T);

            // Rock
            int xR, yR;
            do {
                xR = random.nextInt(n);
                yR = random.nextInt(n);
            } while ((xD == xR && yD == yR) || (xC == xR && yC == yR) || (xT == xR && yT == yR)
                    || (xR == 0 && yR == 0));
            if (map.get(xR).get(yR) == Cell.K) {
                map.get(xR).set(yR, Cell.KR);
            } else {
                map.get(xR).set(yR, Cell.R);
            }
        }

        /**
         * Method to fill in the danger zone of Davy Jones, used by both algorithms
         * @param map the map with Davy
         * @param x the x coordinate of Davy
         * @param y the y coordinate of Davy
         */
        public static void FillDangerDavy(ArrayList<ArrayList<Cell>> map, int x, int y) {
            if (x + 1 < n) map.get(x + 1).set(y, Cell.Z);
            if (x - 1 >= 0) map.get(x - 1).set(y, Cell.Z);
            if (y + 1 < n) map.get(x).set(y + 1, Cell.Z);
            if (y - 1 >= 0) map.get(x).set(y - 1, Cell.Z);
            if (x + 1 < n && y + 1 < n) map.get(x + 1).set(y + 1, Cell.Z);
            if (x + 1 < n && y - 1 >= 0) map.get(x + 1).set(y - 1, Cell.Z);
            if (x - 1 >= 0 && y + 1 < n) map.get(x - 1).set(y + 1, Cell.Z);
            if (x - 1 >= 0 && y - 1 >= 0) map.get(x - 1).set(y - 1, Cell.Z);
            if (map.get(0).get(0) == Cell.Z) map.get(0).set(0, Cell.JZ);
        }

        /**
         * Method to fill in the danger zone of Davy, used only by A*
         * @param map the map with Davy
         * @param x the x coordinate of Davy
         * @param y the y coordinate of Davy
         */
        public static void FillDangerDavyM(ArrayList<ArrayList<A_star_node>> map, int x, int y) {
            if (x + 1 < n) map.get(x + 1).get(y).cell = Cell.Z;
            if (x - 1 >= 0) map.get(x - 1).get(y).cell = Cell.Z;
            if (y + 1 < n) map.get(x).get(y + 1).cell = Cell.Z;
            if (y - 1 >= 0) map.get(x).get(y - 1).cell = Cell.Z;
            if (x + 1 < n && y + 1 < n) map.get(x + 1).get(y + 1).cell = Cell.Z;
            if (x + 1 < n && y - 1 >= 0) map.get(x + 1).get(y - 1).cell = Cell.Z;
            if (x - 1 >= 0 && y + 1 < n) map.get(x - 1).get(y + 1).cell = Cell.Z;
            if (x - 1 >= 0 && y - 1 >= 0) map.get(x - 1).get(y - 1).cell = Cell.Z;
        }

        /**
         * Method to fill in the danger zone of Kraken, used by both algorithms
         * @param map the map with Kraken
         * @param x the x coordinate of Kraken
         * @param y the y coordinate of Kraken
         */
        public static void FillDangerKraken(ArrayList<ArrayList<Cell>> map, int x, int y) {
            if (x + 1 < n && map.get(x + 1).get(y) != Cell.D) map.get(x + 1).set(y, Cell.Z);
            if (x - 1 >= 0 && map.get(x - 1).get(y) != Cell.D) map.get(x - 1).set(y, Cell.Z);
            if (y + 1 < n && map.get(x).get(y + 1) != Cell.D) map.get(x).set(y + 1, Cell.Z);
            if (y - 1 >= 0 && map.get(x).get(y - 1) != Cell.D) map.get(x).set(y - 1, Cell.Z);
            if (map.get(0).get(0) == Cell.Z) map.get(0).set(0, Cell.JZ);
        }

        /**
         * Class based on ArrayList to store both x and y coordinates
         */
        public static class PairedArrayList {
            public ArrayList<Integer> xCells, yCells;

            public PairedArrayList() {
                xCells = new ArrayList<>();
                yCells = new ArrayList<>();
            }

            public PairedArrayList(PairedArrayList other) {
                xCells = new ArrayList<>(other.xCells);
                yCells = new ArrayList<>(other.yCells);
            }

            public boolean contains(int x, int y) {
                for (int i = 0; i < xCells.size(); i++) {
                    if (xCells.get(i) == x && yCells.get(i) == y) return true;
                }
                return false;
            }

            public void add(int x, int y) {
                xCells.add(x);
                yCells.add(y);
            }

            public int size() {
                return xCells.size();
            }

            public void remove(int i) {
                if (i < xCells.size()) {
                    xCells.remove(i);
                    yCells.remove(i);
                }
            }

            public void switchValues(int i, int j) {
                if (i < xCells.size() && j < xCells.size()) {
                    int x = xCells.get(i);
                    int y = yCells.get(i);
                    xCells.set(i, xCells.get(j));
                    yCells.set(i, yCells.get(j));
                    xCells.set(j, x);
                    yCells.set(j, y);
                }
            }
        }

        /**
         * Writes the path to the file
         * @param path the path to write
         * @param myWriter contains the name of the file
         */
        public static void WritePath(Algorithms.PairedArrayList path, FileWriter myWriter) throws IOException {
            if (path.size() != 0) {
                myWriter.write("Win\n" + (path.size() - 1) + "\n");
                for (int i = 0; i < path.size(); i++) {
                    myWriter.write("[" + path.xCells.get(i) + "," + path.yCells.get(i) + "] ");
                }
                myWriter.write("\n");
                for (int i = 0; i < n * 2 + 1; i++) {
                    myWriter.write("-");
                }
                myWriter.write("\n ");
                for (int i = 0; i < n; i++) {
                    myWriter.write(" " + i);
                }
                myWriter.write("\n");
                for (int i = 0; i < n; i++) {
                    myWriter.write(i + " ");
                    for (int j = 0; j < n; j++) {
                        if (path.contains(i, j)) {
                            myWriter.write("* ");
                        } else {
                            myWriter.write("- ");
                        }
                    }
                    myWriter.write("\n");
                }
                for (int i = 0; i < n * 2 + 1; i++) {
                    myWriter.write("-");
                }
            } else {
                myWriter.write("Lose");
            }
            myWriter.write("\n");
            myWriter.close();
        }
    }

    /**
     * A* solving algorithm
     */
    public static class A_star {
        // way 1 - Jack -> Chest
        // way 2 - Jack -> Tortuga -> (Kraken) -> Chest
        static int shortest1, shortest2;
        static A_star_node jack, chest, tortuga, davy;
        static ArrayList<A_star_node> sortedList;
        static ArrayList<A_star_node> foundPath1, foundPath2;
        static ArrayList<ArrayList<A_star_node>> nodeMap;


        /**
         * @param map the game map
         * @return the shortest number of moves to get the chest (-1 if impossible)
         */
        public static int start(ArrayList<ArrayList<Cell>> map) {
            if (map.get(0).get(0) == Cell.JZ) return -1;

            nodeMap = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                nodeMap.add(new ArrayList<>());
                for (int j = 0; j < n; j++) {
                    nodeMap.get(i).add(new A_star_node(i, j, map.get(i).get(j)));
                    if (map.get(i).get(j) == Cell.J) {
                        jack = nodeMap.get(i).get(j);
                    }
                    if (map.get(i).get(j) == Cell.T) {
                        tortuga = nodeMap.get(i).get(j);
                    }
                    if (map.get(i).get(j) == Cell.C) {
                        chest = nodeMap.get(i).get(j);
                    }
                    if (map.get(i).get(j) == Cell.D) {
                        davy = nodeMap.get(i).get(j);
                    }
                }
            }

            findShortest1();

            foundPath2 = new ArrayList<>();
            shortest2 = 0;

            boolean isTortuga = findShortest2(jack, tortuga);
            if (shortest2 != -1 && isTortuga) findShortest2(tortuga, chest);

            System.out.println("Shortest way 1: " + shortest1 + " Shortest way 2: " + shortest2);

            if (shortest1 == -1 || shortest2 == -1) {
                if (shortest2 == -1)
                    parsePath(foundPath1);
                else
                    parsePath(foundPath2);
                return Math.max(shortest1, shortest2);
            }
            if (shortest1 < shortest2) {
                parsePath(foundPath1);
                return shortest1;
            } else {
                parsePath(foundPath2);
                return shortest2;
            }
        }

        /**
         * Method to find the shortest way from Jack to Chest
         */
        public static void findShortest1() {
            sortedList = new ArrayList<>();
            // set p of every node
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    nodeMap.get(i).get(j).p = Math.max(Math.abs(nodeMap.get(i).get(j).x - chest.x),
                            Math.abs(nodeMap.get(i).get(j).y - chest.y));
                }
            }
            nodeMap.get(0).get(0).set_a(0);
            sortedList.add(nodeMap.get(0).get(0));
            try {
                while (true) {
                    if (sortedList.get(0).sum > 999) {
                        shortest1 = -1;
                        return;
                    }
                    A_star_node currentNode = sortedList.get(0);
                    sortedList.remove(0);
                    A_star_node ChestNode = inspectArea(currentNode, nodeMap, false);
                    if (ChestNode != null) {
                        shortest1 = ChestNode.a;
                        break;
                    }
                }
            } catch (IndexOutOfBoundsException e) {
                shortest1 = -1;
            }
            if (foundPath2 != null)
                foundPath1 = foundPath2;
        }

        /**
         * Method to find the shortest way from Jack to Tortuga and from Tortuga to Chest
         *
         * @param start  the node to start from
         * @param target the node to end on
         * @return true if the target node was found, false if not or if found chest while searching for tortuga
         */
        public static boolean findShortest2(A_star_node start, A_star_node target) {
            int a = 0, x = 0, y = 0;
            boolean flag = false;
            if (start != jack) {
                a = nodeMap.get(start.x).get(start.y).a;
                x = start.x;
                y = start.y;
                flag = true;
            }
            resetNodeMap();
            sortedList = new ArrayList<>();
            if (flag) {
                nodeMap.get(x).get(y).tortugaPassed = true;
            }
            // set p of every node
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    nodeMap.get(i).get(j).p = Math.max(Math.abs(start.x - target.x), Math.abs(start.y - target.y));
                }
            }
            nodeMap.get(x).get(y).set_a(a);
            sortedList.add(nodeMap.get(x).get(y));
            try {
                while (true) {
                    if (sortedList.get(0).sum > 999) {
                        shortest2 = -1;
                        return false;
                    }
                    A_star_node currentNode = sortedList.get(0);
                    sortedList.remove(0);
                    if (currentNode.tortugaPassed)
                        findKrakenInArea(currentNode);
                    A_star_node ChestNode = inspectArea(currentNode, nodeMap, !flag);
                    if (ChestNode != null) {
                        shortest2 = ChestNode.a;
                        return ChestNode.cell != Cell.C;
                    }
                }
            } catch (IndexOutOfBoundsException e) {
                shortest2 = -1;
                return false;
            }
        }

        /**
         * Method that checks if the cells around the current cell are accessible
         * @param currentNode the current cell
         * @param nodeMap the map of nodes
         * @param flag true if searching for tortuga, false if searching for chest
         * @return the node of the chest if looking for chest, the node of the tortuga if looking for tortuga, null if not found
         */
        public static A_star_node inspectArea(A_star_node currentNode, ArrayList<ArrayList<A_star_node>> nodeMap, boolean flag) {
            if (currentNode.x + 1 < n) {
                A_star_node radNode = nodeMap.get(currentNode.x + 1).get(currentNode.y);
                if (DefineConnectionType(currentNode, radNode, flag)) return radNode;
            }
            if (currentNode.x - 1 >= 0) {
                A_star_node radNode = nodeMap.get(currentNode.x - 1).get(currentNode.y);
                if (DefineConnectionType(currentNode, radNode, flag)) return radNode;
            }
            if (currentNode.y - 1 >= 0) {
                A_star_node radNode = nodeMap.get(currentNode.x).get(currentNode.y - 1);
                if (DefineConnectionType(currentNode, radNode, flag)) return radNode;
            }
            if (currentNode.y + 1 < n) {
                A_star_node radNode = nodeMap.get(currentNode.x).get(currentNode.y + 1);
                if (DefineConnectionType(currentNode, radNode, flag)) return radNode;
            }
            if (currentNode.y + 1 < n && currentNode.x + 1 < n) {
                A_star_node radNode = nodeMap.get(currentNode.x + 1).get(currentNode.y + 1);
                if (DefineConnectionType(currentNode, radNode, flag)) return radNode;
            }
            if (currentNode.y + 1 < n && currentNode.x - 1 >= 0) {
                A_star_node radNode = nodeMap.get(currentNode.x - 1).get(currentNode.y + 1);
                if (DefineConnectionType(currentNode, radNode, flag)) return radNode;
            }
            if (currentNode.y - 1 >= 0 && currentNode.x - 1 >= 0) {
                A_star_node radNode = nodeMap.get(currentNode.x - 1).get(currentNode.y - 1);
                if (DefineConnectionType(currentNode, radNode, flag)) return radNode;
            }
            if (currentNode.y - 1 >= 0 && currentNode.x + 1 < n) {
                A_star_node radNode = nodeMap.get(currentNode.x + 1).get(currentNode.y - 1);
                if (DefineConnectionType(currentNode, radNode, flag)) return radNode;
            }
            return null;
        }

        /**
         * Method that checks the "connection type" between two nodes, good means that the node is passable,
         * bad means that the node is not passable
         * @param currentNode the current cell
         * @param radNode the cell to check
         * @param flag true if searching for tortuga, false if searching for chest
         * @return true if found the chest while looking for chest or if found tortuga while looking for tortuga, false if neither
         */
        private static boolean DefineConnectionType(A_star_node currentNode, A_star_node radNode, boolean flag) {
            Cell radCell;
            radCell = radNode.cell;
            if (radCell == Cell.C || (radCell == Cell.T && flag)) {
                radNode.connect_good_nodes(currentNode);
                findPath(radNode);
                return true;
            }
            if (radCell == Cell.Z || radCell == Cell.D || radCell == Cell.R || radCell == Cell.K || radCell == Cell.KR) {
                radNode.connect_bad_nodes(currentNode);
                return false;
            }
            if (radCell == Cell.T) {
                radNode.tortugaPassed = true;
            }
            if (radNode.connect_good_nodes(currentNode)) {
                sortedList.add(radNode);
                sortedList.sort(Comparator.comparingInt(o -> o.sum));
            }
            return false;
        }

        /**
         * Method that is looking for kraken around the currentNode and kills it if it is found
         * USED ONLY IF TORTUGA IS PASSED
         * @param currentNode the node around which to look for kraken
         */
        public static void findKrakenInArea(A_star_node currentNode) {
            if (currentNode.x + 1 < n) {
                A_star_node radNode = nodeMap.get(currentNode.x + 1).get(currentNode.y);
                if (radNode.cell == Cell.K || radNode.cell == Cell.KR) {
                    killKraken(radNode);
                    return;
                }
            }
            if (currentNode.x - 1 >= 0) {
                A_star_node radNode = nodeMap.get(currentNode.x - 1).get(currentNode.y);
                if (radNode.cell == Cell.K || radNode.cell == Cell.KR) {
                    killKraken(radNode);
                    return;
                }
            }
            if (currentNode.y - 1 >= 0) {
                A_star_node radNode = nodeMap.get(currentNode.x).get(currentNode.y - 1);
                if (radNode.cell == Cell.K || radNode.cell == Cell.KR) {
                    killKraken(radNode);
                    return;
                }
            }
            if (currentNode.y + 1 < n) {
                A_star_node radNode = nodeMap.get(currentNode.x).get(currentNode.y + 1);
                if (radNode.cell == Cell.K || radNode.cell == Cell.KR) {
                    killKraken(radNode);
                    return;
                }
            }
            if (currentNode.y + 1 < n && currentNode.x + 1 < n) {
                A_star_node radNode = nodeMap.get(currentNode.x + 1).get(currentNode.y + 1);
                if (radNode.cell == Cell.K || radNode.cell == Cell.KR) {
                    killKraken(radNode);
                    return;
                }
            }
            if (currentNode.y + 1 < n && currentNode.x - 1 >= 0) {
                A_star_node radNode = nodeMap.get(currentNode.x - 1).get(currentNode.y + 1);
                if (radNode.cell == Cell.K || radNode.cell == Cell.KR) {
                    killKraken(radNode);
                    return;
                }
            }
            if (currentNode.y - 1 >= 0 && currentNode.x - 1 >= 0) {
                A_star_node radNode = nodeMap.get(currentNode.x - 1).get(currentNode.y - 1);
                if (radNode.cell == Cell.K || radNode.cell == Cell.KR) {
                    killKraken(radNode);
                    return;
                }
            }
            if (currentNode.y - 1 >= 0 && currentNode.x + 1 < n) {
                A_star_node radNode = nodeMap.get(currentNode.x + 1).get(currentNode.y - 1);
                if (radNode.cell == Cell.K || radNode.cell == Cell.KR) {
                    killKraken(radNode);
                }
            }
        }

        /**
         * Method that fills the foundPath2 list with the path from the jack to tortuga or chest
         * @param node the last node in the path (chest or tortuga)
         */
        public static void findPath(A_star_node node) {
            A_star_node currentNode = node;
            ArrayList<A_star_node> temp = new ArrayList<>();
            if (foundPath2 == null)
                foundPath2 = new ArrayList<>();
            while (true) {
                temp.add(currentNode);
                if (currentNode.parents.size() == 0) {
                    break;
                }
                currentNode = currentNode.parents.get(0);
            }
            Collections.reverse(temp);
            foundPath2.addAll(temp);
        }

        /**
         * Method that kills the kraken on the map
         * @param kraken the node of kraken (must be on the map)
         */
        public static void killKraken(A_star_node kraken) {
            if (kraken.cell == Cell.KR)
                kraken.cell = Cell.R;
            else kraken.cell = Cell.E;

            if ((kraken.x + 1 < n) && (nodeMap.get(kraken.x + 1).get(kraken.y).cell == Cell.Z)) {
                nodeMap.get(kraken.x + 1).get(kraken.y).cell = Cell.E;
            }
            if ((kraken.x - 1 >= 0) && (nodeMap.get(kraken.x - 1).get(kraken.y).cell == Cell.Z)) {
                nodeMap.get(kraken.x - 1).get(kraken.y).cell = Cell.E;
            }
            if ((kraken.y - 1 >= 0) && (nodeMap.get(kraken.x).get(kraken.y - 1).cell == Cell.Z)) {
                nodeMap.get(kraken.x).get(kraken.y - 1).cell = Cell.E;
            }
            if ((kraken.y + 1 < n) && (nodeMap.get(kraken.x).get(kraken.y + 1).cell == Cell.Z)) {
                nodeMap.get(kraken.x).get(kraken.y + 1).cell = Cell.E;
            }
            Algorithms.FillDangerDavyM(nodeMap, davy.x, davy.y);
        }

        /**
         * Fills the nodeMap with empty nodes
         */
        public static void resetNodeMap() {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    nodeMap.get(i).set(j, new A_star_node(i, j, nodeMap.get(i).get(j).cell));
                }
            }
        }

        /**
         * Method that parses the ArrayList of nodes to PairedArrayList
         * (two array lists of corresponding coordinates x and y) and sends it to print
         * @param path the path to parse and print
         */
        public static void parsePath(ArrayList<A_star_node> path) {
            Algorithms.PairedArrayList parsedPath = new Algorithms.PairedArrayList();
            if (path != null)
                for (A_star_node a_star_node : path)
                    if (!parsedPath.contains(a_star_node.x, a_star_node.y))
                        parsedPath.add(a_star_node.x, a_star_node.y);

            printPath(parsedPath);
        }

        /**
         * Method that prints the path to the file
         * @param path the path to print
         */
        public static void printPath(Algorithms.PairedArrayList path) {
            try {
                FileWriter myWriter = new FileWriter("outputAStar.txt");
                Algorithms.WritePath(path, myWriter);
            } catch (Exception ignored) {}
        }
    }

    /**
     * Node class for A* algorithm only
     */
    public static class A_star_node {
        public int a, p, sum, x, y;
        // Actual cost, Potential cost, Sum = a + p, (x,y) - coordinates
        public Cell cell;
        public boolean tortugaPassed;
        public ArrayList<A_star_node> parents;


        /**
         * @param x x coordinate
         * @param y y coordinate
         * @param cell cell type
         */
        public A_star_node(int x, int y, Cell cell) {
            parents = new ArrayList<>();
            a = -1;
            p = 1000;
            sum = 1000;
            this.x = x;
            this.y = y;
            this.cell = cell;
            tortugaPassed = false;
        }


        /**
         * @param a actual cost
         */
        public void set_a(int a) {
            this.a = a;
            this.sum = a + p;
        }


        /**
         * Method that connects passable nodes (parent_node and this)
         * @param parent_node node that we came from
         * @return true if passed parent_node is not worse than current parent_node(s) and connected, false otherwise
         */
        public boolean connect_good_nodes(A_star_node parent_node) {
            if (parents.contains(parent_node))
                return false;
            if (this.sum > parent_node.a + 1 + this.p) {
                this.parents.clear();
                this.parents.add(parent_node);
                if (parent_node.tortugaPassed)
                    this.tortugaPassed = true;
                this.set_a(parent_node.a + 1);

                return true;
            } else if (this.sum == parent_node.a + 1 + this.p && (!this.tortugaPassed || parent_node.tortugaPassed)) {
                this.set_a(parent_node.a + 1);
                this.parents.add(parent_node);
                if (parent_node.tortugaPassed)
                    this.tortugaPassed = true;
                return true;
            }
            return false;
        }

        /**
         * Method that connects this node (not passable node) with parent_node
         * @param parent_node node that we came from
         */
        public void connect_bad_nodes(A_star_node parent_node) {
            this.parents.clear();
            this.parents.add(parent_node);
            this.set_a(1000);
        }
    }

    /**
     * Backtracking solving algorithm
     */
    public static class Backtracking {
        // way 1 - Jack -> Chest
        // way 2 - Jack -> Tortuga -> (Kraken) -> Chest
        // maxIterations is used to prevent infinite loops in case of not passable map
        static int shortest2t, shortest1, shortest2, xTortuga, yTortuga, xChest, yChest, maxSteps, xDavy, yDavy, xKraken, yKraken;
        static long iterations, maxIterations = Integer.MAX_VALUE;
        static Algorithms.PairedArrayList foundPath1, foundPath2, tempFoundPath2;
        static ArrayList<ArrayList<Cell>> noKrakenMap, mapWithKraken;

        /**
         * Method that solves the problem using backtracking algorithm
         * @param map map to solve
         * @return the shortest number of moves to get the chest (-1 if impossible)
         */
        public static int start(ArrayList<ArrayList<Cell>> map) {
            shortest1 = -1;
            shortest2 = -1;
            mapWithKraken = map;

            findShortest1();
            findShortest2();

            System.out.println("Shortest way 1: " + shortest1 + " Shortest way 2: " + shortest2);

            if (shortest1 == -1 || shortest2 == -1) {
                if (shortest1 == -1)
                    printPath(foundPath2);
                else
                    printPath(foundPath1);
                return Math.max(shortest1, shortest2);
            }

            if (shortest1 <= shortest2) {
                printPath(foundPath1);
                return shortest1;
            } else {
                printPath(foundPath2);
                return shortest2;
            }
        }

        /**
         * Method that finds the shortest way to get the chest without visiting Tortuga and killing Kraken
         */
        public static void findShortest1() {
            if (mapWithKraken.get(0).get(0) == Cell.JZ) {
                shortest1 = -1;
                return;
            }

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (mapWithKraken.get(i).get(j) == Cell.C) {
                        xChest = i;
                        yChest = j;
                    }
                    if (mapWithKraken.get(i).get(j) == Cell.T) {
                        xTortuga = i;
                        yTortuga = j;
                    }
                    if (mapWithKraken.get(i).get(j) == Cell.D) {
                        xDavy = i;
                        yDavy = j;
                    }
                    if (mapWithKraken.get(i).get(j) == Cell.K || mapWithKraken.get(i).get(j) == Cell.KR) {
                        xKraken = i;
                        yKraken = j;
                    }
                }
            }

            maxSteps = n * 3 + n / 3;
            iterations = 0;
            findRec1(0, 0, 0, new Algorithms.PairedArrayList());
        }

        /**
         * Recursive method that finds the shortest way to get the chest without visiting Tortuga and killing Kraken
         *
         * @param x current x coordinate
         * @param y current y coordinate
         * @param nOfSteps number of steps taken to get to this point
         * @param visitedCells list of visited cells
         */
        public static void findRec1(int x, int y, int nOfSteps, Algorithms.PairedArrayList visitedCells) {
            iterations++;
            if (nOfSteps >= maxSteps || x < 0 || y < 0 || x >= n || y >= n || mapWithKraken.get(x).get(y) == Cell.Z ||
                    mapWithKraken.get(x).get(y) == Cell.R || mapWithKraken.get(x).get(y) == Cell.KR ||
                    mapWithKraken.get(x).get(y) == Cell.K || mapWithKraken.get(x).get(y) == Cell.D ||
                    visitedCells.contains(x, y) || iterations > maxIterations) {
                return;
            }

            Algorithms.PairedArrayList visitedCellsCopy = new Algorithms.PairedArrayList(visitedCells);
            visitedCellsCopy.add(x, y);
            if (x == xChest && y == yChest) {
                if (shortest1 < 0 || nOfSteps < shortest1) {
                    shortest1 = nOfSteps;
                    foundPath1 = visitedCellsCopy;
                    maxSteps = nOfSteps;
                    return;
                }
            }

            Algorithms.PairedArrayList optimized = AreaOptimization(x, y, xChest, yChest);

            for (int i = 0; i < optimized.size(); i++)
                findRec1(optimized.xCells.get(i), optimized.yCells.get(i), nOfSteps + 1, visitedCellsCopy);
        }

        /**
         * Method that finds the shortest way to get the chest with visiting Tortuga and (possibly) killing Kraken
         */
        public static void findShortest2() {
            if (mapWithKraken.get(0).get(0) == Cell.JZ) {
                shortest1 = -1;
                return;
            }

            foundPath2 = new Algorithms.PairedArrayList();
            tempFoundPath2 = new Algorithms.PairedArrayList();
            noKrakenMap = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                noKrakenMap.add(new ArrayList<>());
                for (int j = 0; j < n; j++) {
                    noKrakenMap.get(i).add(mapWithKraken.get(i).get(j));
                }
            }
            killKraken(noKrakenMap, xKraken, yKraken);
            Algorithms.FillDangerDavy(noKrakenMap, xDavy, yDavy);

            maxSteps = shortest1 > 0 ? shortest1 : n * 3 + n / 3;

            shortest2t = -1;
            iterations = 0;
            findRec2(0, 0, 0, new Algorithms.PairedArrayList(), false, false);
            if (shortest2t < 0) return;

            shortest2 = shortest2t;

            maxSteps = shortest1 > 0 ? shortest1 : n * 3 + n / 3;

            iterations = 0;
            shortest2t = -1;
            foundPath2.remove(foundPath2.size() - 1);
            findRec2(xTortuga, yTortuga, 0, new Algorithms.PairedArrayList(), true, false);
            if (shortest2t < 0) {
                shortest2 = -1;
                return;
            }
            for (int i = 0; i < tempFoundPath2.size(); i++) {
                foundPath2.add(tempFoundPath2.xCells.get(i), tempFoundPath2.yCells.get(i));
            }

            shortest2 += shortest2t;
        }

        /**
         * Recursive method that finds the shortest way to get the chest with visiting Tortuga and (possibly) killing Kraken
         *
         * @param x current x coordinate
         * @param y current y coordinate
         * @param nOfSteps number of steps taken to get to this point
         * @param visitedCells list of visited cells
         * @param tortugaPassed true if Tortuga has been visited
         * @param krakenKilled true if Kraken has been killed
         */
        public static void findRec2(int x, int y, int nOfSteps, Algorithms.PairedArrayList visitedCells,
                                    boolean tortugaPassed, boolean krakenKilled) {
            iterations++;
            ArrayList<ArrayList<Cell>> map = krakenKilled ? noKrakenMap : mapWithKraken;


            if (nOfSteps >= maxSteps || x < 0 || y < 0 || x >= n || y >= n || map.get(x).get(y) == Cell.Z ||
                    map.get(x).get(y) == Cell.R || map.get(x).get(y) == Cell.D || visitedCells.contains(x, y) ||
                    iterations > maxIterations ||
                    ((map.get(x).get(y) == Cell.KR || map.get(x).get(y) == Cell.K) && !tortugaPassed)
            ) {
                return;
            }

            if (tortugaPassed && !krakenKilled)
                krakenKilled = killKrakenInArea(x, y);

            map = krakenKilled ? noKrakenMap : mapWithKraken;

            if ((map.get(x).get(y) == Cell.KR || map.get(x).get(y) == Cell.K) && tortugaPassed) {
                map = noKrakenMap;
                krakenKilled = true;
                if (map.get(x).get(y) == Cell.R || map.get(x).get(y) == Cell.Z) return;
            }

            Algorithms.PairedArrayList visitedCellsCopy = new Algorithms.PairedArrayList(visitedCells);
            visitedCellsCopy.add(x, y);
            if (x == xChest && y == yChest && tortugaPassed) {
                if (shortest2t < 0 || nOfSteps < shortest2t) {
                    shortest2t = nOfSteps;
                    tempFoundPath2 = visitedCellsCopy;
                    maxSteps = nOfSteps;
                    return;
                }
            }

            if (x == xTortuga && y == yTortuga && !tortugaPassed) {
                if (shortest2t < 0 || nOfSteps < shortest2t) {
                    shortest2t = nOfSteps;
                    foundPath2 = visitedCellsCopy;
                    maxSteps = nOfSteps;
                    return;
                }
            }

            Algorithms.PairedArrayList optimized;
            if (tortugaPassed)
                 optimized = AreaOptimization(x, y, xChest, yChest);
            else
                optimized = AreaOptimization(x, y, xTortuga, yTortuga);

            for (int i = 0; i < optimized.size(); i++) {
                findRec2(optimized.xCells.get(i), optimized.yCells.get(i), nOfSteps + 1,
                        visitedCellsCopy, tortugaPassed, krakenKilled);
            }
        }

        /**
         * Method for optimizing path choosing by finding the nearest cell to the target out of the 8 cells around
         * @param x current x coordinate
         * @param y current y coordinate
         * @param xDestination x coordinate of the target
         * @param yDestination y coordinate of the target
         * @return sorted by distance to the target list of cells
         */
        public static Algorithms.PairedArrayList AreaOptimization(int x, int y, int xDestination, int yDestination) {
            int k1 = Math.abs((x - 1) - xDestination) + Math.abs((y - 1) - yDestination);
            int k2 = Math.abs((x - 1) - xDestination) + Math.abs(y - yDestination);
            int k3 = Math.abs(x - xDestination) + Math.abs((y - 1) - yDestination);
            int k4 = Math.abs((x + 1) - xDestination) + Math.abs((y + 1) - yDestination);
            int k5 = Math.abs((x + 1) - xDestination) + Math.abs(y - yDestination);
            int k6 = Math.abs(x - xDestination) + Math.abs((y + 1) - yDestination);
            int k7 = Math.abs((x - 1) - xDestination) + Math.abs((y + 1) - yDestination);
            int k8 = Math.abs((x + 1) - xDestination) + Math.abs((y - 1) - yDestination);

            ArrayList<Integer> list = new ArrayList<>();
            list.add(k1);
            list.add(k2);
            list.add(k3);
            list.add(k4);
            list.add(k5);
            list.add(k6);
            list.add(k7);
            list.add(k8);

            Algorithms.PairedArrayList optimized = new Algorithms.PairedArrayList();
            optimized.add(x - 1, y - 1);
            optimized.add(x - 1, y);
            optimized.add(x, y - 1);
            optimized.add(x + 1, y + 1);
            optimized.add(x + 1, y);
            optimized.add(x, y + 1);
            optimized.add(x - 1, y + 1);
            optimized.add(x + 1, y - 1);

            int N = 8;
            int temp;
            for (int i = 0; i < N; i++) {
                for (int j = 1; j < (N - i); j++) {
                    if (list.get(j - 1) > list.get(j)) {
                        temp = list.get(j - 1);
                        list.set(j - 1, list.get(j));
                        list.set(j, temp);
                        optimized.switchValues(j - 1, j);
                    }
                }
            }
            return optimized;
        }

        /**
         * Method that checks if there's a kraken in the diagonal cells around the current cell
         * @param x current x coordinate
         * @param y current y coordinate
         * @return true if there's a kraken in the diagonal cells around the current cell and false otherwise
         */
        public static boolean killKrakenInArea(int x, int y) {
            if (x - 1 > 0 && y - 1 > 0 &&
                    (mapWithKraken.get(x - 1).get(y - 1) == Cell.K || mapWithKraken.get(x - 1).get(y - 1) == Cell.KR)) {
                return true;
            }
            if (x - 1 > 0 && y + 1 < n &&
                    (mapWithKraken.get(x - 1).get(y + 1) == Cell.K || mapWithKraken.get(x - 1).get(y + 1) == Cell.KR)) {
                return true;
            }
            if (x + 1 < n && y - 1 > 0 &&
                    (mapWithKraken.get(x + 1).get(y - 1) == Cell.K || mapWithKraken.get(x + 1).get(y - 1) == Cell.KR)) {
                return true;
            }
            return x + 1 < n && y + 1 < n &&
                    (mapWithKraken.get(x + 1).get(y + 1) == Cell.K || mapWithKraken.get(x + 1).get(y + 1) == Cell.KR);
        }

        /**
         * Method that "kills" the kraken on the map
         * @param map map with kraken
         * @param xKraken x coordinate of the kraken
         * @param yKraken y coordinate of the kraken
         */
        public static void killKraken(ArrayList<ArrayList<Cell>> map, int xKraken, int yKraken) {
            if (map.get(xKraken).get(yKraken) == Cell.KR)
                map.get(xKraken).set(yKraken, Cell.R);
            else map.get(xKraken).set(yKraken, Cell.E);

            if ((xKraken + 1 < n) && (map.get(xKraken + 1).get(yKraken) == Cell.Z)) {
                map.get(xKraken + 1).set(yKraken, Cell.E);
            }
            if ((xKraken - 1 >= 0) && (map.get(xKraken - 1).get(yKraken) == Cell.Z)) {
                map.get(xKraken - 1).set(yKraken, Cell.E);
            }
            if ((yKraken + 1 < n) && (map.get(xKraken).get(yKraken + 1) == Cell.Z)) {
                map.get(xKraken).set(yKraken + 1, Cell.E);
            }
            if ((yKraken - 1 >= 0) && (map.get(xKraken).get(yKraken - 1) == Cell.Z)) {
                map.get(xKraken).set(yKraken - 1, Cell.E);
            }
            Algorithms.FillDangerDavy(map, xDavy, yDavy);
        }

        /**
         * Method that prints the path to the file
         * @param path the path to print
         */
        public static void printPath(Algorithms.PairedArrayList path) {
            try {
                FileWriter myWriter = new FileWriter("outputBacktracking.txt");
                Algorithms.WritePath(path, myWriter);
            } catch (Exception ignored) {
            }
        }
    }
}
